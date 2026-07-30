import 'package:flutter_test/flutter_test.dart';
import 'package:registration_client/provider/auth_provider.dart';

void main() {
  late AuthProvider authProvider;

  setUp(() {
    authProvider = AuthProvider();
  });

  group('Initial State Tests', () {
    test('initial isLoggedIn should be false', () {
      expect(authProvider.isLoggedIn, false);
    });

    test('initial isSyncing should be false', () {
      expect(authProvider.isSyncing, false);
    });

    test('initial isOnboarded should be false', () {
      expect(authProvider.isOnboarded, false);
    });

    test('initial isDefault should be false', () {
      expect(authProvider.isDefault, false);
    });

    test('initial isSupervisor should be false', () {
      expect(authProvider.isSupervisor, false);
    });

    test('initial isOperator should be false', () {
      expect(authProvider.isOperator, false);
    });

    test('initial isOfficer should be false', () {
      expect(authProvider.isOfficer, false);
    });

    test('initial isValidUser should be false', () {
      expect(authProvider.isValidUser, false);
    });

    test('initial isLoggingIn should be false', () {
      expect(authProvider.isLoggingIn, false);
    });

    test('initial isPacketAuthenticated should be false', () {
      expect(authProvider.isPacketAuthenticated, false);
    });

    test('initial isMachineActive should be false', () {
      expect(authProvider.isMachineActive, false);
    });

    test('initial isCenterActive should be false', () {
      expect(authProvider.isCenterActive, false);
    });

    test('initial isNetworkPresent should be false', () {
      expect(authProvider.isNetworkPresent, false);
    });

    test('initial loginError should be empty', () {
      expect(authProvider.loginError, "");
    });

    test('initial packetError should be empty', () {
      expect(authProvider.packetError, "");
    });

    test('initial userId should be empty', () {
      expect(authProvider.userId, "");
    });

    test('initial username should be empty', () {
      expect(authProvider.username, "");
    });

    test('initial userEmail should be empty', () {
      expect(authProvider.userEmail, "");
    });

    test('initial forgotPasswordUrl should be empty', () {
      expect(authProvider.forgotPasswordUrl, "");
    });

    test('initial refreshedLoginTime should be empty', () {
      expect(authProvider.refreshedLoginTime, "");
    });

    test('initial idleTime should be empty', () {
      expect(authProvider.idleTime, "");
    });

    test('initial passwordLength should be empty', () {
      expect(authProvider.passwordLength, "");
    });
  });

  group('Setter Tests', () {
    test('setIsLoggedIn updates state', () {
      authProvider.setIsLoggedIn(true);

      expect(authProvider.isLoggedIn, true);
    });

    test('setIsSyncing updates state', () {
      authProvider.setIsSyncing(true);

      expect(authProvider.isSyncing, true);
    });

    test('setIsOnboarded updates state', () {
      authProvider.setIsOnboarded(true);

      expect(authProvider.isOnboarded, true);
    });

    test('setIsDefault updates state', () {
      authProvider.setIsDefault(true);

      expect(authProvider.isDefault, true);
    });

    test('setIsSupervisor updates state', () {
      authProvider.setIsSupervisor(true);

      expect(authProvider.isSupervisor, true);
    });

    test('setIsOperator updates state', () {
      authProvider.setIsOperator(true);

      expect(authProvider.isOperator, true);
    });

    test('setIsOfficer updates state', () {
      authProvider.setIsOfficer(true);

      expect(authProvider.isOfficer, true);
    });

    test('setIsValidUser updates state', () {
      authProvider.setIsValidUser(true);

      expect(authProvider.isValidUser, true);
    });

    test('setLoginError updates state', () {
      authProvider.setLoginError('Invalid Credentials');

      expect(authProvider.loginError, 'Invalid Credentials');
    });

    test('setIsPacketAuthenticated updates state', () {
      authProvider.setIsPacketAuthenticated(true);

      expect(authProvider.isPacketAuthenticated, true);
    });

    test('setIsMachineActive updates state', () {
      authProvider.setIsMachineActive(true);

      expect(authProvider.isMachineActive, true);
    });

    test('setIsCenterActive updates state', () {
      authProvider.setIsCenterActive(true);

      expect(authProvider.isCenterActive, true);
    });

    test('setPacketError updates state', () {
      authProvider.setPacketError('Packet Error');

      expect(authProvider.packetError, 'Packet Error');
    });

    test('setUserId updates state', () {
      authProvider.setUserId('user123');

      expect(authProvider.userId, 'user123');
    });

    test('setUsername updates state', () {
      authProvider.setUsername('Prashant');

      expect(authProvider.username, 'Prashant');
    });

    test('setUserEmail updates state', () {
      authProvider.setUserEmail('test@example.com');

      expect(authProvider.userEmail, 'test@example.com');
    });

    test('setIsNetworkPresent updates state', () {
      authProvider.setIsNetworkPresent(true);

      expect(authProvider.isNetworkPresent, true);
    });

    test('setIsLoggingIn follows current implementation', () {
      authProvider.setIsLoggingIn(true);

      expect(authProvider.isLoggingIn, false);
    });
  });

  group('clearUser Tests', () {
    test('clearUser resets relevant user state values', () {
      authProvider.setIsLoggedIn(true);
      authProvider.setIsValidUser(true);
      authProvider.setIsDefault(true);
      authProvider.setIsOfficer(true);
      authProvider.setIsOnboarded(true);
      authProvider.setIsSupervisor(true);

      authProvider.clearUser();

      expect(authProvider.isLoggedIn, false);
      expect(authProvider.isValidUser, false);
      expect(authProvider.isDefault, false);
      expect(authProvider.isOfficer, false);
      expect(authProvider.isOnboarded, false);
      expect(authProvider.isSupervisor, false);
    });

    test('clearUser does not affect operator state', () {
      authProvider.setIsOperator(true);

      authProvider.clearUser();

      expect(authProvider.isOperator, true);
    });

    test('clearUser does not affect syncing state', () {
      authProvider.setIsSyncing(true);

      authProvider.clearUser();

      expect(authProvider.isSyncing, true);
    });
  });

  group('State Transition Tests', () {
    test('multiple sequential state updates work correctly', () {
      authProvider.setIsLoggedIn(true);
      authProvider.setIsSyncing(true);
      authProvider.setIsOperator(true);
      authProvider.setIsMachineActive(true);

      expect(authProvider.isLoggedIn, true);
      expect(authProvider.isSyncing, true);
      expect(authProvider.isOperator, true);
      expect(authProvider.isMachineActive, true);

      authProvider.clearUser();

      expect(authProvider.isLoggedIn, false);
      expect(authProvider.isSyncing, true);
      expect(authProvider.isOperator, true);
      expect(authProvider.isMachineActive, true);
    });
  });

  group('Edge Case Tests', () {
    test('set empty username', () {
      authProvider.setUsername('');

      expect(authProvider.username, '');
    });

    test('set empty userEmail', () {
      authProvider.setUserEmail('');

      expect(authProvider.userEmail, '');
    });

    test('set empty loginError', () {
      authProvider.setLoginError('');

      expect(authProvider.loginError, '');
    });

    test('set empty packetError', () {
      authProvider.setPacketError('');

      expect(authProvider.packetError, '');
    });

    test('set long username', () {
      final username = 'a' * 500;

      authProvider.setUsername(username);

      expect(authProvider.username, username);
    });

    test('set long email value', () {
      final email = '${'a' * 200}@example.com';

      authProvider.setUserEmail(email);

      expect(authProvider.userEmail, email);
    });
  });

  group('Notify Listener Behavior Tests', () {
    test('listener gets triggered on state change', () {
      int notifyCount = 0;

      authProvider.addListener(() {
        notifyCount++;
      });

      authProvider.setIsLoggedIn(true);

      expect(notifyCount, 1);
    });
    

    test('multiple listeners are triggered', () {
      int listenerOne = 0;
      int listenerTwo = 0;

      authProvider.addListener(() {
        listenerOne++;
      });

      authProvider.addListener(() {
        listenerTwo++;
      });

      authProvider.setIsDefault(true);

      expect(listenerOne, 1);
      expect(listenerTwo, 1);
    });
  });
}