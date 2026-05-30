package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import p001z.bbj;

/* JADX INFO: loaded from: classes.dex */
public class TransparentActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static final String str = "TransparentActivity";

    /* JADX INFO: renamed from: a */
    private static long[] f727a = {1416523572, 643963549};

    /* JADX INFO: renamed from: a */
    private /* synthetic */ void m1040a() {
        bbj.m4477a((Context) this);
    }

    /* JADX INFO: renamed from: lambda$YlhqXxx_1EKKoeT80Lgb-6engXI */
    public static /* synthetic */ void m10588lambda$YlhqXxx_1EKKoeT80Lgb6engXI(TransparentActivity transparentActivity) {
        transparentActivity.finish();
    }

    /* JADX INFO: renamed from: lambda$t1-eVTiWJQDWDZhq5s62KGDdKjw */
    public static /* synthetic */ void m10589lambda$t1eVTiWJQDWDZhq5s62KGDdKjw(TransparentActivity transparentActivity) {
        transparentActivity.m1040a();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Handler handler;
        Runnable runnable;
        long j;
        long j2;
        super.onCreate(bundle);
        if ("86a73037-57e6-4f7a-b4ae-ff0508df1356".equals(getIntent().getAction())) {
            bbj.m4477a((Context) this);
            handler = new Handler();
            runnable = new Runnable() { // from class: com.mistral.jon.activity.-$$Lambda$TransparentActivity$t1-eVTiWJQDWDZhq5s62KGDdKjw
                public /* synthetic */ $$Lambda$TransparentActivity$t1eVTiWJQDWDZhq5s62KGDdKjw() {
                }

                @Override // java.lang.Runnable
                public final void run() {
                    TransparentActivity.m10589lambda$t1eVTiWJQDWDZhq5s62KGDdKjw(this.f$0);
                }
            };
            j = 1416523572;
            j2 = 1416523456;
        } else {
            handler = new Handler();
            runnable = new Runnable() { // from class: com.mistral.jon.activity.-$$Lambda$TransparentActivity$YlhqXxx_1EKKoeT80Lgb-6engXI
                public /* synthetic */ $$Lambda$TransparentActivity$YlhqXxx_1EKKoeT80Lgb6engXI() {
                }

                @Override // java.lang.Runnable
                public final void run() {
                    TransparentActivity.m10588lambda$YlhqXxx_1EKKoeT80Lgb6engXI(this.f$0);
                }
            };
            j = 643963549;
            j2 = 643963253;
        }
        handler.postDelayed(runnable, j ^ j2);
    }
}
