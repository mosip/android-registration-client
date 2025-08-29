/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/pigeon/auth_response_pigeon.dart';
import 'package:registration_client/pigeon/packet_auth_pigeon.dart';

import 'package:registration_client/pigeon/user_pigeon.dart';
import 'package:registration_client/platform_android/auth_service_impl.dart';

abstract class AuthService {
  Future<User> validateUser(String username, String langCode);

  Future<AuthResponse> login(
      String username, String password, bool isConnected);

  Future<PacketAuth> packetAuthentication(String username, String password);

  Future<String> logout();

  Future<String> stopAlarmService();

  Future<String> forgotPasswordUrl();

  Future<String> getIdleTime();

  Future<String> getAutoLogoutPopupTimeout();

  uture<List<String?>> getRolesByUserId(String userId);

  factory AuthService() => getAuthServiceImpl();
}
