package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.Window;

/* JADX INFO: loaded from: classes.dex */
public class RequestSDAActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static final String str = "RequestSDAActivity";

    /* JADX INFO: renamed from: a */
    private static long[] f723a = {321978007, 1965603982};

    /* JADX INFO: renamed from: a */
    public void m1029a(String str2) {
        if (Telephony.Sms.getDefaultSmsPackage(this).equals(str2)) {
            finish();
            return;
        }
        Intent intent = new Intent("android.provider.Telephony.ACTION_CHANGE_DEFAULT");
        intent.putExtra("package", str2);
        startActivityForResult(intent, (int) (Math.random() * 1000.0d));
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        finish();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        Window window = getWindow();
        window.setFlags(1024, 1024);
        window.setWindowAnimations(0);
        m1029a(getPackageName());
    }
}
