import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class BiometricCaptureExceptionBlock extends StatelessWidget {
  const BiometricCaptureExceptionBlock(
      {super.key, required this.exceptionImage});
  final Widget exceptionImage;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 547.h,
      width: 370.h,
      decoration: BoxDecoration(
          color: secondaryColors.elementAt(3),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: secondaryColors.elementAt(13), width: 1)),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: EdgeInsets.fromLTRB(0, 16, 0, 36),
            child: Center(
              child: Text(
                "Mark Exception",
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontSize: 18, fontWeight: semiBold, color: black_shade_1),
              ),
            ),
          ),
          exceptionImage,
          Padding(
            padding: const EdgeInsets.fromLTRB(20, 0, 20, 0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "Exception Type",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: black_shade_1),
                ),
                SizedBox(
                  height: 17.h,
                ),
                Row(
                  children: [
                    Row(
                      children: [
                        SizedBox(
                          height: 10.h,
                          width: 10.h,
                          child: Radio(
                            activeColor: solid_primary,
                            value: "value",
                            groupValue: "groupValue",
                            onChanged: ((value) {}),
                          ),
                        ),
                        SizedBox(
                          width: 8.w,
                        ),
                        Text(
                          "Permanent",
                          style: Theme.of(context)
                              .textTheme
                              .bodyLarge
                              ?.copyWith(
                                  fontSize: 14,
                                  fontWeight: regular,
                                  color: black_shade_1),
                        ),
                      ],
                    ),
                    SizedBox(
                      width: 28.w,
                    ),
                    Row(
                      children: [
                        SizedBox(
                          height: 10.h,
                          width: 10.h,
                          child: Radio(
                            value: "value",
                            groupValue: "groupValue",
                            onChanged: ((value) {}),
                          ),
                        ),
                        SizedBox(
                          width: 8.w,
                        ),
                        Text(
                          "Temporary",
                          style: Theme.of(context)
                              .textTheme
                              .bodyLarge
                              ?.copyWith(
                                  fontSize: 14,
                                  fontWeight: regular,
                                  color: black_shade_1),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(20, 0, 20, 0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "Comments",
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontSize: 14, fontWeight: semiBold, color: black_shade_1),
                ),
                SizedBox(
                  height: 11.h,
                ),
                SizedBox(
                  width: 284.w,
                  height: 129.h,
                  child: TextField(
                    maxLines: 5,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(5),
                        borderSide: BorderSide(
                          color: secondaryColors.elementAt(14),
                        ),
                      ),
                      hintText: "Add comments for marking the exception",
                      hintStyle: Theme.of(context)
                          .textTheme
                          .bodyLarge
                          ?.copyWith(
                              fontSize: 14,
                              fontWeight: regular,
                              color: secondaryColors.elementAt(2)),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
