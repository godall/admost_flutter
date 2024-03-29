import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:admost_flutter/admost_flutter.dart';

void main() {
  const MethodChannel channel = MethodChannel('admost_flutter');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AdmostFlutter.platformVersion, '42');
  });
}
