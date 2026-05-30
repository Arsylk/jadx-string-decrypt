package com.mistral.jon.dex.usc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import z.ayx;
import z.azg;
import z.azr;

public class UnlockScreenReceiver extends BroadcastReceiver {
    private static final String a = "UnlockScreenReceiver";
    private azg a;

    static {
    }

    public UnlockScreenReceiver() {
        this.a = null;
    }

    private void a(Context context0) {
        if(ayx.d()) {
            return;
        }
        azg azg0 = this.a;
        if(azg0 != null) {
            azg0.run();
        }
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        if("android.intent.action.SCREEN_ON".equals(intent0.getAction()) && !azr.a()) {
            this.a(context0);
        }
    }
}

