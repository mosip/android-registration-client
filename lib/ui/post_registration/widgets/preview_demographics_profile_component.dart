import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/post_registration/widgets/header_image_tile.dart';
import 'package:registration_client/ui/post_registration/widgets/header_response_tile.dart';
import 'package:registration_client/utils/app_style.dart';

class PreviewDemographicsProfileComponent extends StatelessWidget {
  const PreviewDemographicsProfileComponent({Key? key}) : super(key: key);

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
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _getNameTile(),
                    SizedBox(
                      height: 30.h,
                    ),
                    _getDateOfBirthTile(),
                    SizedBox(
                      height: 30.h,
                    ),
                    _getGenderTile(),
                  ],
                ),
              ),
              const Expanded(
                child: SizedBox(),
              ),
              Expanded(
                child: _getImageTile(),
              ),
            ],
          ),
          SizedBox(
            height: 30.h,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: _getPhoneTile(),
              ),
              Expanded(
                child: _getEmailTile(),
              ),
              const Expanded(
                child: SizedBox(),
              ),
            ],
          )
        ],
      ),
    );
  }

  _getNameTile() {
    return const HeaderResponseTile(
      heading: 'Full Name',
      value: 'Julian Albert',
    );
  }

  _getDateOfBirthTile() {
    return const HeaderResponseTile(
      heading: 'DOB',
      value: '1995/01/01',
    );
  }

  _getGenderTile() {
    return const HeaderResponseTile(
      heading: 'Gender',
      value: 'Male',
    );
  }

  _getPhoneTile() {
    return const HeaderResponseTile(
      heading: 'Phone',
      value: '9090909090',
    );
  }

  _getEmailTile() {
    return const HeaderResponseTile(
      heading: 'Email',
      value: 'julian.elbert@domain.com',
    );
  }

  _getImageTile() {
    return const HeaderImageTile(
      heading: 'Photo',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.start,
    );
  }
}
