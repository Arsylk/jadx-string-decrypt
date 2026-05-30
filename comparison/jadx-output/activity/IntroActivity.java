package com.mistral.jon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class IntroActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static long[] f719a = {1532623197};

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String str = bcq.m4272a(this).getString("ddf2a4ba", "");
        if (TextUtils.isEmpty(str)) {
            finish();
            return;
        }
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(str);
        setContentView(webView);
    }
}
