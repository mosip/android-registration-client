import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class RegistrationDataApi {
  @async
  String startRegistration(List<String> languages);

  @async
  bool checkMVEL(String expression);

  @async
  String getPreviewTemplate(bool isPreview);
}
