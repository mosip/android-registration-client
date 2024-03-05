import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/pigeon/dash_board_pigeon.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class UserDashBoard extends StatefulWidget {
  const UserDashBoard({super.key});

  @override
  State<UserDashBoard> createState() => _UserDashBoardState();
}

class _UserDashBoardState extends State<UserDashBoard> {
  Future<List<DashBoardData?>> _getDashBoardList() async {
    return await context.read<RegistrationTaskProvider>().getDashBoardDetails();
  }

  Future<List<String?>> _getPacketUploadPendingList() async {
    return await context
        .read<RegistrationTaskProvider>()
        .getPacketUploadedPendingDetails();
  }

  Future<List<String?>> _getPacketUploadedList() async {
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
              height: 246,
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
                  const Padding(
                    padding: EdgeInsets.symmetric(horizontal: 40),
                    child: Text(
                      "DashBoard",
                      style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Colors.white),
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      FutureBuilder(
                          future: _getPacketUploadedList(),
                          builder: (BuildContext context,
                              AsyncSnapshot<List<String?>> snapshot) {
                            return Container(
                              height: 160,
                              width: MediaQuery.of(context).size.width * 0.45,
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
                                      const Icon(
                                        CupertinoIcons.cube,
                                        size: 35,
                                        color: dashBoardPacketUploadColor,
                                      ),
                                      const SizedBox(width: 5),
                                      Text(
                                        snapshot.hasData
                                            ? snapshot.data!.length
                                                .toString()
                                                .padLeft(2, '0')
                                            : "00",
                                        style: const TextStyle(
                                            fontSize: 35,
                                            fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  const SizedBox(height: 10),
                                  const Text('Packets Uploaded',
                                      style: TextStyle(
                                          fontSize: 20,
                                          fontWeight: FontWeight.bold)),
                                  const SizedBox(height: 8),
                                  if (snapshot.hasData &&
                                      snapshot.data!.isNotEmpty) ...[
                                    Text(
                                        "${dateFormat(snapshot.data!.last.toString())} To ${dateFormat(DateTime.now().millisecondsSinceEpoch.toString())}",
                                        style: const TextStyle(
                                            fontSize: 16,
                                            fontWeight: FontWeight.w500,
                                            color: appBlackShade3)),
                                  ]
                                ],
                              ),
                            );
                          }),
                      FutureBuilder(
                          future: _getPacketUploadPendingList(),
                          builder: (BuildContext context,
                              AsyncSnapshot<List<String?>> snapshot) {
                            return Container(
                              height: 160,
                              width: MediaQuery.of(context).size.width * 0.45,
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
                                      const Icon(
                                        CupertinoIcons.cube,
                                        size: 35,
                                        color:
                                            dashBoardPacketUploadPendingColor,
                                      ),
                                      const SizedBox(width: 5),
                                      Text(
                                        snapshot.hasData
                                            ? snapshot.data!.length
                                                .toString()
                                                .padLeft(2, '0')
                                            : "00",
                                        style: const TextStyle(
                                            fontSize: 35,
                                            fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  const SizedBox(height: 10),
                                  const Text('Packets Upload Pending',
                                      style: TextStyle(
                                          fontSize: 20,
                                          fontWeight: FontWeight.bold)),
                                  const SizedBox(height: 8),
                                  const Text("Last one month data",
                                      style: TextStyle(
                                          fontSize: 16,
                                          fontWeight: FontWeight.w500,
                                          color: appBlackShade3)),
                                ],
                              ),
                            );
                          }),
                    ],
                  )
                ],
              ),
            ),
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 40, vertical: 20),
              child: Text(
                "Users",
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
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
                    margin: const EdgeInsets.only(
                        left: 40, right: 40, bottom: 20),
                    child: SizedBox(
                      width: MediaQuery.of(context).size.width,
                      child: snapshot.hasData
                          ? DataTable(
                              dividerThickness: 2,
                              headingRowHeight: 60,
                              columns: const [
                                DataColumn(
                                    label: Text('User ID',
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: 20,
                                            color: appBlackShade2))),
                                DataColumn(
                                    label: Text('User Name',
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: 20,
                                            color: appBlackShade2))),
                                DataColumn(
                                    label: Text('Status',
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            fontSize: 20,
                                            color: appBlackShade2))),
                              ],
                              rows: snapshot.data!
                                  .map<DataRow>((data) => DataRow(cells: [
                                        DataCell(Text(data!.userId.toString(),
                                            style: const TextStyle(
                                                fontSize: 17,
                                                color: appBlackShade1,
                                                fontWeight: FontWeight.w500))),
                                        DataCell(Text(data.userName.toString(),
                                            style: const TextStyle(
                                                fontSize: 17,
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
      return const Row(
        children: [
          Icon(
            CupertinoIcons.checkmark_alt_circle_fill,
            size: 27,
            color: dashBoardPacketUploadColor,
          ),
          Text(
            " Onboarded",
            style: TextStyle(
                fontSize: 17,
                color: appBlackShade2,
                fontWeight: FontWeight.w500),
          )
        ],
      );
    }
    if (!isActive && isOnboarded) {
      return const Row(
        children: [
          Icon(
            CupertinoIcons.checkmark_alt_circle_fill,
            size: 27,
            color: dashBoardPacketUploadColor,
          ),
          Text(
            " Onboarded",
            style: TextStyle(
                fontSize: 17,
                color: appBlackShade2,
                fontWeight: FontWeight.w500),
          )
        ],
      );
    }
    if (isActive && !isOnboarded) {
      return const Row(
        children: [
          Icon(
            Icons.info,
            size: 27,
            color: dashBoardPacketUploadPendingColor,
          ),
          Text(
            " Active - Not Onboarded",
            style: TextStyle(
                fontSize: 17,
                color: appBlackShade2,
                fontWeight: FontWeight.w500),
          )
        ],
      );
    }
    if (!isActive && !isOnboarded) {
      return const Row(
        children: [
          Icon(
            Icons.info,
            size: 27,
            color: appBlackShade3,
          ),
          Text(
            " User Inactive",
            style: TextStyle(
                fontSize: 17,
                color: appBlackShade2,
                fontWeight: FontWeight.w500),
          )
        ],
      );
    }
    return Container();
  }
}
