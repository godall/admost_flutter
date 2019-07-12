import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

class AdmostFlutter {
  final StreamController<String> _videoRewardStateChangedController =
      StreamController<String>();

  AdmostFlutter() {
    channel.setMethodCallHandler(_callHandler);
  }

  @visibleForTesting
  static const MethodChannel channel = MethodChannel(
    'fanclash.realtimegames.app/admost_flutter',
  );

  @visibleForTesting
  static const EventChannel eventChannel = EventChannel(
    'fanclash.realtimegames.app/admost_flutter',
  );

  static Future<String> get platformVersion async {
    final String version = await channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> initAdmost(String appId) async {
    return await channel.invokeMethod('initAdmost', {'app_id': appId});
  }

  static void initVideoReward(String zoneId) {
    channel.invokeMethod('initVideoReward', {'zone_id': zoneId});
  }

  static loadVideoReward() async {
    await channel.invokeMethod('loadVideoReward');
  }

  static showVideoReward() async {
    await channel.invokeMethod('showVideoReward');
  }

  void onVideoRewardStateChange(MethodCall call) {
    String state = call.arguments["state"];
    if (call.arguments['errorCode'] != null) {
      state += ', errorCode: ${call.arguments['errorCode']}';
    }
    _videoRewardStateChangedController.sink.add(state);
  }

  Stream<String> get onVideoRewardStateChanged {
    return _videoRewardStateChangedController.stream;
  }

  Future<void> _callHandler(MethodCall call) async {
    switch (call.method) {
      case 'onVideoRewardStateChange':
        onVideoRewardStateChange(call);
        break;
    }
  }
}
