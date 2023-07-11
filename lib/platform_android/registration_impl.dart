import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/auth_response_pigeon.dart';
import 'package:registration_client/pigeon/registration_data_pigeon.dart';
import 'package:registration_client/pigeon/user_pigeon.dart';

import 'package:registration_client/platform_spi/auth.dart';
import 'package:registration_client/platform_spi/registration.dart';
import 'package:shared_preferences/shared_preferences.dart';

class RegistrationImpl implements Registration {
  @override
  Future<bool> registerApplicant(RegistrationData registrationData) async {
    bool isRegistered = false;
    try {
      isRegistered = await RegistrationDataApi().registration(registrationData);
    } on PlatformException {
      debugPrint('RegApi call failed');
    } catch (e) {
      debugPrint('Registration not completed! ${e.toString()}');
    }

    return isRegistered;
  }
  
}

Registration getRegistrationImpl() => RegistrationImpl();
