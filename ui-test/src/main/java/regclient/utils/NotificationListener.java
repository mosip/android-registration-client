package regclient.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.otp.Root;
import io.mosip.testrig.apirig.testrunner.OTPListener;
import io.mosip.testrig.apirig.utils.ConfigManager;

public final class NotificationListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);

	private static final long PING_INTERVAL_SECONDS = 30;
	private static final long MAX_RECONNECT_DELAY_SECONDS = 60;

	private static final ObjectMapper MAPPER = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "smtp-notification-listener");
		t.setDaemon(true);
		return t;
	});

	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

	private static volatile WebSocket webSocket;
	private static volatile URI uri;
	private static volatile long reconnectDelaySeconds = 1;
	private static volatile boolean started = false;

	private NotificationListener() {
	}

	public static synchronized void start() {
		if (started) {
			return;
		}
		started = true;
		uri = buildUri();
		connect();
		SCHEDULER.scheduleAtFixedRate(NotificationListener::ping, PING_INTERVAL_SECONDS, PING_INTERVAL_SECONDS,
				TimeUnit.SECONDS);
	}

	private static URI buildUri() {
		String externalUrl = ConfigManager.getIAMUrl();
		if (externalUrl.contains("/auth")) {
			externalUrl = externalUrl.replace("/auth", "");
		}
		String domain = externalUrl.substring(externalUrl.indexOf(".") + 1);
		return URI.create("wss://smtp." + domain + "/mocksmtp/websocket");
	}

	private static void connect() {
		if (OTPListener.bTerminate) {
			return;
		}
		try {
			webSocket = HTTP_CLIENT.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(30))
					.buildAsync(uri, new Client()).join();
			reconnectDelaySeconds = 1;
			LOGGER.info("Connected to mock SMTP websocket {}", uri);
		} catch (Exception e) {
			LOGGER.warn("Failed to connect to mock SMTP websocket {}: {}", uri, e.getMessage());
			scheduleReconnect();
		}
	}

	private static void scheduleReconnect() {
		if (OTPListener.bTerminate) {
			return;
		}
		long delay = reconnectDelaySeconds;
		reconnectDelaySeconds = Math.min(reconnectDelaySeconds * 2, MAX_RECONNECT_DELAY_SECONDS);
		LOGGER.info("Reconnecting to mock SMTP websocket in {} s", delay);
		SCHEDULER.schedule(NotificationListener::connect, delay, TimeUnit.SECONDS);
	}

	private static void ping() {
		WebSocket ws = webSocket;
		if (ws == null || ws.isOutputClosed() || ws.isInputClosed()) {
			return;
		}
		try {
			ws.sendPing(ByteBuffer.allocate(0));
		} catch (Exception e) {
			LOGGER.warn("Mock SMTP websocket ping failed: {}", e.getMessage());
		}
	}

	private static void handleMessage(String data) {
		try {
			Root root = MAPPER.readValue(data, Root.class);
			String message;
			String address;
			if ("SMS".equals(root.type)) {
				message = root.subject;
				address = root.to.text.trim();
			} else if ("MAIL".equals(root.type)) {
				message = root.html;
				address = root.to.value.get(0).address;
			} else {
				LOGGER.warn("Unsupported notification type. type={}", root.type);
				return;
			}
			if (!OTPListener.parseOtp(message).isEmpty() || !OTPListener.parseAdditionalReqId(message).isEmpty()) {
				OTPListener.emailNotificationMapS.put(address, message);
				LOGGER.info("Stored {} notification", root.type);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to process mock SMTP notification: {}", e.getMessage());
		}
	}

	private static class Client implements WebSocket.Listener {

		private final StringBuilder buffer = new StringBuilder();

		@Override
		public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
			buffer.append(data);
			if (last) {
				String message = buffer.toString();
				buffer.setLength(0);
				handleMessage(message);
			}
			ws.request(1);
			return null;
		}

		@Override
		public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
			LOGGER.warn("Mock SMTP websocket closed: status={} reason={}", statusCode, reason);
			scheduleReconnect();
			return null;
		}

		@Override
		public void onError(WebSocket ws, Throwable error) {
			LOGGER.warn("Mock SMTP websocket error: {}", error.getMessage());
			scheduleReconnect();
		}
	}
}
