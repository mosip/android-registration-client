enum MachineAction {
  copy_text,
  download_json,
  error,
}

extension MachineActionExtension on MachineAction {
  String get value {
    switch(this) {
      case MachineAction.copy_text:
        return "Text Copied to clipboard.";

      case MachineAction.download_json:
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
      return MachineAction.copy_text.value;
    }

    if(action == "Download JSON") {
      return MachineAction.download_json.value;
    }

    return MachineAction.error.value;
  }
}