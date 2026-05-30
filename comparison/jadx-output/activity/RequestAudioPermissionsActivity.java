package com.mistral.jon.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes.dex */
public class RequestAudioPermissionsActivity extends AppCompatActivity {

    /* JADX INFO: renamed from: a */
    private static long[] f720a = {952956363, 785239433};

    /* JADX INFO: renamed from: f */
    private void m1027f() {
        String[] strArr = new String[1];
        strArr[0] = "android.permission.RECORD_AUDIO";
        requestPermissions(strArr, 1002);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1002) {
            finish();
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m1027f();
    }
}
