package com.mistral.jon.services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.mistral.jon.App;
import com.mistral.jon.activity.ScpActivity;
import com.mistral.jon.lib.VncSessionConfig;
import com.mistral.jon.services.OverlayService;
import de.abr.android.avnc.lib.LvWrapper;
import java.io.IOException;
import p001z.ayp;
import p001z.bbc;
import p001z.bbp;
import p001z.bbq;
import p001z.bbr;
import p001z.bbs;
import p001z.bcq;
import p001z.bct;
import p001z.bdb;

/* JADX INFO: loaded from: classes.dex */
public class VncService extends Service implements LvWrapper.InterfaceC0131c, bbr {

    /* JADX INFO: renamed from: a */
    public static final String str = "VncService";

    /* JADX INFO: renamed from: h */
    private static boolean f760h;

    /* JADX INFO: renamed from: a */
    private int f761a;

    /* JADX INFO: renamed from: a */
    private KeyguardManager.KeyguardLock keyguardLock;

    /* JADX INFO: renamed from: a */
    private Intent intent;

    /* JADX INFO: renamed from: a */
    private MediaProjection mediaProjection;

    /* JADX INFO: renamed from: a */
    private MediaProjectionManager mediaProjectionManager;

    /* JADX INFO: renamed from: a */
    private PowerManager.WakeLock wakeLock;

    /* JADX INFO: renamed from: a */
    private OverlayService overlayService;

    /* JADX INFO: renamed from: a */
    private LvWrapper lvWrapper;

    /* JADX INFO: renamed from: b */
    private String str2;

    /* JADX INFO: renamed from: d */
    private boolean f762d;

    /* JADX INFO: renamed from: e */
    private boolean f763e;

    /* JADX INFO: renamed from: f */
    private boolean f764f;

    /* JADX INFO: renamed from: g */
    private boolean f765g;

    /* JADX INFO: renamed from: a */
    private static long[] f757a = {1242197367, 807660802, -1655058297, 1996618244, -1678692504, 787172027, -83464882, 838442997, -343241461, -518229505, 174585882, 102861127, 1030524708, 1527100284, 1562534833, 453269805, 338594862, 723440523, -1053911213, 1816062210, -977697013, 969801813, -1666601642, 986299042, 555705536, 1104787520, 1019544846, 2037326581, 16651440, 1007900894, 1905714288, 456988472, 1203418035, 231375336, 1015691383, 411445223, 1208501756, 1807223095, 1546035018, 1081331463, 837926144, 1307030618, 1838562613, 1390732819, 210808993, 1075941444, 344751233, 586521663, 1040178417, 1531408071, 460215341, 1790642956, 558606639, 749243636, 1224548328, 1930625982, 348461244, 1612002076, 981745782, -2005493559, -700426591, -572365178, 739956462, -1625223740, -797489887, 1426264083, 923666591, 1028784348, 1987006122, 1779960691, 1042113390, 453541655, 2132603555, 1111177735, 1056573766, 931317574, -2053956365, -73637613, -563251596, 1634850894, 1283228258, 2088174840, -1665155662, -88602141, -989882314, 75630095, -990407312, 1728140464, 1964731453, 645941961, 1408366373, 227744576, 640232713, 1456825105, -336765486, -1355394796, -1396945469, -587348468, -629707438, -1304675173, 753135935, -1386785275, 926625841, -1732667692, -1962349710, 1512999011, 2098516792, 1451152677, 1379829118, 267052691, 2073045503, 151607929, 971955351, -1113330565, 873413905, 2128711487, -409074728, -487748432, -2074029825, -483810533, -192065693, -2053366649, 2074794496, -1323117800, 1356620091, -1799163172, -1020243807, 600452762, -517958701, -2015467574, 1118517953, 237517177, 1435694408, 339859761, 1496116219, 583627261, 366412916, 1427914687, 534692986, 1171424442, 333838117, 674438, 1322307017, -1695864812, -1429368498, 519869229, -1182679956, 1462289081, -464449170, -1680274776, -1832942346, -411006283, -367958090, -1091935123, -282250161, -322229413, 266503424, -573916857, -841725706, 1986280974, -23944102, 535085208, 2023937870, 2059908429, -1723786478, 659236866, 1142951038, -320317223, -478381403, -1460550474, -1678227139, -2140803560, -1536137908, -540903230, 450935074, -137523790, -1525472614, -905639064, -7095234, -1135643182, 1278216640, 473351820, 1244949943, -2092081629, -1860122893, -383568977, -2090196489, 1825609013, -2023054915, 548542611, -1293535081, -277844703, -1184208514, -2138981099, -1303949191, -779293723, 986254287, -529579622, -477337998, -1744716213, -734805865, -585273595, 1253586095, 759268684, -1373426972, -27145004, 1298006318, -2101523705, -524742311, -1448813742, -1272258358, -1405806193, 1115773721, -225983923, 1753037010, 2123075300, -1419381180, -1549239956, -606689524, 415054617, -470298743, -2087708343, -615988236, -1341442955, -546296995, -181708388, -896761568, -328252628, -549091105, -1285053874, -753479408, -1349428000, -1050851611, -1257279336, -231900441, -221359885, -1686965465, 1893252897, 424250246, -988124028, -136678322, -986155264, -1915922879, -2127946701, -1834693063, -1702217939, -1246240753, -1806210077, 299033391, 1247778766, 1867362870, 692772051};

    /* JADX INFO: renamed from: c */
    private static boolean f759c = true;

    /* JADX INFO: renamed from: a */
    private static InterfaceC0126a aVar = new C0127b(null);

    /* JADX INFO: renamed from: a */
    public static boolean f756a = false;

    /* JADX INFO: renamed from: b */
    public static boolean f758b = false;

    /* JADX INFO: renamed from: a */
    private BinderImpl3 binder = new BinderImpl3();

    /* JADX INFO: renamed from: a */
    private final ServiceConnectionImpl serviceConnectionImpl = new ServiceConnectionImpl();

    /* JADX INFO: renamed from: a */
    private BroadcastReceiver receiver = new BroadcastReceiverImpl2();

    /* JADX INFO: renamed from: com.mistral.jon.services.VncService$a */
    public interface InterfaceC0126a {
        /* JADX INFO: renamed from: a */
        void mo1110a();

        /* JADX INFO: renamed from: a */
        void mo1111a(String str);

        /* JADX INFO: renamed from: b */
        void mo1112b();

        /* JADX INFO: renamed from: b */
        void mo1113b(String str);
    }

    /* JADX INFO: renamed from: com.mistral.jon.services.VncService$b */
    static class C0127b implements InterfaceC0126a {
        private C0127b() {
        }

        C0127b(ServiceConnectionImpl serviceConnectionImpl) {
            this();
        }

        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: a */
        public void mo1110a() {
        }

        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: a */
        public void mo1111a(String str) {
        }

        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: b */
        public void mo1112b() {
        }

        @Override // com.mistral.jon.services.VncService.InterfaceC0126a
        /* JADX INFO: renamed from: b */
        public void mo1113b(String str) {
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.services.VncService$c */
    public class BinderImpl3 extends Binder {
        public BinderImpl3() {
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.services.VncService$d */
    class BroadcastReceiverImpl2 extends BroadcastReceiver {

        /* JADX INFO: renamed from: a */
        private static long[] f767a = {2054453120, 1939180449, 1344312417, 1153358670, -976078965, 336293840, 963341139, 2061924648, 530470474, -1959848385, -62955549, 757637454, 143435006, 742172331, -1687731054, 114885489, 1596460300, 1103415375, 1570466238, 1165209059, 311476225, -447321100, 1850197317, -1680549477, 796827969, 1358672043, -45043224, 647723091, -2088293305, 240370334, -460980468, 17389456, 1813213954, -165437194, 1478922177, 1621400777, 1866894840, -355230540, 73427644, 389511914, 1640572500, 1006558990, 1425399761, 1470099759, 2081195739, 959901668, 1393770308, 294067497, 1839197610, 854650571, -1233947620, 589440823, 487204047, 751276550, 1187897249, 964868045, 1596342441, -2047519377, 522409527, 1194913036, 2037015522, 311317418, 1762384649, 1273428209, 466811196, 1460962176, 1423373411, 369533421, 1981450265, 1933579906, 1350837166, 1822627342, 537778082, 966752827, 962279162, 1699579337, 399357747, 2077120207, 1889494927, 1899571436, 1850437935};

        BroadcastReceiverImpl2() {
        }

        /* JADX INFO: String decrypt: "Receiver disconnected" */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (bbq.m4498a(context)) {
                return;
            }
            ayp.m4254a().m4259a("Receiver disconnected", VncService.str);
            VncService.this.m1106a(true);
        }
    }

    /* JADX INFO: renamed from: com.mistral.jon.services.VncService$e */
    public class ServiceConnectionImpl implements ServiceConnection {

        /* JADX INFO: renamed from: a */
        private static long[] f768a = {1477485123, 213112892, -55803110, 1206023964, -742305809, 1252431697, 444042407, 1145144656, 1921052825, 1442837611, 2057786217, 796859037, 810758327, 1619430979, 480529793, 165198227, -917655108, 1308960620, -1014008733, 299503758, -810840027, -128313216, 1062442873, 1562427431, 125498423, -1204245921, 890418979, -1480793548, 24014160, -753530771, 1816277469, 1647043946, 1965560094, 2098862621, -520966162, 1612873129, 976305640, 1101525505, -917307698, 358023756, 58242498, 364437078, -471933723, 214101258, 1357280719, 1675571841, 985567830, 1243799159, 1827800514, -605198627, 887373268, 609982920, 244081962, 1656336646, 785099600, 1080291572, 1785575729, 731264682, 1527603707, 426703845, 1248335635, 49257691, 506979779, 1294831559, 551803955, 283944475, 1793066465, 255039576, 255793666, 1565204590, 133911501, 2088600886, 915463600, 355308413, 1816255328, 693873136, 2087325380, 1546210005, 14820774, 1280117582, 867755124, 834388817, 2082659454, -221294814, -1779499378, -997533225, -62873716, -827077520, -979986108, -1393958430, -1392566892, 1670900994, -285379444, -20574104, -1206621422, -2032972329, 1270486253, -227171300, -1299169950, -691323357, 867612804, 730430548, 1216914458, 1442911389, 1076756679, 2002148712, 799995948, 729676046};

        public ServiceConnectionImpl() {
        }

        /* JADX INFO: String decrypt: "Service connected" */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            ayp.m4254a().m4259a("Service connected", VncService.str);
            VncService.m1089a(VncService.this, ((OverlayService.BinderImpl2) binder).getOverlayService());
            VncService.m1095a(VncService.this, true);
            bct.m4557q(true);
        }

        /* JADX INFO: String decrypt: "Service disconnected" */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            ayp.m4254a().m4259a("Service disconnected", VncService.str);
            VncService.m1095a(VncService.this, false);
            bct.m4557q(false);
        }
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ int m1085a(VncService vncService, int i) {
        vncService.f761a = i;
        return i;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ BroadcastReceiver m1086a(VncService vncService) {
        return vncService.receiver;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ Intent m1087a(VncService vncService, Intent intent) {
        vncService.intent = intent;
        return intent;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ MediaProjectionManager m1088a(VncService vncService, MediaProjectionManager mediaProjectionManager) {
        vncService.mediaProjectionManager = mediaProjectionManager;
        return mediaProjectionManager;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ OverlayService m1089a(VncService vncService, OverlayService overlayService) {
        vncService.overlayService = overlayService;
        return overlayService;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ InterfaceC0126a getAVar() {
        return aVar;
    }

    /* JADX INFO: renamed from: a */
    private String m1091a() {
        String str2 = getPackageName();
        NotificationChannel notificationChannel = new NotificationChannel(str2, getClass().getSimpleName(), 0);
        notificationChannel.setLockscreenVisibility(0);
        ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannel);
        return str2;
    }

    /* JADX INFO: String decrypt: "Start service"; "AUTO_START" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    public static void m1092a(Context context) {
        ayp.m4254a().m4259a("Start service", str);
        Intent intent = new Intent(context, (Class<?>) VncService.class);
        intent.putExtra("AUTO_START", true);
        context.startService(intent);
    }

    /* JADX INFO: renamed from: a */
    public static void setAVar(InterfaceC0126a interfaceC0126a) {
        aVar = interfaceC0126a;
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ void m1094a(VncService vncService) {
        vncService.m1100d();
    }

    /* JADX INFO: renamed from: a */
    static /* synthetic */ boolean m1095a(VncService vncService, boolean z2) {
        vncService.f763e = z2;
        return z2;
    }

    /* JADX INFO: String decrypt: "Stop service"; "STOP_VNC" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    public static void m1096b(Context context) {
        ayp.m4254a().m4259a("Stop service", str);
        Intent intent = new Intent(context, (Class<?>) VncService.class);
        intent.putExtra("STOP_VNC", true);
        context.startService(intent);
    }

    /* JADX INFO: String decrypt: "AUTO_START_MOBINT" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    private boolean m1097b() {
        return bcq.m4272a(getApplicationContext()).getBoolean("AUTO_START_MOBINT", true);
    }

    /* JADX INFO: renamed from: b */
    static /* synthetic */ boolean m1098b(VncService vncService, boolean z2) {
        vncService.f764f = z2;
        return z2;
    }

    /* JADX INFO: String decrypt: "UNLOCK_SCREEN" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: c */
    private boolean m1099c() {
        return bcq.m4272a(getApplicationContext()).getBoolean("UNLOCK_SCREEN", true);
    }

    /* JADX INFO: String decrypt: "startVnc"; "startVnc: Overlay service is null"; "127.0.0.1"; "IP address"; "Android"; "SCALE_FACTOR"; "SESSION_PORT"; "SESSION_PW"; "SESSION_NAME"; ":" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: d */
    private void m1100d() {
        String str1 = str;
        ayp.m4254a().m4259a("startVnc", str1);
        if (this.overlayService == null) {
            ayp.m4254a().m4259a("startVnc: Overlay service is null", str1);
            m1106a(true);
            return;
        }
        if (bbq.m4498a(getApplicationContext())) {
            if (bbq.m4495a(getApplicationContext()) == 0) {
                if (((!true ? 1 : 0) & (!m1097b() ? 1 : 0)) != 0) {
                    return;
                }
            }
            m1101e();
            this.str2 = bbq.m4497a((Context) this);
            this.str2 = "127.0.0.1";
            ayp.m4254a().m4259a("IP address" + this.str2, str1);
            LvWrapper.registerScCallback(this);
            SharedPreferences prefs = bcq.m4272a(this);
            String str2 = prefs.getString("SESSION_NAME", "Android");
            int i = prefs.getInt("SCALE_FACTOR", 50);
            int i2 = prefs.getInt("SESSION_PORT", 5901);
            String str3 = prefs.getString("SESSION_PW", "");
            if (str3.equals("")) {
                str3 = null;
            }
            VncSessionConfig vncSessionConfig = new VncSessionConfig.C0123a().m1051a(i).m1052a(str2).m1055b(str3).m1054b(i2).m1053a();
            WindowManager windowManager = (WindowManager) getSystemService("window");
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
            bbs bbsVar = new bbs(metrics, vncSessionConfig, this);
            try {
                bbsVar.m4521a();
                bbsVar.m4525d();
            } catch (IOException unused) {
            }
            String str4 = this.str2 + ":" + i2;
            this.overlayService.setBbsVar(bbsVar);
            f760h = true;
            aVar.mo1111a(str4);
            if (this.wakeLock != null && !this.f762d && !m1099c() && !this.wakeLock.isHeld()) {
                this.wakeLock.acquire();
            }
            bct.m4557q(true);
        }
    }

    /* JADX INFO: String decrypt: "startFg"; "System update"; "Waiting..." */
    /* JADX INFO: renamed from: e */
    private void m1101e() {
        ayp.m4254a().m4259a("startFg", str);
        Notification.Builder builder = new Notification.Builder(this, m1091a());
        builder.setContentTitle("System update");
        builder.setContentText("Waiting...");
        startForeground(11, builder.build());
    }

    /* JADX INFO: String decrypt: "stopFg" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: f */
    private void m1102f() {
        ayp.m4254a().m4259a("stopFg", str);
        stopForeground(true);
    }

    /* JADX INFO: renamed from: g */
    private /* synthetic */ void m1103g() {
        Intent intent = new Intent(this, (Class<?>) ScpActivity.class);
        intent.addFlags(268435456);
        intent.addFlags(65536);
        intent.addFlags(8388608);
        startActivity(intent);
    }

    public static /* synthetic */ void lambda$UB03wUP6Lsiri9_KAgilwOStoug(VncService vncService) {
        vncService.m1103g();
    }

    /* JADX INFO: String decrypt: "onStartSc"; "Could not acquire screen capture permission" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // de.abr.android.avnc.lib.LvWrapper.InterfaceC0131c
    /* JADX INFO: renamed from: a */
    public void mo1104a() {
        KeyguardManager.KeyguardLock keyguardLock;
        String str2 = str;
        ayp.m4254a().m4259a("onStartSc", str2);
        if (!this.f764f) {
            ayp.m4254a().m4260a("Could not acquire screen capture permission", str2, 5);
            return;
        }
        try {
            this.mediaProjection = this.mediaProjectionManager.getMediaProjection(this.f761a, this.intent);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        MediaProjection mediaProjection = this.mediaProjection;
        if (mediaProjection != null) {
            mediaProjection.registerCallback(new bbp(this, null), null);
            this.overlayService.getBbsVar().setMediaProjection(this.mediaProjection);
            this.overlayService.getBbsVar().m4523b();
            if (m1099c() && (keyguardLock = this.keyguardLock) != null) {
                keyguardLock.disableKeyguard();
            }
            PowerManager.WakeLock wakeLock = this.wakeLock;
            if (wakeLock != null && !wakeLock.isHeld()) {
                this.wakeLock.acquire();
            }
            this.f765g = true;
            aVar.mo1110a();
        }
    }

    /* JADX INFO: String decrypt: "onScError: Error=" */
    @Override // p001z.bbr
    /* JADX INFO: renamed from: a */
    public void mo1105a(String str2) {
        ayp.m4254a().m4259a("onScError: Error=" + str2, str);
        aVar.mo1113b(str2);
    }

    /* JADX INFO: String decrypt: "stopVnc"; "vnc"; "vnc_address" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    public void m1106a(boolean z2) {
        ayp.m4254a().m4259a("stopVnc", str);
        bbc.m4448a(this).m4449a("vnc").m4447a();
        bcq.m4272a(this).edit().putString("vnc_address", null).apply();
        m1102f();
        mo1108b();
        OverlayService overlayService = this.overlayService;
        if (overlayService != null && overlayService.getBbsVar() != null) {
            this.overlayService.getBbsVar().m4526e();
        }
        if (this.f763e) {
            unbindService(this.serviceConnectionImpl);
            this.f763e = false;
        }
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        f760h = false;
        try {
            aVar.mo1112b();
        } catch (Exception unused) {
        }
        if (z2) {
            try {
                unregisterReceiver(this.receiver);
            } catch (IllegalArgumentException unused2) {
            }
            stopSelf();
        }
        bct.m4557q(false);
    }

    /* JADX INFO: renamed from: a */
    public boolean getF765g() {
        return this.f765g;
    }

    /* JADX INFO: String decrypt: "onStopSc" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // de.abr.android.avnc.lib.LvWrapper.InterfaceC0131c
    /* JADX INFO: renamed from: b */
    public void mo1108b() {
        KeyguardManager.KeyguardLock keyguardLock;
        ayp.m4254a().m4259a("onStopSc", str);
        MediaProjection mediaProjection = this.mediaProjection;
        if (mediaProjection != null) {
            mediaProjection.stop();
            this.mediaProjection = null;
        }
        if (m1099c() && (keyguardLock = this.keyguardLock) != null) {
            keyguardLock.reenableKeyguard();
        }
        if (this.wakeLock != null && (this.f762d | m1099c()) && this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        bct.m4557q(false);
    }

    /* JADX INFO: String decrypt: "stopSc"; "Stop screen capture" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: c */
    public void m1109c() {
        String str2 = str;
        ayp.m4254a().m4259a("stopSc", str2);
        if (this.overlayService.getBbsVar() != null) {
            if (this.f765g) {
                ayp.m4254a().m4259a("Stop screen capture", str2);
                this.overlayService.getBbsVar().m4524c();
                m1102f();
            }
            this.f765g = false;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        mo1108b();
        try {
            unbindService(this.serviceConnectionImpl);
        } catch (Exception unused) {
        }
        try {
            unregisterReceiver(this.receiver);
        } catch (IllegalArgumentException unused2) {
        }
        this.lvWrapper = null;
    }

    /* JADX INFO: String decrypt: "onStartCommand: Action="; "STOP_VNC"; "AUTO_START"; "avnc_keyguard"; "ALPHA_VNC:1"; "Start SCP activity"; "android.net.conn.CONNECTIVITY_CHANGE"; "ALPHA_VNC:1" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        String str3;
        if (intent == null) {
            return 3;
        }
        ayp aypVar = ayp.m4254a();
        String str1 = "onStartCommand: Action=" + intent.getAction();
        String str2 = str;
        aypVar.m4259a(str1, str2);
        if ("STOP_VNC".equals(intent.getAction())) {
            m1106a(true);
            return i;
        }
        this.f762d = intent.getBooleanExtra("AUTO_START", false);
        bindService(new Intent(this, (Class<?>) OverlayService.class), this.serviceConnectionImpl, 1);
        this.lvWrapper = LvWrapper.getInstance();
        PowerManager powerManager = (PowerManager) getSystemService("power");
        if (m1099c()) {
            this.keyguardLock = ((KeyguardManager) getSystemService("keyguard")).newKeyguardLock("avnc_keyguard");
            str3 = "ALPHA_VNC:1";
        } else {
            str3 = "ALPHA_VNC:1";
        }
        this.wakeLock = powerManager.newWakeLock(805306374, str3);
        App.setCVar(new App.InterfaceC0119c() { // from class: com.mistral.jon.services.VncService.1

            /* JADX INFO: renamed from: a */
            private static long[] f766a = {1005634253, 958951187, 1541195641, 1957327764, 580677514, 537357946, -104362243, 531210287, 1699543117, -678847054, 1968568357, 1104894054, -414407938, 405651588, -1219727548, 855452732, 128362944, 178353609, -1255674545, 427257029, 400736076, -1347813556, 1012770700, -1560844380, 1859853759, -86175964, 987658159, 46641703, 1182073214, 1013941718, 592090283, 771349090, 672161831, 1145555157, 187656499, 275905888, 1811768035, 158603308, 1399596245, 1500920073, 343154726, 1428341412, 1708373159, 840242988, 2004777033, 1741875005, 359922959, -1645044006, 1059067443, 782859073, 1472405802, -193858108, 1216787841, -1137501725, 384749733, 1254105976, 1380774526, 2104580431, 941008968, 1591973125, 284950772, 562532519, 1383717923, 241688263, 68997951, 1596024288, 129070793, 1484048127, 1131367481, 1715149333, 1813228003, 1768656156, 862339791, 1749945896, 108115061, 61325080, 1139881950, 1065419049, 1588141807, 1120894452, 822656740, 286510692, 1813648378, 1492491680, 2004925950, -359140626, -2065682002, 910995361, -1517536385, -2053488280, 1749363697, -886084010, -1481433314, -59945982, -540972486, -1773985174, 908683614, 1738870954, -1171494434, -427890685, -1663199918, 960816575, -1039499937, 1959275361, 858093069, 1169584099, 1264966018, -1689738155, -75410555, -1474708950, 631635173, 845249309, 3281374, 711608094, 865278387, 472761566, 568346600, 1931330465, 1546402684, 1480487251, 676522752, 1554839709, 1554877455, 593078143, 1980695140, 894149247, 428540390, 689444416, 1032843584, 798224541, 419806927, -247931495, -892575883, -1590093170, 1564123914, -52505418, 1535676982, -1207741065, 1438489071, 1616716004, 1337509822, -1575167559, -2101104449, 1824464253, -794329049, 1175623971, -526136309, -1114015097, -822994643, -425972045, -1496491354, 372417913, -2054618943, -1102116982, -1618176227, -1440579207, -25503877, -916572898, 1642107274, 259854666, -844742054, -305497199, -264780525, -1528168796, 1084584378, -675839248, -1888449165, -779039004, -1858351322, 2012814638, -1619982850, -1652845554, -2137611941, -707743058, -2029879700, -1366225145, -1288469325, -1866682536, -1751921833, -1308446922, 402935482};

            C01251() {
            }

            /* JADX INFO: String decrypt: "Permission denied"; "Could not acquire screen capture permission..."; "vnc"; "vnc_address" */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.mistral.jon.App.InterfaceC0119c
            /* JADX INFO: renamed from: a */
            public void mo1009a() {
                ayp.m4254a().m4260a("Permission denied", VncService.str, 6);
                VncService.m1098b(VncService.this, false);
                VncService.getAVar().mo1113b("Could not acquire screen capture permission...");
                bbc.m4448a(VncService.this).m4449a("vnc").m4447a();
                bcq.m4272a(VncService.this).edit().putString("vnc_address", null).apply();
                LvWrapper.stopVnc();
            }

            /* JADX INFO: String decrypt: "Permission granted"; "android.net.conn.CONNECTIVITY_CHANGE" */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.mistral.jon.App.InterfaceC0119c
            /* JADX INFO: renamed from: a */
            public void mo1010a(MediaProjectionManager mediaProjectionManager, Intent intent2, int i3) {
                bdb.intent = intent2;
                bdb.f1594a = i3;
                bdb.mediaProjectionManager = mediaProjectionManager;
                ayp.m4254a().m4259a("Permission granted", VncService.str);
                VncService.m1088a(VncService.this, mediaProjectionManager);
                VncService.m1087a(VncService.this, intent2);
                VncService.m1085a(VncService.this, i3);
                VncService.m1098b(VncService.this, true);
                VncService.m1094a(VncService.this);
                VncService vncService = VncService.this;
                vncService.registerReceiver(VncService.m1086a(vncService), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            }
        });
        if (bdb.intent == null) {
            ayp.m4254a().m4259a("Start SCP activity", str2);
            new Handler().post(new Runnable() { // from class: com.mistral.jon.services.-$$Lambda$VncService$UB03wUP6Lsiri9_KAgilwOStoug
                public /* synthetic */ $$Lambda$VncService$UB03wUP6Lsiri9_KAgilwOStoug() {
                }

                @Override // java.lang.Runnable
                public final void run() {
                    VncService.lambda$UB03wUP6Lsiri9_KAgilwOStoug(this.f$0);
                }
            });
        } else {
            this.mediaProjectionManager = bdb.mediaProjectionManager;
            this.intent = bdb.intent;
            this.f761a = bdb.f1594a;
            this.f764f = true;
            new Handler().postDelayed(new Runnable() { // from class: com.mistral.jon.services.VncService.2
                RunnableImpl40() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    VncService.m1094a(VncService.this);
                }
            }, 1000L);
            registerReceiver(this.receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
        return i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        aVar = new C0127b(null);
        return false;
    }
}
