package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/* JADX INFO: loaded from: classes.dex */
public class RequestStorageAccessActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static long[] f724a = {1448615733, 475443992, 1824119469};

    /* JADX INFO: renamed from: a */
    private void m1030a() {
        String[] strArr = new String[2];
        strArr[0] = "android.permission.READ_EXTERNAL_STORAGE";
        strArr[1] = "android.permission.WRITE_EXTERNAL_STORAGE";
        requestPermissions(strArr, 1001);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1001) {
            finish();
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        Window window = getWindow();
        window.setFlags(1024, 1024);
        window.setWindowAnimations(0);
        m1030a();
    }
}
