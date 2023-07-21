import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';

import 'package:registration_client/pigeon/registration_data_pigeon.dart';

import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';

import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/ui/post_registration/authentication_page.dart';
import 'package:registration_client/ui/post_registration/preview_page.dart';

import 'package:registration_client/ui/process_ui/widgets/new_process_screen_content.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class NewProcess extends StatelessWidget {
  NewProcess({super.key});

  static const routeName = '/new_process';

  final List<String> postRegistrationTabs = [
    'Preview',
    'Authentication',
    'Acknowledgement'
  ];
  String username = '';
  String password = '';

  void _showInSnackBar(String value, BuildContext context) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(value),
      ),
    );
  }

  _authenticatePacket(BuildContext context) async {
    if (!_validateUsername(context)) {
      return false;
    }

    if (!_validatePassword(context)) {
      return false;
    }

    if (!_isUserLoggedInUser(context)) {
      return false;
    }

    bool isConnected = context.read<ConnectivityProvider>().isConnected;
    await context
        .read<AuthProvider>()
        .authenticatePacket(username, password, isConnected);
    bool isPacketAuthenticated =
        context.read<AuthProvider>().isPacketAuthenticated;

    if (!isPacketAuthenticated) {
      _showInSnackBar(
          AppLocalizations.of(context)!.password_incorrect, context);
      return false;
    }

    username = '';
    password = '';
    return true;
  }

  bool _validateUsername(BuildContext context) {
    if (username.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.username_required, context);
      return false;
    }

    if (username.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.username_exceed, context);
      return false;
    }

    return true;
  }

  bool _validatePassword(BuildContext context) {
    if (password.isEmpty) {
      _showInSnackBar(AppLocalizations.of(context)!.password_required, context);
      return false;
    }

    if (password.length > 50) {
      _showInSnackBar(AppLocalizations.of(context)!.password_exceed, context);
      return false;
    }

    return true;
  }

  bool _isUserLoggedInUser(BuildContext context) {
    final user = context.read<AuthProvider>().currentUser;
    if (user.userId != username) {
      _showInSnackBar(AppLocalizations.of(context)!.invalid_user, context);
      return false;
    }
    return true;
  }

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    Map<String, dynamic> arguments =
        ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    final Process newProcess = arguments["process"];
    int size = newProcess.screens!.length;
    return Scaffold(
      backgroundColor: secondaryColors.elementAt(10),
      bottomNavigationBar: Container(
        color: pure_white,
        padding: EdgeInsets.all(16),
        height: 84.h,
        child: isMobile
            ? ElevatedButton(
                child: Text(
                    context.read<GlobalProvider>().newProcessTabIndex <= size
                        ? "CONTINUE"
                        : context.read<GlobalProvider>().newProcessTabIndex ==
                                size + 1
                            ? "AUTHENTICATE"
                            : "COMPLETE"),
                onPressed: () async {
                  if (context.read<GlobalProvider>().newProcessTabIndex <
                      size) {
                    if (context
                        .read<GlobalProvider>()
                        .formKey
                        .currentState!
                        .validate()) {
                      if (context.read<GlobalProvider>().newProcessTabIndex ==
                          newProcess.screens!.length - 1) {
                        context
                            .read<RegistrationTaskProvider>()
                            .getPreviewTemplate(true);
                      }

                      context.read<GlobalProvider>().newProcessTabIndex =
                          context.read<GlobalProvider>().newProcessTabIndex + 1;
                    }
                  } else {
                    if (context.read<GlobalProvider>().newProcessTabIndex ==
                        size + 1) {
                      bool isPacketAuthenticated =
                          await _authenticatePacket(context);
                      if (!isPacketAuthenticated) {
                        return;
                      }
                    }
                    context.read<GlobalProvider>().newProcessTabIndex =
                        context.read<GlobalProvider>().newProcessTabIndex + 1;
                  }
                },
              )
            : Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  ElevatedButton(
                    style: ButtonStyle(
                      maximumSize:
                          MaterialStateProperty.all<Size>(Size(209, 52)),
                      minimumSize:
                          MaterialStateProperty.all<Size>(Size(209, 52)),
                    ),
                    onPressed: () async {
                      if (context.read<GlobalProvider>().newProcessTabIndex <
                          size) {
                        if (context
                            .read<GlobalProvider>()
                            .formKey
                            .currentState!
                            .validate()) {
                          if (context
                                  .read<GlobalProvider>()
                                  .newProcessTabIndex ==
                              newProcess.screens!.length - 1) {
                            context
                                .read<RegistrationTaskProvider>()
                                .getPreviewTemplate(true);
                          }

                          context.read<GlobalProvider>().newProcessTabIndex =
                              context
                                      .read<GlobalProvider>()
                                      .newProcessTabIndex +
                                  1;
                        }
                      } else {
                        if (context.read<GlobalProvider>().newProcessTabIndex ==
                            size + 1) {
                          bool isPacketAuthenticated =
                              await _authenticatePacket(context);
                          if (!isPacketAuthenticated) {
                            return;
                          }
                        }
                        context.read<GlobalProvider>().newProcessTabIndex =
                            context.read<GlobalProvider>().newProcessTabIndex +
                                1;
                      }
                    },
                    child: Text(context
                                .read<GlobalProvider>()
                                .newProcessTabIndex <=
                            size
                        ? "CONTINUE"
                        : context.read<GlobalProvider>().newProcessTabIndex ==
                                size + 1
                            ? "AUTHENTICATE"
                            : "COMPLETE"),
                  )
                ],
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
                  ? SizedBox()
                  : Column(
                      children: const [
                        TabletHeader(),
                        TabletNavbar(),
                      ],
                    ),
              Container(
                padding: isMobile
                    ? EdgeInsets.fromLTRB(0, 46, 0, 0)
                    : EdgeInsets.fromLTRB(0, 0, 0, 0),
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [Color(0xff214FBF), Color(0xff1C43A1)],
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SizedBox(
                      width: w,
                      height: isMobile ? 21 : 30,
                    ),
                    Padding(
                      padding: isMobile
                          ? EdgeInsets.fromLTRB(16, 0, 0, 0)
                          : EdgeInsets.fromLTRB(60, 0, 60, 0),
                      child: Text(newProcess.label!["eng"]!,
                          style: Theme.of(context)
                              .textTheme
                              .titleMedium
                              ?.copyWith(
                                  color: pure_white,
                                  fontWeight: semiBold,
                                  fontSize: 21)),
                    ),
                    SizedBox(
                      height: 30.h,
                    ),
                    Divider(
                      height: 12.h,
                      thickness: 1,
                      color: secondaryColors.elementAt(2),
                    ),
                    Padding(
                      padding: isMobile
                          ? EdgeInsets.all(0)
                          : EdgeInsets.fromLTRB(60, 0, 60, 0),
                      child: Stack(
                        alignment: FractionalOffset.centerRight,
                        children: [
                          Padding(
                            padding: const EdgeInsets.fromLTRB(16, 10, 0, 0),
                            child: SizedBox(
                              height: 36.h,
                              child: ListView.builder(
                                  padding: EdgeInsets.all(0),
                                  scrollDirection: Axis.horizontal,
                                  itemCount: newProcess.screens!.length + 3,
                                  itemBuilder:
                                      (BuildContext context, int index) {
                                    return GestureDetector(
                                      onTap: () {
                                        if (index <
                                            context
                                                .read<GlobalProvider>()
                                                .newProcessTabIndex) {
                                          context
                                              .read<GlobalProvider>()
                                              .newProcessTabIndex = index;
                                        }
                                      },
                                      child: Row(
                                        children: [
                                          Container(
                                            padding:
                                                EdgeInsets.fromLTRB(0, 0, 0, 8),
                                            decoration: BoxDecoration(
                                              border: Border(
                                                bottom: BorderSide(
                                                    color: (context
                                                                .watch<
                                                                    GlobalProvider>()
                                                                .newProcessTabIndex ==
                                                            index)
                                                        ? pure_white
                                                        : Colors.transparent,
                                                    width: 3),
                                              ),
                                            ),
                                            child: Row(
                                              children: [
                                                (index <
                                                        context
                                                            .watch<
                                                                GlobalProvider>()
                                                            .newProcessTabIndex)
                                                    ? Icon(
                                                        Icons.check_circle,
                                                        size: 17,
                                                        color: secondaryColors
                                                            .elementAt(11),
                                                      )
                                                    : (context
                                                                .watch<
                                                                    GlobalProvider>()
                                                                .newProcessTabIndex ==
                                                            index)
                                                        ? Icon(
                                                            Icons.circle,
                                                            color: pure_white,
                                                            size: 17,
                                                          )
                                                        : Icon(
                                                            Icons
                                                                .circle_outlined,
                                                            size: 17,
                                                            color:
                                                                secondaryColors
                                                                    .elementAt(
                                                                        9),
                                                          ),
                                                SizedBox(
                                                  width: 6.w,
                                                ),
                                                Text(
                                                  index < size
                                                      ? newProcess
                                                          .screens![index]!
                                                          .label!["eng"]!
                                                      : postRegistrationTabs[
                                                          index - size],
                                                  style: Theme.of(context)
                                                      .textTheme
                                                      .titleSmall
                                                      ?.copyWith(
                                                          color: (context
                                                                      .watch<
                                                                          GlobalProvider>()
                                                                      .newProcessTabIndex ==
                                                                  index)
                                                              ? pure_white
                                                              : secondaryColors
                                                                  .elementAt(9),
                                                          fontWeight: semiBold,
                                                          fontSize: 17),
                                                ),
                                              ],
                                            ),
                                          ),
                                          SizedBox(
                                            width: 35.w,
                                          ),
                                        ],
                                      ),
                                    );
                                  }),
                            ),
                          ),
                          Container(
                            height: 36.h,
                            width: 25.w,
                            padding: EdgeInsets.fromLTRB(0, 0, 0, 0),
                            color: solid_primary,
                            child: Icon(
                              Icons.arrow_forward_ios_outlined,
                              color: pure_white,
                              size: 17,
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(
                      height: 5,
                    ),
                  ],
                ),
              ),
              Padding(
                padding: isMobile
                    ? EdgeInsets.all(0)
                    : EdgeInsets.fromLTRB(60, 0, 60, 0),
                child: context.watch<GlobalProvider>().newProcessTabIndex < size
                    ? NewProcessScreenContent(
                        context: context,
                        screen: newProcess.screens!.elementAt(context
                            .watch<GlobalProvider>()
                            .newProcessTabIndex)!)
                    : context.watch<GlobalProvider>().newProcessTabIndex == size
                        ? const PreviewPage()
                        : context.watch<GlobalProvider>().newProcessTabIndex ==
                                size + 1
                            ? AuthenticationPage(
                                onChangeUsername: (v) {
                                  username = v;
                                },
                                onChangePassword: (v) {
                                  password = v;
                                },
                              )
                            : const PreviewPage(),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
