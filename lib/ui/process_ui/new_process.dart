import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';

import 'package:registration_client/ui/process_ui/widgets/new_process_screen_content.dart';

import 'package:registration_client/utils/app_config.dart';

class NewProcess extends StatelessWidget {
  const NewProcess({super.key});

  static const routeName = '/new_process';

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    Map<String, dynamic> arguments =
        ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    final Process newProcess = arguments["process"];
    return Scaffold(
      backgroundColor: secondaryColors.elementAt(10),
      bottomNavigationBar: Container(
        color: pure_white,
        padding: EdgeInsets.all(16),
        height: 84.h,
        child: isMobile
            ? ElevatedButton(
                child: Text("CONTINUE"),
                onPressed: () {
                  if (context.read<GlobalProvider>().newProcessTabIndex <
                      newProcess.screens!.length - 1) {
                    context.read<GlobalProvider>().newProcessTabIndex =
                        context.read<GlobalProvider>().newProcessTabIndex + 1;
                  }
                },
              )
            : Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  ElevatedButton(
                    child: Text("CONTINUE"),
                    style: ButtonStyle(
                      maximumSize:
                          MaterialStateProperty.all<Size>(Size(209, 52)),
                      minimumSize:
                          MaterialStateProperty.all<Size>(Size(209, 52)),
                    ),
                    onPressed: () {
                      if (context.read<GlobalProvider>().newProcessTabIndex <
                          newProcess.screens!.length - 1) {
                        context.read<GlobalProvider>().newProcessTabIndex =
                            context.read<GlobalProvider>().newProcessTabIndex +
                                1;
                      }
                    },
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
                      children: [
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
                                  itemCount: newProcess.screens!.length,
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
                                                  newProcess.screens![index]!
                                                      .label!["eng"]!,
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
                    SizedBox(
                      height: 5,
                    ),
                  ],
                ),
              ),
              Padding(
                padding: isMobile
                    ? EdgeInsets.all(0)
                    : EdgeInsets.fromLTRB(60, 0, 60, 0),
                child: NewProcessScreenContent(
                    context: context,
                    screen: newProcess.screens!.elementAt(
                        context.watch<GlobalProvider>().newProcessTabIndex)!),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
