package io.mosip.registration_client.telemetry;

import androidx.annotation.NonNull;
import io.mosip.registration_client.model.TelemetryPigeon;

public class TelemetryApiImpl implements TelemetryPigeon.TelemetryApi {
    private final AndroidMetricCollector collector;

    public TelemetryApiImpl(AndroidMetricCollector collector) {
        this.collector = collector;
    }

    @Override
    public void logMetric(@NonNull String metricJson) {
        if (metricJson == null || metricJson.trim().isEmpty()) {
            return;
        }
        // Passes the raw metric from Flutter to the collector, 
        // which wraps it in the Logstash envelope and writes it asynchronously.
        collector.logRawMetric(metricJson);
    }
}