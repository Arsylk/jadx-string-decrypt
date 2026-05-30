package com.mistral.jon.overlay;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout.LayoutParams;
import androidx.appcompat.app.AppCompatActivity;
import z.ayp;
import z.azk;
import z.bbd;
import z.bcq;
import z.bdl;
import z.bdm;
import z.bdq;

public final class OverlayActivity extends AppCompatActivity {
    public static final class WebAppInterface {
        private static long[] a;
        private final AppCompatActivity a;
        private final String a;

        static {
            long[] arr_v = new long[2];
            WebAppInterface.a = arr_v;
            arr_v[0] = 0x26937DL;
            arr_v[1] = 0x12E3580AL;
        }

        public WebAppInterface(AppCompatActivity appCompatActivity0, String s) {
            bdm.b(appCompatActivity0, "");
            super();
            this.a = appCompatActivity0;
            this.a = s;
        }

        @JavascriptInterface
        public final void s(String s) {
            bdm.b(s, "");
            Log.e("OverlayActivity", s);
            long v = WebAppInterface.a[0];
            if(bdq.a(s).toString().length() > 5) {
                ayp.a().b(this.a + ": " + s);
                new bbd(bcq.a(this.a)).a(this.a);
            }
            this.a.finish();
            this.a.overridePendingTransition(((int)WebAppInterface.a[0]) ^ 0x26937D, ((int)WebAppInterface.a[0]) ^ 0x26937D);
            azk.a().performGlobalAction(((int)WebAppInterface.a[1]) ^ 0x12E35808);
        }
    }

    public static final class a {
        private a() {
        }

        public a(bdl bdl0) {
        }
    }

    public static final a a;
    private static long[] a;

    static {
        long[] arr_v = new long[5];
        OverlayActivity.a = arr_v;
        arr_v[0] = 0x71865F05L;
        arr_v[1] = 0x1AA67F9AL;
        arr_v[2] = 2095744682L;
        arr_v[3] = 1506883705L;
        arr_v[4] = 770107448L;
        OverlayActivity.a = new a(null);
    }

    @Override  // androidx.activity.ComponentActivity
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        this.overridePendingTransition(((int)OverlayActivity.a[0]) ^ 0x71865F05, ((int)OverlayActivity.a[0]) ^ 0x71865F05);
        azk.a().performGlobalAction(((int)OverlayActivity.a[4]) ^ 770107450);
    }

    @Override  // androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.overridePendingTransition(((int)OverlayActivity.a[0]) ^ 0x71865F05, ((int)OverlayActivity.a[0]) ^ 0x71865F05);
        String s = this.getIntent().getStringExtra("p");
        String s1 = this.getIntent().getStringExtra("h");
        WebAppInterface overlayActivity$WebAppInterface0 = new WebAppInterface(this, s);
        WebView webView0 = new WebView(this);
        webView0.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        WebSettings webSettings0 = webView0.getSettings();
        bdm.a(webSettings0, "");
        webSettings0.setJavaScriptEnabled(((boolean)(((int)OverlayActivity.a[1]) ^ 0x1AA67F9B)));
        webView0.setScrollBarStyle(((int)OverlayActivity.a[0]) ^ 0x71865F05);
        webView0.addJavascriptInterface(overlayActivity$WebAppInterface0, "I");
        if(s1 != null) {
            webView0.loadData(s1, "text/html; charset=utf-8", "UTF-8");
        }
        this.setContentView(webView0);
    }

    @Override  // androidx.appcompat.app.AppCompatActivity
    public boolean onKeyDown(int v, KeyEvent keyEvent0) {
        bdm.b(keyEvent0, "");
        long[] arr_v = OverlayActivity.a;
        if(v == (((int)arr_v[2]) ^ 2095744681)) {
            return ((int)arr_v[1]) ^ 0x1AA67F9B;
        }
        if(v == 4) {
            return ((int)arr_v[1]) ^ 0x1AA67F9B;
        }
        return v == (((int)arr_v[3]) ^ 1506883627) ? ((int)arr_v[1]) ^ 0x1AA67F9B : ((int)arr_v[0]) ^ 0x71865F05;
    }

    @Override  // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        this.overridePendingTransition(((int)OverlayActivity.a[0]) ^ 0x71865F05, ((int)OverlayActivity.a[0]) ^ 0x71865F05);
    }
}

