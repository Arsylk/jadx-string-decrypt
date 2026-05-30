package com.mistral.jon.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.HashSet;
import java.util.Set;
import p001z.C0597gz;
import p001z.ayp;

/* JADX INFO: loaded from: classes.dex */
public class FgSvc extends Service {

    /* JADX INFO: renamed from: a */
    private Notification notification = null;

    /* JADX INFO: renamed from: a */
    private static long[] f745a = {250202629, 682817964, 456572412, 1901752875, -1541056622, 676744098, 1067391728, 1977923201, 276618780, -1322062023, 1719368037, 1626666212, 593019268, 1863164731, 40946477, -1969479250, 1498499103, -848674441, 1372056557, 82136515, -590846070, 987119767, -728039852, 1200123212, -652069951, 553016883, -1456477050, 1537195040, 438783978, 50163792, -1766606299, 312076493, 533847652, -872584803, 1243145645, -1639026360, 1914230858, 1886623108, 676260045, 879299963, -528820074, 1408790880, -1593790311, 1403569384, -618048461, 168781068, -65164294, 1646866986, 10007836, 1565850323, 1202524157, 1924625618, 1614917391, -1436952217, 1017298305, 100267479, 1817629214, 1996907872, 969967205, 751042035, 462372256, 9568291, 322323614, 1960980743, 2029946036, 467974584, 109863041, 230096752, 1032150273, 2086618669, 150894721, 1327314790, 432518398, 918162630, 1840526924, 1761090078, 374479133, 2054805833, 2063418568, 1471894762, -269286860, 1883454878, 1644890015, -925530082, -1178273395, 1406371155, 1391609547, 1337368332, -765993764, 1924283377, -366520963, 895097685, 889318153, 798780315, 643140190, 1673333234, 1294873117, 1851446484, 1556813616, 1743452392, 2088474537, 642152772, -175419331, -51428772, -1834501985, 391318615, 968402177, -1018877666, 460709374, -741137307, 681015730, 16761891, 1501953179, 602929465, 1282943494, 621212979, 1823060814, -1096320952, -883998745, -831778285, -971408851, 136850302, -1168377071, -549879695, 940998978, 972734474, 1421922420, 1161562348, -1249208414, -1107597812, -196939070, 1514848997, -1136890773, -1834396497, -1185820448, 2119130459, -1352132809, -124328822, -1455582748, 437661194, 193246730, 921889223, 637127045, 1986818400, 868751308, 94200105, -888366732, 133897807, -1040119669, -2014067875, -441681887, 148116846, -1582991338, 656376040, -756020204, 1605593210, -1204458235, 761408385, -1941195298, -436320211, -1074507697, -2087255647, 135094139, 1950463694, 338626056, 932625154, -1212305595, -1541538334, -1956769393, -1886349069, -903068817, 378382139, 1116818430, -578429814, -199558574, -1495842208, -764492964, 1640590429, -1043941369, -818656270, -779369491, -163288187, -1359819816, 23526464, -237863894, -226593477, -1091612507, -694281204, -771693615, -798490555, -1842783881, -581079676, -781753511, -1891580230};

    /* JADX INFO: renamed from: a */
    private static final String str = "NgrokForeground";

    /* JADX INFO: renamed from: b */
    private static final String str2 = "port";

    /* JADX INFO: renamed from: c */
    private static final String str3 = "start";

    /* JADX INFO: renamed from: d */
    private static final String str4 = "stop";

    /* JADX INFO: renamed from: a */
    private static final Set<Integer> set = new HashSet();

    /* JADX INFO: String decrypt: "ch_"; "Notification channel"; "Running" */
    /* JADX INFO: renamed from: a */
    private Notification m1063a() {
        if (this.notification == null) {
            String str5 = "ch_" + getClass().getSimpleName();
            ((NotificationManager) getSystemService("notification")).createNotificationChannel(new NotificationChannel(str5, "Notification channel", 2));
            this.notification = new C0597gz.e(this, str5).m6144a((CharSequence) getPackageName()).m6151b("Running").m6145a("service").m6136a(2131165289).m6134a();
        }
        return this.notification;
    }

    /* JADX INFO: String decrypt: "Start " */
    /* JADX INFO: renamed from: a */
    private void m1064a(int i) {
        ayp.m4254a().m4259a("Start " + i, str);
        set.add(Integer.valueOf(i));
    }

    /* JADX INFO: String decrypt: "Stop "; "Service stopped" */
    /* JADX INFO: renamed from: b */
    private void m1065b(int i) {
        String str5 = str;
        ayp.m4254a().m4259a("Stop " + i, str5);
        if (i > 0) {
            set.remove(Integer.valueOf(i));
        }
        if (set.size() == 0) {
            ayp.m4254a().m4259a("Service stopped", str5);
            stopSelf();
        }
    }

    /* JADX INFO: String decrypt: "Not yet implemented" */
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        startForeground(1638, m1063a());
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        int i3;
        int i4;
        if (intent == null) {
            i3 = 1977923201;
            i4 = 1977923202;
        } else {
            int intExtra = intent.getIntExtra(str2, 0);
            if (intExtra > 0) {
                String str5 = intent.getAction();
                if (str3.equals(str5)) {
                    m1064a(intExtra);
                } else if (str4.equals(str5)) {
                    m1065b(intExtra);
                }
            }
            i3 = 1901752875;
            i4 = 1901752874;
        }
        return i3 ^ i4;
    }
}
