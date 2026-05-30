package com.mistral.jon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.UUID;
import p001z.ayx;
import p001z.bct;
import p001z.bdd;

/* JADX INFO: loaded from: classes.dex */
public class UnlockScreenReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a */
    private static final String str = "UnlockScreenReceiver";

    /* JADX INFO: renamed from: a */
    private void m1059a(Context context) {
        if (bct.m4291e() && !ayx.m4289d()) {
            bdd.m4586a(context, "screenlock" + UUID.randomUUID().toString() + ".mp4", 10000);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!"android.intent.action.SCREEN_ON".equals(intent.getAction()) || bct.m4556a()) {
            return;
        }
        m1059a(context);
    }
}
