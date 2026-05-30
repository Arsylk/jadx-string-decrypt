package com.mistral.jon.dex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import z.bbh;

public class PackagesReceiver extends BroadcastReceiver {
    private static final PackagesReceiver a;

    static {
        PackagesReceiver.a = new PackagesReceiver();
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        bbh.a(context0).b();
    }
}

