import 'package:flutter/widgets.dart';

class DashboardViewModel with ChangeNotifier {
  double _currentDot = 0.0;
  int _currentIndex = 0;

  double get currentDot => _currentDot;
  int get currentIndex => _currentIndex;

  setCurrentDot(int value) {
    _currentDot = double.parse(value.toString());
    notifyListeners();
  }

  setCurrentIndex(int value) {
    _currentIndex = value;
    notifyListeners();
  }

  void refreshProviderState() {
    return notifyListeners();
  }
}
