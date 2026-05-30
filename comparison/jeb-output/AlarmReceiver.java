package com.mistral.jon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import z.aze;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String a = "AlarmReceiver";

    static {
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        if("df9b6479-a388-4525-81b9-6ddc976d32a5".equals(intent0.getAction()) && !aze.a(context0)) {
            aze.a(context0);
        }
    }
}

