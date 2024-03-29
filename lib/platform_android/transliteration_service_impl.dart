/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/transliteration_pigeon.dart';
import 'package:registration_client/platform_spi/transliteration_service.dart';

class TransliterationServiceImpl implements TransliterationService {
  static final TransliterationServiceImpl instance =
      TransliterationServiceImpl._internal();

  factory TransliterationServiceImpl() {
    return instance;
  }

  TransliterationServiceImpl._internal();

  @override
  Future<String> transliterate(TransliterationOptions options) async {
    String result = "";
    try {
      result = await TransliterationApi().transliterate(options);
    } on PlatformException {
      log('Transliteration call failed');
    } catch (e) {
      log(e.toString());
    }
    return result;
  }
}

TransliterationServiceImpl getTransliterationServiceImpl() =>
    TransliterationServiceImpl();
