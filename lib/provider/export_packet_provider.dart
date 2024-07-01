
import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:registration_client/model/registration.dart';
import 'package:registration_client/platform_android/packet_service_impl.dart';
import 'package:registration_client/utils/constants.dart';

import '../platform_spi/packet_service.dart';

class ExportPacketsProvider with ChangeNotifier {

  List<Registration> packetsList = [];

  List<Registration> matchingPackets = [];

  List<bool> matchingSelected = [];

  String? clientStatus;
  String? serverStatus;
  String searchList = "";
  int countSelected  = 0;

  final PacketService packetService = PacketService();

  ExportPacketsProvider(){
    getPackets();
  }

  void setSearchList(String value){
    searchList = value;
    notifyListeners();
  }

  void getPackets()  async {
    List<String?> allPackets = await PacketServiceImpl().getAllRegistrationPacket();

    // Getting all packets
    for (var element in allPackets) {
      packetsList.add(Registration.fromJson(json.decode(element?? "")));
      matchingSelected.add(false);
    }
    // Setting matching packets
    for (var element in packetsList) {
      matchingPackets.add(element);
    }

    log("GOT ALL PACKETS");
    notifyListeners();
  }

  void setSelectedAll(value){
    for(int i=0; i<matchingSelected.length; i++){
      matchingSelected[i] = value;
    }
    if(value){
      countSelected = matchingPackets.length;
    }else{
      countSelected = 0;
    }
    notifyListeners();
  }

  void changeClientStatus(value){
    clientStatus = value;
    notifyListeners();
  }

  void changeServerStatus(value){
    serverStatus = value;
    notifyListeners();
  }

  void setSelected(index, value){
    if(value){
      countSelected += 1;
    }else{
      countSelected -= 1;
    }
    matchingSelected[index] = value;
    notifyListeners();
  }

  void searchedList(){
    matchingPackets.clear();
    for(int i=0;i<packetsList.length; i++){
      matchingSelected[i] = false;
      if(packetsList[i].packetId.contains(searchList)){
        if(clientStatus!=null && clientStatus != packetsList[i].clientStatus){
          continue;
        }
        if(serverStatus!=null && serverStatus != packetsList[i].serverStatus){
          continue;
        }
        matchingPackets.add(packetsList[i]);
      }
    }
    countSelected = 0;
    notifyListeners();
  }

  void filterSearchList(){
    matchingPackets.clear();
    for(int i=0;i<packetsList.length; i++){
      matchingSelected[i] = false;
      if(packetsList[i].packetId.contains(searchList)){
        if(clientStatus!=null && clientStatus != packetsList[i].clientStatus){
          continue;
        }
        if(serverStatus!=null && serverStatus != packetsList[i].serverStatus){
          continue;
        }
        matchingPackets.add(packetsList[i]);
      }
    }
    countSelected = 0;
    notifyListeners();
  }

  void uploadSelected() async {
    List<String> toBeUploaded = [];
    List<String> uploadStatus = [ClientStatus.SYNCED.name, ClientStatus.EXPORTED.name,];

    for(int i=0; i<matchingPackets.length; i++){
      if(matchingSelected[i]){
        if( uploadStatus.contains(matchingPackets[i].clientStatus) ){
          toBeUploaded.add(matchingPackets[i].packetId);
        }
      }
    }

    log("$toBeUploaded : TO BE UPLOADED");
    if(toBeUploaded.isNotEmpty){
      await packetService.packetUploadAll(toBeUploaded);
    }

    await refreshPackets();
    notifyListeners();
  }

  void exportSelected() async{
    List<File> sourceFiles = [];
    List<String> toBeExported = [];
    List<String> exportStatus = [ ClientStatus.APPROVED.name, ClientStatus.SYNCED.name, ClientStatus.EXPORTED.name, ];
    for(int i=0;i<matchingPackets.length;i++){
      if(matchingSelected[i]){
        if(exportStatus.contains(matchingPackets[i].clientStatus)){
          File newFile = File(matchingPackets[i].filePath??"");
          sourceFiles.add(newFile);
          toBeExported.add(matchingPackets[i].packetId);
        }
      }
    }

    if(sourceFiles.isEmpty){
      for(int i=0;i<matchingPackets.length;i++){
        if(exportStatus.contains(matchingPackets[i].clientStatus)) {
          File newFile = File(matchingPackets[i].filePath ?? "");
          sourceFiles.add(newFile);
          toBeExported.add(matchingPackets[i].packetId);
        }
      }
    }

    log("$toBeExported : TO BE EXPORTED");
    if(toBeExported.isNotEmpty){
      String? selectedDirectory = await FilePicker.platform.getDirectoryPath();
      if (selectedDirectory != null) {
        for (int i=0;i<sourceFiles.length;i++) {
          final fileName = sourceFiles[i].path
              .split('/')
              .last;
          final destinationPath = '$selectedDirectory/$fileName';
          try{
            sourceFiles[i].copySync(destinationPath);
            packetService.updatePacketStatus(toBeExported[i], null, ClientStatus.EXPORTED.name);
            log("Files Exported");
          }catch(e){
            log(e.toString());
          }
        }
      }
    }

    await refreshPackets();
    notifyListeners();
  }

  Future<void> packetSyncAll() async {
    List<String> syncStatus = [ClientStatus.APPROVED.name,ClientStatus.EXPORTED.name,];
    List<String> toBeSynced = [];
    for(int i=0; i<matchingPackets.length; i++){
        if(syncStatus.contains(matchingPackets[i].clientStatus)){
          toBeSynced.add(matchingPackets[i].packetId);
        }
    }

    log("$toBeSynced : TO BE SYNCED");
    if(toBeSynced.isNotEmpty){
      await packetService.packetSyncAll(toBeSynced);
    }

    await refreshPackets();
    notifyListeners();
  }

  Future<void> refreshPackets()  async{
    List<String?> allPackets = await PacketServiceImpl().getAllRegistrationPacket();
    packetsList.clear();

    for (var element in allPackets) {
      Registration newRegistration = Registration.fromJson(json.decode(element?? ""));
      packetsList.add(newRegistration);
      for(int i=0;i<matchingPackets.length;i++){
        if(newRegistration.packetId == matchingPackets[i].packetId){
          matchingPackets[i] = newRegistration;
        }
      }
    }

    log("REFRESHED ALL PACKETS");
    notifyListeners();
  }

}