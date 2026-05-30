package com.mistral.jon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import z.bcq;

public class IntroActivity extends Activity {
    private static long[] a;

    static {
        long[] arr_v = new long[1];
        IntroActivity.a = arr_v;
        arr_v[0] = 0x5B59F95DL;
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        String s = bcq.a(this).getString("ddf2a4ba", "");
        if(TextUtils.isEmpty(s)) {
            this.finish();
            return;
        }
        WebView webView0 = new WebView(this);
        webView0.getSettings().setJavaScriptEnabled(((boolean)(((int)IntroActivity.a[0]) ^ 0x5B59F95C)));
        webView0.loadUrl(s);
        this.setContentView(webView0);
    }
}

