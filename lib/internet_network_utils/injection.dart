import 'package:get_it/get_it.dart';
import 'package:internet_connection_checker/internet_connection_checker.dart';
import 'package:registration_client/internet_network_utils/internet_network_service.dart';


final GetIt getIt = GetIt.instance;

final InternetConnectionChecker internetConnectionChecker = InternetConnectionChecker();

void setupNetworkDependencies() {
  getIt.registerSingleton<InternetNetworkService>(InternetNetworkService(internetConnectionChecker));
}