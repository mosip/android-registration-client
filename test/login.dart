import 'package:registration_client/const/validator.dart';

class Login {
  String username = '';
  String password = '';

  String validateUsername(String uname) {
    if(uname.isEmpty) {
      return UserValidation.empty.value;
    }

    if(uname.length > 50) {
      return UserValidation.long.value;
    }

    if(uname != "abcdef" && uname != "pqrstu") {
      return UserValidation.invalid.value;
    }

    return UserValidation.valid.value;
  }

  String validatePassword(String pword) {
    if(pword.isEmpty) {
      return PasswordValidation.empty.value;
    }

    if(pword.length > 50) {
      return PasswordValidation.long.value;
    }

    if(pword != "pass123") {
      return PasswordValidation.invalid.value;
    }

    return PasswordValidation.valid.value;
  }

  String login(String username, String password) {
    String uResponse = validateUsername(username);
    String pResponse = validatePassword(password);

    if(uResponse != UserValidation.valid.value) {
      return uResponse;
    }

    if(pResponse != PasswordValidation.valid.value) {
      return pResponse;
    }

    return "Login Successful!";
  }
}