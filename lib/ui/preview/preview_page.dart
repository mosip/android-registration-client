import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/common/tablet_header.dart';
import 'package:registration_client/ui/common/tablet_navbar.dart';
import 'package:registration_client/ui/preview/widgets/preview_biometrics_component.dart';
import 'package:registration_client/ui/preview/widgets/preview_demographics_address_component.dart';
import 'package:registration_client/ui/preview/widgets/preview_demographics_profile_component.dart';
import 'package:registration_client/ui/preview/widgets/preview_documents_component.dart';
import 'package:registration_client/ui/preview/widgets/preview_head_component.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class PreviewPage extends StatelessWidget {
  const PreviewPage({super.key});

  static const routeName = '/preview-page';

  @override
  Widget build(BuildContext context) {
    bool isMobile = MediaQuery.of(context).size.width < 750;
    double w = ScreenUtil().screenWidth;
    // Map<String, dynamic> arguments =
    //     ModalRoute.of(context)!.settings.arguments! as Map<String, dynamic>;
    return SafeArea(
      child: Scaffold(
        backgroundColor: secondaryColors.elementAt(10),
        bottomNavigationBar: Container(
          color: pure_white,
          padding: const EdgeInsets.all(16),
          height: 84.h,
          child: isMobile
              ? ElevatedButton(
                  child: const Text("CONTINUE"),
                  onPressed: () {},
                )
              : Container(
                  width: w,
                  padding: EdgeInsets.symmetric(
                    horizontal: 60.w,
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      ElevatedButton(
                        style: ButtonStyle(
                          maximumSize:
                              MaterialStateProperty.all<Size>(Size(209, 52)),
                          minimumSize:
                              MaterialStateProperty.all<Size>(Size(209, 52)),
                        ),
                        child: const Text("CONTINUE"),
                        onPressed: () {},
                      )
                    ],
                  ),
                ),
        ),
        body: SingleChildScrollView(
          child: AnnotatedRegion<SystemUiOverlayStyle>(
            value: const SystemUiOverlayStyle(
              statusBarColor: Colors.transparent,
            ),
            child: Column(
              children: [
                isMobile
                    ? const SizedBox()
                    : Column(
                        children: const [
                          TabletHeader(),
                          TabletNavbar(),
                        ],
                      ),
                Padding(
                  padding: isMobile
                      ? const EdgeInsets.all(0)
                      : const EdgeInsets.fromLTRB(60, 0, 60, 0),
                  child: Container(
                    decoration: const BoxDecoration(
                      borderRadius: BorderRadius.all(
                        Radius.circular(6),
                      ),
                      color: AppStyle.appWhite,
                    ),
                    child: Column(
                      children: [
                        _getPreviewHeadComponent(),
                        _getComponentHeader('Demographic Information'),
                        _getPreviewDemographicsProfileComponent(),
                        Container(
                          width: w,
                          height: 10.h,
                          color: AppStyle.previewHeaderComponentColor,
                        ),
                        _getPreviewDemographicsAddressComponent(),
                        _getComponentHeader('Documents'),
                        _getPreviewDocumentsComponent(),
                        _getComponentHeader('Biometrics'),
                        _getPreviewBiometricsComponent(),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _getPreviewHeadComponent() {
    return const PreviewHeadComponent();
  }

  Widget _getPreviewDemographicsProfileComponent() {
    return const PreviewDemographicsProfileComponent();
  }

  Widget _getPreviewDemographicsAddressComponent() {
    return const PreviewDemographicsAddressComponent();
  }

  Widget _getPreviewDocumentsComponent() {
    return const PreviewDocumentsComponent();
  }

  Widget _getPreviewBiometricsComponent() {
    return const PreviewBiometricsComponent();
  }

  Widget _getComponentHeader(String title) {
    return Container(
      width: ScreenUtil().screenWidth,
      padding: EdgeInsets.symmetric(
        vertical: 19.h,
        horizontal: 30.w,
      ),
      color: AppStyle.previewHeaderComponentColor,
      child: Text(
        title,
        style: AppStyle.previewHeaderResponseText,
      ),
    );
  }
}
