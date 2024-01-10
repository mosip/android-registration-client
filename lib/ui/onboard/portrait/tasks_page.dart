import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/onboard/portrait/operational_tasks.dart';
import 'package:registration_client/ui/onboard/portrait/registration_tasks.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class TasksPage extends StatefulWidget {
  const TasksPage({
    super.key,
    required this.operationalTasks,
    required this.getProcessUI,
    required this.syncData,
  });
  final List<Map<String, dynamic>> operationalTasks;
  final Function getProcessUI;
  final Function syncData;

  @override
  State<TasksPage> createState() => _TasksPageState();
}

class _TasksPageState extends State<TasksPage> {
  int currentIndex = 0;
  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          Container(
            padding: EdgeInsets.symmetric(horizontal: 20.w),
            child: Row(
              children: [
                Expanded(
                  child: InkWell(
                    onTap: () {
                      setState(() {
                        currentIndex = 0;
                      });
                    },
                    child: Container(
                      padding: EdgeInsets.symmetric(
                        vertical: 28.h,
                      ),
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: currentIndex == 0
                              ? appSolidPrimary
                              : greyBorderShade,
                        ),
                        borderRadius: const BorderRadius.only(
                          topLeft: Radius.circular(6),
                          topRight: Radius.circular(6),
                        ),
                        color: currentIndex == 0
                            ? appSolidPrimary
                            : appWhite,
                      ),
                      child: Center(
                        child: Text(
                          AppLocalizations.of(context)!.registration_tasks,
                          style: TextStyle(
                            fontSize: isMobileSize ? 14 : 24,
                            fontWeight: semiBold,
                            color: currentIndex == 0
                                ? appWhite
                                : appBlack,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
                Expanded(
                  child: InkWell(
                    onTap: () {
                      setState(() {
                        currentIndex = 1;
                      });
                    },
                    child: Container(
                      padding: EdgeInsets.symmetric(
                        vertical: 28.h,
                      ),
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: currentIndex == 1
                              ? appSolidPrimary
                              : greyBorderShade,
                        ),
                        borderRadius: const BorderRadius.only(
                          topLeft: Radius.circular(6),
                          topRight: Radius.circular(6),
                        ),
                        color: currentIndex == 1
                            ? appSolidPrimary
                            : appWhite,
                      ),
                      child: Center(
                        child: Text(
                          AppLocalizations.of(context)!.operation_tasks,
                          style: TextStyle(
                            fontSize: isMobileSize ? 14 : 24,
                            fontWeight: semiBold,
                            color: currentIndex == 1
                                ? appWhite
                                : appBlack,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          Container(
            color: appSolidPrimary,
            height: 2.5.h,
            margin: EdgeInsets.symmetric(horizontal: 20.w),
          ),
          currentIndex == 0
              ? RegistrationTasks(
                  getProcessUI: (BuildContext context, Process process) {
                    widget.getProcessUI(context, process);
                  },
                  syncData: (BuildContext context) {
                    widget.syncData(context);
                  },
                )
              : OperationalTasks(
                  operationalTasks: widget.operationalTasks,
                ),
        ],
      ),
    );
  }
}
