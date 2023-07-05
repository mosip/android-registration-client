import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';
import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class AuthenticationPage extends StatefulWidget {
  const AuthenticationPage({super.key});

  static const routeName = '/authentication-page';

  @override
  State<AuthenticationPage> createState() => _AuthenticationPageState();
}

class _AuthenticationPageState extends State<AuthenticationPage> {
  String username = '';
  String password = '';
  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    // Map<String, dynamic> arguments =
    //     ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    return SafeArea(
      child: Scaffold(
        backgroundColor: secondaryColors.elementAt(10),
        bottomNavigationBar: Container(
          color: pure_white,
          padding: const EdgeInsets.all(16),
          height: 84.h,
          child: isMobile
              ? ElevatedButton(
                  child: const Text("AUTHENTICATE"),
                  onPressed: () {},
                )
              : Container(
                  width: w,
                  padding: EdgeInsets.symmetric(
                    horizontal: 60.w,
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      ElevatedButton(
                        style: ButtonStyle(
                          maximumSize:
                              MaterialStateProperty.all<Size>(Size(209, 52)),
                          minimumSize:
                              MaterialStateProperty.all<Size>(Size(209, 52)),
                        ),
                        child: const Text("AUTHENTICATE"),
                        onPressed: () {
                          _authenticatePacket();
                        },
                      )
                    ],
                  ),
                ),
        ),
        body: SingleChildScrollView(
          child: AnnotatedRegion<SystemUiOverlayStyle>(
            value: const SystemUiOverlayStyle(
              statusBarColor: Colors.transparent,
            ),
            child: Column(
              children: [
                isMobile
                    ? const SizedBox()
                    : Column(
                        children: const [
                          TabletHeader(),
                          TabletNavbar(),
                        ],
                      ),
                SizedBox(
                  height: 30.h,
                ),
                Container(
                  width: 376.w,
                  padding: EdgeInsets.only(
                    top: 24.h,
                    bottom: 28.h,
                    left: 20.w,
                    right: 20.w,
                  ),
                  decoration: BoxDecoration(
                    borderRadius: const BorderRadius.all(
                      Radius.circular(6),
                    ),
                    color: pure_white,
                  ),
                  child: Column(
                    children: [
                      _getAuthIcon(),
                      SizedBox(
                        height: 26.h,
                      ),
                      Text(
                        'Authentication using Password',
                        style: TextStyle(
                            fontSize: 18.spMax,
                            fontWeight: semiBold,
                            color: AppStyle.appBlack),
                      ),
                      SizedBox(
                        height: 35.h,
                      ),
                      Row(
                        children: [
                          Text(
                            AppLocalizations.of(context)!.username,
                            style: AppStyle.mobileTextfieldHeader,
                          ),
                          const Text(
                            ' *',
                            style: TextStyle(
                              color: AppStyle.mandatoryField,
                            ),
                          ),
                        ],
                      ),
                      SizedBox(
                        height: 11.h,
                      ),
                      _getUsernameTextField(),
                      SizedBox(
                        height: 35.h,
                      ),
                      Row(
                        children: [
                          Text(
                            AppLocalizations.of(context)!.password,
                            style: AppStyle.mobileTextfieldHeader,
                          ),
                          const Text(
                            ' *',
                            style: TextStyle(color: AppStyle.mandatoryField),
                          ),
                        ],
                      ),
                      SizedBox(
                        height: 11.h,
                      ),
                      _getPasswordTextField(),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _showInSnackBar(String value) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _authenticatePacket() async {
    if(!_validateUsername()) {
      return;
    }

    if(!_validatePassword()) {
      return;
    }

    if(!_isUserLoggedInUser()) {
      return;
    }

    bool isConnected = context.read<ConnectivityProvider>().isConnected;
    await context.read<AuthProvider>().authenticatePacket(username, password, isConnected);
    bool isPacketAuthenticated = context.read<AuthProvider>().isPacketAuthenticated;

    if(!isPacketAuthenticated) {
      _showInSnackBar(AppLocalizations.of(context)!.password_incorrect);
      return;
    }

    _showInSnackBar("Authentication Successful!");
  }

  bool _validateUsername() {
    if(username.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.username_required);
      return false;
    }

    if(username.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.username_exceed);
      return false;
    }

    return true;
  }

  bool _validatePassword() {
    if(password.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.password_required);
      return false;
    }

    if(password.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.password_exceed);
      return false;
    }

    return true;
  }

  bool _isUserLoggedInUser() {
    final user = context.read<AuthProvider>().currentUser;
    if(user.userId != username) {
      _showInSnackBar(AppLocalizations.of(context)!.invalid_user);
      return false;
    }
    return true;
  }

  _getAuthIcon() {
    return Container(
      height: 80.w,
      width: 80.w,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(
          color: AppStyle.authIconBorder,
          width: 2,
        ),
        color: AppStyle.authIconBackground,
      ),
      child: Center(
        child: Image.asset('assets/images/Registering an Individual@2x.png'),
      ),
    );
  }

  _getUsernameTextField() {
    return Container(
      height: 52.h,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.symmetric(
        vertical: 12.h,
        horizontal: 12.w,
      ),
      decoration: BoxDecoration(
        border: Border.all(
          width: 1.h,
          color: AppStyle.appGreyShade,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(6),
        ),
      ),
      child: TextField(
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_username,
          hintStyle: AppStyle.mobileTextfieldHintText,
          border: InputBorder.none,
        ),
        onChanged: (v) {
          setState(() {
            username = v;
          });
        },
      ),
    );
  }

  _getPasswordTextField() {
    return Container(
      height: 52.h,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.symmetric(
        vertical: 12.h,
        horizontal: 12.w,
      ),
      decoration: BoxDecoration(
        border: Border.all(
          width: 1.h,
          color: AppStyle.appGreyShade,
        ),
        borderRadius: const BorderRadius.all(
          Radius.circular(6),
        ),
      ),
      child: TextField(
        obscureText: true,
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context)!.enter_password,
          hintStyle: AppStyle.mobileTextfieldHintText,
          border: InputBorder.none,
        ),
        onChanged: (v) {
          setState(() {
            password = v;
          });
        },
      ),
    );
  }
}
