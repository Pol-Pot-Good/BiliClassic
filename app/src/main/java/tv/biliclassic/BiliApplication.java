package tv.biliclassic;

import android.app.Application;

import com.swetake.util.Qrcode;

import tv.biliclassic.util.CrashHandler;
import tv.biliclassic.util.QRCodeUtil;
import tv.biliclassic.util.SharedPreferencesUtil;

public class BiliApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtil.init(this);
        CrashHandler.getInstance().init(this);
        Qrcode.init(this);       // 初始化 Qrcode 库
        QRCodeUtil.init(this);   // 标记 QRCodeUtil 已初始化，缓存保留
    }
}