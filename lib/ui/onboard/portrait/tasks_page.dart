import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/ui/onboard/portrait/operational_tasks.dart';
import 'package:registration_client/ui/onboard/portrait/registration_tasks.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class TasksPage extends StatefulWidget {
  const TasksPage({
    super.key,
    required this.operationalTasks,
    required this.getProcessUI,
  });
  final List<Map<String, dynamic>> operationalTasks;
  final Function getProcessUI;

  @override
  State<TasksPage> createState() => _TasksPageState();
}

class _TasksPageState extends State<TasksPage> {
  int currentIndex = 0;
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          children: [
            SizedBox(
              width: 20.w,
            ),
            Expanded(
              child: InkWell(
                onTap: () {
                  setState(() {
                    currentIndex = 0;
                  });
                },
                child: Container(
                  height: 84.h,
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: currentIndex == 0
                          ? AppStyle.appSolidPrimary
                          : AppStyle.greyBorderShade,
                    ),
                    borderRadius: const BorderRadius.only(
                      topLeft: Radius.circular(6),
                      topRight: Radius.circular(6),
                    ),
                    color: currentIndex == 0
                        ? AppStyle.appSolidPrimary
                        : AppStyle.appWhite,
                  ),
                  child: Center(
                    child: Text(
                      'Registration Tasks',
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: semiBold,
                        color: currentIndex == 0
                            ? AppStyle.appWhite
                            : AppStyle.appBlack,
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
                  height: 84.h,
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: currentIndex == 1
                          ? AppStyle.appSolidPrimary
                          : AppStyle.greyBorderShade,
                    ),
                    borderRadius: const BorderRadius.only(
                      topLeft: Radius.circular(6),
                      topRight: Radius.circular(6),
                    ),
                    color: currentIndex == 1
                        ? AppStyle.appSolidPrimary
                        : AppStyle.appWhite,
                  ),
                  child: Center(
                    child: Text(
                      'Operational Tasks',
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: semiBold,
                        color: currentIndex == 1
                            ? AppStyle.appWhite
                            : AppStyle.appBlack,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            SizedBox(
              width: 20.w,
            ),
          ],
        ),
        Container(
          color: AppStyle.appSolidPrimary,
          height: 2.5.h,
          margin: EdgeInsets.symmetric(horizontal: 20.w),
        ),
        currentIndex == 0
            ? RegistrationTasks(
                getProcessUI: (BuildContext context, Process process) {
                  widget.getProcessUI(context, process);
                },
              )
            : OperationalTasks(
                operationalTasks: widget.operationalTasks,
              ),
      ],
    );
  }
}
