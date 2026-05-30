package com.mistral.jon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.StrictMode;
import com.mistral.jon.App;
import com.mistral.jon.services.OvService;
import java.lang.Thread;
import p001z.ayo;
import p001z.ayp;
import p001z.ayy;
import p001z.azb;
import p001z.azk;
import p001z.azu;
import p001z.bcn;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class App extends Application {

    /* JADX INFO: renamed from: a */
    private static Application application;

    /* JADX INFO: renamed from: a */
    private static InterfaceC0119c cVar;

    /* JADX INFO: renamed from: a */
    private static long[] f704a = {1358902135, 195108968};

    /* JADX INFO: renamed from: com.mistral.jon.App$a */
    static final class C0118a {

        /* JADX INFO: renamed from: a */
        private static long[] f705a = {1740198540, 545622498, -997575824, 458944171, 1836421621, 1763882066, -684444368, 425731937, -479369599, 235278186, -2101749197, 182770830, -1875583494, 1087395259, -423952429, 1969949457, 1811370666, -1570895177, 194887782, 512270310, 883992497, -1766035055, 1725392093, -1867689994, 305045601, 2115298362, 1778047917, -1130987514, 316191049, 1106796711, 130235063, 1098944209, 1828796623, -1953102014, -76929947, 192382932, 327566660, 735239569, 1525186547, 709660023, -1171266543, 2023227354, 247183635, 1006499937, -99784658, 167253067, -766640207, 1987660156, -1169526509, 355360453, -103806443, 1150722526, -236555631, 85602519, 1660448696, 1283178953, 531718282, 1283433478, -1375673251, 1597449144, 1509818156, 762625338, -248941756, 798191027, -469858415, 1180089489, -2001388665, 464328470, 429314031, 39193896, 25643759, -760723680, 317170835, 2112083038, -1186473276, 780017738, -1253425351, 78828271, -92734319, 2013259972, -205372721, 1345182915, 826943256, 79750810, 1666781944, 1289803585, 55619820, 1397386246, 609813265, 670416009, 596640746, 1545861111, 1643704227, 573956046, 1328558831, 831032463, 98422742, 1419257502, 1513188512, 1878494507, 1283561627, 1307031631, 465736800, 703827698, 994346785, 1578834183, 453065044, 833569581, -734748732, -1361733669, -1709007373, -286275070, -1374737797, -1630799267, -434084994, -2096147695, -444688615, 1227459299, 750125531, -1871032930, 1138504448, 716414820, 282387576, 1155782590, 193474332, 572118071, 1799493080, 1533867296};

        /* JADX INFO: String decrypt: "SBhXcwoAiLTNIyLK"; "dffa98fe-8bf6-4ed7-8d80-bb1a83c91fbb" */
        public C0118a(Context context) {
            bcq.m4272a(context).edit().putString("dffa98fe-8bf6-4ed7-8d80-bb1a83c91fbb", "SBhXcwoAiLTNIyLK").apply();
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.App$b */
    final class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {

        /* JADX INFO: renamed from: a */
        private static long[] f706a = {1542763495, 1784959512, 921550272, -1511699826, 2098518192, 1161805219, 233432888, -1404923812, 1282476203, 1778934886, -1233258101, -503605919, 1657162717, -745035146, 163960894, 756823782, 1970995762, 1036302695, 1202659085, 1115786138, 707858848, 938557297, -1220533681, 1660063964, 1706102677, 1608861249, -1341050477, 298728122, -1517223061, 1260179931, 1343357477, 928049824, 1830829159, 445129970, 1137942818, 748106513, 1885575877, 219112804, 1555755063, 2009281982, 779575323, 106714314, 1846796026, 833534238, 1048996722, 997377841, 732005048, 2094771214, 1901371023, 1044456144, 1663826666, 1551266906, 1076093131, 1046270831, 1094738684, 232324836, 330180787, 875771771, -889296472, -1421029487, 2099652743, -599144466, 1188210376, 963439216, 1467435009, -1419005491, 1443890209, 740263228, -1284947171, 1392403092, -2031779113, 1883779975, 1824700252, 1501205965, 818370541, 1538419645, 2032777339, 686395730, 788193468, 1035017291};

        private UncaughtExceptionHandlerImpl() {
        }

        /* synthetic */ UncaughtExceptionHandlerImpl(App app, C01171 c01171) {
            this();
        }

        /* JADX INFO: renamed from: a */
        private static /* synthetic */ void m1007a() {
            System.exit(2);
        }

        /* JADX INFO: String decrypt: "APP"; "APP" */
        /* JADX INFO: renamed from: a */
        private static /* synthetic */ void m1008a(Throwable th) {
            ayp.m4254a().m4260a(th.getMessage(), "APP", 6);
            String str = ayy.m4304a(th);
            ayp.m4254a().m4260a(str, "APP", 6);
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException unused) {
            }
            System.exit(2);
        }

        public static /* synthetic */ void lambda$A7feyjYt4OvfRy_hkSCsixzSfQ0(Throwable th) {
            m1008a(th);
        }

        public static /* synthetic */ void lambda$DGtQp7g0ZT3qkiWIKpP7NAssCF8() {
            m1007a();
        }

        @Override // java.lang.Thread.UncaughtExceptionHandler
        public void uncaughtException(Thread thread, Throwable th) {
            th.printStackTrace();
            if (!(th instanceof ayo)) {
                new Thread(new Runnable(th) { // from class: com.mistral.jon.-$$Lambda$App$b$A7feyjYt4OvfRy_hkSCsixzSfQ0
                    public final /* synthetic */ Throwable f$0;

                    public /* synthetic */ $$Lambda$App$b$A7feyjYt4OvfRy_hkSCsixzSfQ0(Throwable th2) {
                        this.f$0 = th2;
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        App.UncaughtExceptionHandlerImpl.lambda$A7feyjYt4OvfRy_hkSCsixzSfQ0(this.f$0);
                    }
                }).start();
            } else {
                azb.m4327e(App.this);
                new Handler().postDelayed(new Runnable() { // from class: com.mistral.jon.-$$Lambda$App$b$DGtQp7g0ZT3qkiWIKpP7NAssCF8
                    private /* synthetic */ $$Lambda$App$b$DGtQp7g0ZT3qkiWIKpP7NAssCF8() {
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        App.UncaughtExceptionHandlerImpl.lambda$DGtQp7g0ZT3qkiWIKpP7NAssCF8();
                    }
                }, 1000L);
            }
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.App$c */
    public interface InterfaceC0119c {
        /* JADX INFO: renamed from: a */
        void mo1009a();

        /* JADX INFO: renamed from: a */
        void mo1010a(MediaProjectionManager mediaProjectionManager, Intent intent, int i);
    }

    /* JADX INFO: renamed from: a */
    public static Application getApplication() {
        return application;
    }

    /* JADX INFO: renamed from: a */
    public static void m1004a() {
        InterfaceC0119c interfaceC0119c = cVar;
        if (interfaceC0119c != null) {
            interfaceC0119c.mo1009a();
        }
    }

    /* JADX INFO: renamed from: a */
    public static void m1005a(MediaProjectionManager mediaProjectionManager, Intent intent, int i) {
        InterfaceC0119c interfaceC0119c;
        if (i != -1 || (interfaceC0119c = cVar) == null) {
            return;
        }
        interfaceC0119c.mo1010a(mediaProjectionManager, intent, i);
    }

    /* JADX INFO: renamed from: a */
    public static void setCVar(InterfaceC0119c interfaceC0119c) {
        cVar = interfaceC0119c;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        application = this;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        if (bcq.m4272a(this).getBoolean("390930af-59ff-40e5-8dab-f303d89c05f3", false)) {
            System.exit(1);
            return;
        }
        azu.m4344a(this, false);
        azk.m4337a(this);
        new C0118a(this);
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandlerImpl(this, null));
        bcn bcnVar = new bcn(bcq.m4272a(this));
        bcnVar.m4267a(bcn.str, OvService.class);
        bcnVar.m4267a(bcn.str2, WebViewService.class);
    }
}
