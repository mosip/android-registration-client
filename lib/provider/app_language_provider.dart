import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AppLanguageProvider with ChangeNotifier {
  Locale? _appLocale = Locale('en');

  Locale get appLocal => _appLocale ?? Locale("en");

  String _selectedLanguage = 'eng';
  String get selectedLanguage => this._selectedLanguage;
  set selectedLanguage(String value) {
    this._selectedLanguage = value;
    notifyListeners();
  }

  fetchLocale() async {
    var prefs = await SharedPreferences.getInstance();
    if (prefs.getString('language_code') == null) {
      _appLocale = Locale('en');
      return null;
    }
    _appLocale = Locale(prefs.getString('language_code')!);
    return null;
  }

  void changeLanguage(Locale code) async {
    var prefs = await SharedPreferences.getInstance();

    if(_appLocale == code) {
      return;
    }

    if (code == Locale("eng")) {
      _appLocale = Locale("en");
      await prefs.setString('language_code', 'en');
      await prefs.setString('countryCode', '');
    } else if(code == Locale("ara")) {
      _appLocale = Locale("ar");
      await prefs.setString('language_code', 'ar');
      await prefs.setString('countryCode', '');
    } else if(code == Locale("fre")) {
      _appLocale = Locale("fr");
      await prefs.setString('language_code', 'fr');
      await prefs.setString('countryCode', '');
    }

    notifyListeners();
  }
}
