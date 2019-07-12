package app.fanclash.admost_flutter;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import admost.sdk.AdMostInterstitial;
import admost.sdk.base.AdMost;
import admost.sdk.base.AdMostConfiguration;
import admost.sdk.listener.AdMostAdListener;
import admost.sdk.listener.AdMostInitListener;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class AdmostFlutterPlugin implements MethodCallHandler {
    private AdMostInterstitial videoAds;
    private final Activity activity;
    private final MethodChannel channel;

    private AdmostFlutterPlugin(Activity activity, MethodChannel channel) {
        this.activity = activity;
        this.channel = channel;
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "fanclash.realtimegames.app/admost_flutter");
        final AdmostFlutterPlugin instance = new AdmostFlutterPlugin(registrar.activity(), channel);
        channel.setMethodCallHandler(instance);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("initAdmost")) {
            initAdmost(result, (String) call.argument("app_id"));
        } else if (call.method.equals("initVideoReward")) {
            initVideoReward(result, (String) call.argument("zone_id"));
        } else if (call.method.equals("loadVideoReward")) {
            loadVideo(result);
        } else if (call.method.equals("showVideoReward")) {
            showVideo(result);
        } else {
            result.notImplemented();
        }
    }

    private void showVideo(Result result) {
        videoAds.show();
    }

    private void loadVideo(Result result) {
        videoAds.refreshAd(false);
    }

    private void initVideoReward(Result result, String zoneId) {
        videoAds = new AdMostInterstitial(activity, zoneId, new AdMostAdListener() {
            @Override
            public void onReady(String network, int ecpm) {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("state", "ready");
                arguments.put("network", network);
                arguments.put("ecpm", ecpm);
                channel.invokeMethod("onVideoRewardStateChange", arguments);
            }

            @Override
            public void onFail(int errorCode) {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("state", "failed");
                arguments.put("errorCode", errorCode);
                channel.invokeMethod("onVideoRewardStateChange", arguments);
            }

            @Override
            public void onDismiss(String message) {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("state", "dismissed");
                arguments.put("message", message);
                channel.invokeMethod("onVideoRewardStateChange", arguments);
            }

            @Override
            public void onComplete(String network) {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("state", "completed");
                arguments.put("network", network);
                channel.invokeMethod("onVideoRewardStateChange", arguments);
            }

            @Override
            public void onShown(String network) {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("state", "shown");
                arguments.put("network", network);
                channel.invokeMethod("onVideoRewardStateChange", arguments);
            }

            @Override
            public void onClicked(String s) {
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("state", "completed");
                arguments.put("network", s);
                channel.invokeMethod("onVideoRewardStateChange", arguments);
            }
        });
        videoAds.setSSVCustomData();
    }

    private void initAdmost(final Result result, String appId) {
        AdMostConfiguration.Builder configuration = new AdMostConfiguration.Builder(activity, appId);

        configuration.age(24);
        configuration.gender(AdMost.GENDER_MALE);
        configuration.interests("Business, Tech, Travel, Shopping, Entertainment, Fashion, Fitness, Foodie, Gamer, Jobs, Sports");

        AdMost.getInstance().init(configuration.build(), new AdMostInitListener() {
            @Override
            public void onInitCompleted() {
                result.success(true);
            }

            @Override
            public void onInitFailed(int err) {
                result.error("Admost init failed. error: " + err, null, null);
            }
        });
    }

    private void setUserId(String userId){
        AdMost.getInstance().setUserId(userId);
    }
}
