/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/


import 'package:registration_client/platform_android/network_service_impl.dart';

abstract class NetworkService {
  Future<String> checkInternetConnection();

  Future<String> getVersionNoApp();

  Future<String> saveVersionToGlobalParam(String id, String version);

  Future<String> getVersionFromGobalParam(String id);

  Future<String> saveScreenHeaderToGlobalParam(String id, String value);

  factory NetworkService() => getNetworkServiceImpl();
}
