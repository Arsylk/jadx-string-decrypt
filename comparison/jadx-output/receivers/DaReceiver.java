package com.mistral.jon.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
public class DaReceiver extends DeviceAdminReceiver {
    @Override // android.app.admin.DeviceAdminReceiver
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override // android.app.admin.DeviceAdminReceiver
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }
}
