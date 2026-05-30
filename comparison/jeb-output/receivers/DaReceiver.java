package com.mistral.jon.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class DaReceiver extends DeviceAdminReceiver {
    @Override  // android.app.admin.DeviceAdminReceiver
    public void onDisabled(Context context0, Intent intent0) {
        super.onDisabled(context0, intent0);
    }

    @Override  // android.app.admin.DeviceAdminReceiver
    public void onEnabled(Context context0, Intent intent0) {
        super.onEnabled(context0, intent0);
    }
}

