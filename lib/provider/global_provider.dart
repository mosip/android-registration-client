import 'package:flutter/widgets.dart';

class GlobalProvider with ChangeNotifier {
  List<Object?> _listOfProcesses = List.empty(growable: true);
  
  List<Object?> get listOfProcesses => this._listOfProcesses;

  set listOfProcesses(List<Object?> value) {
    this._listOfProcesses = value;
    notifyListeners();
  }

  bool _isLoggedIn = false;
  bool _isOnboarded = false;
  bool _isDefault = false;
  bool _isSupervisor = false;
  bool _isOfficer = false;

  bool get isLoggedIn => _isLoggedIn;
  bool get isOnboarded => _isOnboarded;
  bool get isDefault => _isDefault;
  bool get isSupervisor => _isSupervisor;
  bool get isOfficer => _isOfficer;

  setIsLoggedIn(bool value) {
    _isLoggedIn = value;
    notifyListeners();
  }

  setIsOnboarded(bool value) {
    _isOnboarded = value;
    notifyListeners();
  }

  setIsDefault(bool value) {
    _isDefault = value;
    notifyListeners();
  }

  setIsSupervisor(bool value) {
    _isSupervisor = value;
    notifyListeners();
  }

  setIsOfficer(bool value) {
    _isOfficer = value;
    notifyListeners();
  }
}
