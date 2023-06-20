import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class CustomHtmlBox extends StatelessWidget {
  const CustomHtmlBox({super.key, required this.field});
  final Field field;

  @override
  Widget build(BuildContext context) {
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
                    Expanded(
                      child: InkWell(
                        onTap: () {
                          context.read<GlobalProvider>().htmlBoxTabIndex = i;
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
            child: Text("Content"),
          ),
        ],
      ),
    );
  }
}

// class HtmlRenderer extends StatelessWidget {
//   const HtmlRenderer({super.key});

//   @override
//   Widget build(BuildContext context) {
//     return SingleChildScrollView(
//       child: Html(data:"""<!DOCTYPE html>
//     <html>
//     <head>
//       <title>Terms and Conditions</title>
//     </head>
//     <body>
//       <h1>Terms and Conditions</h1>
//       <p>Welcome to our website. By using our website, you agree to these terms and conditions. If you disagree with any part of these terms and conditions, please do not use our website.</p>
      
//       <h2>1. Use of Website</h2>
//       <p>You may use the website for lawful purposes only and in accordance with these terms and conditions. You must not use the website in any way that violates any applicable federal, state, local, or international law or regulation.</p>
    
//       <h2>2. Intellectual Property Rights</h2>
//       <p>The content, design, and organization of this website are protected by intellectual property laws, including copyright and trademark laws. You may not modify, distribute, transmit, display, perform, reproduce, publish, license, create derivative works from, transfer, or sell any information, software, products, or services obtained from this website.</p>
    
//       <h2>3. Limitation of Liability</h2>
//       <p>In no event shall we be liable for any direct, indirect, incidental, special, or consequential damages arising out of or in connection with the use or inability to use this website or the information, products, or services provided on or through this website. This includes, but is not limited to, any loss of data or profits, even if we have been advised of the possibility of such damages.</p>
    
//       <h2>4. Governing Law</h2>
//       <p>These terms and conditions shall be governed by and construed in accordance with the laws of [insert your state/country]. Any dispute arising out of or related to these terms and conditions shall be subject to the exclusive jurisdiction of the courts of [insert your state/country].</p>
    
//       <h2>5. Changes to Terms and Conditions</h2>
//       <p>We reserve the right to revise and update these terms and conditions at any time and without notice. Your continued use of the website after any such changes are made constitutes your acceptance of the new terms and conditions.</p>
    
//       <h2>6. Contact Us</h2>
//       <p>If you have any questions about these terms and conditions, please contact us at [insert your contact information].</p>
//     </body>
//     </html>
//     """),
//     );
//   }
// }

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
