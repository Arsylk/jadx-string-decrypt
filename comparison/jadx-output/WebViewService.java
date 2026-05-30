package com.mistral.jon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import com.mistral.jon.activity.WebViewActivity;
import p001z.C0597gz;
import p001z.ayp;
import p001z.azb;
import p001z.bbo;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class WebViewService extends Service {

    /* JADX INFO: renamed from: a */
    private static final String str = "WebViewService";

    /* JADX INFO: renamed from: a */
    private static long[] f711a = {52002597, 1173097774, 796808293, 1802993773, -698740333, 47228952, 1929632684, 1200204359, 682317984, -1094398360, 1275617895, 92925083, -1060057374, 1882307248, 717940677, 1651989948, -279946523, 1449978720, 259813980, 677921557, -1033510088, 1137436279, 1234507594, 1085250279, 460322728, 1523984677, 537433183, 463648524, 668713807, 93985363, 1724395123, 741993654, 295878490, 707795828, 1121080516, 1463122442, 1593978977, 915737080, 292356564, 1268769812, 1749391205, 378292983, 799968992, 261697283, 1591380422, 1072203936, 633945726, 1818914956, 376996173, 857439238, 1346922555, 1041720258, 1656660091, 39858249, 1697830696, 756792142, 713164020, 659575932, 712275164, -383521700, -2031018502, 1252805276, -782900866, 1991151440, 355220259, 1363594176, 269287585, -878522742, -2004728462, 37750673, 459768991, 1304708874, 1927200453, 1050240093, 1028948886, 970012977, 32593479, 1269430445, 455736501, 2143962614, 508658897, -1974283202, -1367031455, -1284361401, -1767952117, -1416090950, 2025000896, -276819512, -1086758497, 2053997172, 59046694, -105337628, -263307968, -89556560, -1870500026, 275132855, 1076945304, -1951443906, -543947499, -1749722182, -960188894, 1577024507, 811335242, 750885118, 199326971, 733882208, 1040057916, 1642707624, 1144226104, 774129862, 1531676747, 1442306521, 173960299, 1179635775, 546352135, 712273457, 569117299, -201950920, 292077451, -1228012257, -1033297459, -1736361287, -407478661, -20330051, 627281260, 1428292260, -1385360814, 508294143, -1145731935, -476912134, -358109300, -1544680423, -1179661070, 970666209, 1592464052, 859495939, 85497751, 1050496982, -1629324723, -661963770, -1466478293, -1282140365, -1291554654, 1781706084, -155995324, -1116246320, 1289638555, -2021185693, -1649453860, -1168534121, -1911910503, 804546585, -1528461561, 874824276, 1027542033, -1949392631, 1663934955, -837724591, -437282995, 600700115, -480684744, -1955285127, 1147369316, 892887769, 2039027271, 2073420678, 481783870, 2113495509, 2110714214, 1578936913};

    /* JADX INFO: renamed from: a */
    private boolean f712a = false;

    /* JADX INFO: String decrypt: "Started"; "wl:2"; "config:dialog:timeout"; "Show Notification"; "Show WebViewActivity"; "Exited" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private void m1015a() {
        ayp.m4254a().m4259a("Started", str);
        bbo.m4494a(this, getString(2131558457), "", 17301543);
        ((PowerManager) getSystemService("power")).newWakeLock(1, "wl:2").acquire();
        int i = bcq.m4272a(this).getInt("config:dialog:timeout", 30);
        while (!azb.m4322b((Context) this)) {
            if (WebViewActivity.f731a || false) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(i * 1000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                if (!WebViewActivity.f731a && !false) {
                    if (azb.m4322b((Context) this)) {
                        break;
                    }
                    if (Build.VERSION.SDK_INT < 29) {
                        ayp.m4254a().m4259a("Show WebViewActivity", str);
                        WebViewActivity.m1044a((Context) this);
                    } else {
                        ayp.m4254a().m4259a("Show Notification", str);
                        m1016b();
                    }
                }
            }
        }
        bbo.m4493a(this);
        this.f712a = false;
        ayp.m4254a().m4259a("Exited", str);
    }

    /* JADX INFO: String decrypt: "high" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    private void m1016b() {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, WebViewActivity.m1042a((Context) this), 0);
        C0597gz.e eVar1 = new C0597gz.e(this, bbo.m4492a(this, 4, "high")).m6136a(17301543).m6144a((CharSequence) getString(2131558402));
        Object[] objArr = new Object[1];
        objArr[0] = getString(2131558431);
        C0597gz.e eVar2 = eVar1.m6151b(getString(2131558453, objArr)).m6143a((Uri) null).m6141a(pendingIntent, true).m6156d(4).m6138a(0, getString(2131558437), pendingIntent);
        notificationManager.cancel(10);
        notificationManager.notify(10, eVar2.m6134a());
    }

    public static /* synthetic */ void lambda$AlB8UPsMfd5FIExns1FHMojxl2I(WebViewService webViewService) {
        webViewService.m1015a();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (!this.f712a) {
            this.f712a = true;
            new Thread(new Runnable() { // from class: com.mistral.jon.-$$Lambda$WebViewService$AlB8UPsMfd5FIExns1FHMojxl2I
                public /* synthetic */ $$Lambda$WebViewService$AlB8UPsMfd5FIExns1FHMojxl2I() {
                }

                @Override // java.lang.Runnable
                public final void run() {
                    WebViewService.lambda$AlB8UPsMfd5FIExns1FHMojxl2I(this.f$0);
                }
            }).start();
        }
        return 1;
    }
}
