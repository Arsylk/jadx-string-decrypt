package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony.Sms;
import android.view.Window;

public class RequestSDAActivity extends Activity {
    private static final String a = "RequestSDAActivity";
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        RequestSDAActivity.a = arr_v;
        arr_v[0] = 0x1330FE97L;
        arr_v[1] = 1965603982L;
    }

    public void a(String s) {
        if(Telephony.Sms.getDefaultSmsPackage(this).equals(s)) {
            this.finish();
            return;
        }
        Intent intent0 = new Intent("android.provider.Telephony.ACTION_CHANGE_DEFAULT");
        intent0.putExtra("package", s);
        this.startActivityForResult(intent0, ((int)(Math.random() * 1000.0)));
    }

    @Override  // android.app.Activity
    protected void onActivityResult(int v, int v1, Intent intent0) {
        super.onActivityResult(v, v1, intent0);
        this.finish();
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.requestWindowFeature(((int)RequestSDAActivity.a[0]) ^ 0x1330FE96);
        Window window0 = this.getWindow();
        window0.setFlags(0x400, 0x400);
        window0.setWindowAnimations(((int)RequestSDAActivity.a[1]) ^ 1965603982);
        this.a("com.mistral.jon");
    }
}

