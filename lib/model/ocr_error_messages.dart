const Map<String, String> ocrErrorMessages = {
  // ── Standardised provider error codes (HLD §6.2.1) ──────────────────
  '101': 'No document detected. Please position your document within the frame.',
  '102': 'A technical error occurred. Please try again.',
  '103': 'This document type is not supported.',
  '104': 'Unable to connect to OCR service. Check your network connection.',
  '105': 'Document orientation is incorrect. Please adjust and try again.',
  '106': 'Image quality is too low. Improve lighting and hold steady.',
  '107': 'OCR service licence has expired. Contact your administrator.',
  '108': 'OCR configuration error. Contact your administrator.',
  '109': 'This field cannot be extracted from this document type.',
  '110': 'OCR service is starting up. Please try again shortly.',
  '111': 'OCR service is busy. Please try again shortly.',

  // ── Client-side failure codes (OcrError.ErrorCode) ──────────────────
  'CAMERA_ERROR': 'Camera error. Please try again.',
  'CAMERA_PERMISSION_DENIED':
      'Camera permission is required. Enable it in app Settings.',
  'CAMERA_START_FAILED': 'Failed to start camera. Please try again.',
  'FORCE_CAPTURE_FAILED': 'Capture failed. Please try again.',
  'UPLOAD_PROCESS_FAILED': 'Failed to process the image. Please try again.',
  'IMAGE_DECODE_FAILED': 'Could not read this image file. Try a different image.',
  'IMAGE_NULL': 'Invalid image. Please try again.',
  'IMAGE_BLURRY': 'Image is too blurry. Hold steady and try again.',
  'IMAGE_POOR_LIGHTING': 'Lighting is too poor. Move to a brighter area.',
  'TIMEOUT': 'OCR processing timed out. Please try again.',
  'NETWORK_ERROR': 'Network error. Check your connection and try again.',
  'MALFORMED_RESPONSE': 'Invalid response from OCR service.',
  'NO_FIELDS_EXTRACTED':
      'Could not extract fields from this document. Try a clearer image.',
  'NO_TEXT_FOUND': 'No text found. Ensure the document is visible and well-lit.',
  'ML_KIT_FAILURE': 'Text recognition engine error. Please try again.',
  'UNKNOWN': 'An unexpected error occurred. Please try again.',
};

String ocrErrorMessage(String? errorCode, [String? fallback]) {
  if (errorCode != null && ocrErrorMessages.containsKey(errorCode)) {
    return ocrErrorMessages[errorCode]!;
  }
  return fallback ?? 'An unexpected error occurred. Please try again.';
}
