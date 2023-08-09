import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/platform_spi/registration.dart';

import 'package:registration_client/provider/auth_provider.dart';
import 'package:registration_client/provider/connectivity_provider.dart';

import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';

import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/ui/post_registration/acknowledgement_page.dart';

import 'package:registration_client/ui/post_registration/authentication_page.dart';
import 'package:registration_client/ui/post_registration/preview_page.dart';

import 'package:registration_client/ui/process_ui/widgets/new_process_screen_content.dart';

import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:registration_client/utils/app_style.dart';

class NewProcess extends StatefulWidget {
  NewProcess({super.key});

  static const routeName = '/new_process';

  @override
  State<NewProcess> createState() => _NewProcessState();
}

class _NewProcessState extends State<NewProcess> {
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

  _submitRegistration(BuildContext context) async {
    String regId = await context
        .read<RegistrationTaskProvider>()
        .submitRegistrationDto(username);

    bool isRegistrationSaved =
        context.read<RegistrationTaskProvider>().isRegistrationSaved;

    return regId;
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

  _onTabBackNavigate(int index, BuildContext context) {
    if (index < context.read<GlobalProvider>().newProcessTabIndex) {
      context.read<GlobalProvider>().newProcessTabIndex = index;
    }
  }

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    Map<String, dynamic> arguments =
        ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    final Process newProcess = arguments["process"];
    int size = newProcess.screens!.length;
    evaluateMVEL(
        String fieldData, String? engine, String? expression, Field e) async {
      final Registration registration = Registration();
      bool required = await registration.evaluateMVEL(fieldData, expression!);
      return required;
    }

    customValidation(int currentIndex) async {
      bool isValid = true;
      Screen screen = newProcess.screens!.elementAt(currentIndex)!;
      for (int i = 0; i < screen.fields!.length; i++) {
        if (screen.fields!.elementAt(i)!.inputRequired! &&
            screen.fields!.elementAt(i)!.required!) {
          if (!(context
              .read<GlobalProvider>()
              .fieldInputValue
              .containsKey(screen.fields!.elementAt(i)!.id))) {
            isValid = false;
            print("i: ${i}");
            print("id: ${screen.fields!.elementAt(i)!.id}");
            break;
          }
        }
        if (screen.fields!.elementAt(i)!.requiredOn!.isNotEmpty) {
          bool required = await evaluateMVEL(
              jsonEncode(screen.fields!.elementAt(i)!.toJson()),
              screen.fields!.elementAt(i)!.requiredOn?[0]?.engine,
              screen.fields!.elementAt(i)!.requiredOn?[0]?.expr,
              screen.fields!.elementAt(i)!);
          if (required) {
            if (screen.fields!.elementAt(i)!.inputRequired!) {
              if (!(context
                  .read<GlobalProvider>()
                  .fieldInputValue
                  .containsKey(screen.fields!.elementAt(i)!.id))) {
                isValid = false;
                print("i: ${i}");
                break;
              }
            }
          }
        }
      }
      print("CUSTOM VALIDATION : ${isValid}");
      return isValid;
    }

    _continueButtonTap(BuildContext context, int size, newProcess) async {
      if (context.read<GlobalProvider>().newProcessTabIndex < size) {
        if (context.read<GlobalProvider>().formKey.currentState!.validate()) {
          bool customValidator = await customValidation(
              context.read<GlobalProvider>().newProcessTabIndex);
          if (customValidator) {
            if (context.read<GlobalProvider>().newProcessTabIndex ==
                newProcess.screens!.length - 1) {
              context.read<RegistrationTaskProvider>().setPreviewTemplate("");
              context.read<RegistrationTaskProvider>().getPreviewTemplate(true);
            }

            context.read<GlobalProvider>().newProcessTabIndex =
                context.read<GlobalProvider>().newProcessTabIndex + 1;
          }
        }
      } else {
        if (context.read<GlobalProvider>().newProcessTabIndex == size + 1) {
          bool isPacketAuthenticated = await _authenticatePacket(context);
          if (!isPacketAuthenticated) {
            return;
          }
          String regId = await _submitRegistration(context);
          if (regId.isEmpty) {
            _showInSnackBar("Registration save failed!", context);
            return;
          }
          context.read<GlobalProvider>().setRegId(regId);
          setState(() {
            username = '';
            password = '';
          });
        }
        if (context.read<GlobalProvider>().newProcessTabIndex == size + 2) {
          Navigator.of(context).pop();
          context.read<GlobalProvider>().newProcessTabIndex = 0;
          context.read<GlobalProvider>().htmlBoxTabIndex = 0;
          context.read<GlobalProvider>().setRegId("");
          return;
        }
        context.read<GlobalProvider>().newProcessTabIndex =
            context.read<GlobalProvider>().newProcessTabIndex + 1;
      }
    }

    return Scaffold(
      backgroundColor: secondaryColors.elementAt(10),
      bottomNavigationBar: Container(
        color: pure_white,
        padding: EdgeInsets.symmetric(
          horizontal: 80.w,
          vertical: 16.h,
        ),
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
                onPressed: () {
                  _continueButtonTap(context, size, newProcess);
                },
              )
            : Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  context.read<GlobalProvider>().newProcessTabIndex == size + 2
                      ? ElevatedButton(
                          onPressed: () {
                            context.read<GlobalProvider>().syncPacket(
                                context.read<GlobalProvider>().regId);
                          },
                          child: const Text("Sync Packet"))
                      : const SizedBox.shrink(),
                  SizedBox(
                    width: 10.w,
                  ),
                  context.read<GlobalProvider>().newProcessTabIndex == size + 2
                      ? ElevatedButton(
                          onPressed: () {
                            context.read<GlobalProvider>().uploadPacket(
                                context.read<GlobalProvider>().regId);
                          },
                          child: const Text("Upload Packet"))
                      : const SizedBox.shrink(),
                  const Expanded(
                    child: SizedBox(),
                  ),
                  ElevatedButton(
                    style: ButtonStyle(
                      maximumSize:
                          MaterialStateProperty.all<Size>(const Size(209, 52)),
                      minimumSize:
                          MaterialStateProperty.all<Size>(const Size(209, 52)),
                    ),
                    onPressed: () {
                      _continueButtonTap(context, size, newProcess);
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
                  ),
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
                  ? const SizedBox()
                  : Column(
                      children: [
                        TabletHeader(),
                        TabletNavbar(),
                      ],
                    ),
              Container(
                padding: isMobile
                    ? const EdgeInsets.fromLTRB(0, 46, 0, 0)
                    : const EdgeInsets.fromLTRB(0, 0, 0, 0),
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
                          ? const EdgeInsets.fromLTRB(16, 0, 0, 0)
                          : const EdgeInsets.fromLTRB(60, 0, 60, 0),
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
                          ? const EdgeInsets.all(0)
                          : const EdgeInsets.fromLTRB(60, 0, 60, 0),
                      child: Stack(
                        alignment: FractionalOffset.centerRight,
                        children: [
                          Padding(
                            padding: const EdgeInsets.fromLTRB(16, 10, 0, 0),
                            child: SizedBox(
                              height: 36.h,
                              child: ListView.builder(
                                  padding: const EdgeInsets.all(0),
                                  scrollDirection: Axis.horizontal,
                                  itemCount: newProcess.screens!.length + 3,
                                  itemBuilder:
                                      (BuildContext context, int index) {
                                    return GestureDetector(
                                      onTap: () {
                                        if (context
                                                .read<GlobalProvider>()
                                                .newProcessTabIndex ==
                                            size + 2) {
                                          return;
                                        }

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
                                            padding: const EdgeInsets.fromLTRB(
                                                0, 0, 0, 8),
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
                            padding: const EdgeInsets.fromLTRB(0, 0, 0, 0),
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
                    ? const EdgeInsets.all(0)
                    : const EdgeInsets.fromLTRB(60, 0, 60, 0),
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
                            ? _getPacketAuthComponent()
                            : const AcknowledgementPage(),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _getPacketAuthComponent() {
    return Column(
      children: [
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
    );
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
