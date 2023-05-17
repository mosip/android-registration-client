enum UserValidation {
  empty,
  long,
  invalid,
  valid,
}

extension UserValidationExtension on UserValidation {
  String get value {
    switch(this) {
      case UserValidation.empty:
        return "Username is required!";

      case UserValidation.long:
        return "Username cannot be more than 50 characters!";

      case UserValidation.invalid:
        return "User not found!";

      case UserValidation.valid:
        return "User Validated!";

      default:
        return "";
    }
  }
}

enum PasswordValidation {
  empty,
  long,
  invalid,
  valid,
}

extension PasswordValidationExtension on PasswordValidation {
  String get value {
    switch(this) {
      case PasswordValidation.empty:
        return "Password is required!";

      case PasswordValidation.long:
        return "Password cannot be more than 50 characters!";

      case PasswordValidation.invalid:
        return "Password incorrect!";

      case PasswordValidation.valid:
        return "Password Validated!";

      default:
        return "";
    }
  }
}
