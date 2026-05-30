package com.mistral.jon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import z.hg;

public class RequestPermissionActivity extends Activity {
    private static final String a = "RequestPermissionActivity";
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        RequestPermissionActivity.a = arr_v;
        arr_v[0] = 1830119977L;
        arr_v[1] = 20642990L;
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.requestWindowFeature(((int)RequestPermissionActivity.a[0]) ^ 1830119976);
        Window window0 = this.getWindow();
        window0.setFlags(0x400, 0x400);
        window0.setWindowAnimations(((int)RequestPermissionActivity.a[1]) ^ 20642990);
        String[] arr_s = this.getIntent().getStringArrayExtra("perms");
        long[] arr_v = RequestPermissionActivity.a;
        int v = ((int)arr_v[0]) ^ 1830119976;
        for(int v1 = ((int)arr_v[1]) ^ 20642990; v1 < arr_s.length; ++v1) {
            if(hg.a(this, arr_s[v1]) == -1) {
                v = ((int)RequestPermissionActivity.a[1]) ^ 20642990;
                break;
            }
        }
        if(v != 0) {
            this.finish();
            return;
        }
        this.requestPermissions(arr_s, 1002);
    }

    @Override  // android.app.Activity
    public void onRequestPermissionsResult(int v, String[] arr_s, int[] arr_v) {
        super.onRequestPermissionsResult(v, arr_s, arr_v);
        this.finish();
    }
}

