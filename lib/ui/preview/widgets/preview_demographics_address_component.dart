import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/ui/preview/widgets/header_response_tile.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:registration_client/utils/app_style.dart';

class PreviewDemographicsAddressComponent extends StatelessWidget {
  const PreviewDemographicsAddressComponent({Key? key}) : super(key: key);

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
          Text(
            'Address',
            style: TextStyle(
              fontSize: 16.spMax,
              fontWeight: semiBold,
              color: AppStyle.previewHeaderColor,
            ),
          ),
          SizedBox(
            height: 30.h,
          ),
          _getAddressRow(),
          SizedBox(
            height: 30.h,
          ),
          _getRegionRow(),
          SizedBox(
            height: 30.h,
          ),
          _getZoneRow(),
        ],
      ),
    );
  }

  _getCustomRow(
      {required Widget childOne,
      required Widget childTwo,
      required Widget childThree}) {
    return Row(
      children: [
        Expanded(child: childOne),
        Expanded(child: childTwo),
        Expanded(child: childThree),
      ],
    );
  }

  _getAddressRow() {
    return _getCustomRow(
      childOne: _getAddressLineOne(),
      childTwo: _getAddressLineTwo(),
      childThree: _getAddressLineThree(),
    );
  }

  _getRegionRow() {
    return _getCustomRow(
      childOne: _getRegionTile(),
      childTwo: _getProvinceTile(),
      childThree: _getCityTile(),
    );
  }

  _getZoneRow() {
    return _getCustomRow(
      childOne: _getZoneTile(),
      childTwo: _getPostalTile(),
      childThree: const SizedBox(),
    );
  }

  _getAddressLineOne() {
    return const HeaderResponseTile(
      heading: 'Line 1',
      value: 'Some address goes here',
    );
  }

  _getAddressLineTwo() {
    return const HeaderResponseTile(
      heading: 'Line 2',
      value: 'Some address goes here',
    );
  }

  _getAddressLineThree() {
    return const HeaderResponseTile(
      heading: 'Line 3',
      value: 'Some address goes here',
    );
  }

  _getRegionTile() {
    return const HeaderResponseTile(
      heading: 'Region',
      value: 'Rabat Sale Kenitra',
    );
  }

  _getProvinceTile() {
    return const HeaderResponseTile(
      heading: 'Province',
      value: 'Kenitra',
    );
  }

  _getCityTile() {
    return const HeaderResponseTile(
      heading: 'City',
      value: 'Kenitra',
    );
  }

  _getZoneTile() {
    return const HeaderResponseTile(
      heading: 'Zone',
      value: 'Ben Mansour',
    );
  }

  _getPostalTile() {
    return const HeaderResponseTile(
      heading: 'Postal',
      value: '14022',
    );
  }
}
