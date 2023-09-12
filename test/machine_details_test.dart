import 'package:flutter_test/flutter_test.dart';

import 'machine_details.dart';

void main() {
  final machineDetails = MachineDetails();

  test("Copy Text", () {
    expect(machineDetails.performAction("Copy Text"), MachineAction.copyText.value);
  });

  test("Download JSON", () {
    expect(machineDetails.performAction("Download JSON"), MachineAction.downloadJson.value);
  });

  test("Share JSON", () {
    expect(machineDetails.performAction("Share JSON"), MachineAction.error.value);
  });
}