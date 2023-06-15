import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'validator.dart';
import 'login.dart';

import 'login_test.mocks.dart';

@GenerateMocks([Login])
void main() {
  var loginTest = Login();

  test('validate empty user', () {
    expect(loginTest.validateUsername(''), UserValidation.empty.value);
  });

  test('validate invalid user', () {
    loginTest.username = "Piyush";
    expect(loginTest.validateUsername(loginTest.username),
        UserValidation.invalid.value);
  });

  test('validate long user', () {
    loginTest.username =
        "fhfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    expect(loginTest.validateUsername(loginTest.username),
        UserValidation.long.value);
  });

  test('validate valid user', () {
    loginTest.username = "abcdef";
    expect(loginTest.validateUsername(loginTest.username),
        UserValidation.valid.value);
  });

  test('validate empty password', () {
    expect(loginTest.validatePassword(''), PasswordValidation.empty.value);
  });

  test('validate invalid password', () {
    loginTest.password = "Piyush";
    expect(loginTest.validatePassword(loginTest.password),
        PasswordValidation.invalid.value);
  });

  test('validate long password', () {
    loginTest.password =
        "fhfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    expect(loginTest.validatePassword(loginTest.password),
        PasswordValidation.long.value);
  });

  test('validate valid password', () {
    loginTest.password = "pass123";
    expect(loginTest.validatePassword(loginTest.password),
        PasswordValidation.valid.value);
  });

  test("Login", () {
    loginTest.username = '';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password),
        UserValidation.empty.value);

    loginTest.username =
        'dghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password),
        UserValidation.long.value);

    loginTest.username = 'example';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password),
        UserValidation.invalid.value);

    loginTest.username = 'abcdef';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password),
        PasswordValidation.empty.value);

    loginTest.username = 'abcdef';
    loginTest.password =
        'dghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh';
    expect(loginTest.login(loginTest.username, loginTest.password),
        PasswordValidation.long.value);

    loginTest.username = 'abcdef';
    loginTest.password = 'password';
    expect(loginTest.login(loginTest.username, loginTest.password),
        PasswordValidation.invalid.value);

    loginTest.username = 'abcdef';
    loginTest.password = 'pass123';
    expect(loginTest.login(loginTest.username, loginTest.password),
        'Login Successful!');
  });
  test("check user onboarded", () {
    Map<String, dynamic> userData = {"isOnboarded": true};
    expect(loginTest.isOnboarded(userData), "User Onboarded");
  });
  test("check user onboarded", () {
    Map<String, dynamic> userData = {"isOnboarded": false};
    expect(loginTest.isOnboarded(userData), "User not Onboarded");
  });
  test("check user authorisation", () {
    List<dynamic> rolesList = ["default_role", "supervisor"];
    expect(loginTest.userRoleAuthorised(rolesList), "Authorised");
  });
  test("check user authorisation", () {
    List<dynamic> rolesList = ["supervisor"];
    expect(loginTest.userRoleAuthorised(rolesList), "Unauthorised");
  });

  test("check password hash not present", () {
    Map<String, dynamic> passwordData = {};
    expect(loginTest.isPasswordPresent(passwordData, "tester1"), false);
  });

  test("check current user password hash not present", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester2",
      "password": "secret",
    };
    expect(loginTest.isPasswordPresent(passwordData, "tester1"), false);
  });

  test("check current user password hash present", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester1",
      "password": "secret",
    };
    expect(loginTest.isPasswordPresent(passwordData, "tester1"), true);
  });

  test("check password hash not valid for empty table", () {
    Map<String, dynamic> passwordData = {};
    expect(loginTest.isPasswordValid(passwordData, "tester1", "secret"), false);
  });

  test("check password hash not valid for current user", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester2",
      "password": "secret",
    };
    expect(loginTest.isPasswordValid(passwordData, "tester1", "secret"), false);
  });

  test("check password hash not valid for incorrect user password", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester1",
      "password": "secret",
    };
    expect(loginTest.isPasswordValid(passwordData, "tester1", "secret123"), false);
  });

  test("check password hash valid for current user", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester1",
      "password": "secret",
    };
    expect(loginTest.isPasswordValid(passwordData, "tester1", "secret"), true);
  });

  test("check offline login failed for empty table", () {
    Map<String, dynamic> passwordData = {};
    expect(loginTest.offlineLogin(passwordData, "tester1", "secret"), "Credentials not found. Try online login.");
  });

  test("check offline login failed for current user", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester2",
      "password": "secret",
    };
    expect(loginTest.offlineLogin(passwordData, "tester1", "secret"), "Credentials not found. Try online login.");
  });

  test("check offline login failed for incorrect user password", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester1",
      "password": "secret",
    };
    expect(loginTest.offlineLogin(passwordData, "tester1", "secret123"), "Password Incorrect");
  });

  test("check offline login successful for current user", () {
    Map<String, dynamic> passwordData = {
      "userId": "tester1",
      "password": "secret",
    };
    expect(loginTest.offlineLogin(passwordData, "tester1", "secret"), "Offline Login Successful!");
  });
}
