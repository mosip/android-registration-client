import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:intl/intl.dart';
import 'package:registration_client/ui/post_registration/widgets/header_response_tile.dart';
import 'package:registration_client/utils/app_style.dart';

class PreviewHeadComponent extends StatelessWidget {
  const PreviewHeadComponent({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: ScreenUtil().screenWidth,
      padding: EdgeInsets.symmetric(
        vertical: 30.h,
        horizontal: 30.w,
      ),
      color: AppStyle.appWhite,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Expanded(
            child: _getApplicationIdTile(),
          ),
          const Expanded(
            child: SizedBox(),
          ),
          Expanded(
            child: _getDateTile(),
          ),
        ],
      ),
    );
  }

  _getApplicationIdTile() {
    return const HeaderResponseTile(
      heading: 'Application ID',
      value: '89090976561234567890123456789',
    );
  }

  _getDateTile() {
    return HeaderResponseTile(
      heading: 'Date ID',
      value: _getDateFormat(),
    );
  }

  _getDateFormat() {
    DateTime currentDate = DateTime.now();
    DateFormat dateFormat = DateFormat("dd/MM/yyyy hh:mm aaa");
    String formattedDate = dateFormat.format(currentDate);
    return formattedDate;
  }
}
