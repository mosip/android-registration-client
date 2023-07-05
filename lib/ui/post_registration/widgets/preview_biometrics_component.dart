import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/post_registration/widgets/header_image_tile.dart';
import 'package:registration_client/ui/post_registration/widgets/header_response_tile.dart';
import 'package:registration_client/utils/app_style.dart';

class PreviewBiometricsComponent extends StatelessWidget {
  const PreviewBiometricsComponent({Key? key}) : super(key: key);

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
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Align(
            alignment: Alignment.centerLeft,
            child: _getApplicantBiometricsTile(),
          ),
          SizedBox(
            height: 30.h,
          ),
          _getIrisRow(),
          SizedBox(
            height: 30.h,
          ),
          _getFingersRow(),
        ],
      ),
    );
  }

  _getApplicantBiometricsTile() {
    return const HeaderResponseTile(
      heading: 'Applicant Biometrics',
      value: 'Fingers(10), Iris(2), Face(1)',
    );
  }

  _getIrisRow() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Expanded(
          child: Align(
            child: _getLeftEyeTile(),
          ),
        ),
        Expanded(
          child: Align(
            child: _getRightEyeTile(),
          ),
        ),
        Expanded(
          child: Align(
            child: _getFaceTile(),
          ),
        ),
      ],
    );
  }

  _getFingersRow() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Expanded(
          child: Align(
            child: _getLeftHandPalmTile(),
          ),
        ),
        Expanded(
          child: Align(
            child: _getThumbsTile(),
          ),
        ),
        Expanded(
          child: Align(
            child: _getRightHandPalmTile(),
          ),
        ),
      ],
    );
  }

  _getLeftEyeTile() {
    return const HeaderImageTile(
      heading: 'Left Eye',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.center,
    );
  }

  _getRightEyeTile() {
    return const HeaderImageTile(
      heading: 'Right Eye',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.center,
    );
  }

  _getFaceTile() {
    return const HeaderImageTile(
      heading: 'Face',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.center,
    );
  }

  _getLeftHandPalmTile() {
    return const HeaderImageTile(
      heading: 'Left Hand Palm',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.center,
    );
  }

  _getThumbsTile() {
    return const HeaderImageTile(
      heading: 'Thumbs',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.center,
    );
  }

  _getRightHandPalmTile() {
    return const HeaderImageTile(
      heading: 'Right Hand Palm',
      imageUrl: 'assets/images/user_profile@2x.png',
      crossAxisAlignment: CrossAxisAlignment.center,
    );
  }
}
