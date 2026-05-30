package com.mistral.jon.dex.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import z.azb;
import z.azf;
import z.ban;
import z.bbe;

public class ScreenStateReceiver extends BroadcastReceiver {
    private static ScreenStateReceiver a;
    private static final String a;
    private static long[] a;

    static {
        long[] arr_v = new long[82];
        ScreenStateReceiver.a = arr_v;
        arr_v[0] = 0x44A835ABL;
        arr_v[1] = 0x54ABCF70L;
        arr_v[2] = 304695096L;
        arr_v[3] = 1860950814L;
        arr_v[4] = 0xFFFFFFFFDB777A25L;
        arr_v[5] = 1816620020L;
        arr_v[6] = -1686890730L;
        arr_v[7] = 0x581CEFEEL;
        arr_v[8] = 0x2F9637AEL;
        arr_v[9] = 652990066L;
        arr_v[10] = -1020052800L;
        arr_v[11] = 0x193264EFL;
        arr_v[12] = 0xFFFFFFFF95739E81L;
        arr_v[13] = 0x1A1BE27L;
        arr_v[14] = 0xFFFFFFFFA2AF3B00L;
        arr_v[15] = 1572320102L;
        arr_v[16] = 835160807L;
        arr_v[17] = 1305760281L;
        arr_v[18] = 0x4F416295L;
        arr_v[19] = 0x7579F5FAL;
        arr_v[20] = 410333641L;
        arr_v[21] = 0xFFFFFFFFBD0F5030L;
        arr_v[22] = 0x1E012E28L;
        arr_v[23] = 0x4C16AF30L;
        arr_v[24] = -71037009L;
        arr_v[25] = 1014903408L;
        arr_v[26] = 843134510L;
        arr_v[27] = 0x42C083CBL;
        arr_v[28] = -115635098L;
        arr_v[29] = 0x27CC4FF0L;
        arr_v[30] = -2145067570L;
        arr_v[0x1F] = 0x11F2E79BL;
        arr_v[0x20] = 362097681L;
        arr_v[33] = 0x142BD68EL;
        arr_v[34] = 0x6BC667F0L;
        arr_v[35] = 0xFFFFFFFFCBE41F21L;
        arr_v[36] = 1312170507L;
        arr_v[37] = 0x58F47F8EL;
        arr_v[38] = 1849402536L;
        arr_v[39] = 1750279527L;
        arr_v[40] = 1966085350L;
        arr_v[41] = 712303920L;
        arr_v[42] = 0x7EFFC581L;
        arr_v[43] = 0xFFFFFFFFB8D81193L;
        arr_v[44] = 0xFFFFFFFF986E6720L;
        arr_v[45] = 0x497A0F52L;
        arr_v[46] = 0x178147B5L;
        arr_v[0x2F] = 0x57830C5BL;
        arr_v[0x30] = 1901497228L;
        arr_v[49] = 0x117CEC4AL;
        arr_v[50] = 0x3A8C7D00L;
        arr_v[51] = -1555093075L;
        arr_v[52] = 0x50B9E346L;
        arr_v[53] = 1389830530L;
        arr_v[54] = 0x38805A4AL;
        arr_v[55] = 0x48FEB9EDL;
        arr_v[56] = 0x4BBF60C5L;
        arr_v[57] = 2110114454L;
        arr_v[58] = 0x37BC0026L;
        arr_v[59] = 0x4C124E6FL;
        arr_v[60] = 0x74227287L;
        arr_v[61] = 0x3797D8F8L;
        arr_v[62] = 0x1415BFE6L;
        arr_v[0x3F] = 0x4D5BEAB4L;
        arr_v[0x40] = 0x39311825L;
        arr_v[65] = 0x664EDF9AL;
        arr_v[66] = 0x82F0EF7L;
        arr_v[67] = 0x5FE4E1B8L;
        arr_v[68] = 0x626D39B6L;
        arr_v[69] = 0x63544A8CL;
        arr_v[70] = 1829072360L;
        arr_v[71] = 895068457L;
        arr_v[72] = 0x46BC9F15L;
        arr_v[73] = 0x40D20261L;
        arr_v[74] = 2111237240L;
        arr_v[75] = 78791800L;
        arr_v[76] = 0xA1731E8L;
        arr_v[77] = 1977400421L;
        arr_v[78] = 0x10F196DCL;
        arr_v[0x4F] = 2036302774L;
        arr_v[80] = 950330539L;
        arr_v[81] = 0x450F429AL;
        byte[] arr_b = new byte[((int)arr_v[1]) ^ 0x54ABCF40];
        arr_b[((int)arr_v[0]) ^ 0x44A835AB] = ((int)arr_v[2]) ^ 304695144;
        arr_b[((int)arr_v[3]) ^ 1860950815] = ((int)arr_v[4]) ^ 0x248885C4;
        arr_b[((int)arr_v[5]) ^ 1816620022] = ((int)arr_v[6]) ^ 1686890724;
        arr_b[((int)arr_v[7]) ^ 0x581CEFED] = ((int)arr_v[8]) ^ 0x2F9637B7;
        arr_b[4] = ((int)arr_v[9]) ^ 652990013;
        arr_b[5] = ((int)arr_v[10]) ^ 1020052790;
        arr_b[((int)arr_v[11]) ^ 422733033] = ((int)arr_v[12]) ^ 0x6A8C616E;
        arr_b[((int)arr_v[13]) ^ 0x1A1BE20] = ((int)arr_v[14]) ^ 0x5D50C4E8;
        arr_b[((int)arr_v[15]) ^ 1572320110] = ((int)arr_v[16]) ^ 835160710;
        arr_b[((int)arr_v[17]) ^ 1305760272] = ((int)arr_v[2]) ^ 304695144;
        arr_b[((int)arr_v[18]) ^ 0x4F41629F] = ((int)arr_v[17]) ^ 1305760272;
        arr_b[((int)arr_v[19]) ^ 0x7579F5F1] = ((int)arr_v[4]) ^ 0x248885C4;
        arr_b[((int)arr_v[20]) ^ 410333637] = ((int)arr_v[21]) ^ 0x42F0AFCE;
        arr_b[((int)arr_v[22]) ^ 0x1E012E25] = ((int)arr_v[12]) ^ 0x6A8C616E;
        arr_b[((int)arr_v[23]) ^ 1276555070] = ((int)arr_v[24]) ^ 0x43BF03B;
        arr_b[((int)arr_v[25]) ^ 1014903423] = ((int)arr_v[26]) ^ 0x32413611;
        arr_b[((int)arr_v[27]) ^ 0x42C083DB] = ((int)arr_v[28]) ^ 0x6E473F4;
        arr_b[((int)arr_v[29]) ^ 0x27CC4FE1] = ((int)arr_v[30]) ^ 0x7FDB2215;
        arr_b[((int)arr_v[0x1F]) ^ 0x11F2E789] = ((int)arr_v[0x20]) ^ 362097750;
        arr_b[((int)arr_v[33]) ^ 338417309] = ((int)arr_v[18]) ^ 0x4F41629F;
        arr_b[((int)arr_v[34]) ^ 1808164836] = ((int)arr_v[35]) ^ 0x341BE0E8;
        arr_b[((int)arr_v[36]) ^ 1312170526] = ((int)arr_v[37]) ^ 0x58F47FB2;
        arr_b[((int)arr_v[38]) ^ 1849402558] = ((int)arr_v[39]) ^ 1750279482;
        arr_b[((int)arr_v[40]) ^ 0x753014F1] = ((int)arr_v[41]) ^ 712303889;
        arr_b[((int)arr_v[42]) ^ 0x7EFFC599] = ((int)arr_v[43]) ^ 0x4727EE40;
        arr_b[((int)arr_v[8]) ^ 0x2F9637B7] = ((int)arr_v[44]) ^ 1737595042;
        arr_b[((int)arr_v[45]) ^ 0x497A0F48] = ((int)arr_v[46]) ^ 0x178147EE;
        arr_b[((int)arr_v[0x2F]) ^ 0x57830C40] = ((int)arr_v[0x30]) ^ 0x71568BFF;
        arr_b[((int)arr_v[49]) ^ 0x117CEC56] = ((int)arr_v[14]) ^ 0x5D50C4E8;
        arr_b[((int)arr_v[50]) ^ 0x3A8C7D1D] = ((int)arr_v[51]) ^ 1555093032;
        arr_b[((int)arr_v[52]) ^ 0x50B9E358] = ((int)arr_v[53]) ^ 1389830557;
        arr_b[((int)arr_v[53]) ^ 1389830557] = ((int)arr_v[54]) ^ 0x38805A7D;
        arr_b[((int)arr_v[55]) ^ 0x48FEB9CD] = ((int)arr_v[56]) ^ 0x4BBF608D;
        arr_b[((int)arr_v[41]) ^ 712303889] = ((int)arr_v[57]) ^ 0x7DC5CAF5;
        arr_b[((int)arr_v[58]) ^ 0x37BC0004] = ((int)arr_v[9]) ^ 652990013;
        arr_b[((int)arr_v[59]) ^ 1276268108] = ((int)arr_v[60]) ^ 0x742272D3;
        arr_b[((int)arr_v[61]) ^ 0x3797D8DC] = ((int)arr_v[62]) ^ 0x1415BF90;
        arr_b[((int)arr_v[0x3F]) ^ 0x4D5BEA91] = ((int)arr_v[2]) ^ 304695144;
        arr_b[((int)arr_v[0x40]) ^ 0x39311803] = ((int)arr_v[65]) ^ 0x664EDFCC;
        arr_b[((int)arr_v[66]) ^ 0x82F0ED0] = ((int)arr_v[67]) ^ 0x5FE4E1F6;
        arr_b[((int)arr_v[68]) ^ 0x626D399E] = ((int)arr_v[67]) ^ 0x5FE4E1F6;
        arr_b[((int)arr_v[69]) ^ 0x63544AA5] = ((int)arr_v[70]) ^ 0x6D056DD1;
        arr_b[((int)arr_v[71]) ^ 0x3559A903] = ((int)arr_v[72]) ^ 0x46BC9F61;
        arr_b[((int)arr_v[73]) ^ 1087504970] = ((int)arr_v[74]) ^ 0x7DD6EC29;
        arr_b[((int)arr_v[75]) ^ 0x4B24454] = ((int)arr_v[76]) ^ 0xA173181;
        arr_b[((int)arr_v[77]) ^ 1977400392] = ((int)arr_v[78]) ^ 0x10F196BA;
        arr_b[((int)arr_v[0x4F]) ^ 2036302744] = ((int)arr_v[16]) ^ 835160710;
        arr_b[((int)arr_v[80]) ^ 950330500] = ((int)arr_v[81]) ^ 0x450F42AF;
        ScreenStateReceiver.a = "ScreenStateReceiver";
        ScreenStateReceiver.a = null;
    }

    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context0, Intent intent0) {
        String s = intent0.getAction();
        ban.a("ScreenStateReceiver", s);
        if("android.intent.action.SCREEN_OFF".equals(s)) {
            if(bbe.a().a(((boolean)(((int)ScreenStateReceiver.a[0]) ^ 0x44A835AB)))) {
                azf.b();
            }
            else {
                azf.a();
            }
            azb.a(context0);
        }
    }
}

