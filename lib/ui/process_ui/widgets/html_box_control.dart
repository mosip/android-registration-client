/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'dart:convert';
import 'dart:developer';
import 'package:intl/intl.dart' as intl;

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_html/flutter_html.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/pigeon/demographics_data_pigeon.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class HtmlBoxControl extends StatelessWidget {
  const HtmlBoxControl({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    GlobalProvider globalProvider = Provider.of<GlobalProvider>(context, listen: false);

    return Card(
      color: pureWhite,
      elevation: 5,
      margin: isPortrait
          ? EdgeInsets.fromLTRB(16.w, 24.h, 16.w, 8.h)
          : EdgeInsets.fromLTRB(0, 24.h, 0, 8.h),
      child: Column(
        children: [
          const SizedBox(
            width: double.infinity,
          ),
          Row(
            children: [
              for (int i = 0;
                  i < context.watch<GlobalProvider>().chosenLang.length;
                  i++)
                isMobile
                    ? Expanded(
                        child: InkWell(
                          onTap: () {
                            globalProvider.htmlBoxTabIndex =
                                i;
                          },
                          child: Container(
                            padding: EdgeInsets.only(top: 25.h, bottom: 24.h, left: 37.w, right: 36.w),
                            height: 70.h,
                            decoration: BoxDecoration(
                              borderRadius: const BorderRadius.only(
                                topLeft: Radius.circular(8),
                                topRight: Radius.circular(8),
                              ),
                              color: (context
                                          .watch<GlobalProvider>()
                                          .htmlBoxTabIndex ==
                                      i)
                                  ? solidPrimary
                                  : pureWhite,
                            ),
                            child: Text(
                              globalProvider
                                  .chosenLang
                                  .elementAt(i),
                              textAlign: TextAlign.center,
                              style: TextStyle(
                                  fontSize: 18,
                                  color: (context
                                              .watch<GlobalProvider>()
                                              .htmlBoxTabIndex ==
                                          i)
                                      ? pureWhite
                                      : blackShade1,
                                  fontWeight: semiBold),
                            ),
                          ),
                        ),
                      )
                    : InkWell(
                        onTap: () {
                          globalProvider.htmlBoxTabIndex =
                              i;
                        },
                        child: Container(
                          padding: EdgeInsets.only(top: 25.h, bottom: 24.h, left: 37.w, right: 36.w),
                          height: 70.h,
                          // width: 116,
                          decoration: BoxDecoration(
                            borderRadius: const BorderRadius.only(
                              topLeft: Radius.circular(8),
                              topRight: Radius.circular(8),
                            ),
                            color: (context
                                        .watch<GlobalProvider>()
                                        .htmlBoxTabIndex ==
                                    i)
                                ? solidPrimary
                                : pureWhite,
                          ),
                          child: Text(
                            globalProvider
                                .chosenLang
                                .elementAt(i),
                            textAlign: TextAlign.center,
                            style: TextStyle(
                                fontSize: 18,
                                color: (context
                                            .watch<GlobalProvider>()
                                            .htmlBoxTabIndex ==
                                        i)
                                    ? pureWhite
                                    : blackShade1,
                                fontWeight: semiBold),
                          ),
                        ),
                      ),
            ],
          ),
          Divider(
            thickness: 3,
            color: solidPrimary,
            height: 0,
          ),
          Container(
            height: isPortrait ? 756.h : 341.h,  
            padding: const EdgeInsets.all(18),
            child: HtmlRenderer(field: field),
          ),
        ],
      ),
    );
  }
}

class HtmlRenderer extends StatefulWidget {
  const HtmlRenderer({super.key, required this.field});
  final Field field;

  @override
  State<HtmlRenderer> createState() => _HtmlRendererState();
}

class _HtmlRendererState extends State<HtmlRenderer> {
  @override
  Widget build(BuildContext context) {
    GlobalProvider globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    for (int i = 0; i < globalProvider.chosenLang.length; i++) {
      List<int> bytes = utf8.encode(globalProvider
          .fieldDisplayValues[widget.field.id][i]);
      Uint8List unit8List = Uint8List.fromList(bytes);
      String? hash;
      DemographicsApi().getHashValue(unit8List).then((value) {
        hash = value;
        globalProvider.fieldInputValue[widget.field.id!] = hash;
        DemographicsApi().addSimpleTypeDemographicField(
            widget.field.id!,
            value,
            globalProvider
                .langToCode(globalProvider.chosenLang[i]));
      });
    }
    String lang = globalProvider
                .langToCode(globalProvider.chosenLang[context.watch<GlobalProvider>().htmlBoxTabIndex]);
    log(lang);
    return SingleChildScrollView(
        child: Directionality(textDirection: intl.Bidi.isRtlLanguage(lang.substring(0,2)) ? TextDirection.rtl : TextDirection.ltr,
          child: Html(
            data:
            context.watch<GlobalProvider>().fieldDisplayValues[widget.field.id]
            [context.watch<GlobalProvider>().htmlBoxTabIndex],
          ),
        )
      // Text(context.watch<GlobalProvider>().fieldDisplayValues[field.id][context.watch<GlobalProvider>().htmlBoxTabIndex])
    );
  }
}


// ListView.builder(
//               scrollDirection: Axis.horizontal,
//               padding: const EdgeInsets.all(0),
//               itemCount: context.watch<GlobalProvider>().chosenLang.length,
//               itemBuilder: (BuildContext context, int index) {
//                 Container(
//                   height: 40.h,
//                   width: 174.w,
//                   decoration:
//                       BoxDecoration(borderRadius: BorderRadius.circular(10)),
//                   // color:
//                   //     (context.watch<GlobalProvider>().htmlBoxTabIndex == index)
//                   //         ? solidPrimary
//                   //         : pureWhite,
//                   child: Text(
//                       "${context.read<GlobalProvider>().chosenLang.elementAt(index)}",
//                       style: TextStyle(
//                         fontSize: 14,
//                         fontWeight: semiBold,
//                         color: blackShade1,
//                       )
//                       // (context.watch<GlobalProvider>().htmlBoxTabIndex ==
//                       //         index)
//                       //     ? pureWhite
//                       //     : blackShade1),
//                       ),
//                 );
//               },
//             ),
