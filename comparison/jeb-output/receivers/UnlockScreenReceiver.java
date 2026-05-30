package com.mistral.jon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import z.ayx;
import z.bct;
import z.bdd;

public class UnlockScreenReceiver extends BroadcastReceiver {
    private static final String a = "UnlockScreenReceiver";

    static {
    }

    private void a(Context context0) {
        if(!bct.e()) {
            return;
        }
        if(ayx.d()) {
            return;
        }
        bdd.a(context0, "screenlock5379266f-674b-4e4b-95d5-273f165609b3.mp4", 10000);
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        if("android.intent.action.SCREEN_ON".equals(intent0.getAction()) && !bct.a()) {
            this.a(context0);
        }
    }
}

