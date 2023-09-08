import 'dart:developer';
import 'dart:typed_data';

import 'package:flutter/widgets.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/pigeon/common_details_pigeon.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/platform_spi/dynamic_response_service.dart';

import 'package:registration_client/platform_spi/machine_key.dart';
import 'package:registration_client/platform_spi/packet_service.dart';
import 'package:registration_client/platform_spi/process_spec.dart';
import 'package:shared_preferences/shared_preferences.dart';

class GlobalProvider with ChangeNotifier {
  final MachineKey machineKey = MachineKey();
  final ProcessSpec processSpec = ProcessSpec();
  final PacketService packetService = PacketService();
  final DynamicResponseService dynamicResponseService =
      DynamicResponseService();

  //Variables
  int _currentIndex = 0;
  String _name = "";
  String _centerId = "";
  String _centerName = "";
  String _machineName = "";
  final formKey = GlobalKey<FormState>();

  Process? _currentProcess;
  Map<String?, String?> _machineDetails = {};

  int _newProcessTabIndex = 0;
  int _htmlBoxTabIndex = 0;

  List<String> _chosenLang = [];
  Map<String, bool> _languageMap = {
    'English': true,
    'Arabic': false,
    'French': false,
  };
  Map<String, String> _thresholdValuesMap = {
    'mosip.registration.leftslap_fingerprint_threshold': '0',
    'mosip.registration.rightslap_fingerprint_threshold': '0',
    'mosip.registration.thumbs_fingerprint_threshold': '0',
    'mosip.registration.iris_threshold': '0',
    'mosip.registration.face_threshold': '0',
  };
  Map<String, dynamic> _fieldDisplayValues = {};

  Map<String, dynamic> _fieldInputValue = {};

  Map<String, bool> _mvelValues = {};

  Map<int, String> _hierarchyValues = {};

  Map<String, List<Uint8List?>> _scannedPages = {};

  String _regId = "";
  String _ageGroup = "";

  List<String?> _locationHierarchy = [null, null, null, null, null];

  //GettersSetters
  setScannedPages(String field, List<Uint8List?> value) {
    _scannedPages[field] = value;
    notifyListeners();
  }

  void clearScannedPages() {
    _scannedPages.clear();
    notifyListeners();
  }

  Map<String, List<Uint8List?>> get scannedPages {
    return _scannedPages;
  }

  String get ageGroup => _ageGroup;
  List<String?> get locationHierarchy => _locationHierarchy;

  set scannedPages(Map<String, List<Uint8List?>> value) {
    _scannedPages = value;
    notifyListeners();
  }

  set locationHierarchy(List<String?> value) {
    _locationHierarchy = value;
    notifyListeners();
  }

  set ageGroup(String value) {
    _ageGroup = value;
    notifyListeners();
  }

  void setLocationHierarchy(String? value, int index) {
    _locationHierarchy[index] = value;
    for (int i = index + 1; i < 5; i++) {
      _locationHierarchy[i] = null;
    }
    notifyListeners();
  }

  int get currentIndex => _currentIndex;
  String get name => _name;
  String get centerId => _centerId;
  String get centerName => _centerName;
  String get machineName => _machineName;
  Map<String?, String?> get machineDetails => _machineDetails;
  String get regId => _regId;

  Map<String, bool> get mvelValues => _mvelValues;
  Map<int, String> get hierarchyValues => _hierarchyValues;

  setRegId(String value) {
    _regId = value;
    notifyListeners();
  }

  setMvelValues(String field, bool value) {
    _mvelValues[field] = value;
    notifyListeners();
  }

  setHierarchyValues(int hierarchyLevel, String value) {
    _hierarchyValues[hierarchyLevel] = value;
    notifyListeners();
  }

  removeKeysFromHierarchy(int hierarchyLevel) {
    hierarchyValues.removeWhere((key, value) => key > hierarchyLevel);
    notifyListeners();
  }

  Process? get currentProcess => _currentProcess;

  Map<String, bool> get languageMap => _languageMap;
  Map<String, String> get thresholdValuesMap => _thresholdValuesMap;
  List<String> get chosenLang => _chosenLang;

  set chosenLang(List<String> value) {
    _chosenLang = value;
    notifyListeners();
  }

  set languageMap(Map<String, bool> value) {
    _languageMap = value;
    notifyListeners();
  }

  set thresholdValuesMap(Map<String, String> value) {
    _thresholdValuesMap = value;
    notifyListeners();
  }

  set currentProcess(Process? value) {
    _currentProcess = value;
    notifyListeners();
  }

  int get newProcessTabIndex => _newProcessTabIndex;

  set newProcessTabIndex(int value) {
    _newProcessTabIndex = value;
    notifyListeners();
  }

  int get htmlBoxTabIndex => _htmlBoxTabIndex;

  set htmlBoxTabIndex(int value) {
    _htmlBoxTabIndex = value;
    notifyListeners();
  }

  Map<String, dynamic> get fieldDisplayValues => _fieldDisplayValues;

  set fieldDisplayValues(Map<String, dynamic> value) {
    _fieldDisplayValues = value;
    notifyListeners();
  }

  Map<String, dynamic> get fieldInputValue => _fieldInputValue;

  set fieldInputValue(Map<String, dynamic> value) {
    _fieldInputValue = value;
    notifyListeners();
  }

  set mvelValues(Map<String, bool> value) {
    _mvelValues = value;
    notifyListeners();
  }

  set hierarchyValues(Map<int, String> value) {
    _hierarchyValues = value;
    notifyListeners();
  }

  //Functions

  setCurrentIndex(int value) {
    _currentIndex = value;
    notifyListeners();
  }

  setName(String value) {
    _name = value;
    notifyListeners();
  }

  setCenterId(String value) {
    _centerId = value;
    notifyListeners();
  }

  setCenterName(String value) {
    _centerName = value;
    notifyListeners();
  }

  setMachineName(String value) {
    _machineName = value;

    notifyListeners();
  }

  setMachineDetails() async {
    final machine = await machineKey.getMachineKeys();

    if (machine.errorCode != null) {
      _machineDetails.addAll({});
    } else {
      _machineDetails = machine.map;
      _machineName = _machineDetails["name"]!;
    }

    notifyListeners();
  }

  addRemoveLang(String key, bool value) {
    for (int i = 0; i < languageMap.length; i++) {
      if (languageMap.entries.elementAt(i).key == key) {
        languageMap[key] = value;

        if (value == true) {
          chosenLang.add(key);
        } else {
          for (var e in chosenLang) {
            if (e == key) {
              chosenLang.remove(e);
              break;
            }
          }
        }

        notifyListeners();
        break;
      }
    }
  }

  setInputMapValue(String key, dynamic value, Map<String, dynamic> commonMap) {
    commonMap[key] = value;
    notifyListeners();
  }

  setLanguageSpecificValue(String key, dynamic value, String language,
      Map<String, dynamic> commonMap) {
    if (!commonMap.containsKey(key)) {
      commonMap[key] = <String, dynamic>{language: value};
    } else {
      commonMap[key][language] = value;
    }

    notifyListeners();
  }

  removeFieldFromMap(String key, Map<String, dynamic> commonMap) {
    commonMap.remove(key);
    notifyListeners();
  }

  clearMapValue(Map<String, dynamic> commonMap) {
    commonMap = {};
    notifyListeners();
  }

  getThresholdValues() async {
    for (var e in thresholdValuesMap.keys) {
      thresholdValuesMap[e] = await BiometricsApi().getThresholdValue(e);
    }
  }

  chooseLanguage(Map<String, String> label) {
    String x = '';
    for (var i in chosenLang) {
      for (var element in languageDataList) {
        if (i == element!.name) {
          x = "$x${label[element.code]!}/";
          continue;
        }
      }
    }
    x = x.substring(0, x.length - 1);
    return x;
  }

  langToCode(String lang) {
    String code = "";
    for (var element in languageDataList) {
      if (lang == element!.name) {
        code = element.code;
        continue;
      }
    }
    return code;
  }

  _getDynamicFieldValues(Field field) async {
    if (field.fieldType == "dynamic") {
      fieldDisplayValues[field.id!] =
          await CommonDetailsApi().getFieldValues(field.id!, "eng");
    }
    if (field.templateName != null) {
      List values = List.empty(growable: true);
      for (var lang in chosenLang) {
        String templateContent = await CommonDetailsApi().getTemplateContent(
            field.templateName!,
            langToCode(lang),
          );
        values.add(templateContent);
      }
      fieldDisplayValues[field.id!] = values;
    }
  }

  fieldValues(Process process) async {
    for (var screen in process.screens!) {
      for (var field in screen!.fields!) {
        // if (field!.fieldType == "dynamic") {
        //   fieldDisplayValues[field.id!] =
        //       await CommonDetailsApi().getFieldValues(field.id!, "eng");
        // }
        // if (field.templateName != null) {
        //   List values = List.empty(growable: true);
        //   chosenLang.forEach((lang) async {
        //     values.add(
        //       await CommonDetailsApi().getTemplateContent(
        //         field.templateName!,
        //         langToCode(lang),
        //       ),
        //     );
        //   });
        //   fieldDisplayValues[field.id!] = values;
        // }
        await _getDynamicFieldValues(field!);
      }
    }
  }

  getRegCenterName(String regCenterId, String langCode) async {
    String regCenterName =
        await machineKey.getCenterName(regCenterId, langCode);

    _centerName = regCenterName;
    notifyListeners();
  }

  syncPacket(String packetId) async {
    await packetService.packetSync(packetId);
    log("provider sync packet Success");
  }

  uploadPacket(String packetId) async {
    await packetService.packetUpload(packetId);
    log("provider upload packet Success");
  }

  clearMap() {
    _fieldInputValue = {};
    _fieldInputValue = {};
    _fieldInputValue = {};
    _fieldDisplayValues = {};
    log(_fieldInputValue.toString());
    notifyListeners();
  }

  // Language Config
  fetchAllLanguages() async {
    return await dynamicResponseService.fetchAllLanguages();
  }

  List<LanguageData?> _languageDataList = [];
  Map<String, String> _languageCodeMapper = {};
  List<String> _languages = [];
  List<String?> _mandatoryLanguages = [];
  List<String?> _optionalLanguages = [];
  int _minLanguageCount = 0;
  int _maxLanguageCount = 0;
  Map<String, bool> _mandatoryLanguageMap = {};

  List<LanguageData?> get languageDataList => _languageDataList;
  Map<String, String> get languageCodeMapper => _languageCodeMapper;
  List<String> get languages => _languages;
  List<String?> get mandatoryLanguages => _mandatoryLanguages;
  List<String?> get optionalLanguages => _optionalLanguages;
  int get minLanguageCount => _minLanguageCount;
  int get maxLanguageCount => _maxLanguageCount;
  Map<String, bool> get mandatoryLanguageMap => _mandatoryLanguageMap;

  initializeLanguageDataList() async {
    _languageDataList = await dynamicResponseService.fetchAllLanguages();
    createLanguageCodeMapper();
    notifyListeners();
  }

  setLanguageDataList(List<LanguageData?> value) {
    _languageDataList = value;
    notifyListeners();
  }

  setLanguageCodeMapper(Map<String, String> value) {
    _languageCodeMapper = value;
    notifyListeners();
  }

  setLanguages(List<String> value) {
    _languages = value;
    notifyListeners();
  }

  setMandatoryLanguages(List<String?> value) {
    _mandatoryLanguages = value;
    notifyListeners();
  }

  setOptionalLanguages(List<String?> value) {
    _optionalLanguages = value;
    notifyListeners();
  }

  setMinLanguageCount(int value) {
    _minLanguageCount = value;
    notifyListeners();
  }

  setMaxLanguageCount(int value) {
    _maxLanguageCount = value;
    notifyListeners();
  }

  setMandatoryLanguageMap(Map<String, bool> value) {
    _mandatoryLanguageMap = value;
    notifyListeners();
  }

  setLanguageConfigData() async {
    _mandatoryLanguages = await processSpec.getMandatoryLanguageCodes();
    _optionalLanguages = await processSpec.getOptionalLanguageCodes();
    _minLanguageCount = await processSpec.getMinLanguageCount();
    _maxLanguageCount = await processSpec.getMaxLanguageCount();
    notifyListeners();
  }

  createRegistrationLanguageMap() {
    _chosenLang = [];
    Map<String, bool> languageDataMap = {};
    Map<String, bool> mandatoryMap = {};
    for (var element in _languageDataList) {
      languageDataMap[element!.name] = false;
    }
    for (var element in _mandatoryLanguages) {
      String lang = _languageCodeMapper[element]!;
      languageDataMap[lang] = true;
      mandatoryMap[lang] = true;
      _chosenLang.add(lang);
    }
    _languageMap = languageDataMap;
    _mandatoryLanguageMap = mandatoryMap;
    notifyListeners();
  }

  createLanguageCodeMapper() {
    _languages = [];
    for (var element in _languageDataList) {
      _languageCodeMapper[element!.code] = element.name;
      languages.add(element.code);
    }
  }

  // App Language
  Locale? _appLocale = const Locale("en");

  Locale get appLocal => _appLocale ?? const Locale("en");

  String _selectedLanguage = "";
  String get selectedLanguage => _selectedLanguage;
  set selectedLanguage(String value) {
    _selectedLanguage = value;
    notifyListeners();
  }

  fetchLocale() async {
    var prefs = await SharedPreferences.getInstance();
    if (prefs.getString('language_code') == null) {
      _appLocale = const Locale('en');
      return null;
    }
    _appLocale = Locale(prefs.getString('language_code')!);
    return null;
  }

  void changeLanguage(Locale code) async {
    var prefs = await SharedPreferences.getInstance();

    if (_appLocale == code) {
      return;
    }

    if (code == const Locale("eng")) {
      _appLocale = const Locale("en");
      await prefs.setString('language_code', 'en');
      await prefs.setString('countryCode', '');
    } else if (code == const Locale("ara")) {
      _appLocale = const Locale("ar");
      await prefs.setString('language_code', 'ar');
      await prefs.setString('countryCode', '');
    } else if (code == const Locale("fra")) {
      _appLocale = const Locale("fr");
      await prefs.setString('language_code', 'fr');
      await prefs.setString('countryCode', '');
    } else {
      _appLocale = const Locale("en");
      await prefs.setString('language_code', 'en');
      await prefs.setString('countryCode', '');
    }
    notifyListeners();
  }

  toggleLocale(String code) async {
    if (_selectedLanguage == code) {
      return;
    }
    _selectedLanguage = code;
    if (code == "kan") {
      _appLocale = const Locale("kn");
    } else {
      String localeCode = code.substring(0, 2);
      _appLocale = Locale(localeCode);
    }
    notifyListeners();
  }
}
