/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:registration_client/platform_android/audit_service_impl.dart';

abstract class Audit {
  Future<void> performAudit(String id, String componentId);

  factory Audit() => getAuditImpl();
}
