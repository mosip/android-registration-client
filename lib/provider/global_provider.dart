/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

import 'package:flutter/material.dart';
import 'package:registration_client/data/models/field.dart';
import 'package:registration_client/data/models/label.dart';
import 'package:registration_client/data/models/process.dart';
import 'package:registration_client/widgets/json_agedate.dart';
import 'package:registration_client/widgets/json_biometrics.dart';
import 'package:registration_client/widgets/json_button.dart';
import 'package:registration_client/widgets/json_checkBox.dart';
import 'package:registration_client/widgets/json_dropdown.dart';
import 'package:registration_client/widgets/json_file_upload.dart';
import 'package:registration_client/widgets/json_html_renderer.dart';
import 'package:registration_client/widgets/json_radioButton.dart';
import 'package:registration_client/widgets/json_textbox.dart';

class GlobalProvider extends ChangeNotifier {
  GlobalProvider() {
    initializeProcess();
  }
  final processJsonData = {
    "id": "NEW",
    "order": 1,
    "flow": "NEW",
    "label": {
      "eng": "New Registration",
      "ara": "تسجيل جديد",
      "fra": "Nouvelle inscription"
    },
    "screens": [
      {
        "order": 1,
        "name": "consent",
        "label": {"ara": "موافقة", "fra": "Consentement", "eng": "Consent"},
        "caption": {"ara": "موافقة", "fra": "Consentement", "eng": "Consent"},
        "fields": [
          // {
          //   "id": "IDSchemaVersion",
          //   "inputRequired": false,
          //   "type": "number",
          //   "minimum": 0,
          //   "maximum": 0,
          //   "description": "ID Schema Version",
          //   "label": {"eng": "IDSchemaVersion"},
          //   "controlType": null,
          //   "fieldType": "default",
          //   "format": "none",
          //   "validators": [],
          //   "fieldCategory": "none",
          //   "alignmentGroup": null,
          //   "visible": null,
          //   "contactType": null,
          //   "group": null,
          //   "groupLabel": null,
          //   "changeAction": null,
          //   "transliterate": false,
          //   "templateName": null,
          //   "fieldLayout": null,
          //   "locationHierarchy": null,
          //   "conditionalBioAttributes": null,
          //   "required": true,
          //   "bioAttributes": null,
          //   "requiredOn": [],
          //   "subType": "IdSchemaVersion"
          // },
          {
            "id": "IDSchemaVersion",
            "inputRequired": false,
            "type": "number",
            "minimum": 0,
            "maximum": 0,
            "description": """<!DOCTYPE html>
<html>
<head>
	<title>Terms and Conditions</title>
</head>
<body>
	<h1>Terms and Conditions</h1>
	<p>Welcome to our website. By using our website, you agree to these terms and conditions. If you disagree with any part of these terms and conditions, please do not use our website.</p>
	
	<h2>1. Use of Website</h2>
	<p>You may use the website for lawful purposes only and in accordance with these terms and conditions. You must not use the website in any way that violates any applicable federal, state, local, or international law or regulation.</p>

	<h2>2. Intellectual Property Rights</h2>
	<p>The content, design, and organization of this website are protected by intellectual property laws, including copyright and trademark laws. You may not modify, distribute, transmit, display, perform, reproduce, publish, license, create derivative works from, transfer, or sell any information, software, products, or services obtained from this website.</p>

	<h2>3. Limitation of Liability</h2>
	<p>In no event shall we be liable for any direct, indirect, incidental, special, or consequential damages arising out of or in connection with the use or inability to use this website or the information, products, or services provided on or through this website. This includes, but is not limited to, any loss of data or profits, even if we have been advised of the possibility of such damages.</p>

	<h2>4. Governing Law</h2>
	<p>These terms and conditions shall be governed by and construed in accordance with the laws of [insert your state/country]. Any dispute arising out of or related to these terms and conditions shall be subject to the exclusive jurisdiction of the courts of [insert your state/country].</p>

	<h2>5. Changes to Terms and Conditions</h2>
	<p>We reserve the right to revise and update these terms and conditions at any time and without notice. Your continued use of the website after any such changes are made constitutes your acceptance of the new terms and conditions.</p>

	<h2>6. Contact Us</h2>
	<p>If you have any questions about these terms and conditions, please contact us at [insert your contact information].</p>
</body>
</html>
""",
            "label": {"eng": "IDSchemaVersion"},
            "controlType": "html",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "none",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": null,
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
            "subType": "IdSchemaVersion"
          },
          // {
          //       "id": "consentText",
          //       "inputRequired": true,
          //       "type": "simpleType",
          //       "minimum": 0,
          //       "maximum": 0,
          //       "description": "Consent",
          //       "label": {},
          //       "controlType": "html",
          //       "fieldType": "default",
          //       "format": "none",
          //       "validators": [],
          //       "fieldCategory": "evidence",
          //       "alignmentGroup": null,
          //       "visible": null,
          //       "contactType": null,
          //       "group": "consentText",
          //       "groupLabel": null,
          //       "changeAction": null,
          //       "transliterate": false,
          //       "templateName": "Registration Consent",
          //       "fieldLayout": null,
          //       "locationHierarchy": null,
          //       "conditionalBioAttributes": null,
          //       "required": true,
          //       "bioAttributes": null,
          //       "requiredOn": [],
          //       "subType": "consentText"
          //   },
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
              "eng":
                  "I have read and accept terms and conditions to share my PII"
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
          // {
          //   "id": "preferredLang",
          //   "inputRequired": true,
          //   "type": "string",
          //   "minimum": 0,
          //   "maximum": 0,
          //   "description": "user preferred Language",
          //   "label": {
          //     "ara": "لغة الإخطار",
          //     "fra": "Langue de notification",
          //     "eng": "Notification Langauge"
          //   },
          //   "controlType": "button",
          //   "fieldType": "dynamic",
          //   "format": "none",
          //   "validators": [],
          //   "fieldCategory": "pvt",
          //   "alignmentGroup": "group1",
          //   "visible": null,
          //   "contactType": null,
          //   "group": "PreferredLanguage",
          //   "groupLabel": null,
          //   "changeAction": null,
          //   "transliterate": false,
          //   "templateName": null,
          //   "fieldLayout": null,
          //   "locationHierarchy": null,
          //   "conditionalBioAttributes": null,
          //   "required": true,
          //   "bioAttributes": null,
          //   "requiredOn": [],
          //   "subType": "preferredLang"
          // }
        ],
        "layoutTemplate": null,
        "preRegFetchRequired": false,
        "active": false
      },
      {
        "order": 2,
        "name": "DemographicDetails",
        "label": {
          "ara": "التفاصيل الديموغرافية",
          "fra": "Détails démographiques",
          "eng": "Demographic Details"
        },
        "caption": {
          "ara": "التفاصيل الديموغرافية",
          "fra": "Détails démographiques",
          "eng": "Demographic Details"
        },
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
            "id": "gender",
            "inputRequired": true,
            "type": "simpleType",
            "minimum": 0,
            "maximum": 0,
            "description": "gender",
            "label": {"ara": "جنس", "fra": "Le genre", "eng": "Gender"},
            "controlType": "dropdown",
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
            "controlType": "dropdown",
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
            "requiredOn": [
              {"engine": "MVEL", "expr": "field[1][1]>=18,"}
            ],
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
            "label": {
              "ara": "الاسم الكامل",
              "fra": "Province",
              "eng": "Province"
            },
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
          }
        ],
        "layoutTemplate": null,
        "preRegFetchRequired": true,
        "active": false
      },
      {
        "order": 3,
        "name": "Documents",
        "label": {
          "ara": "تحميل الوثيقة",
          "fra": "Des documents",
          "eng": "Document Upload"
        },
        "caption": {"ara": "وثائق", "fra": "Des documents", "eng": "Documents"},
        "fields": [
          {
            "id": "proofOfAddress",
            "inputRequired": true,
            "type": "documentType",
            "minimum": 0,
            "maximum": 0,
            "description": "proofOfAddress",
            "label": {
              "ara": "إثبات العنوان",
              "fra": "Address Proof",
              "eng": "Address Proof"
            },
            "controlType": "fileupload",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "pvt",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": "Documents",
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
            "subType": "POA"
          },
          {
            "id": "proofOfIdentity",
            "inputRequired": true,
            "type": "documentType",
            "minimum": 0,
            "maximum": 0,
            "description": "proofOfIdentity",
            "label": {
              "ara": "إثبات الهوية",
              "fra": "Identity Proof",
              "eng": "Identity Proof"
            },
            "controlType": "fileupload",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "pvt",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": "Documents",
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
            "subType": "POI"
          },
          {
            "id": "proofOfRelationship",
            "inputRequired": true,
            "type": "documentType",
            "minimum": 0,
            "maximum": 0,
            "description": "proofOfRelationship",
            "label": {
              "ara": "إثبات العلاقة",
              "fra": "Relationship Proof",
              "eng": "Relationship Proof"
            },
            "controlType": "fileupload",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "pvt",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": "Documents",
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
            "subType": "POR"
          },
          {
            "id": "proofOfDateOfBirth",
            "inputRequired": true,
            "type": "documentType",
            "minimum": 0,
            "maximum": 0,
            "description": "proofOfDateOfBirth",
            "label": {
              "ara": "دليل DOB",
              "fra": "DOB Proof",
              "eng": "DOB Proof"
            },
            "controlType": "fileupload",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "pvt",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": "Documents",
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
            "subType": "POB"
          },
          {
            "id": "proofOfException",
            "inputRequired": true,
            "type": "documentType",
            "minimum": 0,
            "maximum": 0,
            "description": "proofOfException",
            "label": {
              "ara": "إثبات الاستثناء",
              "fra": "Exception Proof",
              "eng": "Exception Proof"
            },
            "controlType": "fileupload",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "evidence",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": "Documents",
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
            "subType": "POE"
          }
        ],
        "layoutTemplate": null,
        "preRegFetchRequired": false,
        "active": false
      },
      {
        "order": 4,
        "name": "BiometricDetails",
        "label": {
          "ara": "التفاصيل البيومترية",
          "fra": "Détails biométriques",
          "eng": "Biometric Details"
        },
        "caption": {
          "ara": "التفاصيل البيومترية",
          "fra": "Détails biométriques",
          "eng": "Biometric Details"
        },
        "fields": [
          {
            "id": "individualBiometrics",
            "inputRequired": true,
            "type": "biometricsType",
            "minimum": 0,
            "maximum": 0,
            "description": "",
            "label": {
              "ara": "القياسات الحيوية الفردية",
              "fra": "Applicant Biometrics",
              "eng": "Applicant Biometrics"
            },
            "controlType": "biometrics",
            "fieldType": "default",
            "format": "none",
            "validators": [],
            "fieldCategory": "pvt",
            "alignmentGroup": null,
            "visible": null,
            "contactType": null,
            "group": "Biometrics",
            "groupLabel": null,
            "changeAction": null,
            "transliterate": false,
            "templateName": null,
            "fieldLayout": null,
            "locationHierarchy": null,
            "conditionalBioAttributes": [
              {
                "ageGroup": "INFANT",
                "process": "ALL",
                "validationExpr": "face",
                "bioAttributes": ["face"]
              }
            ],
            "required": true,
            "bioAttributes": [
              "leftEye",
              "rightEye",
              "rightIndex",
              "rightLittle",
              "rightRing",
              "rightMiddle",
              "leftIndex",
              "leftLittle",
              "leftRing",
              "leftMiddle",
              "leftThumb",
              "rightThumb",
              "face"
            ],
            "requiredOn": [],
            "subType": "applicant"
          },
          // {
          //   "id": "introducerBiometrics",
          //   "inputRequired": true,
          //   "type": "biometricsType",
          //   "minimum": 0,
          //   "maximum": 0,
          //   "description": "",
          //   "label": {
          //     "ara": "مقدم القياسات الحيوية",
          //     "fra": "Introducteur Biométrie",
          //     "eng": "Introducer Biometrics"
          //   },
          //   "controlType": "biometrics",
          //   "fieldType": "default",
          //   "format": "none",
          //   "validators": [],
          //   "fieldCategory": "pvt",
          //   "alignmentGroup": null,
          //   "visible": null,
          //   "contactType": null,
          //   "group": "Biometrics",
          //   "groupLabel": null,
          //   "changeAction": null,
          //   "transliterate": false,
          //   "templateName": null,
          //   "fieldLayout": null,
          //   "locationHierarchy": null,
          //   "conditionalBioAttributes": [
          //     {
          //       "ageGroup": "ALL",
          //       "process": "ALL",
          //       "validationExpr":
          //           "leftEye || rightEye || rightIndex || rightLittle || rightRing || rightMiddle || leftIndex || leftLittle || leftRing || leftMiddle || leftThumb || rightThumb || face",
          //       "bioAttributes": [
          //         "leftEye",
          //         "rightEye",
          //         "rightIndex",
          //         "rightLittle",
          //         "rightRing",
          //         "rightMiddle",
          //         "leftIndex",
          //         "leftLittle",
          //         "leftRing",
          //         "leftMiddle",
          //         "leftThumb",
          //         "rightThumb",
          //         "face"
          //       ]
          //     }
          //   ],
          //   "required": false,
          //   "bioAttributes": [
          //     "leftEye",
          //     "rightEye",
          //     "rightIndex",
          //     "rightLittle",
          //     "rightRing",
          //     "rightMiddle",
          //     "leftIndex",
          //     "leftLittle",
          //     "leftRing",
          //     "leftMiddle",
          //     "leftThumb",
          //     "rightThumb",
          //     "face"
          //   ],
          //   "requiredOn": [
          //     {"engine": "MVEL", "expr": "identity.get('ageGroup') == 'INFANT'"}
          //   ],
          //   "subType": "introducer"
          // }
       
        ],
        "layoutTemplate": null,
        "preRegFetchRequired": false,
        "active": false
      }
    ],
    "caption": {
      "eng": "New Registration",
      "ara": "تسجيل جديد",
      "fra": "Nouvelle inscription"
    },
    "icon": "NewReg.png",
    "isActive": true,
    "autoSelectedGroups": null
  };
  Process? processParsed;
  int? _selectedLangCode = 1;
  int? get selectedLangCode => this._selectedLangCode;

  set selectedLangCode(int? value) {
    this._selectedLangCode = value;
    notifyListeners();
  }

  List<List<dynamic>?>? _twoDArray;
  List<List<dynamic>?>? get twoDArray => this._twoDArray;

  set twoDArray(List<List<dynamic>?>? value) {
    this._twoDArray = value;
    notifyListeners();
  }

  customSettwoDArray(int screenIndex, int fieldIndex, dynamic value) {
    twoDArray![screenIndex]![fieldIndex] = value;
    notifyListeners();
  }

  Process? get getProcessParsed => this.processParsed;

  set setProcessParsed(Process? processParsed) {
    this.processParsed = processParsed;
    notifyListeners();
  }

  chooseLanguage(Label label) {
    if (selectedLangCode == 1) {
      return label.eng;
    }
    if (selectedLangCode == 2) {
      return label.ara;
    }
    if (selectedLangCode == 3) {
      return label.fra;
    }
    return label.eng;
  }

  initializeProcess() {
    processParsed = Process.fromJson(processJsonData);

    twoDArray = List.generate(
        processParsed!.screens!.length,
        (i) => List.generate(
            processParsed!.screens!.elementAt(i)!.fields!.length, (j) => -1));
    // print(twoDArray);
    // _screensList = List.generate(processParsed!.screens!.length, (screenIndex) {
    //   processParsed!.screens!.elementAt(screenIndex);
    // });
  }

  makeListOfWidgets(List<Field?>? fieldsList, int screenIndex) {
    List<Widget?>? widgetsList = [];
    for (int i = 0; i < fieldsList!.length; i++) {
      widgetsList.add(decideWidget(fieldsList.elementAt(i)!, i, screenIndex));
      widgetsList.add(
        SizedBox(
          height: 25,
        ),
      );
    }
    return widgetsList;
  }

  decideWidget(Field e, int fieldIndex, int screenIndex) {
    if (e.controlType == null) {
      return Text("Null Widget");
    }
    if (e.controlType == "textbox") {
      return JsonTextBox(
        fieldIndex: fieldIndex,
        screenIndex: screenIndex,
      );
    }
    if (e.controlType == "html") {
      return JsonHtmlRenderer(
        fieldIndex: fieldIndex,
        screenIndex: screenIndex,
      );
    }
    if (e.controlType == "biometrics") {
      return JsonBiometrics(
        fieldIndex: fieldIndex,
        screenIndex: screenIndex,
      );
    }
    if (e.controlType == "dropdown") {
      return JsonDropdown(data: e);
    }
    if (e.controlType == "checkbox") {
      return JsonCheckBox(data: e);
    }
    if (e.controlType == "button") {
      return JsonButton(
        fieldIndex: fieldIndex,
        screenIndex: screenIndex,
      );
    }
    if (e.controlType == "fileupload") {
      return JsonFileUpload(
        fieldIndex: fieldIndex,
        screenIndex: screenIndex,
      );
    }
    if (e.controlType == "ageDate") {
      return JsonAgeDate(
        fieldIndex: fieldIndex,
        screenIndex: screenIndex,
      );
    }
    if (e.controlType == "radio") {
      return JsonRadioButton(data: e);
    }
    return Text("Some non custom widget");
  }
}
