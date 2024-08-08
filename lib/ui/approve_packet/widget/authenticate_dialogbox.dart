import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/approve_packets_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../../../provider/auth_provider.dart';

class AuthenticateDialogBox extends StatefulWidget {
  const AuthenticateDialogBox({super.key});

  @override
  State<AuthenticateDialogBox> createState() => _AuthenticateDialogBoxState();
}

class _AuthenticateDialogBoxState extends State<AuthenticateDialogBox> {
  TextEditingController username = TextEditingController();
  TextEditingController password = TextEditingController();
  bool loading = false;

  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;
  late AuthProvider authProvider;

  @override
  void initState() {
    authProvider = Provider.of<AuthProvider>(context, listen: false);
    super.initState();
  }

  // void _showInSnackBar(String value) {
  //   ScaffoldMessenger.of(context).showSnackBar(
  //     SnackBar(
  //       content: Text(value),
  //     ),
  //   );
  // }

  bool _validateUsername() {
    if (username.text.trim().isEmpty) {
      // _showInSnackBar(appLocalizations.username_required);
      return false;
    }

    if (username.text.trim().length > 50) {
      // _showInSnackBar(appLocalizations.username_exceed);
      return false;
    }

    return true;
  }

  bool _validatePassword() {
    if (password.text.trim().isEmpty) {
      // _showInSnackBar(appLocalizations.password_required);
      return false;
    }

    if (password.text.trim().length > 50) {
      // _showInSnackBar(appLocalizations.password_exceed);
      return false;
    }

    return true;
  }

  // _showErrorInSnackbar() {
  //   String errorMsg = authProvider.packetError;
  //   String snackbarText = "";
  //
  //   switch (errorMsg) {
  //     case "REG_TRY_AGAIN":
  //       snackbarText = appLocalizations.login_failed;
  //       break;
  //
  //     case "REG_INVALID_REQUEST":
  //       snackbarText = appLocalizations.password_incorrect;
  //       break;
  //
  //     case "REG_NETWORK_ERROR":
  //       snackbarText = appLocalizations.network_error;
  //       break;
  //
  //     case "":
  //       return;
  //
  //     default:
  //       snackbarText = errorMsg;
  //       break;
  //   }
  //
  //   _showInSnackBar(snackbarText);
  // }

  Future<bool> _authenticatePacketPassword() async {
    if (!_validateUsername()) {
      return false;
    }

    if (!_validatePassword()) {
      return false;
    }

    if (authProvider.currentUser.userId != username.text) {
      // _showInSnackBar(appLocalizations.invalid_user);
      return false;
    }

    await authProvider.authenticatePacket(username.text, password.text);

    if (!authProvider.isPacketAuthenticated) {
      // _showErrorInSnackbar();
      return false;
    }
    return true;
  }

  Future<void> submitReviews() async {
    setState(() {
      loading = true;
    });
    bool isAuthenticated = await _authenticatePacketPassword();
    if (isAuthenticated) {
      try {
        context.read<ApprovePacketsProvider>().submitSelected();
        Navigator.of(context).pop();
      } catch (e) {
        log(e.toString());
      }
    }
    setState(() {
      loading = false;
    });
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
                const Text(
                  "Supervisor's Authentication",
                  style: TextStyle(fontSize: 28.0, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16.0),
                const SizedBox(
                  width: width * 0.80,
                  child: Text(
                    "Supervisor’s Authentication using Password",
                    textAlign: TextAlign.center,
                    softWrap: true,
                    style: TextStyle(fontSize: 18),
                  ),
                ),
                const SizedBox(height: 24.0),

                // Username TextFeild
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    children: [
                      const Row(
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Text(
                            "Username ",
                            style: TextStyle(
                                fontSize: 18, fontWeight: FontWeight.w500),
                          ),
                          Text(
                            "*",
                            style: TextStyle(color: Colors.red, fontSize: 18),
                          )
                        ],
                      ),
                      const SizedBox(height: 12.0),
                      TextFormField(
                        controller: username,
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
                      const Row(
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Text(
                            "Password ",
                            style: TextStyle(
                                fontSize: 18, fontWeight: FontWeight.w500),
                          ),
                          Text(
                            "*",
                            style: TextStyle(color: Colors.red, fontSize: 18),
                          )
                        ],
                      ),
                      const SizedBox(height: 12.0),
                      TextFormField(
                        controller: password,
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
                    SizedBox(
                      width: 150,
                      height: 60,
                      child: ElevatedButton(
                          onPressed: () async {
                            await submitReviews();
                          },
                          style: ElevatedButton.styleFrom(
                              backgroundColor: solidPrimary,
                              padding: const EdgeInsets.symmetric(
                                  vertical: 16, horizontal: 32)),
                          child: loading
                              ? const CircularProgressIndicator(
                                  color: Colors.white,
                                )
                              : const Text(
                                  "SUBMIT",
                                  style: TextStyle(fontSize: 18),
                                )),
                    ),
                  ],
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
