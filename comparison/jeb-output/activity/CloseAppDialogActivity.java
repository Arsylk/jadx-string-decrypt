package com.mistral.jon.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.mistral.jon.services.a11y.AcService;
import z.ayl.a;

public class CloseAppDialogActivity extends Activity {
    private static long[] a;

    static {
        long[] arr_v = new long[90];
        CloseAppDialogActivity.a = arr_v;
        arr_v[0] = 1557025460L;
        arr_v[1] = 860693603L;
        arr_v[2] = 0x1812F475L;
        arr_v[3] = 0x141AEEF3L;
        arr_v[4] = 1746243500L;
        arr_v[5] = 0x56BE65D1L;
        arr_v[6] = 0xFFFFFFFF9C9C7708L;
        arr_v[7] = 0x58953DF4L;
        arr_v[8] = 596801835L;
        arr_v[9] = 0xFFFFFFFFD73C66FDL;
        arr_v[10] = 0x1970F130L;
        arr_v[11] = 0x4961AEFBL;
        arr_v[12] = -1926510574L;
        arr_v[13] = 0x2F83E2F8L;
        arr_v[14] = 0x14EDE0E2L;
        arr_v[15] = 1706468381L;
        arr_v[16] = 0x40330182L;
        arr_v[17] = 0x6AE2BB0L;
        arr_v[18] = 1305302345L;
        arr_v[19] = 0xFFFFFFFFFCE00BBAL;
        arr_v[20] = 0x2E53A37FL;
        arr_v[21] = 0x256C7694L;
        arr_v[22] = 0x6ED80DD1L;
        arr_v[23] = -673760327L;
        arr_v[24] = 0x29870B11L;
        arr_v[25] = 683529042L;
        arr_v[26] = 2114200164L;
        arr_v[27] = 0x3CAF688FL;
        arr_v[28] = 1515509086L;
        arr_v[29] = 0xFFFFFFFFCFAA25F1L;
        arr_v[30] = 0x199A8A16L;
        arr_v[0x1F] = 0xFFFFFFFFD9F450DDL;
        arr_v[0x20] = 0x17E902E3L;
        arr_v[33] = 0x28F420B4L;
        arr_v[34] = 0x55048617L;
        arr_v[35] = 0x553F9E67L;
        arr_v[36] = 0x113D1569L;
        arr_v[37] = 0x193C4A7L;
        arr_v[38] = 0x668800DAL;
        arr_v[39] = 0x34A15044L;
        arr_v[40] = 0x69D6F5EAL;
        arr_v[41] = 0x407AC52BL;
        arr_v[42] = 0x63591519L;
        arr_v[43] = 40713027L;
        arr_v[44] = 0x36FC287FL;
        arr_v[45] = 330467165L;
        arr_v[46] = 0x22580D89L;
        arr_v[0x2F] = 0x11AC87BL;
        arr_v[0x30] = 39709564L;
        arr_v[49] = 560889155L;
        arr_v[50] = 0x62B650C3L;
        arr_v[51] = 0x9C1C8CEL;
        arr_v[52] = 0x57AB0BD3L;
        arr_v[53] = 0x75FB8C16L;
        arr_v[54] = 0x6FB1C6BEL;
        arr_v[55] = 0x2EFDF142L;
        arr_v[56] = 0x6D3E463EL;
        arr_v[57] = 0x1DA267F5L;
        arr_v[58] = 0x4CBD725AL;
        arr_v[59] = 0x412E274FL;
        arr_v[60] = 0x4E613F45L;
        arr_v[61] = 1008583143L;
        arr_v[62] = 1860634904L;
        arr_v[0x3F] = 0x80007C5L;
        arr_v[0x40] = -333590878L;
        arr_v[65] = 0xA00C287L;
        arr_v[66] = -204767009L;
        arr_v[67] = 200585658L;
        arr_v[68] = 0x2D3690F2L;
        arr_v[69] = -1737747085L;
        arr_v[70] = 0xFFFFFFFFF2CE6A2CL;
        arr_v[71] = 2010284289L;
        arr_v[72] = 0x385F0EADL;
        arr_v[73] = 0xFFFFFFFFC231F005L;
        arr_v[74] = 1460970981L;
        arr_v[75] = 0xFFFFFFFFD107FE30L;
        arr_v[76] = 0x76537209L;
        arr_v[77] = 0x276ED6BFL;
        arr_v[78] = 0x6EE2ED9FL;
        arr_v[0x4F] = 0xFF0458L;
        arr_v[80] = 0x79DAF4A8L;
        arr_v[81] = 0x50B3A1BBL;
        arr_v[82] = 0x4AEFE15AL;
        arr_v[83] = 0x1A43603AL;
        arr_v[84] = 0x3079DAD8L;
        arr_v[85] = 0x2DF186BFL;
        arr_v[86] = 0x517FDE6L;
        arr_v[87] = 0x7E8F86A2L;
        arr_v[88] = 0x28C22FB2L;
        arr_v[89] = 0x2D1B93F8L;
    }

    private void a() {
        int v1;
        int v;
        a ayl$a0 = new a(this).a(this.getString(((int)CloseAppDialogActivity.a[0]) ^ 600003228));
        long[] arr_v = CloseAppDialogActivity.a;
        if(Build.VERSION.SDK_INT >= (((int)arr_v[1]) ^ 860693630)) {
            v = (int)arr_v[2];
            v1 = 0x671FF453;
        }
        else {
            v = (int)arr_v[3];
            v1 = 1796730580;
        }

        a ayl$a1 = ayl$a0.b(this.getString(v ^ v1));
        long[] arr_v1 = CloseAppDialogActivity.a;
        byte[] arr_b = new byte[((int)arr_v1[4]) ^ 0x68158F8C];
        arr_b[((int)arr_v1[5]) ^ 0x56BE65D1] = ((int)arr_v1[6]) ^ 0x63638895;
        arr_b[((int)arr_v1[7]) ^ 0x58953DF5] = -1;
        arr_b[((int)arr_v1[8]) ^ 596801833] = ((int)arr_v1[9]) ^ 683907426;
        arr_b[((int)arr_v1[10]) ^ 0x1970F133] = ((int)arr_v1[11]) ^ 0x4961AE8C;
        arr_b[4] = ((int)arr_v1[12]) ^ 0x72D437AF;
        arr_b[5] = ((int)arr_v1[13]) ^ 0x2F83E29B;
        arr_b[((int)arr_v1[14]) ^ 0x14EDE0E4] = ((int)arr_v1[15]) ^ 1706468435;
        arr_b[((int)arr_v1[16]) ^ 0x40330185] = ((int)arr_v1[17]) ^ 0x6AE2BDF;
        arr_b[((int)arr_v1[18]) ^ 1305302337] = ((int)arr_v1[19]) ^ 0x31FF47A;
        arr_b[((int)arr_v1[20]) ^ 777233270] = ((int)arr_v1[21]) ^ 0x256C76AA;
        arr_b[((int)arr_v1[22]) ^ 0x6ED80DDB] = ((int)arr_v1[23]) ^ 673760332;
        arr_b[((int)arr_v1[24]) ^ 0x29870B1A] = ((int)arr_v1[25]) ^ 683529031;
        arr_b[((int)arr_v1[26]) ^ 2114200168] = ((int)arr_v1[27]) ^ 0x3CAF6880;
        arr_b[((int)arr_v1[28]) ^ 1515509075] = ((int)arr_v1[29]) ^ 0x3055DA28;
        arr_b[((int)arr_v1[30]) ^ 0x199A8A18] = ((int)arr_v1[0x1F]) ^ 0x260BAF1F;
        arr_b[((int)arr_v1[27]) ^ 0x3CAF6880] = ((int)arr_v1[20]) ^ 777233270;
        arr_b[((int)arr_v1[0x20]) ^ 0x17E902F3] = ((int)arr_v1[33]) ^ 0x28F420E5;
        arr_b[((int)arr_v1[34]) ^ 0x55048606] = ((int)arr_v1[35]) ^ 0x553F9E0C;
        arr_b[((int)arr_v1[36]) ^ 0x113D157B] = ((int)arr_v1[37]) ^ 0x193C4D4;
        arr_b[((int)arr_v1[38]) ^ 0x668800C9] = ((int)arr_v1[39]) ^ 0x34A15035;
        arr_b[((int)arr_v1[40]) ^ 0x69D6F5FE] = ((int)arr_v1[41]) ^ 0x407AC541;
        arr_b[((int)arr_v1[25]) ^ 683529031] = ((int)arr_v1[42]) ^ 1666782507;
        arr_b[((int)arr_v1[43]) ^ 40713045] = ((int)arr_v1[44]) ^ 0x36FC2825;
        arr_b[((int)arr_v1[45]) ^ 330467146] = ((int)arr_v1[46]) ^ 0x22580DF1;
        arr_b[((int)arr_v1[0x2F]) ^ 0x11AC863] = ((int)arr_v1[0x30]) ^ 39709515;
        arr_b[((int)arr_v1[49]) ^ 560889178] = ((int)arr_v1[37]) ^ 0x193C4D4;
        arr_b[((int)arr_v1[50]) ^ 0x62B650D9] = ((int)arr_v1[51]) ^ 0x9C1C89A;
        arr_b[((int)arr_v1[52]) ^ 0x57AB0BC8] = ((int)arr_v1[53]) ^ 0x75FB8C40;
        arr_b[((int)arr_v1[54]) ^ 0x6FB1C6A2] = ((int)arr_v1[55]) ^ 0x2EFDF126;
        arr_b[((int)arr_v1[1]) ^ 860693630] = ((int)arr_v1[56]) ^ 0x6D3E464B;
        arr_b[((int)arr_v1[57]) ^ 0x1DA267EB] = ((int)arr_v1[53]) ^ 0x75FB8C40;
        arr_b[((int)arr_v1[58]) ^ 0x4CBD7245] = ((int)arr_v1[39]) ^ 0x34A15035;
        a ayl$a2 = ayl$a1.a(Color.parseColor("#f04130")).a(((int)CloseAppDialogActivity.a[59]) ^ 1076242280, ((boolean)(((int)CloseAppDialogActivity.a[7]) ^ 0x58953DF5))).a(((boolean)(((int)CloseAppDialogActivity.a[7]) ^ 0x58953DF5))).c(this.getString(((int)CloseAppDialogActivity.a[60]) ^ 0x316C3F61)).d(this.getString(((int)CloseAppDialogActivity.a[61]) ^ 0x4310C1C4));
        long[] arr_v2 = CloseAppDialogActivity.a;
        byte[] arr_b1 = new byte[((int)arr_v2[4]) ^ 0x68158F8C];
        arr_b1[((int)arr_v2[5]) ^ 0x56BE65D1] = ((int)arr_v2[62]) ^ 0x6EE70941;
        arr_b1[((int)arr_v2[7]) ^ 0x58953DF5] = ((int)arr_v2[0x3F]) ^ 0x80007E6;
        arr_b1[((int)arr_v2[8]) ^ 596801833] = ((int)arr_v2[0x40]) ^ 333590802;
        arr_b1[((int)arr_v2[10]) ^ 0x1970F133] = ((int)arr_v2[65]) ^ 0xA00C2A8;
        arr_b1[4] = ((int)arr_v2[66]) ^ 0xC347F14;
        arr_b1[5] = ((int)arr_v2[67]) ^ 200585682;
        arr_b1[((int)arr_v2[14]) ^ 0x14EDE0E4] = ((int)arr_v2[68]) ^ 0x2D3690AA;
        arr_b1[((int)arr_v2[16]) ^ 0x40330185] = ((int)arr_v2[69]) ^ 1737747073;
        arr_b1[((int)arr_v2[18]) ^ 1305302337] = ((int)arr_v2[70]) ^ 0xD3195C5;
        arr_b1[((int)arr_v2[20]) ^ 777233270] = ((int)arr_v2[71]) ^ 2010284341;
        arr_b1[((int)arr_v2[22]) ^ 0x6ED80DDB] = ((int)arr_v2[72]) ^ 0x385F0E95;
        arr_b1[((int)arr_v2[24]) ^ 0x29870B1A] = ((int)arr_v2[46]) ^ 0x22580DF1;
        arr_b1[((int)arr_v2[26]) ^ 2114200168] = ((int)arr_v2[73]) ^ 0x3DCE0F80;
        arr_b1[((int)arr_v2[28]) ^ 1515509075] = ((int)arr_v2[74]) ^ 1460970910;
        arr_b1[((int)arr_v2[30]) ^ 0x199A8A18] = ((int)arr_v2[18]) ^ 1305302337;
        arr_b1[0x3CAF6880 ^ ((int)arr_v2[27])] = ((int)arr_v2[75]) ^ 788005304;
        arr_b1[((int)arr_v2[0x20]) ^ 0x17E902F3] = ((int)arr_v2[41]) ^ 0x407AC541;
        arr_b1[((int)arr_v2[34]) ^ 0x55048606] = ((int)arr_v2[76]) ^ 1985180260;
        arr_b1[((int)arr_v2[36]) ^ 0x113D157B] = ((int)arr_v2[77]) ^ 0x276ED6F8;
        arr_b1[((int)arr_v2[38]) ^ 0x668800C9] = ((int)arr_v2[78]) ^ 0x6EE2EDF8;
        arr_b1[((int)arr_v2[40]) ^ 0x69D6F5FE] = ((int)arr_v2[0x4F]) ^ 0xFF040F;
        arr_b1[((int)arr_v2[25]) ^ 683529031] = ((int)arr_v2[33]) ^ 0x28F420E5;
        arr_b1[((int)arr_v2[43]) ^ 40713045] = ((int)arr_v2[80]) ^ 0x79DAF4EC;
        arr_b1[((int)arr_v2[45]) ^ 330467146] = ((int)arr_v2[81]) ^ 0x50B3A1DA;
        arr_b1[((int)arr_v2[0x2F]) ^ 0x11AC863] = ((int)arr_v2[42]) ^ 1666782507;
        arr_b1[((int)arr_v2[49]) ^ 560889178] = ((int)arr_v2[80]) ^ 0x79DAF4EC;
        arr_b1[((int)arr_v2[50]) ^ 0x62B650D9] = ((int)arr_v2[82]) ^ 0x4AEFE12E;
        arr_b1[((int)arr_v2[52]) ^ 0x57AB0BC8] = ((int)arr_v2[83]) ^ 0x1A436003;
        arr_b1[((int)arr_v2[54]) ^ 0x6FB1C6A2] = ((int)arr_v2[84]) ^ 0x3079DA8D;
        arr_b1[((int)arr_v2[1]) ^ 860693630] = ((int)arr_v2[67]) ^ 200585682;
        arr_b1[((int)arr_v2[57]) ^ 0x1DA267EB] = ((int)arr_v2[85]) ^ 770803401;
        arr_b1[((int)arr_v2[58]) ^ 0x4CBD7245] = ((int)arr_v2[86]) ^ 0x517FDB4;
        ayl$a2.b(Color.parseColor("#f04130")).a(() -> {
            new Handler().postDelayed(() -> {
                AcService acService0 = AcService.a;
                long[] arr_v = CloseAppDialogActivity.a;
                int v = ((int)arr_v[88]) ^ 0x57CF2F90;
                Object[] arr_object = new Object[((int)arr_v[7]) ^ 0x58953DF5];
                int v1 = ((int)arr_v[5]) ^ 0x56BE65D1;
                arr_object[v1] = this.getString(((int)arr_v[89]) ^ 0x521693D9);
                Toast.makeText(acService0, this.getString(v, arr_object), ((int)CloseAppDialogActivity.a[5]) ^ 0x56BE65D1).show();
            }, CloseAppDialogActivity.a[87] ^ 0x7E8F8D1AL);
            this.finish();
        }).b(() -> {
            new Handler().postDelayed(() -> {
                AcService acService0 = AcService.a;
                long[] arr_v = CloseAppDialogActivity.a;
                int v = ((int)arr_v[88]) ^ 0x57CF2F90;
                Object[] arr_object = new Object[((int)arr_v[7]) ^ 0x58953DF5];
                int v1 = ((int)arr_v[5]) ^ 0x56BE65D1;
                arr_object[v1] = this.getString(((int)arr_v[89]) ^ 0x521693D9);
                Toast.makeText(acService0, this.getString(v, arr_object), ((int)CloseAppDialogActivity.a[5]) ^ 0x56BE65D1).show();
            }, CloseAppDialogActivity.a[87] ^ 0x7E8F8D1AL);
            this.finish();
        }).a();
    }

    // Detected as a lambda implementation
    private void b() [...]

    // Detected as a lambda implementation
    private void c() [...]

    @Override  // android.app.Activity
    protected void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.a();
    }

    @Override  // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.b();
    }
}

