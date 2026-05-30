package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import p001z.C0604hg;

/* JADX INFO: loaded from: classes.dex */
public class RequestPermissionActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static final String str = "RequestPermissionActivity";

    /* JADX INFO: renamed from: a */
    private static long[] f722a = {1830119977, 20642990};

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        Window window = getWindow();
        window.setFlags(1024, 1024);
        window.setWindowAnimations(0);
        String[] strArr = getIntent().getStringArrayExtra("perms");
        int i = 1;
        for (String str2 : strArr) {
            if (C0604hg.m6204a((Context) this, str2) == -1) {
                i = 0;
                break;
            }
        }
        if (i != 0) {
            finish();
        } else {
            requestPermissions(strArr, 1002);
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        finish();
    }
}
