package io.mosip.registration_client.ocr.models;

public class OcrResultData {
    private final String rawText;
    private final float averageConfidence;

    public OcrResultData(String rawText, float averageConfidence) {
        this.rawText = rawText;
        this.averageConfidence = averageConfidence;
    }

    public String getRawText() { return rawText; }
    public float getAverageConfidence() { return averageConfidence; }
}