import 'package:flutter/material.dart';
import 'package:registration_client/utils/app_config.dart';
import '../../../pigeon/common_details_pigeon.dart';
import '../../../pigeon/global_config_settings_pigeon.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:provider/provider.dart';
import '../../../provider/global_provider.dart';

class GlobalConfigSettingsTab extends StatefulWidget {
  const GlobalConfigSettingsTab({Key? key}) : super(key: key);

  @override
  State<GlobalConfigSettingsTab> createState() => _GlobalConfigSettingsTabState();
}

class _GlobalConfigSettingsTabState extends State<GlobalConfigSettingsTab> {
  Map<String, Object>? serverValues;
  Map<String, String> localValues = {};
  Map<String, String> localConfigurations = {};
  List<String> permittedConfigurations = [];
  final Map<String, TextEditingController> _controllers = {};
  bool isLoading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    _loadInitialData();
  }

  /// Equivalent to loadInitialData() in desktop code
  Future<void> _loadInitialData() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
    });
    try {
      // Load registration params, local configurations, and permitted configurations in parallel

      serverValues = (await GlobalConfigSettingsApi().getRegistrationParams()).cast<String, Object>();
      localConfigurations = (await GlobalConfigSettingsApi().getLocalConfigurations()).cast<String, String>();
      permittedConfigurations = (await GlobalConfigSettingsApi().getPermittedConfigurationNames()).cast<String>();

      for (var key in serverValues!.keys) {
        _controllers[key]?.dispose();
        _controllers[key] = TextEditingController(
          text: _getLocalValue(key) == '-' ? '' : _getLocalValue(key),
        );
      }

      setState(() {
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        errorMessage = e.toString();
        isLoading = false;
      });
    }
  }

  @override
  void dispose() {
    for (var controller in _controllers.values) {
      controller.dispose();
    }
    super.dispose();
  }

  // Update _updateLocalValue to also update the controller's text if needed:
  void _updateLocalValue(String key, String value) {
    setState(() {
      if (value.isEmpty) {
        // If value is empty, remove from localValues to indicate no override
        localValues.remove(key);
      } else {
        localValues[key] = value;
      }
      print('localValues: $localValues');
    });
  }


  bool _isConfigurationPermitted(String configName) {
    return permittedConfigurations.contains(configName);
  }

  String _getLocalValue(String key) {
    // First check if user has modified it in this session
    if (localValues.containsKey(key)) {
      return localValues[key]!;
    }
    // Then check if there's a saved local configuration
    if (localConfigurations.containsKey(key)) {
      return localConfigurations[key]!;
    }
    // Return empty if no local value
    return '';
  }

  bool _hasChanges() {
    if (localValues.isEmpty) {
      print('_hasChanges: localValues is empty, returning false');
      return false;
    }

    // Check if any local value is different from server value
    for (String key in localValues.keys) {
      String serverValue = serverValues?[key]?.toString() ?? '-';
      String localValue = localValues[key]!;

      print('_hasChanges: checking $key - server: $serverValue, local: $localValue');

      // Consider it changed if:
      // 1. Local value is not empty and different from server value, OR
      // 2. Local value is empty but there was a previous local configuration
      if (localValue.isNotEmpty && localValue != serverValue) {
        print('_hasChanges: found change in $key - returning true');
        return true;
      }
      if (localValue.isEmpty && localConfigurations.containsKey(key)) {
        print('_hasChanges: found empty local value for $key with existing config - returning true');
        return true;
      }
    }
    print('_hasChanges: no changes found - returning false');
    return false;
  }

  void _onSaveChanges() {
    if (!_hasChanges()) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No changes to save')),
      );
      return;
    }

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Submit Changes'),
        content: Text('${localValues.length} configuration(s) will be updated.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              await _saveChanges();
            },
            child: const Text('Confirm'),
          ),
        ],
      ),
    );
  }

  Future<void> _saveChanges() async {
    try {
      // Show loading indicator
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => const Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              CircularProgressIndicator(),
              SizedBox(height: 16),
              Text('Saving configuration and refreshing app data...'),
            ],
          ),
        ),
      );

      // Save configuration changes
      await GlobalConfigSettingsApi().modifyConfigurations(localValues);

      // Get the GlobalProvider to refresh app data
      final globalProvider = Provider.of<GlobalProvider>(context, listen: false);
      
      // Refresh all global app data that depends on configuration
      await _refreshGlobalAppData(globalProvider);

      // Update local configurations with the saved values
      setState(() {
        localConfigurations.addAll(localValues);
        localValues.clear();
      });

      // Hide loading indicator
      if (mounted) {
        Navigator.of(context).pop();
      }

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Configuration saved and app data refreshed successfully'),
          duration: Duration(seconds: 3),
        ),
      );
    } catch (e) {
      // Hide loading indicator if there's an error
      if (mounted) {
        Navigator.of(context).pop();
      }
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error saving changes: $e')),
      );
    }
  }

  /// Refresh all global app data that depends on configuration
  Future<void> _refreshGlobalAppData(GlobalProvider globalProvider) async {
    try {
      // Refresh language data (depends on language configuration)
      await globalProvider.initializeLanguageDataList(false);
      
      // Refresh location hierarchy (depends on location configuration)
      await globalProvider.initializeLocationHierarchyMap();
      
      // Refresh registration center name (depends on center configuration)
      if (globalProvider.centerId.isNotEmpty) {
        await globalProvider.getRegCenterName(globalProvider.centerId, globalProvider.selectedLanguage);
      }
      
      // Refresh other configuration-dependent data
      await globalProvider.refreshConfigurationDependentData();
      
      // Notify all listeners to update UI
      globalProvider.notifyListeners();
      
      print('Global app data refreshed successfully after configuration change');
    } catch (e) {
      print('Error refreshing global app data: $e');
      // Don't throw error here, just log it
    }
  }

  List<GlobalConfigItem> _getConfigurations() {
    if (serverValues == null) return [];

    List<GlobalConfigItem> globalConfigItems = [];

    for (String key in serverValues!.keys) {
      String serverValue = serverValues![key]?.toString() ?? '-';
      String localValue = _getLocalValue(key);
      bool isEditable = _isConfigurationPermitted(key);
      bool isModified = localValues.containsKey(key) &&
          localValues[key] != serverValue;

      GlobalConfigItem item = GlobalConfigItem(
        key: key,
        serverValue: serverValue,
        localValue: localValue,
        editable: isEditable,
        isModified: isModified,
      );
      globalConfigItems.add(item);
    }

    return globalConfigItems;
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Card(
        margin: const EdgeInsets.all(5),
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(4),
        ),
        child: Column(
          children: [
            // Header with search and actions
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(4),
                color: Colors.blue[50],
              ),
              child: Column(
                children: [
                  Row(
                    children: [
                      Expanded(
                        flex: 2,
                        child: Text(
                          AppLocalizations.of(context)!.key,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Expanded(
                        flex: 1,
                        child: Text(
                          AppLocalizations.of(context)!.server_value,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Expanded(
                        flex: 1,
                        child: Text(
                          AppLocalizations.of(context)!.local_value,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            // Content - Takes all remaining space
            Expanded(
              child: _buildContent(),
            ),
            // Save button at bottom
            Container(
              padding: const EdgeInsets.all(10),
              alignment: Alignment.centerRight, // Align content to the end
              child: ElevatedButton(
                onPressed: () {
                  _onSaveChanges();
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: solidPrimary,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 60),
                  elevation: 4,
                ),
                child: Text(AppLocalizations.of(context)!.submit),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContent() {
    if (isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error, size: 64, color: Colors.red[300]),
            const SizedBox(height: 16),
            Text(
              'Error: $errorMessage',
              style: TextStyle(color: Colors.red[700]),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadInitialData,
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }
    if (serverValues == null || serverValues!.isEmpty) {
      return const Center(child: Text('No configuration parameters found'));
    }

    final configs = _getConfigurations();
    if (configs.isEmpty) {
      return const Center(
        child: Text('No configurations found'),
      );
    }

    return SizedBox(
      width: double.infinity,
      child: ListView.separated(
        padding: const EdgeInsets.only(top: 10, bottom: 15),
        itemCount: configs.length,
        separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey[300]),
        itemBuilder: (context, index) {
          final config = configs[index];
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 15),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                Expanded(
                  flex: 2,
                  child: Text(
                    config.key,
                    style: TextStyle(
                      //fontFamily: 'monospace',
                      fontSize: 12,
                      fontWeight: config.isModified ? FontWeight.bold : FontWeight.normal,
                      color: config.isModified ? Colors.blue : Colors.black,
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  flex: 1,
                  child: Text(
                    config.serverValue,
                    style: const TextStyle(
                      //fontFamily: 'monospace',
                      fontSize: 12,
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: _buildEditableCell(config),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  /// Equivalent to setCellFactoryForLocalValue() in desktop code
  Widget _buildEditableCell(GlobalConfigItem config) {
    if (config.editable) {
      return _buildEditableTextField(config);
    } else {
      return _buildReadOnlyText(config);
    }
  }

  /// Builds editable TextField for permitted configurations
  Widget _buildEditableTextField(GlobalConfigItem config) {
    final controller = _controllers[config.key]!;
    return TextField(
      controller: controller,
      onChanged: (newValue) => _updateLocalValue(config.key, newValue),
      style: TextStyle(
        color: config.isModified ? Colors.blue : Colors.black,
        //fontFamily: 'monospace',
        fontSize: 12,
        fontWeight: config.isModified ? FontWeight.bold : FontWeight.normal,
      ),
      decoration: InputDecoration(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(4),
          borderSide: BorderSide(
            color: config.isModified ? Colors.blue : Colors.grey,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 8,
          vertical: 4,
        ),
        isDense: true,
        hintText: config.serverValue,
      ),
    );
  }
  /// Builds read-only styled text for non-permitted configurations
  Widget _buildReadOnlyText(GlobalConfigItem config) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: 8,
        vertical: 8,
      ),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey[300]!),
        borderRadius: BorderRadius.circular(4),
        color: Colors.grey[100],
      ),
      child: Text(
        config.localValue.isEmpty ? '-' : config.localValue,
        style: TextStyle(
          color: config.isModified ? Colors.blue : Colors.grey[600],
          //fontFamily: 'monospace',
          fontSize: 12,
          fontWeight: config.isModified ? FontWeight.bold : FontWeight.normal,
        ),
      ),
    );
  }
}

class GlobalConfigItem {
  final String key;
  final String serverValue;
  final String localValue;
  final bool editable;
  final bool isModified;

  GlobalConfigItem({
    required this.key,
    required this.serverValue,
    required this.localValue,
    required this.editable,
    required this.isModified,
  });
}
