import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_style.dart';

class HeaderResponseTile extends StatelessWidget {
  final String heading;
  final String value;

  const HeaderResponseTile({
    Key? key,
    required this.heading,
    required this.value,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          heading,
          style: AppStyle.previewHeaderText,
        ),
        SizedBox(
          height: 2.h,
        ),
        Text(
          value,
          style: AppStyle.previewHeaderResponseText,
        )
      ],
    );
  }
}
