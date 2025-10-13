import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../../../provider/registration_task_provider.dart';

class AdditionalInfoReqIdControl extends StatefulWidget {
  final VoidCallback? onFetched;
  const AdditionalInfoReqIdControl({Key? key, this.onFetched}) : super(key: key);

  @override
  State<AdditionalInfoReqIdControl> createState() => _AdditionalInfoReqIdControlState();
}

class _AdditionalInfoReqIdControlState extends State<AdditionalInfoReqIdControl> {
  late GlobalProvider globalProvider;
  late RegistrationTaskProvider registrationTaskProvider;
  final TextEditingController reqIdController = TextEditingController();

  @override
  void initState() {
    globalProvider = Provider.of<GlobalProvider>(context, listen: false);
    registrationTaskProvider = Provider.of<RegistrationTaskProvider>(context, listen: false);
    if (globalProvider.additionalInfoReqId != null && globalProvider.additionalInfoReqId!.isNotEmpty) {
      reqIdController.text = globalProvider.additionalInfoReqId!;
    }
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    bool isPortrait = MediaQuery.of(context).orientation == Orientation.portrait;
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
              AppLocalizations.of(context)!.additional_info_req_id,
              style: TextStyle(
                  fontSize: isPortrait ? 18 : 14),
            ),
            SizedBox(height: 10.h),
            Row(
              children: [
                Expanded(
                  flex: 3,
                  child: TextFormField(
                    controller: reqIdController,
                    onChanged: (value) {
                      globalProvider.setAdditionalInfoReqId(value);
                      registrationTaskProvider.setAdditionalReqId(value);
                    },
                    textAlign: TextAlign.left,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8.0),
                        borderSide: const BorderSide(color: appGreyShade, width: 1),
                      ),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                      hintText: AppLocalizations.of(context)!.enter_additional_info_req_id,
                      hintStyle: const TextStyle(color: appBlackShade3, fontSize: 14),
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
