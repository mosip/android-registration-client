import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:registration_client/model/registration.dart';
import 'package:registration_client/platform_android/packet_service_impl.dart';
import 'package:registration_client/utils/constants.dart';
import 'package:webview_flutter_plus/webview_flutter_plus.dart';

import '../platform_android/sync_response_service_impl.dart';
import '../platform_spi/packet_service.dart';

class ApprovePacketsProvider with ChangeNotifier {
  final storage = const FlutterSecureStorage();

  List<Map<String, Object>> packetsList = [];
  List<Map<String, Object>> matchingPackets = [];
  List<bool> matchingSelected = [];
  String searchList = "";
  int countSelected = 0;
  int currentInd = 1;
  int totalCreatedPackets = 0;
  WebViewPlusController? webViewPlusController;
  List<String?> reasonList = [];
  String? selectedReason;
  bool rejectError = false;

  void getAllReasonList(String langCode) async {
    List<String?> responseReasonList =
        await SyncResponseServiceImpl().getReasonList(langCode);
    reasonList = responseReasonList;
    log(reasonList.toString());
    notifyListeners();
  }

  void showRejectError() {
    rejectError = true;
    notifyListeners();
    Future.delayed(const Duration(seconds: 3), () {
      rejectError = false;
      notifyListeners();
    });
  }

  void setSelectedReason(String? value) {
    selectedReason = value;
    notifyListeners();
  }

  void setCurrentInd(int value) {
    currentInd = value;
    notifyListeners();
  }

  void setWebViewPlusController(WebViewPlusController controller) {
    webViewPlusController = controller;
    notifyListeners();
  }

  final PacketService packetService = PacketService();

  void setSearchList(String value) {
    searchList = value;
    notifyListeners();
  }

  void getTotalCreatedPackets() async {
    List<String?> allPackets =
        await PacketServiceImpl().getAllCreatedRegistrationPacket();
    totalCreatedPackets = allPackets.length;
    notifyListeners();
  }

  void getPackets() async {
    List<Map<String, Object>> oldPackets = List.from(packetsList);
    packetsList.clear();
    matchingSelected.clear();
    matchingPackets.clear();
    countSelected = 0;
    searchList = "";

    List<String?> allPackets =
        await PacketServiceImpl().getAllCreatedRegistrationPacket();
    totalCreatedPackets = allPackets.length;

    // Getting all packets
    for (var element in allPackets) {
      Registration reg = Registration.fromJson(json.decode(element ?? ""));
      String review = ReviewStatus.NOACTIONTAKEN.name;
      String reviewComment = "";
      for (var oldPacket in oldPackets) {
        Registration oldReg = oldPacket['packet'] as Registration;
        if (reg.packetId == oldReg.packetId) {
          review = oldPacket['review_status'] as String;
          reviewComment = oldPacket['review_comment'] as String;
        }
      }
      packetsList.add({
        "packet": reg,
        "review_status": review,
        "review_comment": reviewComment
      });
    }
    // Setting matching packets
    for (var element in packetsList) {
      matchingSelected.add(false);
      matchingPackets.add(element);
    }

    log("GOT ALL PACKETS : $matchingPackets");
    notifyListeners();
  }

  void setSelectedAll(value) {
    for (int i = 0; i < matchingSelected.length; i++) {
      if (packetsList[i]["review_status"] == ReviewStatus.NOACTIONTAKEN.name) {
        continue;
      }
      if (matchingSelected[i] != value) {
        if (value) {
          countSelected += 1;
        } else {
          countSelected -= 1;
        }
      }
      matchingSelected[i] = value;
    }
    notifyListeners();
  }

  void setSelected(index, value) {
    if (packetsList[index]["review_status"] ==
        ReviewStatus.NOACTIONTAKEN.name) {
      return;
    }
    if (value) {
      countSelected += 1;
    } else {
      countSelected -= 1;
    }
    matchingSelected[index] = value;
    notifyListeners();
  }

  void searchedList() {
    matchingPackets.clear();
    for (int i = 0; i < packetsList.length; i++) {
      matchingSelected[i] = false;
      Registration reg = packetsList[i]["packet"] as Registration;
      if (reg.packetId.contains(searchList)) {
        matchingPackets.add(packetsList[i]);
      }
    }
    countSelected = 0;
    notifyListeners();
  }

  Future<void> submitSelected() async {
    for (int i = 0; i < packetsList.length; i++) {
      if (matchingSelected[i]) {
        Registration regPacket = packetsList[i]["packet"] as Registration;
        String regReview = packetsList[i]["review_status"] as String;
        String regComment = packetsList[i]["review_comment"] as String;
        if (regReview != ReviewStatus.NOACTIONTAKEN.name) {
          await packetService.supervisorReview(
              regPacket.packetId, regReview, regComment);
          await storage.delete(key: regPacket.packetId);
        }
      }
    }
    log("Submitted");
    getPackets();
    notifyListeners();
  }

  Future<void> rejectPacket(String packetId) async {
    for (int i = 0; i < packetsList.length; i++) {
      Registration reg = packetsList[i]["packet"] as Registration;
      if (reg.packetId == packetId) {
        packetsList[i] = {
          "packet": reg,
          "review_status": ReviewStatus.REJECTED.name,
          "review_comment": selectedReason ?? "Rejected",
        };
        matchingPackets[i] = {
          "packet": reg,
          "review_status": ReviewStatus.REJECTED.name,
          "review_comment": selectedReason ?? "Rejected",
        };
      }
    }
    selectedReason = null;
    log("rejected");
    notifyListeners();
  }

  Future<void> approvePacket(String packetId) async {
    for (int i = 0; i < packetsList.length; i++) {
      Registration reg = packetsList[i]["packet"] as Registration;
      if (reg.packetId == packetId) {
        packetsList[i] = {
          "packet": reg,
          "review_status": ReviewStatus.APPROVED.name,
          "review_comment": "Approved",
        };
        matchingPackets[i] = {
          "packet": reg,
          "review_status": ReviewStatus.APPROVED.name,
          "review_comment": "Approved",
        };
      }
    }
    log("Approved");
    notifyListeners();
  }

  void clearReview(String packetId) {
    for (int i = 0; i < packetsList.length; i++) {
      Registration reg = packetsList[i]["packet"] as Registration;
      if (reg.packetId == packetId) {
        packetsList[i] = {
          "packet": reg,
          "review_status": ReviewStatus.NOACTIONTAKEN.name,
          "review_comment": "",
        };
        matchingPackets[i] = {
          "packet": reg,
          "review_status": ReviewStatus.NOACTIONTAKEN.name,
          "review_comment": "",
        };
      }
    }
    notifyListeners();
  }
}
