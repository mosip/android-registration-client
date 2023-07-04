import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/preview/widgets/header_response_tile.dart';
import 'package:registration_client/utils/app_style.dart';

class PreviewDocumentsComponent extends StatelessWidget {
  const PreviewDocumentsComponent({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: ScreenUtil().screenWidth,
      padding: EdgeInsets.symmetric(
        vertical: 30.h,
        horizontal: 30.w,
      ),
      color: AppStyle.appWhite,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _getIdentityProofTile(),
          SizedBox(
            height: 30.h,
          ),
          _getAddressProofTile()
        ],
      ),
    );
  }

  _getIdentityProofTile() {
    return const HeaderResponseTile(
      heading: 'Identity Proof',
      value: 'POI_CIN',
    );
  }

  _getAddressProofTile() {
    return const HeaderResponseTile(
      heading: 'Address Proof',
      value: 'POA_RNC',
    );
  }
}
