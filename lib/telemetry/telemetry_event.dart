class TelemetryEvent {
  final String name;
  final DateTime timestamp;
  final Map<String, dynamic> data;

  TelemetryEvent({
    required this.name,
    DateTime? timestamp,
    required this.data,
  }) : timestamp = timestamp ?? DateTime.now();

  Map<String, dynamic> toJson() {
    return {
      "name": name,
      "timestamp": timestamp.toIso8601String(),
      "data": data,
    };
  }
}