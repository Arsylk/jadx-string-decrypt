package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RequestLocationAccessActivity extends Activity {
    private static long[] a;

    static {
        long[] arr_v = new long[3];
        RequestLocationAccessActivity.a = arr_v;
        arr_v[0] = 0x5EA18277L;
        arr_v[1] = 1407068605L;
        arr_v[2] = 0x10DC1928L;
    }

    private void a() {
        long[] arr_v = RequestLocationAccessActivity.a;
        String[] arr_s = new String[((int)arr_v[0]) ^ 0x5EA18275];
        arr_s[((int)arr_v[1]) ^ 1407068605] = "android.permission.ACCESS_COARSE_LOCATION";
        arr_s[((int)arr_v[2]) ^ 0x10DC1929] = "android.permission.ACCESS_FINE_LOCATION";
        this.requestPermissions(arr_s, 1002);
    }

    @Override  // android.app.Activity
    protected void onActivityResult(int v, int v1, Intent intent0) {
        super.onActivityResult(v, v1, intent0);
        if(v == 1002) {
            this.finish();
        }
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.a();
    }
}

