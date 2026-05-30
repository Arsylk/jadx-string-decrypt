package com.mistral.jon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mistral.jon.services.a11y.AcService;
import z.bdb;

public class UscActivity extends AppCompatActivity {
    public interface a {
    }

    public static a a = null;
    private static final String a = "UscActivity";
    private static long[] a;
    private int a;
    private static final String b;
    private static final String c;
    private static final String d;
    private boolean d;
    private String e;

    static {
        long[] arr_v = new long[104];
        UscActivity.a = arr_v;
        arr_v[0] = 0x293C0FE5L;
        arr_v[1] = 0x705F13A4L;
        arr_v[2] = 1698018924L;
        arr_v[3] = 1009355550L;
        arr_v[4] = 1019828438L;
        arr_v[5] = 458078152L;
        arr_v[6] = 0x953DD7CL;
        arr_v[7] = 0x581070F6L;
        arr_v[8] = -2098566508L;
        arr_v[9] = 0x200D1047L;
        arr_v[10] = -718320076L;
        arr_v[11] = 0x464B99AEL;
        arr_v[12] = 0xFFFFFFFFCB681F02L;
        arr_v[13] = 0xCF3023EL;
        arr_v[14] = 0xFFFFFFFFE616C024L;
        arr_v[15] = 1401558551L;
        arr_v[16] = 0x1CE7062DL;
        arr_v[17] = 0x4590C2E6L;
        arr_v[18] = 0xFFFFFFFFA6A2D3FEL;
        arr_v[19] = 0x5463C427L;
        arr_v[20] = 909544153L;
        arr_v[21] = 0x594B7624L;
        arr_v[22] = -977819800L;
        arr_v[23] = 0x16D0893FL;
        arr_v[24] = 833207649L;
        arr_v[25] = 0x223CA2A2L;
        arr_v[26] = -1697102407L;
        arr_v[27] = 663935081L;
        arr_v[28] = 0x446FCCD4L;
        arr_v[29] = -2078995240L;
        arr_v[30] = 1593109307L;
        arr_v[0x1F] = 0x7E484A45L;
        arr_v[0x20] = 0x7F917C73L;
        arr_v[33] = 0x1CC0D580L;
        arr_v[34] = 121874018L;
        arr_v[35] = 0x3FFD261CL;
        arr_v[36] = 0x5888ACBAL;
        arr_v[37] = 0x14ADA724L;
        arr_v[38] = 0x5794F0A4L;
        arr_v[39] = 0x15CBE076L;
        arr_v[40] = 1170446414L;
        arr_v[41] = 0x8374273L;
        arr_v[42] = 0x1F465DD3L;
        arr_v[43] = 0x1D5FAB20L;
        arr_v[44] = 205077704L;
        arr_v[45] = 0x348CE17AL;
        arr_v[46] = 0x674079E2L;
        arr_v[0x2F] = 0x4416661L;
        arr_v[0x30] = 0xDD20774L;
        arr_v[49] = 0x74096433L;
        arr_v[50] = 0x136E025CL;
        arr_v[51] = 0x16C841C2L;
        arr_v[52] = 0x433F821AL;
        arr_v[53] = 1646407704L;
        arr_v[54] = 112604449L;
        arr_v[55] = 0x1C7BA2E9L;
        arr_v[56] = 0x188F5BE2L;
        arr_v[57] = 0x3B20F260L;
        arr_v[58] = 0x334F264CL;
        arr_v[59] = 0x13C9DFBFL;
        arr_v[60] = 636600651L;
        arr_v[61] = 0x7259C567L;
        arr_v[62] = 0x59619DA1L;
        arr_v[0x3F] = 0x4E4FA50FL;
        arr_v[0x40] = 0xFFFFFFFFAC8F5A16L;
        arr_v[65] = 0x15F43F96L;
        arr_v[66] = 0xFFFFFFFFD8394F91L;
        arr_v[67] = 128308442L;
        arr_v[68] = -911553390L;
        arr_v[69] = 0xF5214DL;
        arr_v[70] = 0xFFFFFFFFD1402C0BL;
        arr_v[71] = -1189604720L;
        arr_v[72] = 0x13523ACFL;
        arr_v[73] = 0x741F7288L;
        arr_v[74] = 0x358CE40BL;
        arr_v[75] = 0x450316CDL;
        arr_v[76] = 350102028L;
        arr_v[77] = 0x479F08E1L;
        arr_v[78] = 777001470L;
        arr_v[0x4F] = 125704093L;
        arr_v[80] = 924084147L;
        arr_v[81] = 1734048225L;
        arr_v[82] = 0x6669AEFL;
        arr_v[83] = 0x1AEC93DFL;
        arr_v[84] = 0xFFFFFFFFD6618E56L;
        arr_v[85] = 0xFFFFFFFFB68E4162L;
        arr_v[86] = 0xFFFFFFFFFB23020FL;
        arr_v[87] = 0xFFFFFFFFEA3BEA4FL;
        arr_v[88] = 1311050871L;
        arr_v[89] = 1925434091L;
        arr_v[90] = 0xFFFFFFFFB06E68A5L;
        arr_v[91] = 0xFFFFFFFFD6107544L;
        arr_v[92] = 0xFFFFFFFFA5B8783BL;
        arr_v[93] = 0xFFFFFFFFBC0F742EL;
        arr_v[94] = 0x168B2790L;
        arr_v[0x5F] = -694545022L;
        arr_v[0x60] = -1823152110L;
        arr_v[97] = 0x1E7BED88L;
        arr_v[98] = 0x2A90F20L;
        arr_v[99] = 0x252F0C1L;
        arr_v[100] = 0x3D04A548L;
        arr_v[101] = 0x1595873FL;
        arr_v[102] = 1505415629L;
        arr_v[103] = 1580003479L;
        long[] arr_v1 = UscActivity.a;
        byte[] arr_b = new byte[((int)arr_v1[6]) ^ 156491100];
        arr_b[((int)arr_v1[0]) ^ 0x293C0FE5] = ((int)arr_v1[7]) ^ 0x581070C9;
        arr_b[((int)arr_v1[5]) ^ 458078153] = ((int)arr_v1[8]) ^ 2098566513;
        arr_b[((int)arr_v1[9]) ^ 0x200D1045] = ((int)arr_v1[10]) ^ 718320087;
        arr_b[((int)arr_v1[11]) ^ 0x464B99AD] = ((int)arr_v1[12]) ^ 0x3497E0BB;
        arr_b[4] = ((int)arr_v1[13]) ^ 0xCF30209;
        arr_b[5] = ((int)arr_v1[14]) ^ 0x19E93FDE;
        arr_b[((int)arr_v1[15]) ^ 1401558545] = ((int)arr_v1[16]) ^ 0x1CE70658;
        arr_b[((int)arr_v1[17]) ^ 0x4590C2E1] = ((int)arr_v1[18]) ^ 0x595D2C5C;
        arr_b[0x5463C42F ^ ((int)arr_v1[19])] = ((int)arr_v1[20]) ^ 909544130;
        arr_b[((int)arr_v1[21]) ^ 0x594B762D] = ((int)arr_v1[22]) ^ 0x3A4858EB;
        arr_b[((int)arr_v1[23]) ^ 0x16D08935] = ((int)arr_v1[24]) ^ 833207588;
        arr_b[((int)arr_v1[25]) ^ 0x223CA2A9] = ((int)arr_v1[26]) ^ 1697102400;
        arr_b[((int)arr_v1[27]) ^ 663935077] = ((int)arr_v1[16]) ^ 0x1CE70658;
        arr_b[((int)arr_v1[28]) ^ 0x446FCCD9] = ((int)arr_v1[29]) ^ 2078995303;
        arr_b[((int)arr_v1[30]) ^ 1593109301] = ((int)arr_v1[0x1F]) ^ 0x7E484A5C;
        arr_b[((int)arr_v1[0x20]) ^ 0x7F917C7C] = ((int)arr_v1[33]) ^ 0x1CC0D5DF;
        arr_b[((int)arr_v1[34]) ^ 121874034] = ((int)arr_v1[35]) ^ 0x3FFD266F;
        arr_b[((int)arr_v1[36]) ^ 1485352107] = ((int)arr_v1[37]) ^ 0x14ADA71D;
        arr_b[((int)arr_v1[38]) ^ 0x5794F0B6] = ((int)arr_v1[39]) ^ 0x15CBE031;
        arr_b[((int)arr_v1[40]) ^ 1170446429] = ((int)arr_v1[41]) ^ 0x8374218;
        arr_b[((int)arr_v1[42]) ^ 0x1F465DC7] = ((int)arr_v1[43]) ^ 0x1D5FAB11;
        arr_b[((int)arr_v1[44]) ^ 205077725] = ((int)arr_v1[45]) ^ 881647907;
        arr_b[((int)arr_v1[46]) ^ 0x674079F4] = ((int)arr_v1[0x2F]) ^ 0x4416620;
        arr_b[((int)arr_v1[0x30]) ^ 0xDD20763] = ((int)arr_v1[49]) ^ 0x7409647C;
        arr_b[((int)arr_v1[50]) ^ 0x136E0244] = ((int)arr_v1[51]) ^ 0x16C841F1;
        arr_b[((int)arr_v1[0x1F]) ^ 0x7E484A5C] = ((int)arr_v1[52]) ^ 0x433F824D;
        arr_b[((int)arr_v1[53]) ^ 0x62223002] = ((int)arr_v1[41]) ^ 0x8374218;
        arr_b[((int)arr_v1[20]) ^ 909544130] = ((int)arr_v1[37]) ^ 0x14ADA71D;
        arr_b[((int)arr_v1[54]) ^ 112604477] = ((int)arr_v1[55]) ^ 477864601;
        arr_b[((int)arr_v1[56]) ^ 0x188F5BFF] = ((int)arr_v1[57]) ^ 0x3B20F204;
        arr_b[((int)arr_v1[58]) ^ 860825170] = ((int)arr_v1[59]) ^ 0x13C9DFD6;
        arr_b[((int)arr_v1[60]) ^ 636600660] = ((int)arr_v1[61]) ^ 0x7259C535;
        UscActivity.b = "record_screen";
        long[] arr_v2 = UscActivity.a;
        byte[] arr_b1 = new byte[((int)arr_v2[6]) ^ 156491100];
        arr_b1[((int)arr_v2[0]) ^ 0x293C0FE5] = ((int)arr_v2[62]) ^ 0x59619DEA;
        arr_b1[((int)arr_v2[5]) ^ 458078153] = 4;
        arr_b1[((int)arr_v2[9]) ^ 0x200D1045] = ((int)arr_v2[0x3F]) ^ 0x4E4FA54C;
        arr_b1[((int)arr_v2[11]) ^ 0x464B99AD] = ((int)arr_v2[0x40]) ^ 0x5370A5BF;
        arr_b1[4] = ((int)arr_v2[60]) ^ 636600660;
        arr_b1[5] = ((int)arr_v2[65]) ^ 0x15F43FBB;
        arr_b1[((int)arr_v2[15]) ^ 1401558545] = ((int)arr_v2[66]) ^ 0x27C6B009;
        arr_b1[((int)arr_v2[17]) ^ 0x4590C2E1] = ((int)arr_v2[54]) ^ 112604477;
        arr_b1[((int)arr_v2[19]) ^ 0x5463C42F] = ((int)arr_v2[67]) ^ 128308458;
        arr_b1[((int)arr_v2[21]) ^ 0x594B762D] = ((int)arr_v2[0x3F]) ^ 0x4E4FA54C;
        arr_b1[((int)arr_v2[23]) ^ 0x16D08935] = ((int)arr_v2[68]) ^ 0x36553303;
        arr_b1[((int)arr_v2[25]) ^ 0x223CA2A9] = ((int)arr_v2[69]) ^ 16064870;
        arr_b1[((int)arr_v2[27]) ^ 663935077] = ((int)arr_v2[70]) ^ 0x2EBFD39D;
        arr_b1[((int)arr_v2[28]) ^ 0x446FCCD9] = ((int)arr_v2[71]) ^ 1189604716;
        arr_b1[((int)arr_v2[30]) ^ 1593109301] = ((int)arr_v2[72]) ^ 324156043;
        arr_b1[((int)arr_v2[0x20]) ^ 0x7F917C7C] = ((int)arr_v2[73]) ^ 0x741F72FE;
        arr_b1[((int)arr_v2[34]) ^ 121874034] = ((int)arr_v2[74]) ^ 0x358CE467;
        arr_b1[((int)arr_v2[36]) ^ 1485352107] = ((int)arr_v2[75]) ^ 0x450316F9;
        arr_b1[((int)arr_v2[38]) ^ 0x5794F0B6] = ((int)arr_v2[76]) ^ 350102118;
        arr_b1[((int)arr_v2[40]) ^ 1170446429] = ((int)arr_v2[77]) ^ 0x479F08B5;
        arr_b1[((int)arr_v2[42]) ^ 0x1F465DC7] = ((int)arr_v2[45]) ^ 881647907;
        arr_b1[((int)arr_v2[44]) ^ 205077725] = ((int)arr_v2[78]) ^ 777001348;
        arr_b1[((int)arr_v2[46]) ^ 0x674079F4] = ((int)arr_v2[0x4F]) ^ 125704172;
        arr_b1[((int)arr_v2[0x30]) ^ 0xDD20763] = ((int)arr_v2[80]) ^ 924084195;
        arr_b1[((int)arr_v2[50]) ^ 0x136E0244] = ((int)arr_v2[78]) ^ 777001348;
        arr_b1[((int)arr_v2[0x1F]) ^ 0x7E484A5C] = ((int)arr_v2[61]) ^ 0x7259C535;
        arr_b1[((int)arr_v2[53]) ^ 0x62223002] = ((int)arr_v2[81]) ^ 1734048187;
        arr_b1[((int)arr_v2[20]) ^ 909544130] = ((int)arr_v2[82]) ^ 107387544;
        arr_b1[((int)arr_v2[54]) ^ 112604477] = ((int)arr_v2[83]) ^ 0x1AEC93B0;
        arr_b1[((int)arr_v2[56]) ^ 0x188F5BFF] = ((int)arr_v2[16]) ^ 0x1CE70658;
        arr_b1[((int)arr_v2[58]) ^ 860825170] = ((int)arr_v2[77]) ^ 0x479F08B5;
        arr_b1[((int)arr_v2[60]) ^ 636600660] = ((int)arr_v2[37]) ^ 0x14ADA71D;
        UscActivity.c = "fn";
        long[] arr_v3 = UscActivity.a;
        byte[] arr_b2 = new byte[((int)arr_v3[6]) ^ 156491100];
        arr_b2[((int)arr_v3[0]) ^ 0x293C0FE5] = ((int)arr_v3[84]) ^ 698249670;
        arr_b2[((int)arr_v3[5]) ^ 458078153] = ((int)arr_v3[85]) ^ 0x4971BEF1;
        arr_b2[((int)arr_v3[9]) ^ 0x200D1045] = ((int)arr_v3[86]) ^ 0x4DCFD96;
        arr_b2[((int)arr_v3[11]) ^ 0x464B99AD] = ((int)arr_v3[59]) ^ 0x13C9DFD6;
        arr_b2[4] = ((int)arr_v3[87]) ^ 0x15C415C4;
        arr_b2[5] = ((int)arr_v3[88]) ^ 1311050788;
        arr_b2[((int)arr_v3[15]) ^ 1401558545] = ((int)arr_v3[89]) ^ 1925434037;
        arr_b2[((int)arr_v3[17]) ^ 0x4590C2E1] = ((int)arr_v3[68]) ^ 0x36553303;
        arr_b2[((int)arr_v3[19]) ^ 0x5463C42F] = ((int)arr_v3[50]) ^ 0x136E0244;
        arr_b2[((int)arr_v3[21]) ^ 0x594B762D] = ((int)arr_v3[90]) ^ 0x4F91972D;
        arr_b2[((int)arr_v3[23]) ^ 0x16D08935] = ((int)arr_v3[91]) ^ 703564510;
        arr_b2[((int)arr_v3[25]) ^ 0x223CA2A9] = ((int)arr_v3[92]) ^ 0x5A4787E0;
        arr_b2[((int)arr_v3[27]) ^ 663935077] = ((int)arr_v3[93]) ^ 0x43F08BDC;
        arr_b2[((int)arr_v3[28]) ^ 0x446FCCD9] = ((int)arr_v3[94]) ^ 378218450;
        arr_b2[((int)arr_v3[30]) ^ 1593109301] = ((int)arr_v3[0x5F]) ^ 694544940;
        arr_b2[((int)arr_v3[0x20]) ^ 0x7F917C7C] = ((int)arr_v3[0x60]) ^ 1823152066;
        arr_b2[((int)arr_v3[34]) ^ 121874034] = ((int)arr_v3[97]) ^ 0x1E7BEDBD;
        arr_b2[((int)arr_v3[36]) ^ 1485352107] = ((int)arr_v3[13]) ^ 0xCF30209;
        arr_b2[((int)arr_v3[38]) ^ 0x5794F0B6] = ((int)arr_v3[81]) ^ 1734048187;
        arr_b2[((int)arr_v3[40]) ^ 1170446429] = ((int)arr_v3[98]) ^ 0x2A90F66;
        arr_b2[((int)arr_v3[42]) ^ 0x1F465DC7] = ((int)arr_v3[99]) ^ 38990005;
        arr_b2[((int)arr_v3[44]) ^ 205077725] = ((int)arr_v3[100]) ^ 1023714609;
        arr_b2[((int)arr_v3[46]) ^ 0x674079F4] = ((int)arr_v3[73]) ^ 0x741F72FE;
        arr_b2[((int)arr_v3[0x30]) ^ 0xDD20763] = ((int)arr_v3[100]) ^ 1023714609;
        arr_b2[((int)arr_v3[50]) ^ 0x136E0244] = ((int)arr_v3[101]) ^ 362121066;
        arr_b2[((int)arr_v3[0x1F]) ^ 0x7E484A5C] = ((int)arr_v3[13]) ^ 0xCF30209;
        arr_b2[((int)arr_v3[53]) ^ 0x62223002] = ((int)arr_v3[98]) ^ 0x2A90F66;
        arr_b2[((int)arr_v3[20]) ^ 909544130] = ((int)arr_v3[72]) ^ 324156043;
        arr_b2[((int)arr_v3[54]) ^ 112604477] = ((int)arr_v3[102]) ^ 1505415557;
        arr_b2[((int)arr_v3[56]) ^ 0x188F5BFF] = ((int)arr_v3[80]) ^ 924084195;
        arr_b2[((int)arr_v3[58]) ^ 860825170] = ((int)arr_v3[103]) ^ 1580003550;
        arr_b2[((int)arr_v3[60]) ^ 636600660] = ((int)arr_v3[16]) ^ 0x1CE70658;
        UscActivity.d = "timeout";
        UscActivity.a = null;
    }

    public UscActivity() {
        this.d = ((int)UscActivity.a[0]) ^ 0x293C0FE5;
        this.e = "";
    }

    public static void a(Context context0, boolean z, String s, int v) {
        Intent intent0 = new Intent(context0.getApplicationContext(), UscActivity.class);
        intent0.addFlags(((int)UscActivity.a[1]) ^ 0x605F13A4);
        intent0.addFlags(((int)UscActivity.a[2]) ^ 0x6534B66C);
        intent0.addFlags(((int)UscActivity.a[3]) ^ 0x38298B1E);
        intent0.addFlags(((int)UscActivity.a[4]) ^ 1011439830);
        intent0.putExtra("record_screen", z);
        intent0.putExtra("fn", s);
        intent0.putExtra("timeout", v);
        context0.startActivity(intent0);
    }

    @Override  // androidx.fragment.app.FragmentActivity
    public void onActivityResult(int v, int v1, Intent intent0) {
        if(v == (((int)UscActivity.a[5]) ^ 458078153)) {
            if(v1 == -1) {
                bdb.a = AcService.a.a.a();
                bdb.a = -1;
                bdb.a = intent0;
            }
            this.finish();
            return;
        }
        super.onActivityResult(v, v1, intent0);
    }

    @Override  // androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.d = this.getIntent().getBooleanExtra("record_screen", ((boolean)(((int)UscActivity.a[0]) ^ 0x293C0FE5)));
        this.e = this.getIntent().getStringExtra("fn");
        this.a = this.getIntent().getIntExtra("timeout", 10000);
        Intent intent0 = AcService.a.a.a().createScreenCaptureIntent();
        intent0.addFlags(((int)UscActivity.a[2]) ^ 0x6534B66C);
        this.startActivityForResult(intent0, ((int)UscActivity.a[5]) ^ 458078153);
    }
}

