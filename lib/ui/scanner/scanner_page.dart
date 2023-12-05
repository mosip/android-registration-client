import 'dart:io';

import 'package:crop_image/crop_image.dart';
import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
//import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:provider/provider.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';

class ScannerPage extends StatefulWidget {
  final String title;
  //final String subTitle;

  const ScannerPage({
    Key? key,
    required this.title,
    //required this.subTitle,
  }) : super(key: key);

  @override
  State<ScannerPage> createState() => _ScannerPageState();
}

class _ScannerPageState extends State<ScannerPage> {
  XFile? _pickedFile;
  Image? _croppedFile;
  final controller = CropController(
    aspectRatio: 1,
    defaultCrop: const Rect.fromLTRB(0.1, 0.1, 0.7, 0.7),
  );

  @override
  void initState() {
  //  WidgetsBinding.instance.addPostFrameCallback((_){
      _uploadImage();
  //  });
    super.initState();
  }



  _documentScanFailedAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-005", "REG-MOD-103");
  }

  _documentPreviewAudit() async {
    await context.read<GlobalProvider>().getAudit("REG-EVT-006", "REG-MOD-103");
  }

  @override
  Widget build(BuildContext context) {
    return _pickedFile != null?Scaffold(
      appBar: !kIsWeb
          ? AppBar(
        title: Text(
          widget.title,
          style: const TextStyle(color: Colors.black),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          IconButton(
              onPressed: () {
                Navigator.pop(context);
              },
              icon: const Icon(
                Icons.close,
                color: Colors.black,
              )),
        ],
      )
          : null,
      body: Column(
        mainAxisSize: MainAxisSize.max,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (kIsWeb)
            Padding(
              padding: const EdgeInsets.all(kIsWeb ? 24.0 : 16.0),
              child: Text(
                widget.title,
                style: Theme.of(context)
                    .textTheme
                    .displayMedium!
                    .copyWith(color: Theme.of(context).highlightColor),
              ),
            ),
          Expanded(child: _body()),
        ],
      ),
    ):const SizedBox.shrink();
  }

  Widget _body() {
    if (_pickedFile != null) {
      return _image();
    } else {
      return _uploaderCard();
    }
  }


  Widget _image() {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    if (_croppedFile != null) {
      _documentPreviewAudit();
      final path = _croppedFile;
      // return ConstrainedBox(
      //   constraints: BoxConstraints(
      //     maxWidth: 0.8 * screenWidth,
      //     maxHeight: 0.7 * screenHeight,
      //   ),
      //   child: kIsWeb ? Image.network(path) : Image.file(File(path)),
      // );
      //}
      return Center(
        child: Column(
          children: [
            Expanded(
              flex: 6,
              child: Card(
                elevation: 4.0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16.0),
                ),
                child: SizedBox(
                  // width: kIsWeb ? 380.0 : 320.0,
                  // height: 400.0,
                  child: Column(
                    mainAxisSize: MainAxisSize.max,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Expanded(
                        child: Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: ConstrainedBox(
                            constraints: BoxConstraints(
                              maxWidth: 0.8 * screenWidth,
                              maxHeight: 0.7 * screenHeight,
                            ),
                            // child: kIsWeb
                            //     ? Image.network(path)
                            //     : Image.file(File(path)),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Expanded(
              flex: 3,
              child: Card(
                child: SizedBox(
                  //width: kIsWeb ? 380.0 : 320.0,
                  height: 200.0,
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          SizedBox(
                            width: 160,
                            child: OutlinedButton(
                              onPressed: () {
                                //_cropImage();
                              },
                              child: const Text('Crop'),
                            ),
                          ),
                          SizedBox(
                            width: 160,
                            child: OutlinedButton(
                              onPressed: () {
                                _clear();
                              },
                              child: const Text('Delete'),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: SizedBox(
                      height: 40,
                      child: ElevatedButton(
                        onPressed: () async {
                          if (_croppedFile != null) {
                            Navigator.pop(context, _croppedFile);
                          } else {
                            Navigator.pop(context, _pickedFile!.path);
                          }
                        },
                        child: const Text(
                          'RETAKE',
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ),
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: SizedBox(
                      height: 40,
                      child: ElevatedButton(
                        onPressed: () async {
                          if (_croppedFile != null) {
                            Navigator.pop(context, _croppedFile);
                          } else {
                            Navigator.pop(context, _pickedFile!.path);
                          }
                        },
                        child: const Text(
                          'SAVE',
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      );
    } else if (_pickedFile != null) {
      _documentPreviewAudit();
      final path = _pickedFile!.path;
      // return ConstrainedBox(
      //   constraints: BoxConstraints(
      //     maxWidth: 0.8 * screenWidth,
      //     maxHeight: 0.7 * screenHeight,
      //   ),
      //   child: kIsWeb ? Image.network(path) :
      //   Image.file(File(path)),
      // );
      return Center(
        child: Column(
          children: [
            Expanded(
              flex: 6,
              child: Card(
                elevation: 0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16.0),
                ),
                child: SizedBox(
                  // width: kIsWeb ? 380.0 : 320.0,
                  // height: 400.0,
                  child: Column(
                    mainAxisSize: MainAxisSize.max,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Expanded(
                        // child: Padding(
                        //   padding: const EdgeInsets.all(16.0),
                        //   child: ConstrainedBox(
                        //     constraints: BoxConstraints(
                        //       maxWidth: 0.8 * screenWidth,
                        //       maxHeight: 0.7 * screenHeight,
                        //     ),
                        //     child: kIsWeb
                        //         ? Image.network(path)
                        //         : Image.file(File(path)),
                        //   ),
                        // ),
                        child: Center(
                          child: CropImage(
                            controller: controller,
                            gridColor: Colors.indigo,
                            gridThickWidth: 12,
                            gridThinWidth: 3,
                            image: Image.file(File(path)),
                            paddingSize: 0.0,
                            scrimColor: Colors.white,
                            alwaysMove: true,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            // Expanded(
            //   flex: 3,
            //   child: Card(
            //     child: SizedBox(
            //       //width: kIsWeb ? 380.0 : 320.0,
            //       height: 200.0,
            //       child: Column(
            //         children: [
            //           Row(
            //             mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            //             children: [
            //               SizedBox(
            //                 width: 160,
            //                 child: OutlinedButton(
            //                   onPressed: () {
            //                     _cropImage();
            //                   },
            //                   child: const Text('Crop'),
            //                 ),
            //               ),
            //               SizedBox(
            //                 width: 160,
            //                 child: OutlinedButton(
            //                   onPressed: () {
            //                     _clear();
            //                   },
            //                   child: const Text('Delete'),
            //                 ),
            //               ),
            //             ],
            //           ),
            //         ],
            //       ),
            //     ),
            //   ),
            // ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(10.0),
                    child: SizedBox(
                      height: 50,
                      child: OutlinedButton(
                        style: OutlinedButton.styleFrom(
                          elevation: 0,
                          backgroundColor: Colors.white,
                          side: BorderSide(width: 2.0, color: solidPrimary),
                          shape: const RoundedRectangleBorder(
                            borderRadius: BorderRadius.all(Radius.circular(2)),
                          ),
                        ),
                        onPressed: () async {
                          _clear();
                          _uploadImage();
                        },
                        child: Text(
                          'RETAKE',
                          style: TextStyle(fontSize: 16,color: solidPrimary),
                        ),
                      ),
                    ),
                  ),
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(10.0),
                    child: SizedBox(
                      height: 50,
                      child: ElevatedButton(
                        onPressed: () async {
                          //Navigator.pop(context);
                          //XFile result = await XFile(file.path);
                          await controller.croppedImage().then((value) async {
                           // var file = await DefaultCacheManager().getSingleFile(value);
                            _croppedFile = value;
                          });
                          if (_croppedFile != null) {
                            Navigator.pop(context, _croppedFile);
                          } else {
                            Navigator.pop(context, _pickedFile!.path);
                          }
                        },
                        child: const Text(
                          'SAVE',
                          style: TextStyle(fontSize: 16),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      );
    } else {
      _documentScanFailedAudit();
      return const SizedBox.shrink();
    }
  }

  // Widget _menu() {
  //   return Row(
  //     mainAxisSize: MainAxisSize.min,
  //     children: [
  //       FloatingActionButton(
  //         onPressed: () {
  //           _clear();
  //         },
  //         backgroundColor: Colors.redAccent,
  //         tooltip: 'Delete',
  //         child: const Icon(Icons.delete),
  //       ),
  //       if (_croppedFile == null)
  //         Padding(
  //           padding: const EdgeInsets.only(left: 32.0),
  //           child: FloatingActionButton(
  //             onPressed: () {
  //               _cropImage();
  //             },
  //             backgroundColor: const Color(0xFFBC764A),
  //             tooltip: 'Crop',
  //             child: const Icon(Icons.crop),
  //           ),
  //         )
  //     ],
  //   );
  // }

  Widget _uploaderCard() {
    return Center(
      child: Column(
        children: [
          Expanded(
            flex: 6,
            child: Card(
              elevation: 4.0,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16.0),
              ),
              child: SizedBox(
                // width: kIsWeb ? 380.0 : 320.0,
                // height: 400.0,
                child: Column(
                  mainAxisSize: MainAxisSize.max,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Expanded(
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: DottedBorder(
                          radius: const Radius.circular(12.0),
                          borderType: BorderType.RRect,
                          dashPattern: const [8, 4],
                          color:
                          Theme.of(context).highlightColor.withOpacity(0.4),
                          child: Container(),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          Expanded(
            flex: 3,
            child: Card(
              child: SizedBox(
                //width: kIsWeb ? 380.0 : 320.0,
                height: 200.0,
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        SizedBox(
                          width: 160,
                          child: OutlinedButton(
                            onPressed: () {
                              _uploadImage();
                            },
                            child: const Text('Stream'),
                          ),
                        ),
                        SizedBox(
                          width: 160,
                          child: OutlinedButton(
                            onPressed: () {
                              _uploadImage();
                            },
                            child: const Text('Capture'),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
          Row(
            children: [
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: SizedBox(
                    height: 40,
                    child: ElevatedButton(
                      onPressed: () {
                        //Navigator.pop(context);
                        if (_croppedFile != null) {
                          Navigator.pop(context, _croppedFile);
                        } else {
                          Navigator.pop(context, _pickedFile!.path);
                        }
                      },
                      child: const Text(
                        'Save',
                        style: TextStyle(fontSize: 16),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }



  Future<void> _uploadImage() async {
    final pickedFile =
    await ImagePicker().pickImage(source: ImageSource.camera);
    if (pickedFile != null) {
      setState(() {
        _pickedFile = pickedFile;
      });
    }
  }



  void _clear() {
    setState(() {
      _pickedFile = null;
      _croppedFile = null;
    });
  }
}
