# Create pigeon folder inside the lib folder
# Create model package/folder inside the android/app/src/main/java/io/mosip/registration_client

# Generate user pigeon files
flutter pub run pigeon --input pigeon/user.dart --dart_out lib/pigeon/user_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/UserPigeon.java --java_package "io.mosip.registration_client.model"

# Generate machine pigeon files
flutter pub run pigeon --input pigeon/machine.dart --dart_out lib/pigeon/machine_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/MachinePigeon.java --java_package "io.mosip.registration_client.model"

# Generate auth_response pigeon files
flutter pub run pigeon --input pigeon/auth_response.dart --dart_out lib/pigeon/auth_response_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/AuthResponsePigeon.java --java_package "io.mosip.registration_client.model"

# Generate common_api pigeon files
flutter pub run pigeon --input pigeon/common_details.dart --dart_out lib/pigeon/common_details_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/CommonDetailsPigeon.java --java_package "io.mosip.registration_client.model"

# Generate process_spec pigeon files
flutter pub run pigeon --input pigeon/process_spec.dart --dart_out lib/pigeon/process_spec_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/ProcessSpecPigeon.java --java_package "io.mosip.registration_client.model"

# Generate biometrics pigeon files
flutter pub run pigeon --input pigeon/biometrics.dart --dart_out lib/pigeon/biometrics_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/BiometricsPigeon.java --java_package "io.mosip.registration_client.model"

# Generate location_response pigeon files
flutter pub run pigeon --input pigeon/location_response.dart --dart_out lib/pigeon/location_response_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/LocationResponsePigeon.java --java_package "io.mosip.registration_client.model"

# Generate registration_data pigeon files
flutter pub run pigeon --input pigeon/registration_data.dart --dart_out lib/pigeon/registration_data_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/RegistrationDataPigeon.java --java_package "io.mosip.registration_client.model"

# Generate packet_auth_response pigeon files
flutter pub run pigeon --input pigeon/packet_auth.dart --dart_out lib/pigeon/packet_auth_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/PacketAuthPigeon.java --java_package "io.mosip.registration_client.model"

# Generate demographics pigeon files
flutter pub run pigeon --input pigeon/demographics_data.dart --dart_out lib/pigeon/demographics_data_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/DemographicsDataPigeon.java --java_package "io.mosip.registration_client.model"

