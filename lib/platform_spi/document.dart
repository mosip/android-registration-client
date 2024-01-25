/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:typed_data';

import 'package:registration_client/pigeon/document_pigeon.dart';
import 'package:registration_client/platform_android/document_impl.dart';

abstract class Document {
  Future<void> addDocument(
      String fieldId, String docType, String reference, Uint8List bytes);
  Future<void> removeDocument(String fieldId, int pageIndex);

  Future<List<DocumentData?>> getScannedPages(String fieldId);

  Future<bool?> hasDocument(String fieldId);

  Future<bool?> removeDocumentField(String fieldId);

  factory Document() => getDocumentImpl();
}
