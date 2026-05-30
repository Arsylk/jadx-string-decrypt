package com.mistral.jon.dex.usc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import p001z.ayx;
import p001z.azg;
import p001z.azr;

/* JADX INFO: loaded from: classes.dex */
public class UnlockScreenReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a */
    private static final String str = "UnlockScreenReceiver";

    /* JADX INFO: renamed from: a */
    private azg runnableImpl = null;

    /* JADX INFO: renamed from: a */
    private void m1050a(Context context) {
        azg runnableImpl;
        if (ayx.m4289d() || (runnableImpl = this.runnableImpl) == null) {
            return;
        }
        runnableImpl.run();
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!"android.intent.action.SCREEN_ON".equals(intent.getAction()) || azr.m4276a()) {
            return;
        }
        m1050a(context);
    }
}
