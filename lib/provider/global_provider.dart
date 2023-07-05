import 'package:flutter/widgets.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/pigeon/common_details_pigeon.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';
import 'package:registration_client/platform_spi/machine_key.dart';
import 'package:registration_client/ui/process_ui/new_process.dart';

class GlobalProvider with ChangeNotifier {
  final MachineKey machineKey = MachineKey();
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

  List<String> _chosenLang = ["English"];
  Map<String, bool> _languageMap = {
    'English': true,
    'Arabic': false,
    'French': false,
  };
  Map<String, dynamic> _fieldDisplayValues = {};

  Map<String, dynamic> _fieldInputValues = {};

  //GettersSetters

  int get currentIndex => _currentIndex;
  String get name => _name;
  String get centerId => _centerId;
  String get centerName => _centerName;
  String get machineName => _machineName;
  Map<String?, String?> get machineDetails => _machineDetails;

  Process? get currentProcess => this._currentProcess;

  Map<String, bool> get languageMap => this._languageMap;

  List<String> get chosenLang => this._chosenLang;

  set chosenLang(List<String> value) => this._chosenLang = value;

  set languageMap(Map<String, bool> value) => this._languageMap = value;

  set currentProcess(Process? value) {
    this._currentProcess = value;
    notifyListeners();
  }

  int get newProcessTabIndex => this._newProcessTabIndex;

  set newProcessTabIndex(int value) {
    this._newProcessTabIndex = value;
    notifyListeners();
  }

  int get htmlBoxTabIndex => this._htmlBoxTabIndex;

  set htmlBoxTabIndex(int value) {
    this._htmlBoxTabIndex = value;
    notifyListeners();
  }

  Map<String, dynamic> get fieldDisplayValues => this._fieldDisplayValues;

  set fieldDisplayValues(Map<String, dynamic> value) {
    this._fieldDisplayValues = value;
    notifyListeners();
  }

  Map<String, dynamic> get fieldInputValues => this._fieldInputValues;

  set fieldInputValues(Map<String, dynamic> value) {
    this._fieldInputValues = value;
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
        languageMap["${key}"] = value;

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

  setInputMapValue(String key, dynamic value) {
    fieldInputValues[key] = value;
    notifyListeners();
  }

  chooseLanguage(Map<String, String> label) {
    String x = '';
    for (var i in chosenLang) {
      if (i == "English") {
        x = x + label["eng"]! + "/";
      }
      if (i == "Arabic") {
        x = x + label["ara"]! + "/";
      }
      if (i == "French") {
        x = x + label["fra"]! + "/";
      }
    }
    x = x.substring(0, x.length - 1);
    return x;
  }

  langToCode(String lang) {
    if (lang == "English") {
      return "eng";
    }
    if (lang == "Arabic") {
      return "ara";
    }
    if (lang == "French") {
      return "fra";
    }
  }

  fieldValues(Process process) {
    process.screens!.forEach((screen) {
      screen!.fields!.forEach((field) async {
        if (field!.fieldType == "dynamic") {
          fieldDisplayValues[field.id!] =
              await CommonDetailsApi().getFieldValues(field.id!, "eng");
        }
        if (field.templateName != null) {
          List values = List.empty(growable: true);
          chosenLang.forEach((lang) async {
            values.add(
              await CommonDetailsApi().getTemplateContent(
                field.templateName!,
                langToCode(lang),
              ),
            );
          });
          fieldDisplayValues[field.id!] = values;
        }
      });
    });
  }

  getRegCenterName(String regCenterId, String langCode) async {
    String regCenterName =
        await machineKey.getCenterName(regCenterId, langCode);

    _centerName = regCenterName;
    notifyListeners();
  }
}
