import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AppLanguageProvider with ChangeNotifier {
  Locale? _appLocale = const Locale('en');

  Locale get appLocal => _appLocale ?? const Locale("en");

  String _selectedLanguage = 'eng';
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
}
