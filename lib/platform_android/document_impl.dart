/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

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
  Future<List<DocumentData?>> getScannedPages(String fieldId) async {
    List<DocumentData?> scannedPages = [];
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
    try {
      await DocumentApi().removeDocumentField(fieldId);
      return true;
    } on PlatformException {
      debugPrint('DocumentApi call failed');
    } catch (e) {
      debugPrint('remove document field failed ${e.toString()}');
    }
    return false;
  }
}

Document getDocumentImpl() => DocumentImpl();
