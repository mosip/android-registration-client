import 'package:registration_client/pigeon/transliteration_pigeon.dart';
import 'package:registration_client/platform_android/transliteration_service_impl.dart';

abstract class TransliterationService {
  Future<String> transliterate(TransliterationOptions options);
  factory TransliterationService() => getTransliterationServiceImpl();
}
