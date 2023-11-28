
import 'package:registration_client/platform_android/network_service_impl.dart';

abstract class NetworkService {
  Future<String> checkInternetConnection();

  Future<String> getVersionNoApp();

  Future<String> saveVersionToGlobalParam(String id, String version);

  Future<String> getVersionFromGobalParam(String id);

  Future<String> saveScreenHeaderToGlobalParam(String id, String value);

  factory NetworkService() => getNetworkServiceImpl();
}
