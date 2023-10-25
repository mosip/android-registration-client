import 'package:json_annotation/json_annotation.dart';

part 'actuator_info.g.dart';

@JsonSerializable()
class ActuatorInfo {
  ActuatorInfo(this.git, this.build);

  Map<String, dynamic> git;
  Map<String, String> build;

  factory ActuatorInfo.fromJson(Map<String, dynamic> json) => _$ActuatorInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ActuatorInfoToJson(this);
}