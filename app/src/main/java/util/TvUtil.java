package tv.biliclassic.tv.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TvUtil {

    private static final String TAG = "TvUtil";

    // 开发调试开关
    private static final boolean FORCE_TV_MODE = false;

    public static boolean isTv(Context context) {
        if (FORCE_TV_MODE) {
            Log.d(TAG, "isTv: 强制 TV 模式开启");
            return true;
        }

        PackageManager pm = context.getPackageManager();

        // 1. 检测 FEATURE_TELEVISION
        boolean hasTV = hasSystemFeature(pm, "FEATURE_TELEVISION");
        Log.d(TAG, "FEATURE_TELEVISION: " + hasTV);
        if (hasTV) {
            Log.d(TAG, "isTv: 检测到 FEATURE_TELEVISION，返回 true");
            return true;
        }

        // 2. 检测 FEATURE_LEANBACK
        boolean hasLeanback = hasSystemFeature(pm, "FEATURE_LEANBACK");
        Log.d(TAG, "FEATURE_LEANBACK: " + hasLeanback);
        if (hasLeanback) {
            Log.d(TAG, "isTv: 检测到 FEATURE_LEANBACK，返回 true");
            return true;
        }

        // 3. 没有触摸屏 + 有键盘 = 可能是电视
        boolean hasTouch = hasSystemFeature(pm, "FEATURE_TOUCHSCREEN");
        boolean hasKeyboard = hasSystemFeature(pm, "FEATURE_KEYBOARD");
        Log.d(TAG, "FEATURE_TOUCHSCREEN: " + hasTouch + ", FEATURE_KEYBOARD: " + hasKeyboard);

        if (!hasTouch && hasKeyboard) {
            Log.d(TAG, "isTv: 无触摸屏且有键盘，判定为电视");
            return true;
        }

        Log.d(TAG, "isTv: 判定为非电视设备");
        return false;
    }

    // 反射检测系统特征（兼容 Android 2.2）
    private static boolean hasSystemFeature(PackageManager pm, String featureName) {
        try {
            // 先获取 feature 常量值
            Field field = PackageManager.class.getField(featureName);
            String feature = (String) field.get(null);

            // 再用反射调用 hasSystemFeature
            Method method = PackageManager.class.getMethod("hasSystemFeature", String.class);
            Boolean result = (Boolean) method.invoke(pm, feature);
            Log.d(TAG, "hasSystemFeature: " + featureName + " = " + result);
            return result != null && result.booleanValue();

        } catch (NoSuchFieldException e) {
            Log.d(TAG, "hasSystemFeature: " + featureName + " 字段不存在");
            return false;
        } catch (NoSuchMethodException e) {
            // Android 2.2 没有 hasSystemFeature 方法，直接返回 false
            Log.d(TAG, "hasSystemFeature: hasSystemFeature 方法不存在 (Android 2.2 及以下)");
            return false;
        } catch (Exception e) {
            Log.d(TAG, "hasSystemFeature: " + featureName + " 检测异常 - " + e.getMessage());
            return false;
        }
    }

    public static boolean isLeanbackSupported(Context context) {
        PackageManager pm = context.getPackageManager();
        return hasSystemFeature(pm, "FEATURE_LEANBACK");
    }

    public static boolean isTouchScreen(Context context) {
        PackageManager pm = context.getPackageManager();
        return hasSystemFeature(pm, "FEATURE_TOUCHSCREEN");
    }
}