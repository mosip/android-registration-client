import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/registration.dart';

class RegistrationImpl implements Registration {
  @override
  Future<bool> startRegistration(List<String> langauages) async {
    bool isRegistered = false;
    try {
      isRegistered = await RegistrationDataApi().startRegistration(langauages);
    } on PlatformException {
      debugPrint('RegApi call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }

    return isRegistered;
  }
  
  @override
  Future<bool> checkMVEL(String expression) async {
    bool isMvelValid = false;
    try {
      isMvelValid = await RegistrationDataApi().checkMVEL(expression);
    } on PlatformException {
      debugPrint('RegApi call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }
    return isMvelValid;
  }
  
  @override
  Future<String> getPreviewTemplate(bool isPreview) async {
    String previewTemplate = '';
    try {
      previewTemplate = await RegistrationDataApi().getPreviewTemplate(isPreview);
    } on PlatformException {
      debugPrint('Registration API template call failed');
    } catch (e) {
      debugPrint('Preview Template not fetched! ${e.toString()}');
    }

    return previewTemplate;
  }
  
}

Registration getRegistrationImpl() => RegistrationImpl();
