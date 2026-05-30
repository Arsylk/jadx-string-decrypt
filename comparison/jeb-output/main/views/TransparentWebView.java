package com.mistral.jon.main.views;

import android.content.Context;
import android.webkit.WebView;

public class TransparentWebView extends WebView {
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        TransparentWebView.a = arr_v;
        arr_v[0] = 0x264D2AB9L;
        arr_v[1] = 0x4EE565C5L;
    }

    public TransparentWebView(Context context0) {
        super(context0);
        this.setBackgroundColor(((int)TransparentWebView.a[0]) ^ 0x264D2AB9);
        this.setLayerType(((int)TransparentWebView.a[1]) ^ 0x4EE565C4, null);
        this.getSettings().setJavaScriptEnabled(((boolean)(((int)TransparentWebView.a[1]) ^ 0x4EE565C4)));
    }
}

