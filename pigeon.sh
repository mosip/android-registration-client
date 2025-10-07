# Create pigeon folder inside the lib folder
# Create model package/folder inside the android/app/src/main/java/io/mosip/registration_client

# Get dependencies
flutter pub get

# Run Build Runner
dart run build_runner build --delete-conflicting-outputs

mkdir -p ./android/app/src/main/java/io/mosip/registration_client/model
mkdir -p lib/pigeon

# Generate user pigeon files
dart run pigeon --input pigeon/user.dart --dart_out lib/pigeon/user_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/UserPigeon.java --java_package "io.mosip.registration_client.model"

# Generate machine pigeon files
dart run pigeon --input pigeon/machine.dart --dart_out lib/pigeon/machine_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/MachinePigeon.java --java_package "io.mosip.registration_client.model"

# Generate auth_response pigeon files
dart run pigeon --input pigeon/auth_response.dart --dart_out lib/pigeon/auth_response_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/AuthResponsePigeon.java --java_package "io.mosip.registration_client.model"

# Generate common_api pigeon files
dart run pigeon --input pigeon/common_details.dart --dart_out lib/pigeon/common_details_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/CommonDetailsPigeon.java --java_package "io.mosip.registration_client.model"

# Generate process_spec pigeon files
dart run pigeon --input pigeon/process_spec.dart --dart_out lib/pigeon/process_spec_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/ProcessSpecPigeon.java --java_package "io.mosip.registration_client.model"

# Generate biometrics pigeon files
dart run pigeon --input pigeon/biometrics.dart --dart_out lib/pigeon/biometrics_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/BiometricsPigeon.java --java_package "io.mosip.registration_client.model"

# Generate registration_data pigeon files
dart run pigeon --input pigeon/registration_data.dart --dart_out lib/pigeon/registration_data_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/RegistrationDataPigeon.java --java_package "io.mosip.registration_client.model"

# Generate packet_auth_response pigeon files
dart run pigeon --input pigeon/packet_auth.dart --dart_out lib/pigeon/packet_auth_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/PacketAuthPigeon.java --java_package "io.mosip.registration_client.model"

# Generate demographics pigeon files
dart run pigeon --input pigeon/demographics_data.dart --dart_out lib/pigeon/demographics_data_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/DemographicsDataPigeon.java --java_package "io.mosip.registration_client.model"

# Generate document pigeon files
dart run pigeon --input pigeon/document.dart --dart_out lib/pigeon/document_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/DocumentDataPigeon.java --java_package "io.mosip.registration_client.model"

# Generate dynamic_response pigeon files
dart run pigeon --input pigeon/dynamic_response.dart --dart_out lib/pigeon/dynamic_response_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/DynamicResponsePigeon.java --java_package "io.mosip.registration_client.model"

# Generate master data sync files
dart run pigeon --input pigeon/master_data_sync.dart --dart_out lib/pigeon/master_data_sync_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/MasterDataSyncPigeon.java --java_package "io.mosip.registration_client.model"

# Generate audit event files
dart run pigeon --input pigeon/audit_response.dart --dart_out lib/pigeon/audit_response_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/AuditResponsePigeon.java --java_package "io.mosip.registration_client.model"

# Generate transliteration files
dart run pigeon --input pigeon/transliteration.dart --dart_out lib/pigeon/transliteration_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/TransliterationPigeon.java --java_package "io.mosip.registration_client.model"

# Generate document category files
dart run pigeon --input pigeon/document_category.dart --dart_out lib/pigeon/document_category_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/DocumentCategoryPigeon.java --java_package "io.mosip.registration_client.model"

# Generate dash board files
dart run pigeon --input pigeon/dash_board.dart --dart_out lib/pigeon/dash_board_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/DashBoardPigeon.java --java_package "io.mosip.registration_client.model"

# Generate global config settings files
dart run pigeon --input pigeon/global_config_settings.dart --dart_out lib/pigeon/global_config_settings_pigeon.dart --objc_header_out ios/Runner/pigeon.h --objc_source_out ios/Runner/pigeon.m --java_out ./android/app/src/main/java/io/mosip/registration_client/model/GlobalConfigSettingsPigeon.java --java_package "io.mosip.registration_client.model"