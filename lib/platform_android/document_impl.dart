import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/document_pigeon.dart';
import 'package:registration_client/platform_spi/document.dart';

class DocumentImpl implements Document {
  @override
  Future<void> addDocument(
      String fieldId, String docType, String reference, Uint8List bytes) async {
    try {
      await DocumentApi().addDocument(fieldId, docType, reference, bytes);
    } on PlatformException {
      debugPrint('DocumentApi call failed');
    } catch (e) {
      debugPrint('Document not added ${e.toString()}');
    }
  }

  @override
  Future<void> removeDocument(String fieldId, int pageIndex) async {
    try {
      await DocumentApi().removeDocument(fieldId, pageIndex);
    } on PlatformException {
      debugPrint('DocumentApi call failed');
    } catch (e) {
      debugPrint('Document not removed ${e.toString()}');
    }
  }

  @override
  Future<List<Uint8List?>> getScannedPages(String fieldId) async {
    List<Uint8List?> scannedPages = [];
    try {
      scannedPages = await DocumentApi().getScannedPages(fieldId);
    } on PlatformException {
      debugPrint('DocumentApi call failed');
    } catch (e) {
      debugPrint('get scanned pages failed ${e.toString()}');
    }
    return scannedPages;
  }

  @override
  Future<bool?> hasDocument(String fieldId) async {
    bool hasdocument = false;
    try {
      hasdocument = await DocumentApi().hasDocument(fieldId);
    } on PlatformException {
      debugPrint('DocumentApi call failed');
    } catch (e) {
      debugPrint('has document call failed ${e.toString()}');
    }
    return hasdocument;
  }

  @override
  Future<bool?> removeDocumentField(String fieldId) async {
    bool removeDocument = false;
    try {
      await DocumentApi().removeDocumentField(fieldId);
    } on PlatformException {
      debugPrint('DocumentApi call failed');
    } catch (e) {
      debugPrint('remove document field failed ${e.toString()}');
    }
  }
}

Document getDocumentImpl() => DocumentImpl();
