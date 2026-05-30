package com.mistral.jon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.mistral.jon.main.views.TransparentWebView;

/* JADX INFO: loaded from: classes.dex */
public class CommonWebViewActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static long[] f714a = {877585985, 2121391044, 9349290, -1721446895, 10586937, 1625274855, 2143341028, 534782873, -2093504206, 796461614, 875795367, 1644378605, -2029154071, 580140077, -724049990, 1115453563, -313671291, 1272736803, 1349362175, 603390768, 1191482522, 1968534790, -1902148661, 1198837823, 946788033, 1672380330, -801907653, 1702999636, 1095091175, 478820435, -865645855, 2074743926, 141923164, 476870008, 479804927, 389212478, 918367375, 384808925, 786291546, 567596932, 509179392, 56725698, 2075980876, 1167667296, 1018873394, 378246677, 1249438094, 1368373770, 1009626386, 1759457029, 689685461, 1803917211, 461465827, 1494557453, 1727561737, 248592676, 1858696042, 993424950, -374979752, -994903311, 1669118719, 965918055, 1147119325, -1633280180, 579616756, -1761652048, -285944856, -2044493927, 1974721918, -1317559868, 202544876, 1060891700, 1298348411, 1457732004, 1299957998, 739599371, 256501472, 380915405, 624594040, -2101430668, 1240527319, 1838958967, -1867868072, 277248431, 821895772, 418840574, 947495720, 731187081, 987261809, 835001636, 1234124343};

    /* JADX INFO: renamed from: a */
    public static final String str = "content";

    /* JADX INFO: String decrypt: "utf8"; "text/html" */
    /* JADX INFO: renamed from: a */
    private void m1020a() {
        String str2 = getIntent().getStringExtra(str);
        TransparentWebView transparentWebView = new TransparentWebView(this);
        transparentWebView.loadData(str2, "text/html", "utf8");
        transparentWebView.setOnTouchListener(new View.OnTouchListener() { // from class: com.mistral.jon.activity.-$$Lambda$CommonWebViewActivity$PnvGjLTMA0yV25s3PXdnULPcnAo
            public /* synthetic */ $$Lambda$CommonWebViewActivity$PnvGjLTMA0yV25s3PXdnULPcnAo() {
            }

            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent event) {
                return CommonWebViewActivity.lambda$PnvGjLTMA0yV25s3PXdnULPcnAo(this.f$0, view, event);
            }
        });
        setContentView(transparentWebView);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private /* synthetic */ boolean m1021a(View view, MotionEvent event) {
        finish();
        return false;
    }

    public static /* synthetic */ boolean lambda$PnvGjLTMA0yV25s3PXdnULPcnAo(CommonWebViewActivity commonWebViewActivity, View view, MotionEvent event) {
        return commonWebViewActivity.m1021a(view, event);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        m1020a();
    }
}
