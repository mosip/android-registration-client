/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/process.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/provider/registration_task_provider.dart';
import 'package:registration_client/ui/process_ui/process_type.dart';
import 'package:registration_client/ui/process_ui/widgets/age_date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/biometric_capture_control.dart';

import 'package:registration_client/ui/process_ui/widgets/checkbox_control.dart';
import 'package:registration_client/ui/process_ui/widgets/date_control.dart';
import 'package:registration_client/ui/process_ui/widgets/document_upload_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/dynamic_dropdown_control.dart';
import 'package:registration_client/ui/process_ui/widgets/gender_control.dart';
import 'package:registration_client/ui/process_ui/widgets/html_box_control.dart';

import 'package:registration_client/ui/process_ui/widgets/button_control.dart';
import 'package:registration_client/ui/process_ui/widgets/terms_and_conditions.dart';
import 'package:registration_client/ui/process_ui/widgets/pre_reg_data_control.dart';
import 'package:registration_client/ui/process_ui/widgets/additional_Info_ReqId_control.dart';
import 'package:registration_client/ui/process_ui/widgets/textbox_control.dart';
import 'package:registration_client/provider/ocr_scan_provider.dart';
import 'package:registration_client/ui/scanner/ocr_scan_page.dart';
import 'package:registration_client/ui/scanner/ocr_upload_page.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'radio_button_control.dart';

class GenericProcessScreenContent extends StatefulWidget {
  const GenericProcessScreenContent({
    super.key,
    required this.context,
    required this.screen,
    required this.processType,
    required this.process,
  });
  
  final BuildContext context;
  final Screen screen;
  final ProcessType processType;
  final Process process;

  @override
  State<GenericProcessScreenContent> createState() =>
      _GenericProcessScreenContentState();
}

class _GenericProcessScreenContentState extends State<GenericProcessScreenContent> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  int refreshValue = 0;

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider =
        Provider.of<RegistrationTaskProvider>(context, listen: false);
    super.initState();
  }

  Widget widgetType(Field e) {
    RegExp regexPattern = RegExp(r'^.*$');

    if (e.validators != null && e.validators!.isNotEmpty) {
      final validation = e.validators?.first?.validator;
      if (validation != null) {
        regexPattern = RegExp(validation);
      }
    }

    if (e.id == "preferredLang") {
      return const SizedBox.shrink();
    }
    if ((widget.processType == ProcessType.newProcess ||
            widget.processType == ProcessType.correctionProcess) &&
        e.inputRequired == false) {
      return const SizedBox.shrink();
    }

    switch (e.controlType) {
      case "checkbox":
        if (e.subType == "gender") {
          return RadioButtonControl(field: e);
        }
        if (e.group!.toLowerCase() == "consent") {
          return TermsAndConditions(field: e);
        }
        return CheckboxControl(field: e);
      case "html":
        return HtmlBoxControl(field: e);
      case "biometrics":
        if (context.watch<GlobalProvider>().mvelRequiredFields[e.id] ?? _getDefaultBiometricVisibility()) {
          return BiometricCaptureControl(e: e);
        }
        return Container();
      case "button":
        if (e.subType == "preferredLang") {
          return ButtonControl(field: e);
        }
        if (e.subType == "gender" || e.subType == "residenceStatus") {
          return RadioButtonControl(field: e);
        }
        //feature will implement
        if (e.subType == "selectedHandles") {
          return const SizedBox.shrink();
        }
        return Text("${e.controlType}");
      case "textbox":
        return TextBoxControl(e: e, validation: regexPattern);
      case "dropdown":
        if (e.id == "gender") {
          return GenderControl(field: e, validation: regexPattern);
        }
        if (e.fieldType == "dynamic") {
          return DynamicDropDownControl(field: e, validation: regexPattern);
        }
        return DropDownControl(
          validation: regexPattern,
          field: e,
        );

      case "ageDate":
        return AgeDateControl(
          field: e,
          validation: regexPattern,
        );
      case "date":
        return DateControl(
          validation: regexPattern,
          field: e,
        );
      case "fileupload":
        return DocumentUploadControl(
          field: e,
          validation: regexPattern,
        );
      default:
        return (e.controlType != null) ? Text("${e.controlType}") : const SizedBox.shrink();
    }
  }

  bool _getDefaultBiometricVisibility() {
    if (widget.processType == ProcessType.updateProcess) {
      return false;
    }
    return true;
  }

  evaluateMVELVisible(String fieldData, Field e) async {
    registrationTaskProvider.evaluateMVELVisible(fieldData).then((value) {
      if (!value) {
        globalProvider.removeFieldFromMap(
            e.id!, globalProvider.fieldInputValue);
        registrationTaskProvider.removeDemographicField(e.id!);
      }
      globalProvider.setMvelVisibleFields(e.id!, value);
    });
  }

  evaluateMVELRequired(String fieldData, Field e) async {
    registrationTaskProvider.evaluateMVELRequired(fieldData).then((value) {
      globalProvider.setMvelRequiredFields(e.id!, value);
    });
  }

  _checkMvelVisible(Field e) async {
    if (widget.processType == ProcessType.updateProcess) {
      if (e.requiredOn != null && e.requiredOn!.isNotEmpty) {
        await evaluateMVELVisible(jsonEncode(e.toJson()), e);
        await evaluateMVELRequired(jsonEncode(e.toJson()), e);
      }
    } else {
      if (e.required == false) {
        if (e.requiredOn != null && e.requiredOn!.isNotEmpty) {
          await evaluateMVELVisible(jsonEncode(e.toJson()), e);
          await evaluateMVELRequired(jsonEncode(e.toJson()), e);
        }
      }
    }
  }

  bool _shouldShowField(Field e) {

    if (widget.processType == ProcessType.updateProcess) {
      if (widget.process.autoSelectedGroups!.contains(e.group)) {
        return true;
      } else if (globalProvider.selectedUpdateFields[e.group] != null) {
        return true;
      }
      return false;
    }

    if (context.watch<GlobalProvider>().mvelVisibleFields[e.id] ?? true) {
      return true;
    }

    return false;
  }

@override
  Widget build(BuildContext context) {
    return Column(
      children: [
        if (widget.screen.preRegFetchRequired == true) ...[
          PreRegDataControl(
              screen: widget.screen,
              onFetched: () {
                setState(() {
                  refreshValue = 1;
                });
              }),
        ],

        if (widget.screen.additionalInfoRequestIdRequired == true) ...[
          const AdditionalInfoReqIdControl(),
        ],

        // OCR action row — only on Demographics tab when OCR is enabled
        Consumer<OcrScanProvider>(
          builder: (context, ocrProvider, _) {
            final isDemographicsTab =
                context.watch<GlobalProvider>().newProcessTabIndex == 0;
            if (ocrProvider.isOcrEnabled && isDemographicsTab) {
              return _buildOcrActionRow(context);
            }
            return const SizedBox.shrink();
          },
        ),
        
        (context.watch<GlobalProvider>().preRegControllerRefresh)
            ? const CircularProgressIndicator()
            : Form(
                key: context.watch<GlobalProvider>().formKey,
                child: Column(
                  children: [
                    ...widget.screen.fields!.map((e) {
                      _checkMvelVisible(e!);
                      if (_shouldShowField(e)) {
                        return widgetType(e);
                      }
                      return Container();
                    }).toList(),
                  ],
                ),
              ),
      ],
    );
  }

  /// Builds the two-card OCR action row (Scan + Upload).
  Widget _buildOcrActionRow(BuildContext context) {
    final bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    final EdgeInsets rowMargin = EdgeInsets.symmetric(
      vertical: 8.h,
      horizontal: isPortrait ? 16.w : 0,
    );

    return Padding(
      padding: rowMargin,
      child: IntrinsicHeight(
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(child: _buildOcrCard(
              context: context,
              icon: Icons.document_scanner_outlined,
              label: 'Scan Document',
              description: 'Use camera to auto-fill fields',
              onTap: () => Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const OcrScanPage()),
              ),
            )),
            SizedBox(width: 10.w),
            Expanded(child: _buildOcrCard(
              context: context,
              icon: Icons.upload_file_outlined,
              label: 'Upload Document',
              description: 'Pick an image to auto-fill fields',
              onTap: () => Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const OcrUploadPage()),
              ),
            )),
          ],
        ),
      ),
    );
  }

  /// Builds a single OCR action card tile.
  Widget _buildOcrCard({
    required BuildContext context,
    required IconData icon,
    required String label,
    required String description,
    required VoidCallback onTap,
  }) {
    return Card(
      elevation: 3,
      color: pureWhite,
      margin: EdgeInsets.zero,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: solidPrimary.withOpacity(0.15),
          width: 1,
        ),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: EdgeInsets.symmetric(horizontal: 14.w, vertical: 16.h),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            gradient: LinearGradient(
              colors: [
                solidPrimary.withOpacity(0.05),
                solidPrimary.withOpacity(0.01),
              ],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              // Icon badge
              Container(
                width: 42,
                height: 42,
                decoration: BoxDecoration(
                  color: solidPrimary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(icon, color: solidPrimary, size: 22),
              ),
              SizedBox(height: 12.h),
              Text(
                label,
                style: TextStyle(
                  color: solidPrimary,
                  fontSize: 13.5,
                  fontWeight: FontWeight.w700,
                  letterSpacing: 0.1,
                ),
              ),
              SizedBox(height: 3.h),
              Text(
                description,
                style: TextStyle(
                  color: appBlackShade3,
                  fontSize: 11.5,
                  fontWeight: FontWeight.w400,
                  height: 1.4,
                ),
              ),
              SizedBox(height: 10.h),
              Align(
                alignment: Alignment.centerRight,
                child: Icon(
                  Icons.arrow_forward_ios_rounded,
                  color: solidPrimary.withOpacity(0.45),
                  size: 14,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

