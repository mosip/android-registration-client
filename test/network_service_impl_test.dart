import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/platform_android/network_service_impl.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  late NetworkServiceImpl networkService;

  const StandardMessageCodec codec = StandardMessageCodec();

  const List<String> saveVersionChannels = <String>[
    'dev.flutter.pigeon.registration_client.CommonDetailsApi.saveVersionToGlobalParam',
    'dev.flutter.pigeon.registration_client.pigeon.CommonDetailsApi.saveVersionToGlobalParam',
  ];

  const List<String> getVersionChannels = <String>[
    'dev.flutter.pigeon.registration_client.CommonDetailsApi.getVersionFromGlobalParam',
    'dev.flutter.pigeon.registration_client.pigeon.CommonDetailsApi.getVersionFromGlobalParam',
  ];

  void mockStringResponse(List<String> channels, String value) {
    for (final channel in channels) {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMessageHandler(channel, (ByteData? message) async {
        return codec.encodeMessage(<Object?>[value]);
      });
    }
  }

  void mockPlatformError(List<String> channels) {
    for (final channel in channels) {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMessageHandler(channel, (ByteData? message) async {
        return codec.encodeMessage(<Object?>[
          'error',
          'Mock platform error',
          null,
        ]);
      });
    }
  }

  void clearMockHandlers() {
    for (final channel in <String>[
      ...saveVersionChannels,
      ...getVersionChannels,
    ]) {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMessageHandler(channel, null);
    }
  }

  setUp(() {
    networkService = NetworkServiceImpl();

    mockStringResponse(saveVersionChannels, 'saved_successfully');
    mockStringResponse(getVersionChannels, '1.0.0');
  });

  tearDown(() {
    clearMockHandlers();
  });

  group('Factory Tests', () {
    test('getNetworkServiceImpl returns NetworkServiceImpl instance', () {
      final service = getNetworkServiceImpl();

      expect(service, isA<NetworkServiceImpl>());
    });

    test('factory creates separate instances', () {
      final first = getNetworkServiceImpl();
      final second = getNetworkServiceImpl();

      expect(first, isNot(same(second)));
    });
  });

  group('Service Initialization Tests', () {
    test('NetworkServiceImpl initializes correctly', () {
      expect(networkService, isNotNull);
    });

    test('multiple instances remain independent', () {
      final first = NetworkServiceImpl();
      final second = NetworkServiceImpl();

      expect(first, isNot(same(second)));
    });
  });

  group('checkInternetConnection Tests', () {
    test('checkInternetConnection method exists', () {
      expect(networkService.checkInternetConnection, isA<Function>());
    });
  });

  group('getVersionNoApp Tests', () {
    test('getVersionNoApp method exists', () {
      expect(networkService.getVersionNoApp, isA<Function>());
    });
  });

  group('saveVersionToGlobalParam Tests', () {
    test('saveVersionToGlobalParam returns success response', () async {
      final response = await networkService.saveVersionToGlobalParam(
        'version_id',
        '1.0.0',
      );

      expect(response, 'saved_successfully');
    });

    test('saveVersionToGlobalParam handles empty values', () async {
      final response = await networkService.saveVersionToGlobalParam('', '');

      expect(response, 'saved_successfully');
    });

    test('saveVersionToGlobalParam handles long strings', () async {
      final longString = 'a' * 500;

      final response = await networkService.saveVersionToGlobalParam(
        longString,
        longString,
      );

      expect(response, 'saved_successfully');
    });
  });

  group('getVersionFromGobalParam Tests', () {
    test('getVersionFromGobalParam returns version successfully', () async {
      final response = await networkService.getVersionFromGobalParam(
        'version_id',
      );

      expect(response, '1.0.0');
    });

    test('getVersionFromGobalParam handles empty id', () async {
      final response = await networkService.getVersionFromGobalParam('');

      expect(response, '1.0.0');
    });

    test('getVersionFromGobalParam handles special characters', () async {
      final response = await networkService.getVersionFromGobalParam(
        r'!@#$%^&*()',
      );

      expect(response, '1.0.0');
    });
  });

  group('saveScreenHeaderToGlobalParam Tests', () {
    test('saveScreenHeaderToGlobalParam returns success response', () async {
      final response = await networkService.saveScreenHeaderToGlobalParam(
        'header_id',
        'Registration Client',
      );

      expect(response, 'saved_successfully');
    });

    test('saveScreenHeaderToGlobalParam handles empty values', () async {
      final response = await networkService.saveScreenHeaderToGlobalParam(
        '',
        '',
      );

      expect(response, 'saved_successfully');
    });

    test('saveScreenHeaderToGlobalParam handles unicode values', () async {
      final response = await networkService.saveScreenHeaderToGlobalParam(
        'header',
        '测试-प्रशांत-اختبار',
      );

      expect(response, 'saved_successfully');
    });
  });

  group('Platform Exception Handling Tests', () {
    test('saveVersionToGlobalParam returns empty string on platform error',
        () async {
      mockPlatformError(saveVersionChannels);

      final response = await networkService.saveVersionToGlobalParam(
        'id',
        '1.0.0',
      );

      expect(response, '');
    });

    test('getVersionFromGobalParam returns empty string on platform error',
        () async {
      mockPlatformError(getVersionChannels);

      final response = await networkService.getVersionFromGobalParam('id');

      expect(response, '');
    });

    test('saveScreenHeaderToGlobalParam returns empty string on platform error',
        () async {
      mockPlatformError(saveVersionChannels);

      final response = await networkService.saveScreenHeaderToGlobalParam(
        'header',
        'value',
      );

      expect(response, '');
    });
  });

  group('Edge Case Tests', () {
    test('service handles repeated save calls correctly', () async {
      for (int i = 0; i < 5; i++) {
        final response = await networkService.saveVersionToGlobalParam(
          'id_$i',
          'version_$i',
        );

        expect(response, 'saved_successfully');
      }
    });

    test('service handles special characters correctly', () async {
      final response = await networkService.saveScreenHeaderToGlobalParam(
        r'!@#$%',
        'Header_测试_प्रशांत',
      );

      expect(response, 'saved_successfully');
    });

    test('service handles numeric strings correctly', () async {
      final response = await networkService.saveVersionToGlobalParam(
        '123',
        '456',
      );

      expect(response, 'saved_successfully');
    });
  });


    group('checkInternetConnection Logic Tests', () {
    test('method returns Future<String> type', () {
      expect(
        networkService.checkInternetConnection(),
        isA<Future<String>>(),
      );
    });
  });

  group('getVersionNoApp Logic Tests', () {
    test('method returns Future<String> type', () {
      expect(
        networkService.getVersionNoApp(),
        isA<Future<String>>(),
      );
    });
  });

  group('Stability Tests', () {
    test('service instance remains stable across calls', () async {
      final first = await networkService.saveVersionToGlobalParam(
        'id',
        '1.0.0',
      );

      final second = await networkService.getVersionFromGobalParam('id');

      expect(first, 'saved_successfully');
      expect(second, '1.0.0');
    });

    test('multiple sequential async calls work correctly', () async {
      final responses = <String>[];

      for (int i = 0; i < 3; i++) {
        final response = await networkService.saveVersionToGlobalParam(
          'id_$i',
          'version_$i',
        );

        responses.add(response);
      }

      expect(responses.length, 3);
      expect(
        responses.every((element) => element == 'saved_successfully'),
        true,
      );
    });
  });
}
