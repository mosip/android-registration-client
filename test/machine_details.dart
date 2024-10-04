enum MachineAction {
  copyText,
  downloadJson,
  error,
}

extension MachineActionExtension on MachineAction {
  String get value {
    switch(this) {
      case MachineAction.copyText:
        return "Text Copied to clipboard.";

      case MachineAction.downloadJson:
        return "File saved to downloads.";

      case MachineAction.error:
        return "Some error occurred!";

      default:
        return "Some error occurred!";
    }
  }
}

class MachineDetails {
  String performAction(String action) {
    if(action == 'Copy Text') {
      return MachineAction.copyText.value;
    }

    if(action == "Download JSON") {
      return MachineAction.downloadJson.value;
    }

    return MachineAction.error.value;
  }
}