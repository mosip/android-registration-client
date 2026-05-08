import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/utils/sync_job_def.dart';

void main() {
  group('SyncJobDef Constructor Tests', () {
    test('creates SyncJobDef with all values', () {
      final syncJobDef = SyncJobDef(
        id: 'job_001',
        name: 'Master Sync',
        apiName: 'masterSyncJob',
        parentSyncJobId: 'parent_001',
        syncFreq: '30m',
        lockDuration: '5m',
        langCode: 'eng',
        isDeleted: false,
        isActive: true,
      );

      expect(syncJobDef.id, 'job_001');
      expect(syncJobDef.name, 'Master Sync');
      expect(syncJobDef.apiName, 'masterSyncJob');
      expect(syncJobDef.parentSyncJobId, 'parent_001');
      expect(syncJobDef.syncFreq, '30m');
      expect(syncJobDef.lockDuration, '5m');
      expect(syncJobDef.langCode, 'eng');
      expect(syncJobDef.isDeleted, false);
      expect(syncJobDef.isActive, true);
    });

    test('creates SyncJobDef with nullable values', () {
      final syncJobDef = SyncJobDef();

      expect(syncJobDef.id, null);
      expect(syncJobDef.name, null);
      expect(syncJobDef.apiName, null);
      expect(syncJobDef.parentSyncJobId, null);
      expect(syncJobDef.syncFreq, null);
      expect(syncJobDef.lockDuration, null);
      expect(syncJobDef.langCode, null);
      expect(syncJobDef.isDeleted, null);
      expect(syncJobDef.isActive, null);
    });
  });

  group('fromJson Tests', () {
    test('fromJson parses complete json correctly', () {
      final json = {
        'id': 'job_001',
        'name': 'Master Sync',
        'apiName': 'masterSyncJob',
        'parentSyncJobId': 'parent_001',
        'syncFreq': '30m',
        'lockDuration': '5m',
        'langCode': 'eng',
        'isDeleted': false,
        'isActive': true,
      };

      final syncJobDef = SyncJobDef.fromJson(json);

      expect(syncJobDef.id, 'job_001');
      expect(syncJobDef.name, 'Master Sync');
      expect(syncJobDef.apiName, 'masterSyncJob');
      expect(syncJobDef.parentSyncJobId, 'parent_001');
      expect(syncJobDef.syncFreq, '30m');
      expect(syncJobDef.lockDuration, '5m');
      expect(syncJobDef.langCode, 'eng');
      expect(syncJobDef.isDeleted, false);
      expect(syncJobDef.isActive, true);
    });

    test('fromJson handles null values correctly', () {
      final json = {
        'id': null,
        'name': null,
        'apiName': null,
        'parentSyncJobId': null,
        'syncFreq': null,
        'lockDuration': null,
        'langCode': null,
        'isDeleted': null,
        'isActive': null,
      };

      final syncJobDef = SyncJobDef.fromJson(json);

      expect(syncJobDef.id, null);
      expect(syncJobDef.name, null);
      expect(syncJobDef.apiName, null);
      expect(syncJobDef.parentSyncJobId, null);
      expect(syncJobDef.syncFreq, null);
      expect(syncJobDef.lockDuration, null);
      expect(syncJobDef.langCode, null);
      expect(syncJobDef.isDeleted, null);
      expect(syncJobDef.isActive, null);
    });

    test('fromJson handles empty json correctly', () {
      final syncJobDef = SyncJobDef.fromJson({});

      expect(syncJobDef.id, null);
      expect(syncJobDef.name, null);
      expect(syncJobDef.apiName, null);
      expect(syncJobDef.parentSyncJobId, null);
      expect(syncJobDef.syncFreq, null);
      expect(syncJobDef.lockDuration, null);
      expect(syncJobDef.langCode, null);
      expect(syncJobDef.isDeleted, null);
      expect(syncJobDef.isActive, null);
    });

    test('fromJson ignores extra json fields', () {
      final json = {
        'id': 'job_002',
        'name': 'Global Params Sync',
        'apiName': 'globalParamsSyncJob',
        'extraField': 'extra_value',
        'anotherExtraField': 12345,
      };

      final syncJobDef = SyncJobDef.fromJson(json);

      expect(syncJobDef.id, 'job_002');
      expect(syncJobDef.name, 'Global Params Sync');
      expect(syncJobDef.apiName, 'globalParamsSyncJob');
      expect(syncJobDef.parentSyncJobId, null);
      expect(syncJobDef.syncFreq, null);
      expect(syncJobDef.lockDuration, null);
      expect(syncJobDef.langCode, null);
      expect(syncJobDef.isDeleted, null);
      expect(syncJobDef.isActive, null);
    });
  });

  group('Boolean Field Tests', () {
    test('isDeleted supports true value', () {
      final syncJobDef = SyncJobDef.fromJson({
        'isDeleted': true,
      });

      expect(syncJobDef.isDeleted, true);
    });

    test('isDeleted supports false value', () {
      final syncJobDef = SyncJobDef.fromJson({
        'isDeleted': false,
      });

      expect(syncJobDef.isDeleted, false);
    });

    test('isActive supports true value', () {
      final syncJobDef = SyncJobDef.fromJson({
        'isActive': true,
      });

      expect(syncJobDef.isActive, true);
    });

    test('isActive supports false value', () {
      final syncJobDef = SyncJobDef.fromJson({
        'isActive': false,
      });

      expect(syncJobDef.isActive, false);
    });
  });

  group('Edge Case Tests', () {
    test('supports long string values', () {
      final longString = 'a' * 1000;

      final syncJobDef = SyncJobDef(
        id: longString,
        name: longString,
        apiName: longString,
        parentSyncJobId: longString,
        syncFreq: longString,
        lockDuration: longString,
        langCode: longString,
      );

      expect(syncJobDef.id, longString);
      expect(syncJobDef.name, longString);
      expect(syncJobDef.apiName, longString);
      expect(syncJobDef.parentSyncJobId, longString);
      expect(syncJobDef.syncFreq, longString);
      expect(syncJobDef.lockDuration, longString);
      expect(syncJobDef.langCode, longString);
    });

    test('supports special characters in string values', () {
      const specialString = r'!@#$%^&*()_+-=[]{}|;:",.<>?/`~';

      final syncJobDef = SyncJobDef(
        id: specialString,
        name: specialString,
        apiName: specialString,
      );

      expect(syncJobDef.id, specialString);
      expect(syncJobDef.name, specialString);
      expect(syncJobDef.apiName, specialString);
    });

    test('supports unicode characters', () {
      const unicodeString = 'सिंक-जॉब-测试-وظيفة';

      final syncJobDef = SyncJobDef(
        id: unicodeString,
        name: unicodeString,
      );

      expect(syncJobDef.id, unicodeString);
      expect(syncJobDef.name, unicodeString);
    });

    test('supports empty string values', () {
      final syncJobDef = SyncJobDef(
        id: '',
        name: '',
        apiName: '',
      );

      expect(syncJobDef.id, '');
      expect(syncJobDef.name, '');
      expect(syncJobDef.apiName, '');
    });
  });

  group('Instance Integrity Tests', () {
    test('multiple instances maintain independent values', () {
      final firstSyncJob = SyncJobDef(
        id: 'job_001',
        name: 'Master Sync',
      );

      final secondSyncJob = SyncJobDef(
        id: 'job_002',
        name: 'Policy Sync',
      );

      expect(firstSyncJob.id, 'job_001');
      expect(firstSyncJob.name, 'Master Sync');

      expect(secondSyncJob.id, 'job_002');
      expect(secondSyncJob.name, 'Policy Sync');
    });

    test('fromJson creates separate object instances', () {
      final json = {
        'id': 'job_001',
        'name': 'Master Sync',
      };

      final firstInstance = SyncJobDef.fromJson(json);
      final secondInstance = SyncJobDef.fromJson(json);

      expect(firstInstance, isNot(same(secondInstance)));
      expect(firstInstance.id, secondInstance.id);
      expect(firstInstance.name, secondInstance.name);
    });
  });
}