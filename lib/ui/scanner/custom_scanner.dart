import 'dart:async';
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:native_image_cropper/native_image_cropper.dart';
import 'package:registration_client/provider/global_provider.dart';
import 'package:registration_client/utils/app_config.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';

import '../../model/field.dart';

class CustomScanner extends StatefulWidget {
  final Field field;
  const CustomScanner({
    required this.field,
    super.key,
  });

  @override
  State<CustomScanner> createState() => _CustomScannerState();
}

class _CustomScannerState extends State<CustomScanner> {
  Uint8List? _pickedFile;
  late CropController _controller;
  CropMode mode = CropMode.rect;
  double? _aspectRatio;
  ImageFormat format = ImageFormat.jpg;

  @override
  void initState() {
    super.initState();
    _uploadImage();
    _controller = CropController();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final cropPreview = _pickedFile!=null ? CropPreview(
      controller: _controller,
      bytes: _pickedFile!,
      mode: mode,
      hitSize: 30,
      loadingWidget: const CircularProgressIndicator(),
      maskOptions: MaskOptions(
        aspectRatio: _aspectRatio,
        backgroundColor: Colors.transparent.withOpacity(0.5),
        borderColor: solidPrimary,
        minSize: 100,
        //strokeWidth: 3,
      ),
      dragPointBuilder: _buildCropDragPoints,
    ) : const SizedBox.shrink();

    return _pickedFile!=null? Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text(
            context.read<GlobalProvider>().chooseLanguage(widget.field.label!),
          style: const TextStyle(color: Colors.black),
        ),
        backgroundColor: Colors.white,
        elevation: 1,
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
      ),
      body: _pickedFile!= null ? Column(
        children: [
         Expanded(
              child: cropPreview,
         ),
          ///customized options for cropper
          // Row(
          //   mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          //   children: [
          //     Flexible(
          //       child: ImageFormatDropdown(
          //         onChanged: (value) => _format = value,
          //       ),
          //     ),
          //     Flexible(
          //       child: AspectRatioDropdown(
          //         aspectRatio: _aspectRatio,
          //         onChanged: (value) => setState(() => _aspectRatio = value),
          //       ),
          //     ),
          //     Flexible(
          //       child: CropModesButtons(
          //         onChanged: (value) => setState(() => _mode = value),
          //       ),
          //     ),
          //   ],
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
                        _uploadImage();
                      },
                      child: Text(
                        AppLocalizations.of(context)!.retake,
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
                        _cropImage(context);
                      },
                      child: Text(
                        AppLocalizations.of(context)!.save,
                        style: const TextStyle(fontSize: 16),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),
        ],
      ) : const SizedBox.shrink(),
    ): const SizedBox.shrink();
  }

  Future<void> _uploadImage() async {
    final pickedFile =
    await ImagePicker().pickImage(source: ImageSource.camera);
    if (pickedFile != null) {
      _pickedFile = await pickedFile.readAsBytes();
      setState(() {
      });
    }
  }

  Future<void> _cropImage(BuildContext context) async {
    final croppedBytes = await _controller.crop(format: format);
    if (mounted) {
      return  Navigator.pop(context, croppedBytes);
    }
  }

  CustomPaint _buildCropDragPoints(
      double size,
      CropDragPointPosition position,
      ) {
    List<Offset> points;
    switch (position) {
      case CropDragPointPosition.topLeft:
        points = [
          Offset(0, size),
          Offset.zero,
          Offset(size, 0),
        ];
        break;
      case CropDragPointPosition.topRight:
        points = [
          Offset(-size, 0),
          Offset.zero,
          Offset(0, size),
        ];
        break;
      case CropDragPointPosition.bottomLeft:
        points = [
          Offset(0, -size),
          Offset.zero,
          Offset(size, 0),
        ];
        break;
      case CropDragPointPosition.bottomRight:
        points = [
          Offset(0, -size),
          Offset.zero,
          Offset(-size, 0),
        ];
    }

    return CustomPaint(
      foregroundPainter: _CropDragPointPainter(
        points: points,
        color: solidPrimary,
      ),
    );
  }
}

class _CropDragPointPainter extends CustomPainter {
  const _CropDragPointPainter({
    required this.points,
    required this.color,
  });

  final List<Offset> points;
  final Color color;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..strokeCap = StrokeCap.round
      ..strokeWidth = 4;
    canvas.drawPoints(PointMode.polygon, points, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
