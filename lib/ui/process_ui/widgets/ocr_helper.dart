import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';
import 'package:image_picker/image_picker.dart';

Future<Map<String, String>> scanAndExtractFields(BuildContext context) async {
  final picker = ImagePicker();
  final pickedImage = await picker.pickImage(source: ImageSource.camera);

  if (pickedImage == null) return {};

  final inputImage = InputImage.fromFile(File(pickedImage.path));
  final textRecognizer = TextRecognizer(script: TextRecognitionScript.latin);

  final RecognizedText recognizedText = await textRecognizer.processImage(inputImage);
  await textRecognizer.close();

  String rawText = recognizedText.text.toLowerCase();

  Map<String, String> extracted = {
    'name': _extractByRegex(rawText, r'\b(?:name|full\s*name)[:\-]?\s*([a-zA-Z ]{3,})', fallbackKeyword: 'name'),
    'age': _extractByRegex(rawText, r'\b(?:age)[:\-]?\s*(\d{1,3})\b|(?:dob|date\s*of\s*birth)[:\-]?\s*(\d{2}[\/\-]\d{2}[\/\-]\d{4})', fallbackKeyword: 'age'),
    'phone': _extractByRegex(rawText, r'\b(?:mobile|phone|contact|tel)[:\-]?\s*(?:\+91[\s\-]?)?(\d{10})\b', fallbackKeyword: 'phone'),
    'postal': _extractByRegex(rawText, r'\b(?:postal|zip|pincode|pin\s*code)[:\-]?\s*(\d{6})\b', fallbackKeyword: 'postal'),
    'gender': _extractByRegex(rawText, r'\b(?:gender|sex)[:\-]?\s*(male|female|other)\b', fallbackKeyword: 'gender'),
    'address': _extractByRegex(rawText, r'\b(?:address|addr)[:\-]?\s*([a-zA-Z0-9,.\-/ ]{10,})', fallbackKeyword: 'address'),
    'email': _extractByRegex(rawText, r'\b([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})\b', fallbackKeyword: 'email'),
  };

  return extracted;
}

String _extractByRegex(String text, String pattern, {String? fallbackKeyword}) {
  final regex = RegExp(pattern, caseSensitive: false);
  final match = regex.firstMatch(text);

  if (match != null) {
    for (int i = 1; i <= match.groupCount; i++) {
      final value = match.group(i)?.trim();
      if (value != null && value.isNotEmpty) return value;
    }
  }

  if (fallbackKeyword != null) {
    final lines = text.split('\n');
    for (final line in lines) {
      if (line.toLowerCase().contains(fallbackKeyword.toLowerCase())) {
        final parts = line.split(RegExp(r'[:\-]'));
        if (parts.length > 1) {
          final fallbackValue = parts.last.trim();
          if (fallbackValue.isNotEmpty) return fallbackValue;
        }
      }
    }
  }

  return '';
}
