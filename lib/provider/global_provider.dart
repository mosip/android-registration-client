import 'dart:io';

import 'package:flutter/widgets.dart';
import 'package:registration_client/platform_android/machine_key_impl.dart';

class GlobalProvider with ChangeNotifier {
  //Variables
  int _currentIndex = 0;
  String _name = "";
  String _centerId = "";
  String _centerName = "";
  String _machineName = "";
  Process? _currentProcess;
  Map<String?, String?> _machineDetails = {};

  //GettersSetters

  int get currentIndex => _currentIndex;
  String get name => _name;
  String get centerId => _centerId;
  String get centerName => _centerName;
  String get machineName => _machineName;
  Map<String?, String?> get machineDetails => _machineDetails;

  
  
  Process? get currentProcess => this._currentProcess;

  set currentProcess(Process? value) {
    this._currentProcess = value;
    notifyListeners();
  }

  int _newProcessTabIndex = 0;
  int get newProcessTabIndex => this._newProcessTabIndex;

  set newProcessTabIndex(int value) {
    this._newProcessTabIndex = value;
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
    final machine = await MachineKeyImpl().getMachineKeys();
    if(machine.errorCode != null) {
      _machineDetails.addAll({});
    } else {
      _machineDetails = machine.map;
      _machineName = _machineDetails["name"]!;
    }
    notifyListeners();
  }
}
