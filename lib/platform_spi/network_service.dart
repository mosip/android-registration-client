
import 'package:registration_client/platform_android/network_service_impl.dart';

abstract class NetworkService {
  Future<String> checkInternetConnection();

  factory NetworkService() => getNetworkServiceImpl();
}
