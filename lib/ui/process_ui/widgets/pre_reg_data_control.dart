import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:registration_client/utils/app_config.dart';

class PreRegDataControl extends StatefulWidget {
  const PreRegDataControl({super.key});

  @override
  State<PreRegDataControl> createState() => _PreRegDataControlState();
}

class _PreRegDataControlState extends State<PreRegDataControl> {
  final TextEditingController preRegIdController =
      TextEditingController(text: "");
  @override
  Widget build(BuildContext context) {
    bool isPortrait =
        MediaQuery.of(context).orientation == Orientation.portrait;
    return Card(
      elevation: 5,
      color: pureWhite,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(6.0),
      ),
      margin: EdgeInsets.symmetric(
          vertical: 20.h, horizontal: isPortrait ? 16.w : 0),
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 24.h, horizontal: 16.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "Pre-Registration ID",
              style: TextStyle(
                  fontSize: isPortrait && !isMobileSize ? 18 : 14,
                  fontWeight: semiBold),
            ),
            SizedBox(
              height: 10.h,
            ),
            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: TextFormField(
                    autovalidateMode: AutovalidateMode.onUserInteraction,
                    textCapitalization: TextCapitalization.words,
                    controller: preRegIdController,
                    onChanged: (value) {
                      preRegIdController.text = value;
                    },
                    textAlign: TextAlign.left,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                        borderSide:
                            const BorderSide(color: appGreyShade, width: 1),
                      ),
                      contentPadding: EdgeInsets.symmetric(horizontal: 16.w),
                      hintText: "Enter Pre-Registration ID",
                      hintStyle:
                          const TextStyle(color: appBlackShade3, fontSize: 14),
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: 30.w),
                    child: OutlinedButton(
                      style: OutlinedButton.styleFrom(
                        fixedSize: const Size(100, 50),
                        elevation: 0,
                        backgroundColor: Colors.white,
                        side: BorderSide(width: 1.0, color: solidPrimary),
                        shape: const RoundedRectangleBorder(
                          borderRadius: BorderRadius.all(Radius.circular(2)),
                        ),
                      ),
                      onPressed: () async {

                      },
                      child: Text(
                        "FETCH DATA",
                        style: TextStyle(fontSize: 16, color: solidPrimary,fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                ),
                const Spacer(),
              ],
            )
          ],
        ),
      ),
    );
  }
}
