import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_style.dart';

class HeaderImageTile extends StatelessWidget {
  final String heading;
  final String imageUrl;
  final CrossAxisAlignment crossAxisAlignment;

  const HeaderImageTile({
    Key? key,
    required this.heading,
    required this.imageUrl,
    required this.crossAxisAlignment,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: crossAxisAlignment,
      children: [
        Text(
          heading,
          style: AppStyle.previewHeaderText,
        ),
        SizedBox(
          height: 2.h,
        ),
        Container(
          decoration: BoxDecoration(
            border: Border.all(
              color: AppStyle.appBlack,
            ),
          ),
          child: Image.asset(imageUrl),
        ),
      ],
    );
  }
}
