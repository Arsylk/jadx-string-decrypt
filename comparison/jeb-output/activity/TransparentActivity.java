package com.mistral.jon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import z.bbj;

public class TransparentActivity extends Activity {
    private static final String a = "TransparentActivity";
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        TransparentActivity.a = arr_v;
        arr_v[0] = 0x546E6F34L;
        arr_v[1] = 0x26621A9DL;
    }

    // Detected as a lambda implementation
    private void a() [...]

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        long v1;
        long v;
        -..Lambda.TransparentActivity.t1-eVTiWJQDWDZhq5s62KGDdKjw -$$Lambda$TransparentActivity$t1-eVTiWJQDWDZhq5s62KGDdKjw0;
        Handler handler0;
        super.onCreate(bundle0);
        if("86a73037-57e6-4f7a-b4ae-ff0508df1356".equals(this.getIntent().getAction())) {
            bbj.a(this);
            handler0 = new Handler();
            -$$Lambda$TransparentActivity$t1-eVTiWJQDWDZhq5s62KGDdKjw0 = () -> bbj.a(this);
            v = TransparentActivity.a[0];
            v1 = 0x546E6EC0L;
        }
        else {
            handler0 = new Handler();
            -$$Lambda$TransparentActivity$t1-eVTiWJQDWDZhq5s62KGDdKjw0 = new -..Lambda.TransparentActivity.YlhqXxx_1EKKoeT80Lgb-6engXI(this);
            v = TransparentActivity.a[1];
            v1 = 0x26621975L;
        }
        handler0.postDelayed(-$$Lambda$TransparentActivity$t1-eVTiWJQDWDZhq5s62KGDdKjw0, v ^ v1);
    }
}

