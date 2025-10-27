/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

/// Model for SyncJobDef entity from Android
class SyncJobDef {
  final String? id;
  final String? name;
  final String? apiName;
  final String? parentSyncJobId;
  final String? syncFreq;
  final String? lockDuration;
  final String? langCode;
  final bool? isDeleted;
  final bool? isActive;

  SyncJobDef({
    this.id,
    this.name,
    this.apiName,
    this.parentSyncJobId,
    this.syncFreq,
    this.lockDuration,
    this.langCode,
    this.isDeleted,
    this.isActive,
  });

  factory SyncJobDef.fromJson(Map<String, dynamic> json) {
    return SyncJobDef(
      id: json['id'] as String?,
      name: json['name'] as String?,
      apiName: json['apiName'] as String?,
      parentSyncJobId: json['parentSyncJobId'] as String?,
      syncFreq: json['syncFreq'] as String?,
      lockDuration: json['lockDuration'] as String?,
      langCode: json['langCode'] as String?,
      isDeleted: json['isDeleted'] as bool?,
      isActive: json['isActive'] as bool?,
    );
  }
}
