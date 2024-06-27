import 'package:flutter/material.dart';
import 'package:qr_code_scanner/qr_code_scanner.dart';
import 'package:registration_client/utils/app_config.dart';

class QRCodeScannerApp extends StatefulWidget {
  const QRCodeScannerApp({super.key});

  @override
  _QRCodeScannerAppState createState() => _QRCodeScannerAppState();
}

class _QRCodeScannerAppState extends State<QRCodeScannerApp> {
  Barcode? result;
  late QRViewController _controller;
  final GlobalKey _qrKey = GlobalKey(debugLabel: 'QR');
  bool isFlashOn = false;
  bool isFrontCamera = false;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var scanArea = (MediaQuery.of(context).size.width < 400 ||
        MediaQuery.of(context).size.height < 400)
        ? 150.0
        : 300.0;

    return Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          toolbarHeight: 55,
          backgroundColor: Colors.black,
          automaticallyImplyLeading: false,
          title: SizedBox(
            height: 40,
            width: 100,
            child: FloatingActionButton.extended(
              heroTag: "top",
              onPressed: () {
                if (_controller != null) {
                  _controller.toggleFlash();
                  setState(() {
                    isFlashOn = !isFlashOn;
                  });
                }
              },
              backgroundColor: Colors.grey[800],
              elevation: 0,
              label: Text(isFlashOn ? 'OFF':'ON',style: const TextStyle(color: appWhite)),
              icon: const Icon(Icons.flashlight_on_outlined,color: appWhite),
            ),
          ),
        ),
        body: Column(
          children: [
            Expanded(
              flex: 5,
              child: QRView(
                key: _qrKey,
                onQRViewCreated: _onQRViewCreated,
                overlay: QrScannerOverlayShape(
                    borderColor: solidPrimary,
                    borderRadius: 10,
                    borderLength: 30,
                    borderWidth: 10,
                    cutOutSize: scanArea),
              ),
            ),
            Expanded(
              flex: 1,
              child: Container(
                color: Colors.black,
                width: double.infinity,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        SizedBox(
                          height: 70,
                          width: 70,
                          child: FloatingActionButton(
                            heroTag: "one",
                            onPressed: () {
                              Navigator.of(context).pop();
                            },
                            backgroundColor: Colors.grey[800],
                            elevation: 0,
                            child: const Icon(Icons.close,size: 40,color: appWhite),
                          ),
                        ),
                        const SizedBox(height: 5),
                        const Text('CANCEL',style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold))
                      ],
                    ),
                    SizedBox(
                      height: 90,
                      width: 90,
                      child: FloatingActionButton(
                        heroTag: "two",
                        backgroundColor: Colors.white,
                        onPressed: () {
                          if(result!=null) {
                            return Navigator.pop(context, result!.code);
                          }
                        },
                        elevation: 0,
                        child: const SizedBox.shrink(),
                      ),
                    ),
                    Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        SizedBox(
                          height: 70,
                          width: 70,
                          child: FloatingActionButton(
                            heroTag: "three",
                            onPressed: () {
                              if (_controller != null) {
                                _controller.flipCamera();
                                setState(() {
                                  isFrontCamera = !isFrontCamera;
                                });
                              }
                            },
                            backgroundColor: Colors.grey[800],
                            elevation: 0,
                            child: const Icon(Icons.flip_camera_ios_outlined,size: 40,color: appWhite),
                          ),
                        ),
                        const SizedBox(height: 5),
                        const Text('FLIP',style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold))
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      );
  }

  void _onQRViewCreated(QRViewController controller) {
    setState(() {
      _controller = controller;
    });
    _controller.scannedDataStream.listen((scanData) {
      setState(() {
        result = scanData;
      });// Handle the scanned data as desired
    });
  }
}