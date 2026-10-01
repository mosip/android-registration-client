/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import 'package:pigeon/pigeon.dart';

class OcrResultMessage {
  String? documentType;
  double? confidence;
  Map<String?, String?>? data;
}

class OcrErrorMessage {
  String? errorCode;
  String? message;
  bool? isRetryable;
}

class ImageQualityMessage {
  bool? isAcceptable;
  String? guidanceMessage;
}

@HostApi()
abstract class OcrHostApi {
  int startDocumentScan();
  void cancelScan();
  void forceCapture();
  ImageQualityMessage checkImageQuality();
  void processImageFile(String filePath);
}

@FlutterApi()
abstract class OcrFlutterApi {
  void onOcrSuccess(OcrResultMessage result);
  void onOcrError(OcrErrorMessage error);
  void onQualityUpdate(ImageQualityMessage quality);
}