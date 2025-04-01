/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
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
  final void Function(String) onSelectedItemChanged;
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
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _scrollController.jumpToItem(_selectedIndex);
    });
  }

  int _getInitialSelectedIndex() {
    if (widget.initialValue != null && widget.snapshot.hasData) {
      List<String> items =
      (widget.snapshot.data as List<dynamic>).whereType<String>().toList();
      return items.indexOf(widget.initialValue!);
    }
    return 0;
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

    // Convert data to List<String> to avoid type mismatch
    List<String> items =
    (widget.snapshot.data as List<dynamic>).whereType<String>().toList();

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
        widget.onSelectedItemChanged(items[index]);
      },
      itemBuilder: (context, index) => ListTile(
        title: Center(
          child: Text(
            items[index],
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
      ),
    );
  }
}