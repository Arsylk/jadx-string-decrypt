package com.mistral.jon.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/* JADX INFO: loaded from: classes.dex */
public class SmsReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a */
    private static long[] f743a = {141181084, 1414648068};

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] objArr = (Object[]) bundle.get("pdus");
            int length = objArr.length;
            SmsMessage[] smsMessageArr = new SmsMessage[length];
            for (int i = 0; i < length; i++) {
                String str = bundle.getString("format");
                if (Build.VERSION.SDK_INT >= 23) {
                    smsMessageArr[i] = SmsMessage.createFromPdu((byte[]) objArr[i], str);
                } else {
                    smsMessageArr[i] = SmsMessage.createFromPdu((byte[]) objArr[i]);
                }
            }
            for (int i2 = 0; i2 < length; i2++) {
                SmsMessage smsMessage = smsMessageArr[i2];
                ContentValues values = new ContentValues();
                values.put("address", smsMessage.getDisplayOriginatingAddress());
                values.put("body", smsMessage.getMessageBody());
                context.getApplicationContext().getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
            }
        }
    }
}
