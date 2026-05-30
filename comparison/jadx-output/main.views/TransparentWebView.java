package com.mistral.jon.main.views;

import android.content.Context;
import android.webkit.WebView;

/* JADX INFO: loaded from: classes.dex */
public class TransparentWebView extends WebView {

    /* JADX INFO: renamed from: a */
    private static long[] f739a = {642591417, 1323656645};

    /* JADX WARN: Multi-variable type inference failed */
    public TransparentWebView(Context context) {
        super(context);
        setBackgroundColor(0);
        setLayerType(1, null);
        getSettings().setJavaScriptEnabled(true);
    }
}
