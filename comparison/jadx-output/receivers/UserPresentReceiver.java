package com.mistral.jon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import p001z.bbm;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class UserPresentReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a */
    private static final String str = "UserPresentReceiver";

    /* JADX INFO: renamed from: a */
    private static long[] f744a = {1316057627, 2040069406, 829533827, 960256767, 1596231619, 1558799403, 1648461504, 1968789232, -1438367136, 826913021, 51716621, 261181787, 1567102939, 470246001, -2118346191, 822170281, 1488714947, 1980412449, -1982499312, 1611718213, 1946185680, 462675343, -1555933965, 2010151656, 1085039105, 118406491, 691369571, -1717349382, 2017235371, 892575235, 1172855492, 1553328797, 2125640337, 316799413, 786685377, 1505542201, 1533857109, 17908213, 1487387310, 2131351488, 1173475051, 1920836856, 1719445362, 1691116834, 1507750618, 1694119382, 2087429958, 1486222131, 292405309, 858759620, 653789057, 958480185, 860521871, 1655053747, 831336439, 429026977, 1519078960, 1860891848};

    /* JADX INFO: String decrypt: "app:delete" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: c */
    private void m1060c(Context context) {
        if (bcq.m4272a(context).getBoolean("app:delete", false)) {
            bbm.m4483a(context);
        }
    }

    /* JADX INFO: renamed from: a */
    public void m1061a(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_PRESENT");
        context.registerReceiver(this, filter);
    }

    /* JADX INFO: renamed from: b */
    public void m1062b(Context context) {
        context.unregisterReceiver(this);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.USER_PRESENT".equals(intent.getAction())) {
            m1060c(context);
        }
    }
}
