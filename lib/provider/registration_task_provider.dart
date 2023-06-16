import 'package:flutter/material.dart';

class RegistrationTaskProvider with ChangeNotifier {
  List<Object?> _listOfProcesses = List.empty(growable: true);
  
  List<Object?> get listOfProcesses => this._listOfProcesses;

  set listOfProcesses(List<Object?> value) {
    this._listOfProcesses = value;
    notifyListeners();
  }
}