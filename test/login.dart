import 'validator.dart';

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

  String isOnboarded(Map<String,dynamic> mockData){
    if(mockData["isOnboarded"]==false){
      return "User not Onboarded";
    }else{
      return "User Onboarded";
    }
  }
  String userRoleAuthorised(List<dynamic> mockData){
    if(mockData.contains("default_role")||mockData.contains("administrator")){
      return "Authorised";
    }else{
      return "Unauthorised";
    }
  }

  bool isPasswordPresent(Map<String, dynamic> passwordData, String userId) {
    if(passwordData.isEmpty) {
      return false;
    }

    if(passwordData["userId"] != userId) {
      return false;
    }

    return true;
  }

  bool isPasswordValid(Map<String, dynamic> passwordData, String userId, String password) {
    if(!isPasswordPresent(passwordData, userId)) {
      return false;
    }

    if(passwordData["password"] != password) {
      return false;
    }

    return true;
  }

  String offlineLogin(Map<String, dynamic> passwordData, String userId, String password) {
    if(!isPasswordPresent(passwordData, userId)) {
      return "Credentials not found. Try online login.";
    }

    if(!isPasswordValid(passwordData, userId, password)) {
      return "Password Incorrect";
    }

    return "Offline Login Successful!";
  }
}