import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../../provider/export_packet_provider.dart';
import '../../../utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ExportTable extends StatelessWidget {
  const ExportTable({super.key});

  @override
  Widget build(BuildContext context) {
    double tableWidth = 1500;
    TextStyle? textTheme = Theme.of(context).textTheme.bodySmall?.copyWith(fontSize: 16, fontWeight: FontWeight.bold);

    return SizedBox(
      width: tableWidth,
      child: ListView.separated(
        itemCount: context.watch<ExportPacketsProvider>().matchingPackets.length+1,
        itemBuilder: (BuildContext context, int index) {

          if(index == 0){
            return   Column(
              children: [
                Container(
                  color: Colors.white,
                  child: ListTile(
                    horizontalTitleGap: 0,
                    leading: Checkbox(
                      activeColor: solidPrimary,
                      tristate: true,
                      side: BorderSide(color: solidPrimary, width: 1.5),
                      value: context.watch<ExportPacketsProvider>().countSelected==0?false: (context.watch<ExportPacketsProvider>().countSelected == context.watch<ExportPacketsProvider>().matchingPackets.length?true:null),
                      onChanged: (bool? value) {
                        context.read<ExportPacketsProvider>().setSelectedAll(value??false);
                      },
                    ),
                    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 0),
                    title:  Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        SizedBox(width: tableWidth/35, child: Text(AppLocalizations.of(context)!.serial_number, textAlign: TextAlign.center, style: textTheme)) ,
                        SizedBox(width: tableWidth/5.5, child: Text(AppLocalizations.of(context)!.application_id, textAlign: TextAlign.center, style: textTheme)),
                        SizedBox(width: tableWidth/9, child: Text(AppLocalizations.of(context)!.reg_date, textAlign: TextAlign.center, style: textTheme)),
                        SizedBox(width: tableWidth/15, child: Text(AppLocalizations.of(context)!.reg_type,textAlign: TextAlign.center, style: textTheme)),
                        SizedBox(width: tableWidth/9, child: Text(AppLocalizations.of(context)!.client_status,textAlign: TextAlign.center, style: textTheme)),
                        SizedBox(width: tableWidth/9, child: Text(AppLocalizations.of(context)!.server_status,textAlign: TextAlign.center, style: textTheme)),
                        SizedBox(width: tableWidth/15, child: Text(AppLocalizations.of(context)!.operator_id,textAlign: TextAlign.center, style: textTheme)),
                      ],
                    ),
                    onTap: () {
                      // Handle tile selection here
                      // You can access myClass.myList[index]
                    },
                  ),
                ),
                Container(height: 1.25, color:Colors.grey.shade200),
              ],
            );
          }
          var dateTime = DateTime.fromMillisecondsSinceEpoch(context.watch<ExportPacketsProvider>().matchingPackets[index-1].crDtime??DateTime.now().millisecond);
          var formattedDate = DateFormat('dd/MM/yyyy, hh:mm a').format(dateTime);
          return ListTile(
            horizontalTitleGap: 0,
            leading: Checkbox( activeColor: solidPrimary, value: context.watch<ExportPacketsProvider>().matchingSelected[index-1], onChanged: (bool? value) {
              context.read<ExportPacketsProvider>().setSelected(index-1, value);
            }, side: BorderSide(color: solidPrimary, width: 1.5),),
            contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            tileColor:  context.watch<ExportPacketsProvider>().matchingSelected[index-1]? const Color(0XFFF0F5FF) : Colors.white,
            title: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                SizedBox(width: tableWidth/35, child:Text("$index.", textAlign: TextAlign.center, )),
                SizedBox(width: tableWidth/5.5, child: Text(context.watch<ExportPacketsProvider>().matchingPackets[index-1].appId ?? context.watch<ExportPacketsProvider>().matchingPackets[index-1].id!, textAlign: TextAlign.center,  overflow: TextOverflow.ellipsis,maxLines: 2,)),
                SizedBox(width: tableWidth/9, child: Text(formattedDate,textAlign: TextAlign.center, overflow: TextOverflow.ellipsis, maxLines: 2,)),
                SizedBox(width: tableWidth/15, child:Text(context.watch<ExportPacketsProvider>().matchingPackets[index-1].regType.toString(), textAlign: TextAlign.center, overflow: TextOverflow.ellipsis,maxLines: 2,)),
                SizedBox(width: tableWidth/9, child: Text(context.watch<ExportPacketsProvider>().matchingPackets[index-1].clientStatus.toString(),textAlign: TextAlign.center, overflow: TextOverflow.ellipsis,maxLines: 2,)),
                SizedBox(width: tableWidth/9, child: Text(context.watch<ExportPacketsProvider>().matchingPackets[index-1].serverStatus?? "NOT UPLOADED", textAlign: TextAlign.center, overflow: TextOverflow.ellipsis,maxLines: 2,)),
                SizedBox(width: tableWidth/15, child:Text(context.watch<ExportPacketsProvider>().matchingPackets[index-1].crBy.toString(), textAlign: TextAlign.center, overflow: TextOverflow.ellipsis,maxLines: 2,)),

              ],
            ),
            onTap: () {
              // Handle tile selection here
            },
          );
        },
        separatorBuilder: (BuildContext context, int index) {
          return Container(height: 1,color:context.watch<ExportPacketsProvider>().matchingSelected[index]?Colors.white: const Color(0XFFF0F5FF),);
        },
      ),
    );
  }
}
