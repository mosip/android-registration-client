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
import 'package:permission_handler/permission_handler.dart';

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

        // OCR action row — only on Demographics screen when OCR is enabled
        Consumer<OcrScanProvider>(
          builder: (context, ocrProvider, _) {
            final isDemographicsScreen =
                widget.screen.preRegFetchRequired == true;
            if (ocrProvider.isOcrEnabled && isDemographicsScreen) {
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

  /// Builds a compact single-row OCR action bar (Scan + Upload).
  Widget _buildOcrActionRow(BuildContext context) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 10.h, horizontal: 16.w),
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 12.w, vertical: 8.h),
        decoration: BoxDecoration(
          color: solidPrimary.withOpacity(0.04),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(
            color: solidPrimary.withOpacity(0.12),
            width: 1,
          ),
        ),
        child: Row(
          children: [
            // Label
            Icon(Icons.auto_fix_high_rounded,
                color: solidPrimary, size: 16),
            SizedBox(width: 6.w),
            Text(
              'Auto-fill',
              style: TextStyle(
                color: solidPrimary,
                fontSize: 12,
                fontWeight: FontWeight.w600,
              ),
            ),
            SizedBox(width: 12.w),
            // Scan button
            Expanded(
              child: SizedBox(
                height: 34,
                child: OutlinedButton.icon(
                  onPressed: () async {
                    context.read<GlobalProvider>().getAudit("REG-EVT-106", "REG-MOD-103");
                    final status = await Permission.camera.status;
                    if (status.isPermanentlyDenied) {
                      if (context.mounted) {
                        showDialog(
                          context: context,
                          builder: (ctx) => AlertDialog(
                            title: const Text('Camera Permission Required'),
                            content: const Text(
                              'Camera access is needed to scan documents. '
                              'Please enable it in app Settings.',
                            ),
                            actions: [
                              TextButton(
                                onPressed: () => Navigator.of(ctx).pop(),
                                child: const Text('Cancel'),
                              ),
                              TextButton(
                                onPressed: () {
                                  Navigator.of(ctx).pop();
                                  openAppSettings();
                                },
                                child: const Text('Open Settings'),
                              ),
                            ],
                          ),
                        );
                      }
                      return;
                    }
                    if (context.mounted) {
                      await Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (_) => OcrScanPage(
                                screenFields: widget.screen.fields)),
                      );
                      if (context.mounted) {
                        context.read<GlobalProvider>().preRegControllerRefresh = true;
                        await Future.delayed(const Duration(milliseconds: 30));
                        if (context.mounted) {
                          context.read<GlobalProvider>().preRegControllerRefresh = false;
                        }
                      }
                    }
                  },
                  icon: const Icon(Icons.document_scanner_outlined,
                      size: 16),
                  label: const Text('Scan'),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: solidPrimary,
                    side: BorderSide(
                        color: solidPrimary.withOpacity(0.4), width: 1),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8)),
                    padding:
                        EdgeInsets.symmetric(horizontal: 10.w),
                    textStyle: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ),
            ),
            SizedBox(width: 8.w),
            // Upload button
            Expanded(
              child: SizedBox(
                height: 34,
                child: OutlinedButton.icon(
                  onPressed: () async {
                    await Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (_) => OcrUploadPage(
                              screenFields: widget.screen.fields)),
                    );
                    if (context.mounted) {
                      context.read<GlobalProvider>().preRegControllerRefresh = true;
                      await Future.delayed(const Duration(milliseconds: 30));
                      if (context.mounted) {
                        context.read<GlobalProvider>().preRegControllerRefresh = false;
                      }
                    }
                  },
                  icon: const Icon(Icons.upload_file_outlined,
                      size: 16),
                  label: const Text('Upload'),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: solidPrimary,
                    side: BorderSide(
                        color: solidPrimary.withOpacity(0.4), width: 1),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8)),
                    padding:
                        EdgeInsets.symmetric(horizontal: 10.w),
                    textStyle: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

