# Generate user pigeon files
flutter pub run pigeon --input pigeon/user.dart --dart_out lib/pigeon/user_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/UserPigeon.java --java_package "io.mosip.registration_client.model"

# Generate machine pigeon files
flutter pub run pigeon --input pigeon/machine.dart --dart_out lib/pigeon/machine_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/MachinePigeon.java --java_package "io.mosip.registration_client.model"

# Generate auth_response pigeon files
flutter pub run pigeon --input pigeon/auth_response.dart --dart_out lib/pigeon/auth_response_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/AuthResponsePigeon.java --java_package "io.mosip.registration_client.model"
