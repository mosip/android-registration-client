package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.interceptor.RestAuthInterceptor;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.LocalDateTimeDeserializer;
import io.mosip.registration.clientmanager.util.LocalDateTimeSerializer;
import io.mosip.registration.clientmanager.util.RestServiceTestHelper;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Anshul vanawat
 * @since 01/06/2022.
 */

@RunWith(RobolectricTestRunner.class)
public class MasterDataServiceImplTest {

    private static final String GLOBAL_PARAM_ID = "mosip.kernel.transliteration.english-language-code";
    private static final String GLOBAL_PARAM_VALUE = "eng";
    private static final int GLOBAL_PARAM_COUNT = 184;
    private static final String DECRYPTED_RESPONSE = "eyJtb3NpcC5rZXJuZWwudHJhbnNsaXRlcmF0aW9uLmVuZ2xpc2gtbGFuZ3VhZ2UtY29kZSI6ImVuZyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5wcmVfcmVnX25vX29mX2RheXNfbGltaXQiOiI3IiwibW9zaXAubWluLWxhbmd1YWdlcy5jb3VudCI6IjIiLCJtb3NpcC5rZXJuZWwucmlkLnNlcXVlbmNlLWxlbmd0aCI6IjUiLCJtb3NpcC5yZWdpc3RyYXRpb24uY2JlZmZfZm9ybWF0X29yZyI6Ik1vc2lwIiwibW9zaXAucmVnaXN0cmF0aW9uLnByZVJlZ2lzdHJhdGlvblBhY2tldERlbGV0aW9uSm9iLmZyZXF1ZW5jeSI6IjE5MCIsIm1vc2lwLm1hbmRhdG9yeS1sYW5ndWFnZXMiOiJlbmciLCJvYmplY3RzdG9yZS5hZGFwdGVyLm5hbWUiOiJQb3NpeEFkYXB0ZXIiLCJtb3NpcC5iaW9tZXRyaWMuc2RrLnByb3ZpZGVycy5pcmlzLm1vY2t2ZW5kb3IudmVyc2lvbiI6IjAuOSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5tZG0udHJ1c3QuZG9tYWluLmRpZ2l0YWxJZCI6IkRFVklDRSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5ncHNfcG9ydF90aW1lb3V0IjoiMTAwMCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5zeW5jX2pvYnNfcmVzdGFydF9mcmVxIjoiMCAwICovMTEgPyAqICoiLCJtb3NpcC5rZXJuZWwuY3J5cHRvLmdjbS10YWctbGVuZ3RoIjoiMTI4IiwibW9zaXAucmVnaXN0cmF0aW9uLmF1ZGl0X2FwcGxpY2F0aW9uX25hbWUiOiJSRUdJU1RSQVRJT04iLCJtb3NpcC5vcHRpb25hbC1sYW5ndWFnZXMiOiJmcmEsYXJhIiwibW9zaXAua2VybmVsLnByaWQucmVwZWF0aW5nLWJsb2NrLWxpbWl0IjoiMyIsIm1vc2lwLmtlcm5lbC52aWQubGVuZ3RoLnJlcGVhdGluZy1ibG9jay1saW1pdCI6IjIiLCJtb3NpcC5yZWdpc3RyYXRpb24uZmFjZV90aHJlc2hvbGQiOiI5MCIsIm1vc2lwLmJpb21ldHJpYy5zZGsucHJvdmlkZXJzLmZhY2UubW9ja3ZlbmRvci50aHJlc2hvbGQiOiI2MCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5IVFRQX0FQSV9XUklURV9USU1FT1VUIjoiNjAwMDAiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmVnaXN0cmF0aW9uUGFja2V0U3luY0pvYi5mcmVxdWVuY3kiOiIxOTAiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmlkX3N5bmNfYmF0Y2hfc2l6ZSI6IjUiLCJtb3NpcC5yZWdpc3RyYXRpb24uam9icy5vZmZsaW5lIjoiREVMX0owMDAxMyxSREpfSjAwMDEwLEFESl9KMDAwMTIsUFZTX0owMDAxNSIsIm1vc2lwLmtlcm5lbC5rZXlnZW5lcmF0b3IuYXN5bW1ldHJpYy1hbGdvcml0aG0tbmFtZSI6IlJTQSIsIm1vc2lwLmtlcm5lbC51aW4ucmVzdHJpY3RlZC1udW1iZXJzIjoiNzg2LDY2NiIsIm1vc2lwLmtlcm5lbC5rZXlnZW5lcmF0b3Iuc3ltbWV0cmljLWtleS1sZW5ndGgiOiIyNTYiLCJtb3NpcC5yZWdpc3RyYXRpb24uZGVsZXRlQXVkaXRMb2dzSm9iLmZyZXF1ZW5jeSI6IjE5MCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5ncHNfZGV2aWNlX2VuYWJsZV9mbGFnIjoiTiIsIm1vc2lwLmtlcm5lbC52aWQubGVuZ3RoLnJlcGVhdGluZy1saW1pdCI6IjIiLCJtb3NpcC5rZXJuZWwuY3J5cHRvLnNpZ24tYWxnb3JpdGhtLW5hbWUiOiJSUzI1NiIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5yZWdVc2VyTWFwcGluZ1N5bmNKb2IuZnJlcXVlbmN5IjoiMTkwIiwibW9zaXAua2VybmVsLnJlZ2lzdHJhdGlvbmNlbnRlcmlkLmxlbmd0aCI6IjUiLCJtb3NpcC5yZWdpc3RyYXRpb24uYXVkaXRfZGVmYXVsdF9ob3N0X25hbWUiOiJsb2NhbGhvc3QiLCJtb3NpcC5tYXgtbGFuZ3VhZ2VzLmNvdW50IjoiMyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5qb2JzLnNjaGVkdWxlci5lbmFibGUiOiJZIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5maW5nZXIuYXJncyI6IiIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5jYXB0dXJlX3RpbWVfb3V0IjoiMTAwMDAiLCJtb3NpcC5rZXJuZWwua2V5Z2VuZXJhdG9yLnN5bW1ldHJpYy1hbGdvcml0aG0tbmFtZSI6IkFFUyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5yZWdpc3RlcmluZ19pbmRpdmlkdWFsX3VybCI6Imh0dHBzOi8vZG9jcy5tb3NpcC5pby9wbGF0Zm9ybS9tb2R1bGVzL3JlZ2lzdHJhdGlvbi1jbGllbnQvcmVnaXN0cmF0aW9uLXBhY2tldCIsIm1vc2lwLmtlcm5lbC5jcnlwdG8uc3ltbWV0cmljLWFsZ29yaXRobS1uYW1lIjoiQUVTL0dDTS9QS0NTNVBhZGRpbmciLCJtb3NpcC5maW5nZXJwcmludF9hdXRoZW50aWNhdGlvbi5xdWFsaXR5X3Njb3JlIjoiMzAiLCJtb3NpcC5yZWdpc3RyYXRpb24uZG9jdW1lbnRfc2Nhbm5lcl9jb250cmFzdCI6IjEwIiwibW9zaXAucmVnaXN0cmF0aW9uLmlkbGVfdGltZSI6IjkwMCIsIm1vc2lwLmJpb21ldHJpYy5zZGsucHJvdmlkZXJzLmZhY2UubW9ja3ZlbmRvci5hcmdzIjoiIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5mYWNlLnRocmVzaG9sZCI6IjYwIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuZmluZ2VyLm1vY2t2ZW5kb3IuYXJncyI6IiIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5kb2N1bWVudF9zY2FubmVyX2RvY3R5cGUiOiJQREYiLCJtb3NpcC5yZWdpc3RyYXRpb24uaW52YWxpZF9sb2dpbl9jb3VudCI6IjUwIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5mYWNlLmNsYXNzbmFtZSI6ImlvLm1vc2lwLm1vY2suc2RrLmltcGwuU2FtcGxlU0RLIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuaXJpcy5tb2NrdmVuZG9yLnRocmVzaG9sZCI6IjYwIiwibW9zaXAua2VybmVsLnJpZC50aW1lc3RhbXAtbGVuZ3RoIjoiMTQiLCJtb3NpcC5yZWdpc3RyYXRpb24ucGFja2V0Lm1heGltdW0uY291bnQub2ZmbGluZS5mcmVxdWVuY3kiOiI3MCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5yZWdfcGFrX21heF90aW1lX2FwcHJ2X2xpbWl0IjoiNTAiLCJtb3NpcC5rZXJuZWwub3RwLmV4cGlyeS10aW1lIjoiMTgwIiwibW9zaXAucmVnaXN0cmF0aW9uLnJlZ2lzdHJhdGlvbl9wcmVfcmVnX3BhY2tldF9sb2NhdGlvbiI6Ii4uLy9QcmVSZWdQYWNrZXRTdG9yZSIsIm1vc2lwLmtlcm5lbC5wcmlkLmxlbmd0aCI6IjE0IiwibW9zaXAucmVnaXN0cmF0aW9uLm1kbS50cnVzdC5kb21haW4ucmNhcHR1cmUiOiJERVZJQ0UiLCJtb3NpcC5rZXJuZWwuY3J5cHRvLmFzeW1tZXRyaWMtYWxnb3JpdGhtLW5hbWUiOiJSU0EvRUNCL09BRVBXSVRIU0hBLTI1NkFORE1HRjFQQURESU5HIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuaXJpcy5tb2NrdmVuZG9yLmNsYXNzbmFtZSI6ImlvLm1vc2lwLm1vY2suc2RrLmltcGwuU2FtcGxlU0RLIiwibW9zaXAucmVnaXN0cmF0aW9uLm51bV9vZl9pcmlzX3JldHJpZXMiOiIzIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5maW5nZXIuY2xhc3NuYW1lIjoiaW8ubW9zaXAubW9jay5zZGsuaW1wbC5TYW1wbGVTREsiLCJtb3NpcC5rZXJuZWwuc2lnbmF0dXJlLnNpZ25hdHVyZS12ZXJzaW9uLWlkIjoidjEuMCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5tZG0uTU9TSVBESVNDLmNvbm5lY3Rpb24udGltZW91dCI6IjUwMDAiLCJtb3NpcC5yZWdpc3RyYXRpb24ucGFja2V0U3luY1N0YXR1c0pvYi5mcmVxdWVuY3kiOiIxOTAiLCJtb3NpcC5iaW9tZXRyaWMuc2RrLnByb3ZpZGVycy5mYWNlLm1vY2t2ZW5kb3IuY2xhc3NuYW1lIjoiaW8ubW9zaXAubW9jay5zZGsuaW1wbC5TYW1wbGVTREsiLCJtb3NpcC5yZWdpc3RyYXRpb24udXNlcm5hbWVfcHdkX2xlbmd0aCI6IjUwIiwibW9zaXAua2VybmVsLnByaWQucmVwZWF0aW5nLWxpbWl0IjoiMiIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5wcmVSZWdpc3RyYXRpb25EYXRhU3luY0pvYi5mcmVxdWVuY3kiOiIxOTAiLCJtb3NpcC5yZWdpc3RyYXRpb24ubWRtLmNvbm5lY3Rpb24udGltZW91dCI6IjEwMDAwIiwibW9zaXAucmVnaXN0cmF0aW9uLmpvYnMucmVzdGFydCI6IlJDU19KMDAwMDUiLCJtb3NpcC5rZXJuZWwuY3J5cHRvLmhhc2gtc3ltbWV0cmljLWtleS1sZW5ndGgiOiIyNTYiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmV2aWV3ZXJfYXV0aGVudGljYXRpb25fY29uZmlndXJhdGlvbiI6IlkiLCJtb3NpcC5yZWdpc3RyYXRpb24uZ2VvLmNhcHR1cmUuZnJlcXVlbmN5IjoibiIsIm1vc2lwLnJlZ2lzdHJhdGlvbi51aW5fdXBkYXRlX2NvbmZpZ19mbGFnIjoiWSIsIm1vc2lwLmJpb21ldHJpYy5zZGsucHJvdmlkZXIuZmluZ2VyLnRocmVzaG9sZCI6IjYwIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuZmluZ2VyLm1vY2t2ZW5kb3IudGhyZXNob2xkIjoiNjAiLCJtb3NpcC5yZWdpc3RyYXRpb24ubWF4X2FnZSI6IjE1MCIsIm1vc2lwLmtlcm5lbC5wcmlkLm5vdC1zdGFydC13aXRoIjoiMCwxIiwibW9zaXAucmVnaXN0cmF0aW9uLmRpc3RhbmNlLmZyb20ubWFjaGluZS50by5jZW50ZXIiOiI5MDAwMDAiLCJtb3NpcC5yZWdpc3RyYXRpb24ubWRtLnBvcnRSYW5nZUZyb20iOiI0NTAxIiwibW9zaXAucmVnaXN0cmF0aW9uLnF1YWxpdHlfY2hlY2tfd2l0aF9zZGsiOiJOIiwibW9zaXAua2VybmVsLmRhdGEta2V5LXNwbGl0dGVyIjoiI0tFWV9TUExJVFRFUiMiLCJtb3NpcC5yZWdpc3RyYXRpb24ubWRtLlJDQVBUVVJFLmNvbm5lY3Rpb24udGltZW91dCI6IjQwMDAwIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5pcmlzLmFyZ3MiOiIiLCJtb3NpcC5iaW9tZXRyaWMuc2RrLnByb3ZpZGVyLmlyaXMuY2xhc3NuYW1lIjoiaW8ubW9zaXAubW9jay5zZGsuaW1wbC5TYW1wbGVTREsiLCJtb3NpcC5yZWdpc3RyYXRpb24uZ3BzX2RldmljZV9tb2RlbCI6IkdQU0JVMzQzQ29ubmVjdG9yIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5pcmlzLnRocmVzaG9sZCI6IjYwIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuaXJpcy5tb2NrdmVuZG9yLmFyZ3MiOiIiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmVnX2RlbGV0aW9uX2NvbmZpZ3VyZWRfZGF5cyI6IjEiLCJtb3NpcC5yZWdpc3RyYXRpb24uYXVkaXRfZGVmYXVsdF9ob3N0X2lwIjoiMTIwLjAuMC4wIiwibW9zaXAucmVnaXN0cmF0aW9uLmRvY3VtZW50X3NjYW5uZXJfZW5hYmxlZCI6Ik5vIiwibW9zaXAua2VybmVsLnByaWQuc2VxdWVuY2UtbGltaXQiOiIzIiwibW9zaXAucmVnaXN0cmF0aW9uLkhUVFBfQVBJX1JFQURfVElNRU9VVCI6IjYwMDAwIiwibW9zaXAucmVnaXN0cmF0aW9uLnNlcnZlcl9wcm9maWxlIjoiU3RhZ2luZyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5yZWdfcGFrX21heF9jbnRfYXBwcnZfbGltaXQiOiIxMDAiLCJtb3NpcC5yZWdpc3RyYXRpb24udXBsb2FkaW5nX2RhdGFfdXJsIjoiaHR0cHM6Ly9kb2NzLm1vc2lwLmlvL3BsYXRmb3JtL21vZHVsZXMvcmVnaXN0cmF0aW9uLWNsaWVudC91aS1zcGVjaWZpY2F0aW9uLWZvci1yZWdpc3RyYXRpb24tY2xpZW50IiwibW9zaXAua2VybmVsLm1hY2hpbmVpZC5sZW5ndGgiOiI1IiwibW9zaXAucmVnaXN0cmF0aW9uLnByZV9yZWdfZGVsZXRpb25fY29uZmlndXJlZF9kYXlzIjoiMSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5tZG0udHJ1c3QuZG9tYWluLmRldmljZWluZm8iOiJERVZJQ0UiLCJtb3NpcC5yZWdpc3RyYXRpb24ub3RwX2NoYW5uZWxzIjoiZW1haWwiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmlnaHRzbGFwX2ZpbmdlcnByaW50X3RocmVzaG9sZCI6IjQwIiwibW9zaXAucmVnaXN0cmF0aW9uLnBhY2tldF91cGxvYWRfYmF0Y2hfc2l6ZSI6IjUiLCJtb3NpcC5rZXJuZWwudmlydXMtc2Nhbm5lci5ob3N0IjoiY2xhbWF2LmNsYW1hdiIsIm1vc2lwLmtlcm5lbC51aW4ubGVuZ3RoLmNvbmp1Z2F0aXZlLWV2ZW4tZGlnaXRzLWxpbWl0IjoiMyIsIm1vc2lwLmtlcm5lbC5zaWduYXR1cmUuc2lnbmF0dXJlLXJlcXVlc3QtaWQiOiJTSUdOQVRVUkUuUkVRVUVTVCIsIm1vc2lwLmJpb21ldHJpYy5zZGsucHJvdmlkZXIuZmFjZS5hcmdzIjoiIiwibW9zaXAucmVnaXN0cmF0aW9uLm1hcHBpbmdfZGV2aWNlc191cmwiOiJodHRwczovL2RvY3MubW9zaXAuaW8vcGxhdGZvcm0vbW9kdWxlcy9yZWdpc3RyYXRpb24tY2xpZW50L2RldmljZS1pbnRlZ3JhdGlvbi1zcGVjaWZpY2F0aW9ucyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5qb2JzLnVuVGFnZ2VkIjoiUERTX0owMDAwMyIsIm1vc2lwLnJpZ2h0X3RvX2xlZnRfb3JpZW50YXRpb24iOiJhcmEiLCJtb3NpcC5iaW9tZXRyaWMuc2RrLnByb3ZpZGVyLmlyaXMudmVyc2lvbiI6IjAuOSIsIm1vc2lwLmtlcm5lbC5yaWQubGVuZ3RoIjoiMjkiLCJtb3NpcC5rZXJuZWwudHJhbnNsaXRlcmF0aW9uLmZyYW5jaC1sYW5ndWFnZS1jb2RlIjoiZnJhIiwibW9zaXAucmVnaXN0cmF0aW9uLnJlZ2lzdHJhdGlvbkRlbGV0aW9uSm9iLmZyZXF1ZW5jeSI6IjE5MCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5vbmJvYXJkX3lvdXJzZWxmX3VybCI6Imh0dHBzOi8vZG9jcy5tb3NpcC5pby9wbGF0Zm9ybS9tb2R1bGVzL3JlZ2lzdHJhdGlvbi1jbGllbnQvZmlyc3QtdXNlci1yZWdpc3RyYXRpb24tYW5kLW9uYm9hcmRpbmciLCJtb3NpcC5yZWdpc3RyYXRpb24uc3RhdHVzX3N5bmNfYmF0Y2hfc2l6ZSI6IjUiLCJtb3NpcC5yZWdpc3RyYXRpb24uZ3BzX3NlcmlhbF9wb3J0X2xpbnV4IjoiL2Rldi90dHl1c2IwIiwibW9zaXAucmVnaXN0cmF0aW9uLmRvY3VtZW50X3NpemUiOiIyMDAwMDAwIiwibW9zaXAucmVnaXN0cmF0aW9uLnN5bmNoQ29uZmlnRGF0YUpvYi5mcmVxdWVuY3kiOiIxOTAiLCJtb3NpcC5rZXJuZWwudWluLmxlbmd0aC5yZXZlcnNlLWRpZ2l0cy1saW1pdCI6IjUiLCJtb3NpcC5rZXJuZWwudmlkLnJlc3RyaWN0ZWQtbnVtYmVycyI6Ijc4Niw2NjYiLCJtb3NpcC5rZXJuZWwudmlkLm5vdC1zdGFydC13aXRoIjoiMCwxIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuZmluZ2VyLm1vY2t2ZW5kb3IuY2xhc3NuYW1lIjoiaW8ubW9zaXAubW9jay5zZGsuaW1wbC5TYW1wbGVTREsiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmVwbGFjZV9zZGtfcXVhbGl0eV9zY29yZSI6Ik4iLCJtb3NpcC5yZWdpc3RyYXRpb24uYXVkaXRfYXBwbGljYXRpb25faWQiOiJSRUciLCJtb3NpcC5iaW9tZXRyaWMuc2RrLnByb3ZpZGVycy5mYWNlLm1vY2t2ZW5kb3IudmVyc2lvbiI6IjAuOSIsIm1vc2lwLmtlcm5lbC52aWQubGVuZ3RoIjoiMTYiLCJtb3NpcC5rZXJuZWwudWluLmxlbmd0aC5yZXBlYXRpbmctYmxvY2stbGltaXQiOiIyIiwibW9zaXAua2VybmVsLnRyYW5zbGl0ZXJhdGlvbi5hcmFiaWMtbGFuZ3VhZ2UtY29kZSI6ImFyYSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi50aHVtYnNfZmluZ2VycHJpbnRfdGhyZXNob2xkIjoiNDAiLCJtb3NpcC5yZWdpc3RyYXRpb24ub3BlcmF0b3Iub25ib2FyZGluZy5iaW9hdHRyaWJ1dGVzIjoibGVmdExpdHRsZSxsZWZ0UmluZyxsZWZ0TWlkZGxlLGxlZnRJbmRleCxsZWZ0VGh1bWIscmlnaHRMaXR0bGUscmlnaHRSaW5nLHJpZ2h0TWlkZGxlLHJpZ2h0SW5kZXgscmlnaHRUaHVtYixsZWZ0RXllLHJpZ2h0RXllLGZhY2UiLCJtb3NpcC5yZWdpc3RyYXRpb24ubWRzLmRlZHVwbGljYXRpb24uZW5hYmxlLmZsYWciOiJOIiwibW9zaXAucmVnaXN0cmF0aW9uLmRvY3VtZW50X3NjYW5uZXJfYnJpZ2h0bmVzcyI6IjEwIiwibW9zaXAua2VybmVsLnVpbi5sZW5ndGguc2VxdWVuY2UtbGltaXQiOiIzIiwibW9zaXAucmVnaXN0cmF0aW9uLnNvZnR3YXJlVXBkYXRlQ2hlY2tfY29uZmlndXJlZF9mcmVxdWVuY3kiOiIzMDAiLCJtb3NpcC5yZWdpc3RyYXRpb24uZG9jdW1lbnRfc2Nhbm5lcl9kcGkiOiI3NSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi51cGRhdGluZ19iaW9tZXRyaWNzX3VybCI6Imh0dHBzOi8vZG9jcy5tb3NpcC5pby9wbGF0Zm9ybS9tb2R1bGVzL3JlZ2lzdHJhdGlvbi1jbGllbnQvZ3VpZGUtdG8tY29uZmlndXJlLW1vc2lwLWZvci1iaW9tZXRyaWNzIiwibW9zaXAua2VybmVsLnZpcnVzLXNjYW5uZXIucG9ydCI6IjMzMTAiLCJtb3NpcC5yZWdpc3RyYXRpb24uYXVkaXRfbG9nX2RlbGV0aW9uX2NvbmZpZ3VyZWRfZGF5cyI6IjEwIiwibW9zaXAucmVnaXN0cmF0aW9uLmFwcC5pZCI6InJlZ2lzdHJhdGlvbmNsaWVudCIsIm1vc2lwLmxlZnRfdG9fcmlnaHRfb3JpZW50YXRpb24iOiJlbmcsZnJhIiwibW9zaXAua2VybmVsLnVpbi5sZW5ndGguZGlnaXRzLWxpbWl0IjoiNSIsIm9iamVjdC5zdG9yZS5iYXNlLmxvY2F0aW9uIjoiLi4vcGFja2V0cyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5sZWZ0c2xhcF9maW5nZXJwcmludF90aHJlc2hvbGQiOiI0MCIsIm1vc2lwLnNpZ24ucmVmaWQiOiJTSUdOQVRVUkVLRVkiLCJtb3NpcC5rZXJuZWwudmlkLmxlbmd0aC5zZXF1ZW5jZS1saW1pdCI6IjMiLCJtb3NpcC5yZWdpc3RyYXRpb24ubG9zdF91aW5fZGlzYWJsZV9mbGFnIjoiWSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5udW1fb2ZfZmFjZV9yZXRyaWVzIjoiMyIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5kb2N1bWVudF9lbmFibGVfZmxhZyI6InkiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmVzZXRfcGFzc3dvcmRfdXJsIjoiaHR0cHM6Ly9hcGktaW50ZXJuYWwuZGV2My5tb3NpcC5uZXQva2V5Y2xvYWsvYXV0aC9yZWFsbXMvbW9zaXAvYWNjb3VudC8iLCJtb3NpcC5yZWdpc3RyYXRpb24ubWRtLk1PU0lQRElORk8uY29ubmVjdGlvbi50aW1lb3V0IjoiNTAwMCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5tZG0ucG9ydFJhbmdlVG8iOiI0NjAwIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5mYWNlLnZlcnNpb24iOiIwLjkiLCJtb3NpcC5rZXJuZWwudWluLmxlbmd0aCI6IjEwIiwibW9zaXAucmVnaXN0cmF0aW9uLmZpbmdlcl9wcmludF9zY29yZSI6IjgwIiwibW9zaXAua2VybmVsLmNyeXB0by5oYXNoLWl0ZXJhdGlvbiI6IjEwMDAwMCIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5pcmlzX3RocmVzaG9sZCI6IjYwIiwibW9zaXAucmVnaXN0cmF0aW9uLmtleVBvbGljeVN5bmNKb2IuZnJlcXVlbmN5IjoiMTkwIiwibW9zaXAucmVnaXN0cmF0aW9uLnN1cGVydmlzb3JfYXBwcm92YWxfY29uZmlnX2ZsYWciOiJZIiwibW9zaXAucmVnaXN0cmF0aW9uLmNiZWZmX2FsZ29yaXRobV9vcmciOiJITUFDIiwibW9zaXAucmVnaXN0cmF0aW9uLm1kbS5TVFJFQU0uY29ubmVjdGlvbi50aW1lb3V0IjoiNDAwMDAiLCJtb3NpcC5rZXJuZWwueHNkZmlsZSI6IkxPQ0FMIiwibW9zaXAucmVnaXN0cmF0aW9uLnJlZnJlc2hlZF9sb2dpbl90aW1lIjoiNjAwIiwibW9zaXAua2VybmVsLmtleWdlbmVyYXRvci5hc3ltbWV0cmljLWtleS1sZW5ndGgiOiIyMDQ4IiwibW9zaXAucmVnaXN0cmF0aW9uLmNiZWZmX2FsZ29yaXRobV90eXBlIjoiU0hBLTI1NiIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5tZG0uaG9zdCI6IjEyNy4wLjAuMSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5zeW5jX2RhdGFfdXJsIjoiaHR0cHM6Ly9kb2NzLm1vc2lwLmlvL3BsYXRmb3JtL21vZHVsZXMvcmVnaXN0cmF0aW9uLWNsaWVudC9yZWdpc3RyYXRpb24tZnVuY3Rpb25hbGl0eSIsIm9iamVjdHN0b3JlLmNyeXB0by5uYW1lIjoiT2ZmbGluZVBhY2tldENyeXB0b1NlcnZpY2VJbXBsIiwibW9zaXAucmVnaXN0cmF0aW9uLmludmFsaWRfbG9naW5fdGltZSI6IjIiLCJtb3NpcC5yZWdpc3RyYXRpb24ubWRtLnZhbGlkYXRlLnRydXN0IjoidHJ1ZSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5zeW5jX3RyYW5zYWN0aW9uX25vX29mX2RheXNfbGltaXQiOiI1IiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlci5maW5nZXIudmVyc2lvbiI6IjAuOSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5sYXN0X2V4cG9ydF9yZWdpc3RyYXRpb25fY29uZmlnX3RpbWUiOiIxMDAiLCJtb3NpcC5rZXJuZWwucHJpZC5yZXN0cmljdGVkLW51bWJlcnMiOiI3ODYsNjY2IiwibW9zaXAucmVnaXN0cmF0aW9uLm1hc3RlclN5bmNKb2IuZnJlcXVlbmN5IjoiMTkwIiwibW9zaXAua2VybmVsLmNyeXB0by5oYXNoLWFsZ29yaXRobS1uYW1lIjoiUEJLREYyV2l0aEhtYWNTSEE1MTIiLCJtb3NpcC5yZWdpc3RyYXRpb24ubnVtX29mX2ZpbmdlcnByaW50X3JldHJpZXMiOiIzIiwibW9zaXAuYmlvbWV0cmljLnNkay5wcm92aWRlcnMuZmluZ2VyLm1vY2t2ZW5kb3IudmVyc2lvbiI6IjAuOSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5kaXNrX3NwYWNlX3NpemUiOiI1IiwibW9zaXAucmVncHJvYy5wYWNrZXQuY2xhc3NpZmllci50YWdnaW5nLmFnZWdyb3VwLnJhbmdlcyI6InsnSU5GQU5UJzonMC01JywnTUlOT1InOic2LTE3JywnQURVTFQnOicxOC0yMDAnfSIsIm1vc2lwLmtlcm5lbC51aW4ubGVuZ3RoLnJlcGVhdGluZy1saW1pdCI6IjIiLCJtb3NpcC5yZWdpc3RyYXRpb24uZ3BzX3NlcmlhbF9wb3J0X3dpbmRvd3MiOiIiLCJtb3NpcC5yZWdpc3RyYXRpb24ucmVnaXN0cmF0aW9uX3BhY2tldF9zdG9yZV9sb2NhdGlvbiI6Ii4uLy9QYWNrZXRTdG9yZSIsIm1vc2lwLnJlZ2lzdHJhdGlvbi5maWVsZHMudG8ucmV0YWluLnBvc3QucHJpZC5mZXRjaCI6ImNvbnNlbnQsY29uc2VudFRleHQscHJlZmVycmVkTGFuZyIsIm1vc2lwLmtlcm5lbC54c2RzdG9yYWdlLXVyaSI6IkxPQ0FMIn0";
    private static final String KEY_INDEX = "10:13:4a:ce:61:9b:bf:0d:e1:46:18:0a:5a:b9:d7:fb:7b:c0:51:09:74:7a:c1:32:00:8b:a1:3c:cb:37:cb:ed";

    private static final String GET_GLOBAL_CONFIGS_STATUS_200 = "getGlobalConfigs_200.json";

    Context appContext;
    ClientDatabase clientDatabase;
    ObjectMapper objectMapper = new ObjectMapper();
    MockWebServer server;
    Retrofit retrofit;

    SyncRestService syncRestService;
    ClientCryptoManagerService clientCryptoManagerService;
    GlobalParamRepository globalParamRepository;
    TemplateRepository templateRepository;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        GlobalParamDao globalParamDao = clientDatabase.globalParamDao();
        globalParamRepository = new GlobalParamRepository(globalParamDao);

        TemplateDao templateDao = clientDatabase.templateDao();
        templateRepository = new TemplateRepository(templateDao);

        server = new MockWebServer();
        server.start();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        Gson gson = gsonBuilder.create();

        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(appContext.getCacheDir(), cacheSize);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        client.addInterceptor(new RestAuthInterceptor(appContext));
        OkHttpClient okHttpClient = client.build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(server.url("/").toString())
                .client(okHttpClient)
                .build();

        syncRestService = retrofit.create(SyncRestService.class);
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void syncGlobalParamsData() throws Exception {
        //Mocking clientCrypto
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto(DECRYPTED_RESPONSE);

        clientCryptoManagerService = mock(LocalClientCryptoServiceImpl.class);
        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class)))
                .thenReturn(cryptoResponseDto);
        when(clientCryptoManagerService.getClientKeyIndex())
                .thenReturn(KEY_INDEX);

        MasterDataServiceImpl masterDataService = new MasterDataServiceImpl(appContext
                , objectMapper, syncRestService, clientCryptoManagerService, null
                , null, null, null
                , null, null, null
                , globalParamRepository, null, null
                , null, null, null
                , null, null);

        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(appContext, GET_GLOBAL_CONFIGS_STATUS_200)));

        masterDataService.syncGlobalParamsData();

        //waiting for sync to completed
        Thread.sleep(1000);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_ID);
        assertEquals(GLOBAL_PARAM_VALUE, globalParamValue);

        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertEquals(GLOBAL_PARAM_COUNT, globalParams.size());
    }


    @Test
    public void syncGlobalParamsDataNotFound() throws Exception {
        //Mocking clientCrypto
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto(DECRYPTED_RESPONSE);

        clientCryptoManagerService = mock(LocalClientCryptoServiceImpl.class);
        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class)))
                .thenReturn(cryptoResponseDto);

        when(clientCryptoManagerService.getClientKeyIndex())
                .thenReturn(KEY_INDEX);

        MasterDataServiceImpl masterDataService = new MasterDataServiceImpl(appContext
                , objectMapper, syncRestService, clientCryptoManagerService, null
                , null, null, null
                , null, null, null
                , globalParamRepository, null, null
                , null, null, null
                , null, null);

        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(RestServiceTestHelper.GET_PACKET_STATUS_404));

        masterDataService.syncGlobalParamsData();

        //waiting for sync to completed
        Thread.sleep(1000);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_ID);
        assertNull(globalParamValue);

        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertTrue(globalParams.isEmpty());
    }

    @Test
    public void syncGlobalParamsDataInvalidKeyIndex() throws Exception {
        //Mocking clientCrypto
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto(DECRYPTED_RESPONSE);

        clientCryptoManagerService = mock(LocalClientCryptoServiceImpl.class);
        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class)))
                .thenReturn(cryptoResponseDto);

        MasterDataServiceImpl masterDataService = new MasterDataServiceImpl(appContext
                , objectMapper, syncRestService, clientCryptoManagerService, null
                , null, null, null
                , null, null, null
                , globalParamRepository, null, null
                , null, null, null
                , null, null);

        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(RestServiceTestHelper.GET_PACKET_STATUS_404));

        masterDataService.syncGlobalParamsData();

        //waiting for sync to completed
        Thread.sleep(1000);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_ID);
        assertNull(globalParamValue);

        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertTrue(globalParams.isEmpty());
    }

    @Test
    public void syncGlobalParamsDataDecryptionFailed() throws Exception {
        clientCryptoManagerService = mock(LocalClientCryptoServiceImpl.class);
        when(clientCryptoManagerService.getClientKeyIndex())
                .thenReturn(KEY_INDEX);

        MasterDataServiceImpl masterDataService = new MasterDataServiceImpl(appContext
                , objectMapper, syncRestService, clientCryptoManagerService, null
                , null, null, null
                , null, null, null
                , globalParamRepository, null, null
                , null, null, null
                , null, null);

        //Preparing mock response
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(RestServiceTestHelper.GET_PACKET_STATUS_404));

        masterDataService.syncGlobalParamsData();

        //waiting for sync to completed
        Thread.sleep(1000);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_ID);
        assertNull(globalParamValue);

        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertTrue(globalParams.isEmpty());
    }

    @Test
    public void getPreviewTemplateContentTest() {
        MasterDataServiceImpl masterDataService = new MasterDataServiceImpl(appContext
                , objectMapper, syncRestService, clientCryptoManagerService, null
                , null, null, null
                , templateRepository, null, null
                , globalParamRepository, null, null
                , null, null, null
                , null, null);

        assertNotNull(masterDataService.getPreviewTemplateContent("reg-android-preview-template", "eng"));
    }
}