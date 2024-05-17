import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dash_board_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class UserDashBoard extends StatefulWidget {
  const UserDashBoard({super.key});

  @override
  State<UserDashBoard> createState() => _UserDashBoardState();
}

class _UserDashBoardState extends State<UserDashBoard> {
  late AppLocalizations appLocalizations = AppLocalizations.of(context)!;

  Future<List<DashBoardData?>> _getDashBoardList() async {
    return await context.read<RegistrationTaskProvider>().getDashBoardDetails();
  }

  Future<int> _getPacketUploadPendingList() async {
    return await context
        .read<RegistrationTaskProvider>()
        .getPacketUploadedPendingDetails();
  }

  Future<int> _getCreatedPacketList() async {
    return await context
        .read<RegistrationTaskProvider>()
        .getCreatedPacketDetails();
  }

  Future<int> _getSyncedPacketList() async {
    return await context
        .read<RegistrationTaskProvider>()
        .getSyncedPacketDetails();
  }

  Future<int> _getPacketUploadedList() async {
    return await context
        .read<RegistrationTaskProvider>()
        .getPacketUploadedDetails();
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              height: isMobileSize ? 200 : 246,
              width: MediaQuery.of(context).size.width,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [appBlueShade1, solidPrimary],
                ),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: isMobileSize
                        ? const EdgeInsets.symmetric(horizontal: 10)
                        : const EdgeInsets.symmetric(horizontal: 40),
                    child: Text(
                      appLocalizations.dashboard,
                      style: TextStyle(
                          fontSize: isMobileSize ? 18 : 24,
                          fontWeight: FontWeight.bold,
                          color: Colors.white),
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      FutureBuilder(
                          future: _getCreatedPacketList(),
                          builder: (BuildContext context,
                              AsyncSnapshot<int> snapshot) {
                            return Container(
                              height: isMobileSize ? 100 : 160,
                              width: MediaQuery.of(context).size.width / 3.4,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(6),
                                color: Colors.white,
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Icon(
                                        CupertinoIcons.cube,
                                        size: isMobileSize ? 25 : 35,
                                        color:
                                        dashBoardPacketUploadPendingColor,
                                      ),
                                      const SizedBox(width: 5),
                                      Text(
                                        snapshot.hasData
                                            ? snapshot.data
                                            .toString()
                                            .padLeft(2, '0')
                                            : "00",
                                        style: TextStyle(
                                            fontSize: isMobileSize ? 25 : 35,
                                            fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  const SizedBox(height: 10),
                                  Text(appLocalizations.packets_created,
                                      style: TextStyle(
                                          fontSize: isMobileSize ? 15 : 20,
                                          fontWeight: FontWeight.bold)),
                                ],
                              ),
                            );
                          }),
                      FutureBuilder(
                          future: _getSyncedPacketList(),
                          builder: (BuildContext context,
                              AsyncSnapshot<int> snapshot) {
                            return Container(
                              height: isMobileSize ? 100 : 160,
                              width: MediaQuery.of(context).size.width / 3.4,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(6),
                                color: Colors.white,
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Icon(
                                        CupertinoIcons.cube,
                                        size: isMobileSize ? 25 : 35,
                                        color:
                                        dashBoardPacketUploadPendingColor,
                                      ),
                                      const SizedBox(width: 5),
                                      Text(
                                        snapshot.hasData
                                            ? snapshot.data
                                            .toString()
                                            .padLeft(2, '0')
                                            : "00",
                                        style: TextStyle(
                                            fontSize: isMobileSize ? 25 : 35,
                                            fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  const SizedBox(height: 10),
                                  Text(appLocalizations.packets_synced,
                                      style: TextStyle(
                                          fontSize: isMobileSize ? 15 : 20,
                                          fontWeight: FontWeight.bold)),
                                ],
                              ),
                            );
                          }),
                      FutureBuilder(
                          future: _getPacketUploadedList(),
                          builder: (BuildContext context,
                              AsyncSnapshot<int> snapshot) {
                            return Container(
                              height: isMobileSize ? 100 : 160,
                              width: MediaQuery.of(context).size.width / 3.4,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(6),
                                color: Colors.white,
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Icon(
                                        CupertinoIcons.cube,
                                        size: isMobileSize ? 25 : 35,
                                        color: dashBoardPacketUploadColor,
                                      ),
                                      const SizedBox(width: 5),
                                      Text(
                                        snapshot.hasData
                                            ? snapshot.data
                                                .toString()
                                                .padLeft(2, '0')
                                            : "00",
                                        style: TextStyle(
                                            fontSize: isMobileSize ? 25 : 35,
                                            fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  const SizedBox(height: 10),
                                  Text(appLocalizations.packets_uploaded,
                                      style: TextStyle(
                                          fontSize: isMobileSize ? 15 : 20,
                                          fontWeight: FontWeight.bold)),
                                ],
                              ),
                            );
                          }),
                      /*FutureBuilder(
                          future: _getPacketUploadPendingList(),
                          builder: (BuildContext context,
                              AsyncSnapshot<int> snapshot) {
                            return Container(
                              height: isMobileSize ? 100 : 160,
                              width: MediaQuery.of(context).size.width / 3.4,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(6),
                                color: Colors.white,
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Icon(
                                        CupertinoIcons.cube,
                                        size: isMobileSize ? 25 : 35,
                                        color:
                                            dashBoardPacketUploadPendingColor,
                                      ),
                                      const SizedBox(width: 5),
                                      Text(
                                        snapshot.hasData
                                            ? snapshot.data
                                                .toString()
                                                .padLeft(2, '0')
                                            : "00",
                                        style: TextStyle(
                                            fontSize: isMobileSize ? 25 : 35,
                                            fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  const SizedBox(height: 10),
                                  Text('Packets Upload Pending',
                                      style: TextStyle(
                                          fontSize: isMobileSize ? 15 : 20,
                                          fontWeight: FontWeight.bold)),
                                  // const SizedBox(height: 8),
                                  // Text("Last one month data",
                                  //     style: TextStyle(
                                  //         fontSize: isMobileSize ? 12 : 16,
                                  //         fontWeight: FontWeight.w500,
                                  //         color: appBlackShade3)),
                                ],
                              ),
                            );
                          }),*/
                    ],
                  )
                ],
              ),
            ),
            Padding(
              padding: isMobileSize
                  ? const EdgeInsets.symmetric(horizontal: 10, vertical: 10)
                  : const EdgeInsets.symmetric(horizontal: 40, vertical: 20),
              child: Text(
                appLocalizations.users,
                style: TextStyle(
                    fontSize: isMobileSize ? 18 : 24,
                    fontWeight: FontWeight.bold),
              ),
            ),
            FutureBuilder(
                future: _getDashBoardList(),
                builder: (BuildContext context,
                    AsyncSnapshot<List<DashBoardData?>> snapshot) {
                  return Card(
                    elevation: 3,
                    shadowColor: appBlueShade,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                    margin: isMobileSize
                        ? const EdgeInsets.only(left: 10, right: 10, bottom: 10)
                        : const EdgeInsets.only(
                            left: 40, right: 40, bottom: 20),
                    child: SizedBox(
                      width: MediaQuery.of(context).size.width,
                      child: snapshot.hasData
                          ? DataTable(
                              dividerThickness: 2,
                              headingRowHeight: 60,
                              columns: [
                                DataColumn(
                                    label: Text(appLocalizations.user_id,
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: isMobileSize ? 12 : 20,
                                            color: appBlackShade2))),
                                DataColumn(
                                    label: Text(appLocalizations.user_name,
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: isMobileSize ? 12 : 20,
                                            color: appBlackShade2))),
                                DataColumn(
                                    label: Text(appLocalizations.status,
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: isMobileSize ? 12 : 20,
                                            color: appBlackShade2))),
                              ],
                              rows: snapshot.data!
                                  .map<DataRow>((data) => DataRow(cells: [
                                        DataCell(Text(data!.userId.toString(),
                                            style: TextStyle(
                                                fontSize:
                                                    isMobileSize ? 10 : 17,
                                                color: appBlackShade1,
                                                fontWeight: FontWeight.w500))),
                                        DataCell(Text(data.userName.toString(),
                                            style: TextStyle(
                                                fontSize:
                                                    isMobileSize ? 10 : 17,
                                                color: appBlackShade2,
                                                fontWeight: FontWeight.w500))),
                                        DataCell(statusWidget(data.userStatus,
                                            data.userIsOnboarded))
                                      ]))
                                  .toList())
                          : const SizedBox.shrink(),
                    ),
                  );
                })
          ],
        ),
      ),
    );
  }

  String dateFormat(String value) {
    DateTime dateTime = DateTime.fromMillisecondsSinceEpoch(int.parse(value));
    String formattedDate = DateFormat('dd-MMM-yyyy').format(dateTime);
    return formattedDate;
  }

  Widget statusWidget(bool isActive, bool isOnboarded) {
    if (isActive && isOnboarded) {
      return getUserOnboardedWidget();
    }
    if (!isActive && isOnboarded) {
      return getUserOnboardedWidget();
    }
    if (isActive && !isOnboarded) {
      return Row(
        children: [
          Icon(
            Icons.info,
            size: isMobileSize ? 15 : 27,
            color: dashBoardPacketUploadPendingColor,
          ),
          Text(
            " ${appLocalizations.active_not_onboarded}",
            style: TextStyle(
                fontSize: isMobileSize ? 10 : 17,
                color: appBlackShade2,
                fontWeight: FontWeight.w500),
          )
        ],
      );
    }
    if (!isActive && !isOnboarded) {
      return Row(
        children: [
          Icon(
            Icons.info,
            size: isMobileSize ? 15 : 27,
            color: appBlackShade3,
          ),
          Text(
            " ${appLocalizations.user_inactive}",
            style: TextStyle(
                fontSize: isMobileSize ? 10 : 17,
                color: appBlackShade2,
                fontWeight: FontWeight.w500),
          )
        ],
      );
    }
    return Container();
  }

  Widget getUserOnboardedWidget(){
    return Row(
      children: [
        Icon(
          CupertinoIcons.checkmark_alt_circle_fill,
          size: isMobileSize ? 15 : 27,
          color: dashBoardPacketUploadColor,
        ),
        Text(
          " ${appLocalizations.onboarded}",
          style: TextStyle(
              fontSize: isMobileSize ? 10 : 17,
              color: appBlackShade2,
              fontWeight: FontWeight.w500),
        )
      ],
    );
  }
}
