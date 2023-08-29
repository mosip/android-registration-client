import 'dart:io';

import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
//import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';

class Scanner extends StatefulWidget {
  final String title;

  const Scanner({
    Key? key,
    required this.title,
  }) : super(key: key);

  @override
  State<Scanner> createState() => _ScannerState();
}

class _ScannerState extends State<Scanner> {
  XFile? _pickedFile;
  CroppedFile? _croppedFile;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
    );
  }

  Widget _body() {
    if (_croppedFile != null || _pickedFile != null) {
      return _image();
    } else {
      return _uploaderCard();
    }
  }

  // Widget _imageCard() {
  //   return Center(
  //     child: Column(
  //       mainAxisSize: MainAxisSize.min,
  //       crossAxisAlignment: CrossAxisAlignment.center,
  //       children: [
  //         Padding(
  //           padding:
  //               const EdgeInsets.symmetric(horizontal: kIsWeb ? 24.0 : 16.0),
  //           child: SizedBox(
  //             height: 600,
  //             width: 400,
  //             child: Card(
  //               elevation: 4.0,
  //               child: Padding(
  //                 padding: const EdgeInsets.all(kIsWeb ? 24.0 : 16.0),
  //                 child: _image(),
  //               ),
  //             ),
  //           ),
  //         ),
  //         //const SizedBox(height: 24.0),
  //         //_menu(),
  //       ],
  //     ),
  //   );
  // }

  // Widget _image() {
  //   final screenWidth = MediaQuery.of(context).size.width;
  //   final screenHeight = MediaQuery.of(context).size.height;
  //   if (_croppedFile != null) {
  //     final path = _croppedFile!.path;
  //     return ConstrainedBox(
  //       constraints: BoxConstraints(
  //         maxWidth: 0.8 * screenWidth,
  //         maxHeight: 0.7 * screenHeight,
  //       ),
  //       child: kIsWeb ? Image.network(path) : Image.file(File(path)),
  //     );
  //   } else if (_pickedFile != null) {
  //     final path = _pickedFile!.path;
  //     return ConstrainedBox(
  //       constraints: BoxConstraints(
  //         maxWidth: 0.8 * screenWidth,
  //         maxHeight: 0.7 * screenHeight,
  //       ),
  //       child: kIsWeb ? Image.network(path) :
  //       Image.file(File(path)),
  //     );
  //   } else {
  //     return const SizedBox.shrink();
  //   }
  // }

  Widget _image() {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    if (_croppedFile != null) {
      final path = _croppedFile!.path;
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
                            child: kIsWeb
                                ? Image.network(path)
                                : Image.file(File(path)),
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
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          SizedBox(
                            width: 160,
                            child: OutlinedButton(
                              onPressed: () {
                                _cropImage();
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
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: SizedBox(
                      height: 40,
                      child: ElevatedButton(
                        onPressed: () async {
                          if (_croppedFile != null) {
                            Navigator.pop(context, _croppedFile!.path);
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
    } else if (_pickedFile != null) {
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
                            child: kIsWeb
                                ? Image.network(path)
                                : Image.file(File(path)),
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
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          SizedBox(
                            width: 160,
                            child: OutlinedButton(
                              onPressed: () {
                                _cropImage();
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
                            Navigator.pop(context, _croppedFile!.path);
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
    } else {
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
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        SizedBox(
                          width: 160,
                          child: OutlinedButton(
                            onPressed: () {
                              _cropImage();
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
                          Navigator.pop(context, _croppedFile!.path);
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

  Future<void> _cropImage() async {
    if (_pickedFile != null) {
      final croppedFile = await ImageCropper().cropImage(
        sourcePath: _pickedFile!.path,
        compressFormat: ImageCompressFormat.jpg,
        compressQuality: 100,
        uiSettings: [
          AndroidUiSettings(
              toolbarTitle: 'Cropper',
              toolbarColor: Colors.transparent,
              toolbarWidgetColor: Colors.black,
              initAspectRatio: CropAspectRatioPreset.original,
              lockAspectRatio: false),
          IOSUiSettings(
            title: 'Cropper',
          ),
          WebUiSettings(
            context: context,
            presentStyle: CropperPresentStyle.dialog,
            boundary: const CroppieBoundary(
              width: 520,
              height: 520,
            ),
            viewPort:
                const CroppieViewPort(width: 480, height: 480, type: 'circle'),
            enableExif: true,
            enableZoom: true,
            showZoomer: true,
          ),
        ],
      );
      if (croppedFile != null) {
        setState(() {
          _croppedFile = croppedFile;
        });
      }
    }
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

  // Future<void> _saveImageToCache() async {
  //   // final cacheManager = await CacheManager()
  //   // .getInstance();
  //   final file = await DefaultCacheManager().getSingleFile(_pickedFile!.path);
  //   final dir = await getTemporaryDirectory();
  //   final path = '${dir.path}/thumbnail.png';
  //   await file.copy(path);
  // }
  //  Future<void> _loadCachedImage() async {
  //   final dir = await getTemporaryDirectory();
  //   final path = '${dir.path}/thumbnail.png';
  //   setState(() {
  //     _thumbnailUrl = path;
  //   });
  // }

  void _clear() {
    setState(() {
      _pickedFile = null;
      _croppedFile = null;
    });
  }
}
