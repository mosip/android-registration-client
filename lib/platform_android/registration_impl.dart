import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/platform_spi/registration.dart';

class RegistrationImpl implements Registration {
  @override
  Future<String> startRegistration(List<String> langauages) async {
    String registrationStartResponse = '';
    try {
      registrationStartResponse = await RegistrationDataApi().startRegistration(langauages);
    } on PlatformException {
      registrationStartResponse = "Something went wrong!";
      debugPrint('RegApi call failed');
    } catch (e) {
      registrationStartResponse = "Something went wrong!";
      debugPrint('Registration not completed! ${e.toString()}');
    }

    return registrationStartResponse;
  }

  @override
  Future<bool> evaluateMVEL(String fieldData, String expression) async {
    bool isMvelValid = false;
    try {
      isMvelValid = await RegistrationDataApi().evaluateMVEL(fieldData, expression);
    } on PlatformException {
      debugPrint('RegApi mvel call failed');
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
