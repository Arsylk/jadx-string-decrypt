package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class RequestLocationAccessActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static long[] f721a = {1587642999, 1407068605, 282859816};

    /* JADX INFO: renamed from: a */
    private void m1028a() {
        String[] strArr = new String[2];
        strArr[0] = "android.permission.ACCESS_COARSE_LOCATION";
        strArr[1] = "android.permission.ACCESS_FINE_LOCATION";
        requestPermissions(strArr, 1002);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1002) {
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m1028a();
    }
}
