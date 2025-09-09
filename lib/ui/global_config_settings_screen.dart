import 'package:flutter/material.dart';
import '../pigeon/common_details_pigeon.dart';

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
  bool isLoading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    _loadRegistrationParams();
  }

  Future<void> _loadRegistrationParams() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
    });
    try {
      final CommonDetailsApi api = CommonDetailsApi();
      
      // Load registration params, local configurations, and permitted configurations in parallel
      final results = await Future.wait([
        api.getRegistrationParams(),
        api.getLocalConfigurations(),
        api.getPermittedConfigurationNames(),
      ] as Iterable<Future>);
      
      setState(() {
        serverValues = results[0].cast<String, Object>();
        localConfigurations = results[1].cast<String, String>();
        permittedConfigurations = results[2].cast<String>();
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        errorMessage = e.toString();
        isLoading = false;
      });
    }
  }

  void _updateLocalValue(String key, String value) {
    setState(() {
      if (value.isEmpty) {
        localValues.remove(key);
      } else {
        localValues[key] = value;
      }
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

  void _submitChanges() {
    if (localValues.isEmpty) {
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
      await CommonDetailsApi().modifyConfigurations(localValues);
      
      // Update local configurations with the saved values
      setState(() {
        localConfigurations.addAll(localValues);
        localValues.clear();
      });
      
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Changes saved successfully')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error saving changes: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Header
        Container(
          padding: const EdgeInsets.all(16),
          color: Colors.blue[100],
          child: Row(
            children: [
              Expanded(
                flex: 2,
                child: Text(
                  'Key',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    color: Colors.blue[800],
                  ),
                ),
              ),
              Expanded(
                flex: 1,
                child: Text(
                  'Server Value',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    color: Colors.blue[800],
                  ),
                ),
              ),
              Expanded(
                flex: 1,
                child: Text(
                  'Local Value',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    color: Colors.blue[800],
                  ),
                ),
              ),
            ],
          ),
        ),
        // Content - Takes all remaining space
        Expanded(
          child: _buildContent(),
        ),
        // Submit button (only show when there are changes)
        if (localValues.isNotEmpty)
          Container(
            padding: const EdgeInsets.all(16),
            child: SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _submitChanges,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue[600],
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text(
                  'SUBMIT',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
      ],
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
              onPressed: _loadRegistrationParams,
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }
    if (serverValues == null || serverValues!.isEmpty) {
      return const Center(child: Text('No configuration parameters found'));
    }
    return SizedBox(
      width: double.infinity,
      child: ListView.separated(
        padding: const EdgeInsets.only(top: 10),
        itemCount: serverValues!.length,
        separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey[300]),
        itemBuilder: (context, index) {
          final entry = serverValues!.entries.elementAt(index);
          final key = entry.key;
          final serverValue = entry.value?.toString() ?? '-';
          final localValue = _getLocalValue(key);
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
            child: Row(
              children: [
                Expanded(
                  flex: 2,
                  child: Text(
                    key,
                    style: const TextStyle(
                      fontFamily: 'monospace',
                      fontSize: 12,
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Text(
                    serverValue,
                    style: const TextStyle(
                      fontFamily: 'monospace',
                      fontSize: 12,
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: _isConfigurationPermitted(key)
                      ? TextField(
                          controller: TextEditingController(text: localValue),
                          style: const TextStyle(
                            fontFamily: 'monospace',
                            fontSize: 12,
                          ),
                          decoration: const InputDecoration(
                            border: OutlineInputBorder(),
                            contentPadding: EdgeInsets.symmetric(
                              horizontal: 8,
                              vertical: 4,
                            ),
                            isDense: true,
                          ),
                          onChanged: (value) => _updateLocalValue(key, value),
                        )
                      : Container(
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
                            localValue.isEmpty ? '-' : localValue,
                            style: const TextStyle(
                              fontFamily: 'monospace',
                              fontSize: 12,
                              color: Colors.grey,
                            ),
                          ),
                        ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
