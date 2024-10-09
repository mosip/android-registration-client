import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../../../provider/auth_provider.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class AuthenticateDialogBox extends StatefulWidget {
  const AuthenticateDialogBox({super.key});

  @override
  State<AuthenticateDialogBox> createState() => _AuthenticateDialogBoxState();
}

class _AuthenticateDialogBoxState extends State<AuthenticateDialogBox> {
  TextEditingController username = TextEditingController();
  TextEditingController password = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool loading = false;
  String loginError = "";
  bool showLoginError = false;

  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  late AuthProvider authProvider;

  @override
  void initState() {
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    super.initState();
  }

  _showErrorInSnackbar() {
    String errorMsg = authProvider.packetError;
    String snackbarText = "";
    log(errorMsg);

    switch (errorMsg) {
      case "REG_TRY_AGAIN":
        snackbarText = appLocalizations.login_failed;
        break;

      case "REG_INVALID_REQUEST":
        snackbarText = appLocalizations.password_incorrect;
        break;

      case "REG_NETWORK_ERROR":
        snackbarText = appLocalizations.network_error;
        break;

      case "":
        return;

      default:
        snackbarText = errorMsg;
        break;
    }
    return snackbarText;
  }

  Future<bool> _authenticatePacketPassword() async {
    if (_formKey.currentState!.validate()) {
      await authProvider.authenticatePacket(username.text, password.text);
      if (!authProvider.isPacketAuthenticated) {
        setState(() {
          loginError = _showErrorInSnackbar();
        });
        return false;
      }
      return true;
    }
    return false;
  }

  Future<void> submitReviews() async {
    setState(() {
      loading = true;
    });
    bool isAuthenticated = await _authenticatePacketPassword();
    if (isAuthenticated) {
      try {
        await context.read<ApprovePacketsProvider>().submitSelected();
        Navigator.of(context).pop();
      } catch (e) {
        log(e.toString());
      }
    } else {
      setState(() {
        showLoginError = true;
      });
      Future.delayed(const Duration(seconds: 2), () {
        setState(() {
          showLoginError = false;
          loginError = appLocalizations.login_failed;
        });
      });
    }
    setState(() {
      loading = false;
    });
  }

  bool validateForm(){
    bool value = false;
    if(username.text.trim().isNotEmpty && password.text.trim().isNotEmpty){
      setState(() {
        value = true;
      });
    }else{
      setState(() {
        value = false;
      });
    }
    return value;
  }

  @override
  Widget build(BuildContext context) {
    const double width = 600;
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
      ),
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: SizedBox(
            width: width,
            child: Form(
              key: _formKey,
              autovalidateMode: AutovalidateMode.always,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    padding: const EdgeInsets.all(32),
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: backgroundColor,
                    ),
                    child: Card(
                      elevation: 0,
                      shape: const CircleBorder(),
                      child: Icon(
                        Icons.verified_user_outlined,
                        color: solidPrimary,
                        size: 64,
                      ),
                    ),
                  ),
                  const SizedBox(height: 24.0),
                  Text(
                    AppLocalizations.of(context)!.supervisor_auth_heading,
                    style: const TextStyle(
                        fontSize: 28.0, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 16.0),
                  SizedBox(
                    width: width * 0.80,
                    child: Text(
                      AppLocalizations.of(context)!.supervisor_auth_subheading,
                      textAlign: TextAlign.center,
                      softWrap: true,
                      style: const TextStyle(fontSize: 18),
                    ),
                  ),
                  const SizedBox(height: 24.0),

                  // Username TextFeild
                  Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            Text(
                              "${AppLocalizations.of(context)!.username} ",
                              style: const TextStyle(
                                  fontSize: 18, fontWeight: FontWeight.w500),
                            ),
                            const Text(
                              "*",
                              style: TextStyle(color: Colors.red, fontSize: 18),
                            )
                          ],
                        ),
                        const SizedBox(height: 12.0),
                        TextFormField(
                          controller: username,
                          validator: (value) {
                            if (username.text.trim().isEmpty) {
                              return appLocalizations.username_required;
                            } else if (username.text.trim().length > 50) {
                              return appLocalizations.username_exceed;
                            }
                            if (authProvider.currentUser.userId !=
                                username.text) {
                              return appLocalizations.invalid_user;
                            }

                            return null;
                          },
                          onChanged: (String value){
                            validateForm();
                          },
                          decoration: InputDecoration(
                            border: OutlineInputBorder(
                              borderSide: const BorderSide(
                                color: Colors.black26,
                              ),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: 20, vertical: 22),
                          ),
                          style: const TextStyle(fontSize: 18),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 16,
                  ),
                  // Password TextFeild
                  Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            Text(
                              "${AppLocalizations.of(context)!.password} ",
                              style: const TextStyle(
                                  fontSize: 18, fontWeight: FontWeight.w500),
                            ),
                            const Text(
                              "*",
                              style: TextStyle(color: Colors.red, fontSize: 18),
                            )
                          ],
                        ),
                        const SizedBox(height: 12.0),
                        TextFormField(
                          controller: password,
                          validator: (value) {
                            if (password.text.trim().isEmpty) {
                              return appLocalizations.password_required;
                            } else if (password.text.trim().length > 50) {
                              return appLocalizations.password_exceed;
                            }
                            return null;
                          },
                          onChanged: (String value){
                            validateForm();
                          },
                          obscureText: true,
                          decoration: InputDecoration(
                            border: OutlineInputBorder(
                              borderSide: const BorderSide(
                                color: Colors.black26,
                              ),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: 20, vertical: 22),
                          ),
                          style: const TextStyle(fontSize: 18),
                        ),
                      ],
                    ),
                  ),

                  const SizedBox(
                    height: 16,
                  ),
                  const Divider(
                    height: 45,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      showLoginError
                          ? Text(
                              loginError,
                              style: TextStyle(
                                fontSize: 16,
                                color: Colors.red[900],
                              ),
                            )
                          : const SizedBox(
                              width: 2,
                            ),
                      const SizedBox(
                        width: 25,
                      ),
                      SizedBox(
                        width: 150,
                        height: 65,
                        child: ElevatedButton(
                            onPressed: validateForm() ? () async {
                              await submitReviews();
                            } : (){},
                            style: ElevatedButton.styleFrom(
                                backgroundColor: validateForm() ? solidPrimary : Colors.grey,
                                padding: const EdgeInsets.symmetric(
                                    vertical: 16, horizontal: 32)),
                            child: loading
                                ? const Padding(
                                    padding:
                                        EdgeInsets.symmetric(horizontal: 20),
                                    child: CircularProgressIndicator(
                                      color: Colors.white,
                                    ),
                                  )
                                : Text(
                                    AppLocalizations.of(context)!.submit,
                                    style: const TextStyle(fontSize: 18),
                                  )),
                      ),
                    ],
                  )
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
