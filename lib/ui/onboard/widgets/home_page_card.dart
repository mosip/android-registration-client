import 'package:flutter/material.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

import 'package:flutter_svg/flutter_svg.dart';

class HomePageCard extends StatelessWidget {
  const HomePageCard(
      {super.key,
      required this.icon,
      required this.title,
      required this.ontap});
  final Widget icon;
  final String title;
  final void Function() ontap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: ontap,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                    color: const Color(0xffF4F7FF),
                    borderRadius: BorderRadius.circular(4)),
                child: icon,
              ),
              SizedBox(
                width: 10.w,
              ),
              Flexible(
                child: Text(
                  "${title}",
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      fontWeight: semiBold,
                      fontSize: 15,
                      color: const Color(0xff333333)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
