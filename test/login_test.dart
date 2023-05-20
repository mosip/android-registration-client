import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:registration_client/const/validator.dart';
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
    expect(loginTest.validateUsername(loginTest.username), UserValidation.invalid.value);
  });

  test('validate long user', () {
    loginTest.username = "fhfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    expect(loginTest.validateUsername(loginTest.username), UserValidation.long.value);
  });

  test('validate valid user', () {
    loginTest.username = "abcdef";
    expect(loginTest.validateUsername(loginTest.username), UserValidation.valid.value);
  });

  test('validate empty password', () {
    expect(loginTest.validatePassword(''), PasswordValidation.empty.value);
  });

  test('validate invalid password', () {
    loginTest.password = "Piyush";
    expect(loginTest.validatePassword(loginTest.password), PasswordValidation.invalid.value);
  });

  test('validate long password', () {
    loginTest.password = "fhfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    expect(loginTest.validatePassword(loginTest.password), PasswordValidation.long.value);
  });

  test('validate valid password', () {
    loginTest.password = "pass123";
    expect(loginTest.validatePassword(loginTest.password), PasswordValidation.valid.value);
  });
  
  test("Login", () {
    loginTest.username = '';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password), UserValidation.empty.value);

    loginTest.username = 'dghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password), UserValidation.long.value);

    loginTest.username = 'example';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password), UserValidation.invalid.value);

    loginTest.username = 'abcdef';
    loginTest.password = '';
    expect(loginTest.login(loginTest.username, loginTest.password), PasswordValidation.empty.value);

    loginTest.username = 'abcdef';
    loginTest.password = 'dghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh';
    expect(loginTest.login(loginTest.username, loginTest.password), PasswordValidation.long.value);

    loginTest.username = 'abcdef';
    loginTest.password = 'password';
    expect(loginTest.login(loginTest.username, loginTest.password), PasswordValidation.invalid.value);

    loginTest.username = 'abcdef';
    loginTest.password = 'pass123';
    expect(loginTest.login(loginTest.username, loginTest.password), 'Login Successful!');
  });

}