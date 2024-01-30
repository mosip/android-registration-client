/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/transliteration_pigeon.dart';
import 'package:registration_client/platform_android/transliteration_service_impl.dart';

abstract class TransliterationService {
  Future<String> transliterate(TransliterationOptions options);
  factory TransliterationService() => getTransliterationServiceImpl();
}
