import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:registration_client/model/field.dart';
import 'package:registration_client/model/screen.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_checkbox.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_html_box.dart';
import 'package:registration_client/ui/process_ui/widgets/custom_preferred_lang_button.dart';

class NewProcessScreenContent extends StatelessWidget {
  const NewProcessScreenContent({super.key, required this.context, required this.screen});
  final BuildContext context;
  final Screen screen;

  Widget widgetType(Field e){
     if(e.controlType=="checkbox"){
        return CustomCheckbox(field: e);
     }
     if(e.controlType=="html"){
        return CustomHtmlBox(field: e);
     }
     if(e.controlType=="button"){
        if(e.subType=="preferredLang"){
          return CustomPreferredLangButton(field: e);
        }
        return CustomCheckbox(field: e);
     }
     return Text("${e.controlType}");
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...screen.fields!.map((e){
          if(e!.inputRequired == true){
            return widgetType(e);
          }
          return Container();
        }).toList(),
      ],
    );
  }
}