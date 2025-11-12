/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

enum ProcessType {
  newProcess,
  lostProcess,
  updateProcess,
  correctionProcess,
}

extension ProcessTypeExtension on ProcessType {
  String get id {
    switch (this) {
      case ProcessType.newProcess:
        return 'NEW';
      case ProcessType.lostProcess:
        return 'LOST';
      case ProcessType.updateProcess:
        return 'UPDATE';
      case ProcessType.correctionProcess:
        return 'CORRECTION';
    }
  }

  String get routeName {
    switch (this) {
      case ProcessType.newProcess:
        return '/new_process';
      case ProcessType.lostProcess:
        return '/lost_process';
      case ProcessType.updateProcess:
        return '/update_process';
      case ProcessType.correctionProcess:
        return '/correction_process';
    }
  }
}

