package com.mistral.jon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import z.bbm;
import z.bcq;

public class UserPresentReceiver extends BroadcastReceiver {
    private static final String a = "UserPresentReceiver";
    private static long[] a;

    static {
        long[] arr_v = new long[58];
        UserPresentReceiver.a = arr_v;
        arr_v[0] = 1316057627L;
        arr_v[1] = 2040069406L;
        arr_v[2] = 0x3171AE83L;
        arr_v[3] = 0x393C5AFFL;
        arr_v[4] = 0x5F248FC3L;
        arr_v[5] = 1558799403L;
        arr_v[6] = 0x624186C0L;
        arr_v[7] = 0x755956F0L;
        arr_v[8] = 0xFFFFFFFFAA444260L;
        arr_v[9] = 0x3149B0FDL;
        arr_v[10] = 0x315220DL;
        arr_v[11] = 0xF91515BL;
        arr_v[12] = 1567102939L;
        arr_v[13] = 470246001L;
        arr_v[14] = 0xFFFFFFFF81BC9A31L;
        arr_v[15] = 0x310152A9L;
        arr_v[16] = 0x58BBFCC3L;
        arr_v[17] = 0x760AB221L;
        arr_v[18] = 0xFFFFFFFF89D57610L;
        arr_v[19] = 0x6010DE45L;
        arr_v[20] = 0x74006FD0L;
        arr_v[21] = 0x1B93DD8FL;
        arr_v[22] = 0xFFFFFFFFA34254F3L;
        arr_v[23] = 2010151656L;
        arr_v[24] = 1085039105L;
        arr_v[25] = 0x70EBD5BL;
        arr_v[26] = 0x29357663L;
        arr_v[27] = 0xFFFFFFFF99A353FAL;
        arr_v[28] = 2017235371L;
        arr_v[29] = 0x35339E03L;
        arr_v[30] = 0x45E85AC4L;
        arr_v[0x1F] = 0x5C95EA9DL;
        arr_v[0x20] = 2125640337L;
        arr_v[33] = 0x12E1F9B5L;
        arr_v[34] = 0x2EE3DDC1L;
        arr_v[35] = 1505542201L;
        arr_v[36] = 1533857109L;
        arr_v[37] = 0x11141F5L;
        arr_v[38] = 1487387310L;
        arr_v[39] = 0x7F09D7C0L;
        arr_v[40] = 0x45F1CEEBL;
        arr_v[41] = 0x727DA4F8L;
        arr_v[42] = 0x667CA772L;
        arr_v[43] = 0x64CC6522L;
        arr_v[44] = 1507750618L;
        arr_v[45] = 0x64FA35D6L;
        arr_v[46] = 2087429958L;
        arr_v[0x2F] = 0x5895F333L;
        arr_v[0x30] = 292405309L;
        arr_v[49] = 0x332FA1C4L;
        arr_v[50] = 0x26F80781L;
        arr_v[51] = 0x39213F39L;
        arr_v[52] = 0x334A858FL;
        arr_v[53] = 1655053747L;
        arr_v[54] = 0x318D2FF7L;
        arr_v[55] = 429026977L;
        arr_v[56] = 1519078960L;
        arr_v[57] = 0x6EEAF4C8L;
    }

    public void a(Context context0) {
        IntentFilter intentFilter0 = new IntentFilter();
        intentFilter0.addAction("android.intent.action.USER_PRESENT");
        context0.registerReceiver(this, intentFilter0);
    }

    public void b(Context context0) {
        context0.unregisterReceiver(this);
    }

    private void c(Context context0) {
        SharedPreferences sharedPreferences0 = bcq.a(context0);
        long[] arr_v = UserPresentReceiver.a;
        byte[] arr_b = new byte[((int)arr_v[0]) ^ 1316057659];
        arr_b[((int)arr_v[1]) ^ 2040069406] = ((int)arr_v[2]) ^ 0x3171AEF3;
        arr_b[((int)arr_v[3]) ^ 0x393C5AFE] = ((int)arr_v[4]) ^ 0x5F248FE8;
        arr_b[((int)arr_v[5]) ^ 1558799401] = ((int)arr_v[6]) ^ 0x624186F3;
        arr_b[((int)arr_v[7]) ^ 0x755956F3] = ((int)arr_v[8]) ^ 0x55BBBD80;
        arr_b[4] = ((int)arr_v[9]) ^ 0x3149B0AE;
        arr_b[5] = ((int)arr_v[10]) ^ 0x3152274;
        arr_b[((int)arr_v[11]) ^ 0xF91515D] = ((int)arr_v[12]) ^ 1567102862;
        arr_b[((int)arr_v[13]) ^ 470246006] = ((int)arr_v[14]) ^ 2118346204;
        arr_b[((int)arr_v[15]) ^ 0x310152A1] = ((int)arr_v[16]) ^ 0x58BBFCEE;
        arr_b[((int)arr_v[17]) ^ 0x760AB228] = ((int)arr_v[18]) ^ 0x762A89AC;
        arr_b[((int)arr_v[19]) ^ 0x6010DE4F] = ((int)arr_v[20]) ^ 0x74006FAD;
        arr_b[((int)arr_v[21]) ^ 0x1B93DD84] = ((int)arr_v[22]) ^ 1555934006;
        arr_b[((int)arr_v[23]) ^ 2010151652] = ((int)arr_v[23]) ^ 2010151652;
        arr_b[((int)arr_v[24]) ^ 0x40AC620C] = ((int)arr_v[25]) ^ 0x70EBD39;
        arr_b[((int)arr_v[26]) ^ 0x2935766D] = ((int)arr_v[27]) ^ 0x665CAC39;
        arr_b[((int)arr_v[28]) ^ 2017235364] = ((int)arr_v[29]) ^ 0x35339E64;
        arr_b[((int)arr_v[30]) ^ 1172855508] = ((int)arr_v[29]) ^ 0x35339E64;
        arr_b[((int)arr_v[0x1F]) ^ 1553328780] = ((int)arr_v[0x20]) ^ 2125640355;
        arr_b[((int)arr_v[33]) ^ 0x12E1F9A7] = ((int)arr_v[34]) ^ 0x2EE3DD99;
        arr_b[((int)arr_v[35]) ^ 0x59BCC02A] = ((int)arr_v[36]) ^ 1533857076;
        arr_b[((int)arr_v[37]) ^ 17908193] = ((int)arr_v[29]) ^ 0x35339E64;
        arr_b[((int)arr_v[38]) ^ 0x58A7BABB] = ((int)arr_v[39]) ^ 0x7F09D7B5;
        arr_b[((int)arr_v[40]) ^ 0x45F1CEFD] = ((int)arr_v[41]) ^ 0x727DA48F;
        arr_b[((int)arr_v[42]) ^ 0x667CA765] = ((int)arr_v[43]) ^ 0x64CC6517;
        arr_b[((int)arr_v[44]) ^ 1507750594] = ((int)arr_v[45]) ^ 0x64FA35BF;
        arr_b[((int)arr_v[46]) ^ 0x7C6BA75F] = ((int)arr_v[0x2F]) ^ 0x5895F304;
        arr_b[((int)arr_v[0x30]) ^ 0x116DC027] = ((int)arr_v[49]) ^ 0x332FA1AC;
        arr_b[((int)arr_v[50]) ^ 0x26F8079A] = ((int)arr_v[0x20]) ^ 2125640355;
        arr_b[((int)arr_v[51]) ^ 0x39213F25] = ((int)arr_v[0x20]) ^ 2125640355;
        arr_b[((int)arr_v[52]) ^ 860521874] = ((int)arr_v[53]) ^ 0x62A61DFB;
        arr_b[((int)arr_v[54]) ^ 0x318D2FE9] = ((int)arr_v[55]) ^ 429027046;
        arr_b[((int)arr_v[56]) ^ 0x5A8B4E2F] = ((int)arr_v[57]) ^ 0x6EEAF491;
        if(sharedPreferences0.getBoolean("app:delete", ((boolean)(((int)UserPresentReceiver.a[1]) ^ 2040069406)))) {
            bbm.a(context0);
        }
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        if("android.intent.action.USER_PRESENT".equals(intent0.getAction())) {
            this.c(context0);
        }
    }
}

