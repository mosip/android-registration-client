import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

import 'package:flutter_html/flutter_html.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class HtmlBoxControl extends StatelessWidget {
  const HtmlBoxControl({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;

    return Card(
      color: pure_white,
      margin: EdgeInsets.fromLTRB(16, 24, 16, 8),
      child: Column(
        children: [
          const SizedBox(
            width: double.infinity,
          ),
          SizedBox(
              height: 40.h,
              child: Row(
                children: [
                  for (int i = 0;
                      i < context.watch<GlobalProvider>().chosenLang.length;
                      i++)
                    isMobile
                        ? Expanded(
                            child: InkWell(
                              onTap: () {
                                context.read<GlobalProvider>().htmlBoxTabIndex =
                                    i;
                              },
                              child: Container(
                                padding: EdgeInsets.only(top: 10),
                                height: 40.h,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.only(
                                    topLeft: Radius.circular(8),
                                    topRight: Radius.circular(8),
                                  ),
                                  color: (context
                                              .watch<GlobalProvider>()
                                              .htmlBoxTabIndex ==
                                          i)
                                      ? solid_primary
                                      : pure_white,
                                ),
                                child: Text(
                                  "${context.read<GlobalProvider>().chosenLang.elementAt(i)}",
                                  textAlign: TextAlign.center,
                                  style: TextStyle(
                                      fontSize: 14,
                                      color: (context
                                                  .watch<GlobalProvider>()
                                                  .htmlBoxTabIndex ==
                                              i)
                                          ? pure_white
                                          : black_shade_1,
                                      fontWeight: semiBold),
                                ),
                              ),
                            ),
                          )
                        : InkWell(
                            onTap: () {
                              context.read<GlobalProvider>().htmlBoxTabIndex =
                                  i;
                            },
                            child: Container(
                              padding: EdgeInsets.only(top: 10),
                              height: 40.h,
                              width: 116,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.only(
                                  topLeft: Radius.circular(8),
                                  topRight: Radius.circular(8),
                                ),
                                color: (context
                                            .watch<GlobalProvider>()
                                            .htmlBoxTabIndex ==
                                        i)
                                    ? solid_primary
                                    : pure_white,
                              ),
                              child: Text(
                                "${context.read<GlobalProvider>().chosenLang.elementAt(i)}",
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                    fontSize: 14,
                                    color: (context
                                                .watch<GlobalProvider>()
                                                .htmlBoxTabIndex ==
                                            i)
                                        ? pure_white
                                        : black_shade_1,
                                    fontWeight: semiBold),
                              ),
                            ),
                          ),
                ],
              )),
          Divider(
            thickness: 3,
            color: solid_primary,
            height: 0,
          ),
          Container(
            height: 341.h,
            padding: EdgeInsets.all(18),
            child: HtmlRenderer(field: field),
          ),
        ],
      ),
    );
  }
}

class HtmlRenderer extends StatelessWidget {
  const HtmlRenderer({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Html(
        data: context.watch<GlobalProvider>().fieldDisplayValues[field.id]
            [context.watch<GlobalProvider>().htmlBoxTabIndex],
      ),
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
//                   //         ? solid_primary
//                   //         : pure_white,
//                   child: Text(
//                       "${context.read<GlobalProvider>().chosenLang.elementAt(index)}",
//                       style: TextStyle(
//                         fontSize: 14,
//                         fontWeight: semiBold,
//                         color: black_shade_1,
//                       )
//                       // (context.watch<GlobalProvider>().htmlBoxTabIndex ==
//                       //         index)
//                       //     ? pure_white
//                       //     : black_shade_1),
//                       ),
//                 );
//               },
//             ),
