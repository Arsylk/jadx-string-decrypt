package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class RequestStorageAccessActivity extends Activity {
    private static long[] a;

    static {
        long[] arr_v = new long[3];
        RequestStorageAccessActivity.a = arr_v;
        arr_v[0] = 0x56581F35L;
        arr_v[1] = 0x1C56B318L;
        arr_v[2] = 0x6CB9DAADL;
    }

    private void a() {
        long[] arr_v = RequestStorageAccessActivity.a;
        String[] arr_s = new String[((int)arr_v[2]) ^ 0x6CB9DAAF];
        arr_s[((int)arr_v[1]) ^ 0x1C56B318] = "android.permission.READ_EXTERNAL_STORAGE";
        arr_s[((int)arr_v[0]) ^ 0x56581F34] = "android.permission.WRITE_EXTERNAL_STORAGE";
        this.requestPermissions(arr_s, 1001);
    }

    @Override  // android.app.Activity
    protected void onActivityResult(int v, int v1, Intent intent0) {
        if(v == 1001) {
            this.finish();
        }
        super.onActivityResult(v, v1, intent0);
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.requestWindowFeature(((int)RequestStorageAccessActivity.a[0]) ^ 0x56581F34);
        Window window0 = this.getWindow();
        window0.setFlags(0x400, 0x400);
        window0.setWindowAnimations(((int)RequestStorageAccessActivity.a[1]) ^ 0x1C56B318);
        this.a();
    }
}

