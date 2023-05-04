/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/data/models/screen.dart';
import 'package:registration_client/provider/global_provider.dart';

import 'package:provider/provider.dart';

final Color secondaryColor = Colors.blue;

final Color primaryColor = Colors.blue;
final specificJsonData = {
  "fields": [
    {
      "id": "fullName",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "Full Name",
      "label": {
        "ara": "الاسم الكامل",
        "fra": "Nom complet",
        "eng": "Full Name"
      },
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{3,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": null,
      "visible": null,
      "contactType": null,
      "group": "FullName",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "name"
    },
    {
      "id": "dateOfBirth",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "dateOfBirth",
      "label": {"ara": "الاسم الكامل", "fra": "DOB", "eng": "DOB"},
      "controlType": "ageDate",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator":
              "^(1869|18[7-9][0-9]|19[0-9][0-9]|20[0-9][0-9])/([0][1-9]|1[0-2])/([0][1-9]|[1-2][0-9]|3[01])\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": null,
      "visible": null,
      "contactType": null,
      "group": "DateOfBirth",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "dateOfBirth"
    },
    {
      "id": "fullName",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "Tester Custom",
      "label": {
        "ara": "الاسم الكامل",
        "fra": "Nom complet",
        "eng": "Full Name"
      },
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{3,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": null,
      "visible": null,
      "contactType": null,
      "group": "FullName",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "name"
    },
    {
      "id": "gender",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "gender",
      "label": {"ara": "جنس", "fra": "Le genre", "eng": "Gender"},
      "controlType": "button",
      "fieldType": "dynamic",
      "format": "",
      "validators": [],
      "fieldCategory": "pvt",
      "alignmentGroup": "group1",
      "visible": null,
      "contactType": null,
      "group": "Gender",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "gender"
    },
    {
      "id": "addressLine1",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "addressLine1",
      "label": {"ara": "الاسم الكامل", "fra": "line1", "eng": "line1"},
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{0,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "address",
      "visible": null,
      "contactType": "Postal",
      "group": "Address",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "addressLine1"
    },
    {
      "id": "addressLine2",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "addressLine2",
      "label": {"ara": "الاسم الكامل", "fra": "line2", "eng": "line2"},
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{3,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "address",
      "visible": null,
      "contactType": "Postal",
      "group": "Address",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "addressLine2"
    },
    {
      "id": "addressLine3",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "addressLine3",
      "label": {"ara": "الاسم الكامل", "fra": "line3", "eng": "line3"},
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{3,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "address",
      "visible": null,
      "contactType": "Postal",
      "group": "Address",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "addressLine3"
    },
    {
      "id": "residenceStatus",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "residenceStatus",
      "label": {
        "ara": "الاسم الكامل",
        "fra": "Reside Status",
        "eng": "Residence Status"
      },
      "controlType": "button",
      "fieldType": "dynamic",
      "format": "none",
      "validators": [],
      "fieldCategory": "kyc",
      "alignmentGroup": "group1",
      "visible": null,
      "contactType": null,
      "group": "ResidenceStatus",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": false,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "residenceStatus"
    },
    {
      "id": "referenceIdentityNumber",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "referenceIdentityNumber",
      "label": {
        "ara": "الاسم الكامل",
        "fra": "Reference Identity Number",
        "eng": "Reference Identity Number"
      },
      "controlType": "textbox",
      "fieldType": "default",
      "format": "kyc",
      "validators": [
        {
          "type": "regex",
          "validator": "^([0-9]{10,30})\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": null,
      "visible": null,
      "contactType": null,
      "group": "ReferenceIdentityNumber",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": false,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "none"
    },
    {
      "id": "region",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "region",
      "label": {"ara": "الاسم الكامل", "fra": "Region", "eng": "Region"},
      "controlType": "dropdown",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{0,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "location",
      "visible": null,
      "contactType": "Postal",
      "group": "Location",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "Region"
    },
    {
      "id": "province",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "province",
      "label": {"ara": "الاسم الكامل", "fra": "Province", "eng": "Province"},
      "controlType": "dropdown",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{0,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "location",
      "visible": null,
      "contactType": "Postal",
      "group": "Location",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "Province"
    },
    {
      "id": "city",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "city",
      "label": {"ara": "الاسم الكامل", "fra": "City", "eng": "City"},
      "controlType": "dropdown",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^(?=.{0,50}\$).*",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "location",
      "visible": null,
      "contactType": "Postal",
      "group": "Location",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "City"
    },
    {
      "id": "zone",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "zone",
      "label": {"ara": "الاسم الكامل", "fra": "Zone", "eng": "Zone"},
      "controlType": "dropdown",
      "fieldType": "default",
      "format": "none",
      "validators": [],
      "fieldCategory": "pvt",
      "alignmentGroup": "location",
      "visible": null,
      "contactType": null,
      "group": "Location",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "Zone"
    },
    {
      "id": "postalCode",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "postalCode",
      "label": {"ara": "الاسم الكامل", "fra": "Postal", "eng": "Postal"},
      "controlType": "dropdown",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^[(?i)A-Z0-9]{5}\$|^NA\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "location",
      "visible": null,
      "contactType": "Postal",
      "group": "Location",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "Postal Code"
    },
    {
      "id": "phone",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "phone",
      "label": {"ara": "الاسم الكامل", "fra": "Phone", "eng": "Phone"},
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^[+]*([0-9]{1})([0-9]{9})\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "contact",
      "visible": null,
      "contactType": "email",
      "group": "Phone",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "Phone"
    },
    {
      "id": "email",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "email",
      "label": {"ara": "الاسم الكامل", "fra": "Email", "eng": "Email"},
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator":
              "^[A-Za-z0-9_\\-]+(\\.[A-Za-z0-9_]+)*@[A-Za-z0-9_-]+(\\.[A-Za-z0-9_]+)*(\\.[a-zA-Z]{2,})\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "pvt",
      "alignmentGroup": "contact",
      "visible": null,
      "contactType": "email",
      "group": "Email",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "Email"
    },
    {
      "id": "introducerName",
      "inputRequired": true,
      "type": "simpleType",
      "minimum": 0,
      "maximum": 0,
      "description": "introducerName",
      "label": {
        "ara": "اسم المُعرّف",
        "fra": "nom del'introducteur",
        "eng": "Introducer Name"
      },
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [],
      "fieldCategory": "evidence",
      "alignmentGroup": "introducer",
      "visible": {
        "engine": "MVEL",
        "expr": "identity.get('ageGroup') == 'INFANT'"
      },
      "contactType": null,
      "group": "IntroducerDetails",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": false,
      "bioAttributes": null,
      "requiredOn": [
        {"engine": "MVEL", "expr": "identity.get('ageGroup') == 'INFANT'"}
      ],
      "subType": "introducerName"
    },
    {
      "id": "introducerRID",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "introducerRID",
      "label": {
        "ara": "مقدم RID",
        "fra": "Introducteur RID",
        "eng": "Introducer RID"
      },
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^([0-9]{10,30})\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "evidence",
      "alignmentGroup": "introducer",
      "visible": {
        "engine": "MVEL",
        "expr":
            "identity.get('ageGroup') == 'INFANT' && (identity.get('introducerUIN') == nil || identity.get('introducerUIN') == empty)"
      },
      "contactType": null,
      "group": "IntroducerDetails",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": false,
      "bioAttributes": null,
      "requiredOn": [
        {
          "engine": "MVEL",
          "expr":
              "identity.get('ageGroup') == 'INFANT' && (identity.get('introducerUIN') == nil || identity.get('introducerUIN') == empty)"
        }
      ],
      "subType": "RID"
    },
    {
      "id": "introducerUIN",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "introducerUIN",
      "label": {
        "ara": "مقدم في",
        "fra": "Introducteur UIN",
        "eng": "Introducer UIN"
      },
      "controlType": "textbox",
      "fieldType": "default",
      "format": "none",
      "validators": [
        {
          "type": "regex",
          "validator": "^([0-9]{10,30})\$",
          "arguments": [],
          "langCode": null,
          "errorCode": "UI_100001"
        }
      ],
      "fieldCategory": "evidence",
      "alignmentGroup": "introducer",
      "visible": {
        "engine": "MVEL",
        "expr":
            "identity.get('ageGroup') == 'INFANT' && (identity.get('introducerRID') == nil || identity.get('introducerRID') == empty)"
      },
      "contactType": null,
      "group": "IntroducerDetails",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": false,
      "bioAttributes": null,
      "requiredOn": [
        {
          "engine": "MVEL",
          "expr":
              "identity.get('ageGroup') == 'INFANT' && (identity.get('introducerRID') == nil || identity.get('introducerRID') == empty)"
        }
      ],
      "subType": "UIN"
    },
    {
      "id": "consent",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "consent accepted",
      "label": {
        "ara": "الاسم الكامل الكامل الكامل",
        "fra":
            "J'ai lu et j'accepte les termes et conditions pour partager mes PII",
        "eng": "I have read and accept terms and conditions to share my PII"
      },
      "controlType": "checkbox",
      "fieldType": "default",
      "format": "none",
      "validators": [],
      "fieldCategory": "evidence",
      "alignmentGroup": null,
      "visible": null,
      "contactType": null,
      "group": "consent",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "consent"
    },
    {
      "id": "consent",
      "inputRequired": true,
      "type": "string",
      "minimum": 0,
      "maximum": 0,
      "description": "consent accepted",
      "label": {
        "ara": "الاسم الكامل الكامل الكامل",
        "fra":
            "J'ai lu et j'accepte les termes et conditions pour partager mes PII",
        "eng": "I have read and accept terms and conditions to share my PII"
      },
      "controlType": "radio",
      "fieldType": "default",
      "format": "none",
      "validators": [],
      "fieldCategory": "evidence",
      "alignmentGroup": null,
      "visible": null,
      "contactType": null,
      "group": "consent",
      "groupLabel": null,
      "changeAction": null,
      "transliterate": false,
      "templateName": null,
      "fieldLayout": null,
      "locationHierarchy": null,
      "conditionalBioAttributes": null,
      "required": true,
      "bioAttributes": null,
      "requiredOn": [],
      "subType": "consent"
    }
  ]
};

class DemographicDetailsView extends StatefulWidget {
  const DemographicDetailsView({super.key});

  @override
  State<DemographicDetailsView> createState() => _DemographicDetailsViewState();
}

class _DemographicDetailsViewState extends State<DemographicDetailsView> {
  @override
  Widget build(BuildContext context) {
    return ScreenWidget(
      data: context.read<GlobalProvider>().processParsed!.screens!.elementAt(0),
      screenIndex: 0,
    );
  }
}

class ScreenWidget extends StatefulWidget {
  const ScreenWidget({
    super.key,
    required this.data,
    this.screenIndex,
  });
  final Screen? data;

  final int? screenIndex;

  @override
  State<ScreenWidget> createState() => _ScreenWidgetState();
}

class _ScreenWidgetState extends State<ScreenWidget> {
  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;

    List<Widget?>? widgetsList = context
        .read<GlobalProvider>()
        .makeListOfWidgets(widget.data!.fields!.cast<Field?>(), widget.screenIndex!);
    return Scaffold(
      appBar: AppBar(
        title: Text(
            context.read<GlobalProvider>().chooseLanguage(widget.data!.label!)),
      ),
      body: SingleChildScrollView(
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 15, 16, 5),
            child: Column(
              children: [
                ...widgetsList!.map((e) => e!).toList(),
              ],
            ),
          ),
        ),
      ),
      bottomNavigationBar: (widget.screenIndex! <
              context.read<GlobalProvider>().processParsed!.screens!.length - 1)
          ? Container(
              // color: Colors.amber,
              height: height * .07,
              child: ElevatedButton(
                child: Text("NEXT"),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ScreenWidget(
                        data: context
                            .read<GlobalProvider>()
                            .processParsed!
                            .screens!
                            .elementAt(widget.screenIndex! + 1),
                        screenIndex: (widget.screenIndex! + 1),
                      ),
                    ),
                  );
                },
              ),
            )
          : SizedBox(),
    );
  }
}
