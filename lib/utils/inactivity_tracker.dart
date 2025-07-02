import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import '../main.dart';

class InactivityTracker extends StatefulWidget {
  final Widget child;
  final Duration timeout;       // inactivity before warning
  final Duration gracePeriod;   // countdown inside dialog
  final bool isUserLoggedIn;
  final Future<void> Function() onTimeout;

  const InactivityTracker({
    Key? key,
    required this.child,
    required this.timeout,
    required this.gracePeriod,
    required this.isUserLoggedIn,
    required this.onTimeout,
  }) : super(key: key);

  @override
  State<InactivityTracker> createState() => _InactivityTrackerState();
}

class _InactivityTrackerState extends State<InactivityTracker> with WidgetsBindingObserver {
  Timer? _inactivityTimer;
  Timer? _logoutTicker;
  bool _warningShown = false;
  final ValueNotifier<int> _countdown = ValueNotifier<int>(0);
  DateTime? _pausedAt;
  bool _dialogOpen = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _startInactivityTimer();
  }

  @override
  void didUpdateWidget(covariant InactivityTracker oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.isUserLoggedIn != widget.isUserLoggedIn ||
        oldWidget.timeout != widget.timeout ||
        oldWidget.gracePeriod != widget.gracePeriod) {
      _resetAllTimers();
    }
  }

  @override
  void dispose() {
    _countdown.dispose();
    _cancelAllTimers();
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  void _startInactivityTimer() {
    if (!widget.isUserLoggedIn) return;
    _inactivityTimer = Timer(widget.timeout, _showWarningDialog);
  }

  void _resetAllTimers() {
    _inactivityTimer?.cancel();
    _logoutTicker?.cancel();
    _warningShown = false;
    _startInactivityTimer();
  }

  void _cancelAllTimers() {
    _inactivityTimer?.cancel();
    _logoutTicker?.cancel();
  }

  void _showWarningDialog() {
    if (!widget.isUserLoggedIn) return;
    _warningShown = true;
    _dialogOpen = true;
    _countdown.value = widget.gracePeriod.inSeconds;

    _logoutTicker = Timer.periodic(const Duration(seconds: 1), (t) {
      _countdown.value -= 1;
      if (_countdown.value <= 0) {
        t.cancel();
        widget.onTimeout();
      }
    });

    final dialogCtx = rootNavigatorKey.currentContext!;
    final loc = AppLocalizations.of(dialogCtx)!;

    showDialog<void>(
      context: dialogCtx,
      barrierDismissible: false,
      builder: (_) => _IdleWarningDialog(
        countdown: _countdown,
        onStayLoggedIn: () {
          _logoutTicker?.cancel();
          _warningShown = false;
          _dialogOpen = false;
          Navigator.of(dialogCtx, rootNavigator: true).pop();
          _resetAllTimers();
        },
        onLogOut: () async {
          Navigator.of(dialogCtx, rootNavigator: true).pop();
          _dialogOpen = false;
          await widget.onTimeout();
        },
        loc: loc,
      ),
    );
  }

  void _onUserInteraction() {
    if (!widget.isUserLoggedIn) return;
    if (_dialogOpen) return;
    if (_warningShown) {
      _logoutTicker?.cancel();
      Navigator.of(rootNavigatorKey.currentContext!, rootNavigator: true).maybePop();
      _warningShown = false;
    }
    _resetAllTimers();
  }

  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
      case AppLifecycleState.inactive:
      case AppLifecycleState.detached:
        _pausedAt = DateTime.now();
        _cancelAllTimers();
        break;

      case AppLifecycleState.resumed:
        if (!widget.isUserLoggedIn) return;

        if (_pausedAt != null) {
          final away = DateTime.now().difference(_pausedAt!);

          if (away >= widget.timeout + widget.gracePeriod) {
            widget.onTimeout();
            _pausedAt = null;
            return;
          }

          if (away >= widget.timeout) {
            final remainingGrace = widget.gracePeriod - (away - widget.timeout);
            _countdown.value = remainingGrace.inSeconds;
            _showWarningDialog();
            _pausedAt = null;
            return;
          }
        }

        _pausedAt = null;
        _resetAllTimers();
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTap: _onUserInteraction,
      onPanDown: (_) => _onUserInteraction(),
      onScaleStart: (_) => _onUserInteraction(),
      child: widget.child,
    );
  }
}

class _IdleWarningDialog extends StatelessWidget {
  const _IdleWarningDialog({
    required this.countdown,
    required this.onStayLoggedIn,
    required this.onLogOut,
    required this.loc,
  });

  final ValueNotifier<int> countdown;
  final VoidCallback onStayLoggedIn;
  final Future<void> Function() onLogOut;
  final AppLocalizations loc;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 420),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.fromLTRB(24, 20, 24, 20),
              decoration: const BoxDecoration(
                border: Border(
                  bottom: BorderSide(color: Color(0xFFE5EBFA), width: 1),
                ),
              ),
              child: Text(
                loc.inactive_logout_heading,
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  fontSize: 22,
                ),
              ),
            ),
            Container(
              padding: const EdgeInsets.fromLTRB(24, 20, 24, 20),
              decoration: const BoxDecoration(
                border: Border(
                  bottom: BorderSide(color: Color(0xFFE5EBFA), width: 1),
                ),
              ),
              child: ValueListenableBuilder<int>(
                valueListenable: countdown,
                builder: (_, secs, __) {
                  // Decide which unit to show
                  final String timeLeft = secs >= 60
                      ? '${(secs / 60).ceil()}${loc.minutes}'   // e.g. “2 minutes”
                      : '$secs${loc.seconds}';                   // e.g. “45 seconds”

                  return RichText(
                    text: TextSpan(
                      style: theme.textTheme.bodyMedium,
                      children: [
                        TextSpan(text: loc.inactive_logout_description),
                        TextSpan(
                          text: timeLeft,
                          style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
            const SizedBox(height: 24),
            Padding(
              padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
              child: Row(
                children: [
                  Expanded(
                    child: OutlinedButton(
                      onPressed: onLogOut,
                      child: Text(loc.logout_button), // You can localize this too
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: onStayLoggedIn,
                      child: Text(loc.stay_logged_in_button), // And this
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
