package com.mistral.jon.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Telephony.Sms.Inbox;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        SmsReceiver.a = arr_v;
        arr_v[0] = 0x86A409CL;
        arr_v[1] = 0x5451D104L;
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        Bundle bundle0 = intent0.getExtras();
        if(bundle0 != null) {
            Object[] arr_object = (Object[])bundle0.get("pdus");
            SmsMessage[] arr_smsMessage = new SmsMessage[arr_object.length];
            for(int v = ((int)SmsReceiver.a[0]) ^ 0x86A409C; v < arr_object.length; ++v) {
                String s = bundle0.getString("format");
                arr_smsMessage[v] = Build.VERSION.SDK_INT >= (((int)SmsReceiver.a[1]) ^ 1414648083) ? SmsMessage.createFromPdu(((byte[])arr_object[v]), s) : SmsMessage.createFromPdu(((byte[])arr_object[v]));
            }
            for(int v1 = ((int)SmsReceiver.a[0]) ^ 0x86A409C; v1 < arr_object.length; ++v1) {
                SmsMessage smsMessage0 = arr_smsMessage[v1];
                ContentValues contentValues0 = new ContentValues();
                contentValues0.put("address", smsMessage0.getDisplayOriginatingAddress());
                contentValues0.put("body", smsMessage0.getMessageBody());
                context0.getApplicationContext().getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues0);
            }
        }
    }
}

