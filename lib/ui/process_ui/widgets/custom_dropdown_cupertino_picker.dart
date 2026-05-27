/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:registration_client/pigeon/document_category_pigeon.dart';
import 'package:registration_client/utils/app_config.dart';

class CustomCupertinoDropDownPicker extends StatefulWidget {
  final AsyncSnapshot snapshot;
  final double itemExtent;
  final Widget selectionOverlay;
  final double diameterRatio;
  final Color? backgroundColor;
  final double offAxisFraction;
  final bool useMagnifier;
  final double magnification;
  final double squeeze;
  final String? initialValue;
  final void Function(String label, String code) onSelectedItemChanged;
  final TextStyle? selectedStyle;
  final TextStyle? unselectedStyle;

  const CustomCupertinoDropDownPicker({
    Key? key,
    required this.snapshot,
    required this.itemExtent,
    required this.onSelectedItemChanged,
    this.selectedStyle,
    this.unselectedStyle,
    this.backgroundColor,
    this.squeeze = 1.45,
    this.diameterRatio = 1.1,
    this.magnification = 1.0,
    this.offAxisFraction = 0.0,
    this.useMagnifier = false,
    this.selectionOverlay = const CupertinoPickerDefaultSelectionOverlay(),
    this.initialValue,
  }) : super(key: key);

  @override
  State<CustomCupertinoDropDownPicker> createState() =>
      _CustomCupertinoDropDownPickerState();
}

class _CustomCupertinoDropDownPickerState
    extends State<CustomCupertinoDropDownPicker> {
  late int _selectedIndex;
  late final FixedExtentScrollController _scrollController;

  @override
  void initState() {
    super.initState();
    _selectedIndex = _getInitialSelectedIndex();
    _scrollController =
        FixedExtentScrollController(initialItem: _selectedIndex);
    _syncSelectionWithSnapshot(notifySingleItemSelection: true);
  }

  @override
  void didUpdateWidget(covariant CustomCupertinoDropDownPicker oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.snapshot != widget.snapshot ||
        oldWidget.initialValue != widget.initialValue) {
      _syncSelectionWithSnapshot(notifySingleItemSelection: true);
    }
  }

  int _getInitialSelectedIndex() {
    if (widget.initialValue != null && widget.snapshot.hasData) {
      final items = widget.snapshot.data as List<dynamic>;
      final index = items.indexWhere((item) {
        if (item is DocumentType) {
          return item.code == widget.initialValue ||
              item.label == widget.initialValue;
        }
        return item.toString() == widget.initialValue;
      });
      return index >= 0 ? index : 0;
    }
    return 0;
  }

  void _syncSelectionWithSnapshot({required bool notifySingleItemSelection}) {
    _updateSelectedIndex();
    _jumpToSelectedIndex();
    if (notifySingleItemSelection) {
      _autoSelectIfSingleItem();
    }
  }

  void _updateSelectedIndex() {
    final int nextIndex = _getInitialSelectedIndex();
    if (nextIndex != _selectedIndex && mounted) {
      setState(() => _selectedIndex = nextIndex);
    } else {
      _selectedIndex = nextIndex;
    }
  }

  void _jumpToSelectedIndex() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.jumpToItem(_selectedIndex);
      }
    });
  }

  void _autoSelectIfSingleItem() {
    if (!widget.snapshot.hasData) return;
    final items = widget.snapshot.data as List<dynamic>;
    if (items.length != 1) return;

    final item = items[0];
    if (item == null) return;
    final label = item is DocumentType ? item.label : item.toString();
    final code = item is DocumentType ? item.code : item.toString();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      widget.onSelectedItemChanged(label, code);
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.snapshot.connectionState == ConnectionState.waiting) {
      return const Center(child: CupertinoActivityIndicator());
    }

    if (!widget.snapshot.hasData ||
        widget.snapshot.data == null ||
        (widget.snapshot.data is List &&
            (widget.snapshot.data as List).isEmpty)) {
      return const Center(child: Text("No data available"));
    }

    final List<dynamic> items = widget.snapshot.data as List<dynamic>;

    return CupertinoPicker.builder(
      childCount: items.length,
      squeeze: widget.squeeze,
      itemExtent: widget.itemExtent,
      scrollController: _scrollController,
      useMagnifier: widget.useMagnifier,
      diameterRatio: widget.diameterRatio,
      magnification: widget.magnification,
      backgroundColor: widget.backgroundColor,
      offAxisFraction: widget.offAxisFraction,
      selectionOverlay: widget.selectionOverlay,
      onSelectedItemChanged: (index) {
        setState(() => _selectedIndex = index);
        final item = items[index];
        if (item == null) return;
        final label = item is DocumentType ? item.label : item.toString();
        final code = item is DocumentType ? item.code : item.toString();
        widget.onSelectedItemChanged(label, code);
      },
      itemBuilder: (context, index) {
        final item = items[index];
        final displayText = item == null
            ? ''
            : (item is DocumentType ? item.label : item.toString());
        return ListTile(
          title: Center(
            child: Text(
              displayText,
              style: index == _selectedIndex
                  ? widget.selectedStyle
                  : widget.unselectedStyle,
            ),
          ),
          trailing: Icon(
            Icons.check,
            size: 28,
            color: index == _selectedIndex ? dropDownSelector : Colors.white,
          ),
        );
      },
    );
  }
}

