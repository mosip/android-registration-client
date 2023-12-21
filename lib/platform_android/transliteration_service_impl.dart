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
    late String result;
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
