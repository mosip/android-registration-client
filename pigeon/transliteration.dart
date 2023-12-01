// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:pigeon/pigeon.dart';

class TransliterationOptions {
  String input;
  String sourceLanguage;
  String targetLanguage;

  TransliterationOptions({
    required this.input,
    required this.sourceLanguage,
    required this.targetLanguage,
  });
}

class TransliterationResult {
  String output;
  TransliterationResult({
    required this.output,
  });
}

@HostApi()
abstract class TransliterationApi {
  @async
  TransliterationResult transliterate(TransliterationOptions options);
}
