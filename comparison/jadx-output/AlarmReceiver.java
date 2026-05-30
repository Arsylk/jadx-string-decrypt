package com.mistral.jon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import p001z.ayq;
import p001z.aze;

/* JADX INFO: loaded from: classes.dex */
public class AlarmReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a */
    private static final String str = "AlarmReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!ayq.str.equals(intent.getAction()) || aze.m4330a(context)) {
            return;
        }
        aze.m4329a(context);
    }
}
