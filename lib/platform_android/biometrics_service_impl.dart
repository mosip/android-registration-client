import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/platform_spi/biometrics_service.dart';

class BiometricsServiceImpl implements BiometricsService {
  @override
  Future<String> addBioException(String fieldId, String modality, String attribute) async {
    String response = '';
    try {
      response = await BiometricsApi().addBioException(fieldId, modality, attribute);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed');
    } catch (e) {
      debugPrint('Add Bio Exception failed: ${e.toString()}');
    }
    return response;
  }

  @override
  Future<bool> conditionalBioAttributeValidation(String fieldId, String expression) async {
    bool response = false;
    try {
      response = await BiometricsApi().conditionalBioAttributeValidation(fieldId, expression);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Conditional Bio Attribute Validation failed: ${e.toString()}');
    }
    return response;
  }

  @override
  Future<List<Uint8List?>> extractImageValues(String fieldId, String modality) async {
    List<Uint8List?> imageValuesList = List.empty();
    try {
      imageValuesList = await BiometricsApi().extractImageValues(fieldId, modality);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Extract Image Values failed: ${e.toString()}');
    }
    return imageValuesList;
  }

  @override
  Future<List<Uint8List?>> extractImageValuesByAttempt(String fieldId, String modality, int attempt) async {
    List<Uint8List?> imageValuesListByAttempt = List.empty();
    try {
      imageValuesListByAttempt = await BiometricsApi().extractImageValuesByAttempt(fieldId, modality, attempt);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Extract Image Values By Attempt failed: ${e.toString()}');
    }
    return imageValuesListByAttempt;
  }

  @override
  Future<String> getAgeGroup() async {
    String response = '';
    try {
      response = await BiometricsApi().getAgeGroup();
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Fetch Age Group failed: ${e.toString()}');
    }
    return response;
  }

  @override
  Future<List<String?>> getBestBiometrics(String fieldId, String modality) async {
    List<String?> bestBiometricsList = List.empty();
    try {
      bestBiometricsList = await BiometricsApi().getBestBiometrics(fieldId, modality);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Fetch Best Biometrics failed: ${e.toString()}');
    }
    return bestBiometricsList;
  }

  @override
  Future<int> getBioAttempt(String fieldId, String modality) async {
    int response = 0;
    try {
      response = await BiometricsApi().getBioAttempt(fieldId, modality);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Fetch Bio Attempt failed: ${e.toString()}');
    }
    return response;
  }

  @override
  Future<List<String?>> getBiometrics(String fieldId, String modality, int attempt) async {
    List<String?> biometricsList = List.empty();
    try {
      biometricsList = await BiometricsApi().getBiometrics(fieldId, modality, attempt);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Fetch Biometrics failed: ${e.toString()}');
    }
    return biometricsList;
  }

  @override
  Future<String> getThresholdValue(String key) async {
    String response = '';
    try {
      response = await BiometricsApi().getThresholdValue(key);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Fetch Threshold Value failed! ${e.toString()}');
    }
    return response;
  }

  @override
  Future<int> incrementBioAttempt(String fieldId, String modality) async {
    int response = 0;
    try {
      response = await BiometricsApi().incrementBioAttempt(fieldId, modality);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Increment Bio Attempt failed: ${e.toString()}');
    }
    return response;
  }

  @override
  Future<String> invokeDiscoverSbi(String fieldId, String modality) async {
    String response = '';
    try {
      response = await BiometricsApi().invokeDiscoverSbi(fieldId, modality);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Invoke Discover Sbi failed: ${e.toString()}');
    }
    return response;
  }

  @override
  Future<String> removeBioException(String fieldId, String modality, String attribute) async {
    String response = '';
    try {
      response = await BiometricsApi().removeBioException(fieldId, modality, attribute);
    } on PlatformException {
      debugPrint('BiomtericsApi call failed!');
    } catch (e) {
      debugPrint('Remove Bio Exception failed: ${e.toString()}');
    }
    return response;
  }

}

BiometricsService getBiometricsServiceImpl() => BiometricsServiceImpl();
