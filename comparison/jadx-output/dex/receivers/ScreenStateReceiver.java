package com.mistral.jon.dex.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import p001z.azb;
import p001z.azf;
import p001z.ban;
import p001z.bbe;

/* JADX INFO: loaded from: classes.dex */
public class ScreenStateReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a */
    private static long[] f734a = {1151874475, 1420545904, 304695096, 1860950814, -612926939, 1816620020, -1686890730, 1478291438, 798373806, 652990066, -1020052800, 422733039, -1787584895, 27377191, -1565574400, 1572320102, 835160807, 1305760281, 1329685141, 1970927098, 410333641, -1123069904, 503393832, 1276555056, -71037009, 1014903408, 843134510, 1119912907, -115635098, 667701232, -2145067570, 301131675, 362097681, 338417294, 1808164848, -874242271, 1312170507, 1492418446, 1849402536, 1750279527, 1966085350, 712303920, 2130691457, -1193799277, -1737595104, 1232736082, 394348469, 1468206171, 1901497228, 293399626, 982285568, -1555093075, 1354359622, 1389830530, 947935818, 1224653293, 1270833349, 2110114454, 935067686, 1276268143, 1948414599, 932698360, 336969702, 1297869492, 959518757, 1716445082, 137301751, 1608835512, 1651325366, 1666468492, 1829072360, 895068457, 1186766613, 1087504993, 2111237240, 78791800, 169292264, 1977400421, 284268252, 2036302774, 950330539, 1158627994};

    /* JADX INFO: renamed from: a */
    private static final String str = "ScreenStateReceiver";

    /* JADX INFO: renamed from: a */
    private static ScreenStateReceiver screenStateReceiver = null;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String str2 = intent.getAction();
        ban.m4406a(str, str2);
        if ("android.intent.action.SCREEN_OFF".equals(str2)) {
            if (bbe.m4454a().m4457a(false)) {
                azf.m4332b();
            } else {
                azf.m4331a();
            }
            azb.m4319a(context);
        }
    }
}
