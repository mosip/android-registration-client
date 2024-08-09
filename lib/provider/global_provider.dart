/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/pigeon/biometrics_pigeon.dart';
import 'package:registration_client/pigeon/common_details_pigeon.dart';
import 'package:registration_client/pigeon/dynamic_response_pigeon.dart';
import 'package:registration_client/platform_spi/audit_service.dart';
import 'package:registration_client/platform_spi/dynamic_response_service.dart';

import 'package:registration_client/platform_spi/machine_key_service.dart';
import 'package:registration_client/platform_spi/network_service.dart';
import 'package:registration_client/platform_spi/packet_service.dart';
import 'package:registration_client/platform_spi/process_spec_service.dart';
import 'package:registration_client/utils/constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class GlobalProvider with ChangeNotifier {
  final MachineKeyService machineKeyService = MachineKeyService();
  final ProcessSpecService processSpecService = ProcessSpecService();
  final PacketService packetService = PacketService();
  final DynamicResponseService dynamicResponseService =
      DynamicResponseService();
  final Audit audit = Audit();
  final NetworkService networkService = NetworkService();

  //Variables
  int _currentIndex = 0;
  String _name = "";
  String _centerId = "";
  String _centerName = "";
  String _machineName = "";
  String _preRegId = "";
  final formKey = GlobalKey<FormState>();
  final updateFieldKey = GlobalKey<FormState>();
  String _updateUINNumber = "";
  String _onboardingProcessName = "";
  bool _isPageChanged = false;
  String get updateUINNumber => _updateUINNumber;
  String get onboardingProcessName => _onboardingProcessName;
  bool get isPageChanged => _isPageChanged;
  bool _preRegControllerRefresh = false;
  bool get preRegControllerRefresh => _preRegControllerRefresh;

  set isPageChanged(bool value) {
    _isPageChanged = value;
    notifyListeners();
  }

  set preRegControllerRefresh(bool value) {
    _preRegControllerRefresh = value;
    notifyListeners();
  }

  set onboardingProcessName(String value) {
    _onboardingProcessName = value;
    notifyListeners();
  }

  set updateUINNumber(String value) {
    _updateUINNumber = value;
    notifyListeners();
  }

  Process? _currentProcess;
  Map<String?, String?> _machineDetails = {};

  int _newProcessTabIndex = 0;
  int _htmlBoxTabIndex = 0;

  List<String> _chosenLang = [];
  String _operatorOnboardingAttributes = "";
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
  Map<String, dynamic> _completeException = {};

  Map<String, bool> _mvelVisibleFields = {};
  Map<String, bool> _mvelRequiredFields = {};

  Map<String, List<Uint8List?>> _scannedPages = {};

  String _regId = "";
  String _ageGroup = "";

  String _checkAgeGroupChange = "";
  String get checkAgeGroupChange => _checkAgeGroupChange;
  set checkAgeGroupChange(String value) {
    _checkAgeGroupChange = value;
  }

  //GettersSetters
  setScannedPages(String field, List<Uint8List?> value) {
    _scannedPages[field] = value;
    notifyListeners();
  }

  removeScannedPages(
      String field, Uint8List? value, List<Uint8List?> listOfValue) {
    _scannedPages[field] = listOfValue;
    _scannedPages[field]!.remove(value);
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

  set scannedPages(Map<String, List<Uint8List?>> value) {
    _scannedPages = value;
    notifyListeners();
  }

  set ageGroup(String value) {
    _ageGroup = value;
    notifyListeners();
  }

  void setLocationHierarchy(String group, String? value, int index) {
    _groupedHierarchyValues[group]![index] = value;
    for (int i = index + 1; i < hierarchyReverse.length; i++) {
      _groupedHierarchyValues[group]![i] = null;
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
  String get preRegId => _preRegId;

  String get operatorOnboardingAttributes => _operatorOnboardingAttributes;
  set operatorOnboardingAttributes(String value) {
    _operatorOnboardingAttributes = value;
    notifyListeners();
  }

  Map<String, bool> get mvelVisibleFields => _mvelVisibleFields;
  Map<String, bool> get mvelRequiredFields => _mvelRequiredFields;

  setRegId(String value) {
    _regId = value;
    notifyListeners();
  }

  setMvelVisibleFields(String field, bool value) {
    _mvelVisibleFields[field] = value;
    notifyListeners();
  }

  setMvelRequiredFields(String field, bool value) {
    _mvelRequiredFields[field] = value;
    notifyListeners();
  }

  Process? get currentProcess => _currentProcess;

  Map<String, bool> get languageMap => _languageMap;
  Map<String, String> get thresholdValuesMap => _thresholdValuesMap;
  List<String> get chosenLang => _chosenLang;

  String _versionNoApp = "";
  String get versionNoApp => _versionNoApp;
  set versionNoApp(String value) {
    _versionNoApp = value;
    notifyListeners();
  }

  String _branchNameApp = "";
  String get branchNameApp => _branchNameApp;
  set branchNameApp(String value) {
    _branchNameApp = value;
    notifyListeners();
  }

  String _commitIdApp = "";
  String get commitIdApp => _commitIdApp;
  set commitIdApp(String value) {
    _commitIdApp = value;
    notifyListeners();
  }

  saveVersionToGlobalParam(String id, String version) async {
    await networkService.saveVersionToGlobalParam(id, version);
  }

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

  Map<String, dynamic> get completeException => _completeException;
  set completeException(Map<String, dynamic> value) {
    _completeException = value;
    notifyListeners();
  }

  set mvelVisibleFields(Map<String, bool> value) {
    _mvelVisibleFields = value;
    notifyListeners();
  }

  set mvelRequiredFields(Map<String, bool> value) {
    _mvelRequiredFields = value;
    notifyListeners();
  }

  //Functions
  setCompleteExceptionByKey(String key, dynamic value) {
    completeException[key] = value;
    notifyListeners();
  }

  getCompleteExceptionByKey(String key) {
    if (completeException.containsKey(key)) {
      return completeException[key];
    } else {
      return [];
    }
  }

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

  setPreRegistrationId(String value) {
    _preRegId = value;
    notifyListeners();
  }

  setMachineDetails() async {
    final machine = await machineKeyService.getMachineKeys();

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

  removeInputMapValue(String key, Map<String, dynamic> commonMap) {
    commonMap.remove(key);
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

  getVersionNoApp() async {
    String versionNoAppTemp = await networkService.getVersionNoApp();
    if (versionNoAppTemp == "") {
      versionNoAppTemp = await networkService
          .getVersionFromGobalParam("mosip.registration.server_version");
    }
    versionNoApp = versionNoAppTemp;
  }

  setGitHeadAttributes() async {
    String head = "";
    String branchName = "";
    String commitId = "";
    try {
      head = await rootBundle.loadString('.git/HEAD');
      branchName = head.split('/').last;
      if (head.startsWith('ref: ')) {
        branchName = head.split('ref: refs/heads/').last.trim();
        commitId = await rootBundle.loadString('.git/refs/heads/$branchName');
      } else {
        commitId = head;
      }
    } catch (e) {
      debugPrint("Failed fetching git info: $e");
    }

    branchNameApp = branchName;
    commitIdApp = commitId;
  }

  removeFieldFromMap(String key, Map<String, dynamic> commonMap) {
    commonMap.remove(key);
    notifyListeners();
  }

  removeValidFromMap(
      String key, Uint8List? item, Map<String, dynamic> commonMap) {
    if (commonMap[key].listofImages != null &&
        commonMap[key].listofImages.length >= 1) {
      commonMap[key].listofImages.remove(item);
    } else {
      commonMap.remove(key);
    }
    notifyListeners();
  }

  clearMapValue(Map<String, dynamic> commonMap) {
    commonMap = {};
    notifyListeners();
  }

  getThresholdValues() async {
    for (var e in thresholdValuesMap.keys) {
      thresholdValuesMap[e] = await BiometricsApi().getMapValue(e);
    }
  }

  chooseLanguage(Map<String, String> label) {
    String x = '';
    for (var i in chosenLang) {
      String code = languageToCodeMapper[i]!;
      if (label[code] != null) {
        x = "$x${label[code] ?? ""}/ ";
      }
    }
    return x.isEmpty ? x : x.substring(0, x.length - 2);
  }

  langToCode(String lang) {
    String code = "";
    for (var element in _languages) {
      if (languageToCodeMapper[lang] == element) {
        code = element!;
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
        await _getDynamicFieldValues(field!);
      }
    }
  }

  getRegCenterName(String regCenterId, String langCode) async {
    String regCenterName =
        await machineKeyService.getCenterName(regCenterId, langCode);

    _centerName = regCenterName;
    notifyListeners();
  }

  syncPacket(String packetId) async {
    await packetService.packetSyncAll([packetId]);
    log("provider sync packet Success");
  }

  uploadPacket(String packetId) async {
    await packetService.packetUploadAll([packetId]);
    log("provider upload packet Success");
  }

  clearMap() {
    _fieldInputValue = {};
    log("input value $_fieldInputValue");
    notifyListeners();
  }

  clearExceptions() {
    _exceptionAttributes = [];
    _completeException = {};
    notifyListeners();
  }

  // Language Config
  fetchAllLanguages() async {
    return await dynamicResponseService.fetchAllLanguages();
  }

  List<LanguageData?> _languageDataList = [];
  Map<String, String> _codeToLanguageMapper = {"eng": "English"};
  Map<String, String> _languageToCodeMapper = {"English": "eng"};
  List<String?> _languages = ['eng'];
  List<String?> _mandatoryLanguages = [];
  List<String?> _optionalLanguages = [];
  int _minLanguageCount = 0;
  int _maxLanguageCount = 0;
  Map<String, bool> _mandatoryLanguageMap = {};
  List<DynamicFieldData?> _notificationLanguages = [];
  Map<String, bool> _disabledLanguageMap = {};

  List<LanguageData?> get languageDataList => _languageDataList;
  Map<String, String> get codeToLanguageMapper => _codeToLanguageMapper;
  Map<String, String> get languageToCodeMapper => _languageToCodeMapper;
  List<String?> get languages => _languages;
  List<String?> get mandatoryLanguages => _mandatoryLanguages;
  List<String?> get optionalLanguages => _optionalLanguages;
  int get minLanguageCount => _minLanguageCount;
  int get maxLanguageCount => _maxLanguageCount;
  Map<String, bool> get mandatoryLanguageMap => _mandatoryLanguageMap;
  List<DynamicFieldData?> get notificationLanguages => _notificationLanguages;
  Map<String, bool> get disabledLanguageMap => _disabledLanguageMap;
  List<String> _exceptionAttributes = [];

  List<String> get exceptionAttributes => _exceptionAttributes;

  initializeLanguageDataList(bool isManualSync) async {
    _languageDataList = await dynamicResponseService.fetchAllLanguages();
    await setLanguageConfigData();
    await createLanguageCodeMapper();
    String mandatoryLang = selectedLanguage ?? _mandatoryLanguages[0] ?? "eng";
    if (!isManualSync) {
      await toggleLocale(mandatoryLang);
    }
    notifyListeners();
  }

  set exceptionAttributes(List<String> value) {
    _exceptionAttributes = value;
    notifyListeners();
  }

  setLanguageDataList(List<LanguageData?> value) {
    _languageDataList = value;
    notifyListeners();
  }

  setCodeToLanguageMapper(Map<String, String> value) {
    _codeToLanguageMapper = value;
    notifyListeners();
  }

  setLanguageToCodeMapper(Map<String, String> value) {
    _languageToCodeMapper = value;
    notifyListeners();
  }

  setLanguages(List<String?> value) {
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

  setNotificationLanguages(List<DynamicFieldData?> value) {
    _notificationLanguages = value;
    notifyListeners();
  }

  setDisabledLanguages(Map<String, bool> value) {
    _disabledLanguageMap = value;
    notifyListeners();
  }

  Future<bool> isFilePresent(String filePath) async {
    try {
      await rootBundle.load(filePath);
      return true;
    } catch (e) {
      return false;
    }
  }

  setDisabledLanguage(String langCode) async {
    String code = languageCodeToLocale[langCode]!;
    String filepath = "assets/l10n/app_$code.arb";
    bool isPresent = await isFilePresent(filepath);
    _disabledLanguageMap[langCode] = !isPresent;
    notifyListeners();
  }

  setLanguageConfigData() async {
    _mandatoryLanguages = await processSpecService.getMandatoryLanguageCodes();
    _optionalLanguages = await processSpecService.getOptionalLanguageCodes();
    _minLanguageCount = await processSpecService.getMinLanguageCount();
    _maxLanguageCount = await processSpecService.getMaxLanguageCount();
    _languages = [..._mandatoryLanguages, ..._optionalLanguages];
    notifyListeners();
  }

  createRegistrationLanguageMap() {
    _chosenLang = [];
    Map<String, bool> languageDataMap = {};
    Map<String, bool> mandatoryMap = {};
    for (var element in _mandatoryLanguages) {
      String lang = _codeToLanguageMapper[element]!;
      languageDataMap[lang] = true;
      mandatoryMap[lang] = true;
      _chosenLang.add(lang);
    }
    for (var element in _optionalLanguages) {
      String lang = _codeToLanguageMapper[element]!;
      languageDataMap[lang] = false;
      mandatoryMap[lang] = false;
    }
    _languageMap = languageDataMap;
    _mandatoryLanguageMap = mandatoryMap;
    notifyListeners();
  }

  createLanguageCodeMapper() async {
    if (_languageDataList.isEmpty) {
      _languages = ["eng"];
      _codeToLanguageMapper["eng"] = "English";
      _languageToCodeMapper["English"] = "eng";
      _disabledLanguageMap["eng"] = false;
      return;
    }
    List<String> languageList = [];
    _codeToLanguageMapper = {};
    for (var element in _mandatoryLanguages) {
      languageList.add(element!);
      _codeToLanguageMapper[element] = element;
    }
    for (var element in _languageDataList) {
      if (_codeToLanguageMapper[element!.code] == null) {
        languageList.add(element.code);
      }
      _codeToLanguageMapper[element.code] = element.name;
      _languageToCodeMapper[element.name] = element.code;
      await setDisabledLanguage(element.code);
    }
    _languages = languageList;
  }

  // App Language
  Locale? _appLocale = const Locale("en");

  Locale get appLocal => _appLocale ?? const Locale("en");

  String _selectedLanguage = "eng";
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

  toggleLocale(String code) async {
    if (_selectedLanguage == code) {
      return;
    }
    _selectedLanguage = code;
    _appLocale = Locale(languageCodeToLocale[code]!);
    notifyListeners();
  }

  getAudit(String id, String componentId) async {
    await audit.performAudit(id, componentId);
  }

  Map<String?, String?> _locationHierarchyMap = {};
  Map<String, List<String?>> _groupedHierarchyValues = {};
  List<String> _hierarchyReverse = [];

  Map<String?, String?> get locationHierarchyMap => _locationHierarchyMap;
  Map<String, List<String?>> get groupedHierarchyValues =>
      _groupedHierarchyValues;
  List<String> get hierarchyReverse => _hierarchyReverse;

  setLocationHierarchyMap(Map<String, String> value) {
    _locationHierarchyMap = value;
    notifyListeners();
  }

  setHierarchyReverse(List<String> value) {
    _hierarchyReverse = value;
    notifyListeners();
  }

  initializeLocationHierarchyMap() async {
    Map<String?, String?> hierarchyMap =
        await dynamicResponseService.fetchLocationHierarchyMap();
    _locationHierarchyMap = hierarchyMap;
    List<String> hReverse = [];
    _locationHierarchyMap.forEach((key, value) {
      hReverse.add(value!);
    });
    _hierarchyReverse = hReverse;
    notifyListeners();
  }

  setGroupedHierarchyValues(Map<String, List<String>> value) {
    _groupedHierarchyValues = value;
    notifyListeners();
  }

  initializeGroupedHierarchyMap(String key) {
    List<String?> hValues = [];
    _locationHierarchyMap.forEach((key, value) {
      hValues.add(null);
    });
    _groupedHierarchyValues[key] = hValues;
    notifyListeners();
  }

  saveScreenHeaderToGlobalParam(String id, String value) async {
    await networkService.saveScreenHeaderToGlobalParam(id, value);
  }

  removeProofOfExceptionFieldFromMap(
      String key, Map<String, dynamic> commonMap) {
    commonMap.remove(key);
  }

  Map<String, bool> _selectedUpdateFields = {};
  List<String> _selectedFieldList = [];
  Map<String, bool> get selectedUpdateFields => _selectedUpdateFields;
  List<String> get selectedFieldList => _selectedFieldList;

  set selectedUpdateFields(Map<String, bool> value) {
    _selectedUpdateFields = value;
    notifyListeners();
  }

  addSelectedUpdateFieldKey(String key) {
    _selectedUpdateFields[key] = true;
    notifyListeners();
  }

  removeSelectedUpdateFieldKey(String key) {
    _selectedUpdateFields.remove(key);
    notifyListeners();
  }

  setSelectedFieldList(List<String> value) {
    _selectedFieldList = value;
    notifyListeners();
  }

  addToSelectedFieldList(String id) {
    _selectedFieldList.add(id);
    notifyListeners();
  }

  clearRegistrationProcessData() {
    clearMap();
    clearExceptions();
    clearScannedPages();
    newProcessTabIndex = 0;
    htmlBoxTabIndex = 0;
    setRegId("");
    selectedUpdateFields = {};
    updateUINNumber = "";
  }
}
