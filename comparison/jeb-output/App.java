package com.mistral.jon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.os.StrictMode;
import com.mistral.jon.services.OvService;
import z.ayo;
import z.ayp;
import z.ayy;
import z.azb;
import z.azk;
import z.azu;
import z.bcn;
import z.bcq;

public class App extends Application {
    static final class a {
        private static long[] a;

        static {
            long[] arr_v = new long[0x80];
            a.a = arr_v;
            arr_v[0] = 1740198540L;
            arr_v[1] = 0x208589E2L;
            arr_v[2] = 0xFFFFFFFFC48A3370L;
            arr_v[3] = 0x1B5AEEABL;
            arr_v[4] = 0x6D7591F5L;
            arr_v[5] = 1763882066L;
            arr_v[6] = 0xFFFFFFFFD7343530L;
            arr_v[7] = 0x19602761L;
            arr_v[8] = 0xFFFFFFFFE36D6681L;
            arr_v[9] = 0xE060F6AL;
            arr_v[10] = -2101749197L;
            arr_v[11] = 182770830L;
            arr_v[12] = 0xFFFFFFFF9034DDFAL;
            arr_v[13] = 0x40D055BBL;
            arr_v[14] = 0xFFFFFFFFE6BAFFD3L;
            arr_v[15] = 0x756B0B11L;
            arr_v[16] = 0x6BF752AAL;
            arr_v[17] = 0xFFFFFFFFA25E0AB7L;
            arr_v[18] = 0xB9DC066L;
            arr_v[19] = 512270310L;
            arr_v[20] = 0x34B0A7B1L;
            arr_v[21] = -1766035055L;
            arr_v[22] = 1725392093L;
            arr_v[23] = 0xFFFFFFFF90AD4FF6L;
            arr_v[24] = 305045601L;
            arr_v[25] = 0x7E14E43AL;
            arr_v[26] = 0x69FADBADL;
            arr_v[27] = 0xFFFFFFFFBC968006L;
            arr_v[28] = 316191049L;
            arr_v[29] = 0x41F860A7L;
            arr_v[30] = 130235063L;
            arr_v[0x1F] = 1098944209L;
            arr_v[0x20] = 0x6D0138CFL;
            arr_v[33] = -1953102014L;
            arr_v[34] = 0xFFFFFFFFFB6A2465L;
            arr_v[35] = 0xB7787D4L;
            arr_v[36] = 327566660L;
            arr_v[37] = 0x2BD2DD91L;
            arr_v[38] = 0x5AE87FF3L;
            arr_v[39] = 709660023L;
            arr_v[40] = 0xFFFFFFFFBA2FE411L;
            arr_v[41] = 0x7897FFDAL;
            arr_v[42] = 0xEBBB913L;
            arr_v[43] = 0x3BFDF861L;
            arr_v[44] = 0xFFFFFFFFFA0D682EL;
            arr_v[45] = 0x9F8144BL;
            arr_v[46] = 0xFFFFFFFFD24DFFB1L;
            arr_v[0x2F] = 1987660156L;
            arr_v[0x30] = -1169526509L;
            arr_v[49] = 355360453L;
            arr_v[50] = 0xFFFFFFFFF9D00A15L;
            arr_v[51] = 1150722526L;
            arr_v[52] = 0xFFFFFFFFF1E67291L;
            arr_v[53] = 0x51A30D7L;
            arr_v[54] = 0x62F86FB8L;
            arr_v[55] = 0x4C7BC1C9L;
            arr_v[56] = 0x1FB1608AL;
            arr_v[57] = 0x4C7FA406L;
            arr_v[58] = 0xFFFFFFFFAE00E45DL;
            arr_v[59] = 0x5F3723B8L;
            arr_v[60] = 0x59FDFF2CL;
            arr_v[61] = 0x2D74BD3AL;
            arr_v[62] = 0xFFFFFFFFF1297344L;
            arr_v[0x3F] = 0x2F936DB3L;
            arr_v[0x40] = 0xFFFFFFFFE3FE8791L;
            arr_v[65] = 1180089489L;
            arr_v[66] = -2001388665L;
            arr_v[67] = 464328470L;
            arr_v[68] = 0x1996CFEFL;
            arr_v[69] = 0x2560D28L;
            arr_v[70] = 0x1874AEFL;
            arr_v[71] = -760723680L;
            arr_v[72] = 317170835L;
            arr_v[73] = 2112083038L;
            arr_v[74] = 0xFFFFFFFFB947DAC4L;
            arr_v[75] = 780017738L;
            arr_v[76] = 0xFFFFFFFFB54A3F39L;
            arr_v[77] = 0x4B2D2EFL;
            arr_v[78] = 0xFFFFFFFFFA78FC91L;
            arr_v[0x4F] = 0x77FFE8C4L;
            arr_v[80] = -205372721L;
            arr_v[81] = 0x502DDCC3L;
            arr_v[82] = 0x314A2718L;
            arr_v[83] = 79750810L;
            arr_v[84] = 0x635912F8L;
            arr_v[85] = 0x4CE0D741L;
            arr_v[86] = 0x350B0ECL;
            arr_v[87] = 0x534A6C06L;
            arr_v[88] = 0x24590311L;
            arr_v[89] = 670416009L;
            arr_v[90] = 0x239003EAL;
            arr_v[91] = 0x5C23F7F7L;
            arr_v[92] = 0x61F8EFA3L;
            arr_v[93] = 0x2235DFCEL;
            arr_v[94] = 0x4F3032EFL;
            arr_v[0x5F] = 0x31888C8FL;
            arr_v[0x60] = 0x5DDCFD6L;
            arr_v[97] = 1419257502L;
            arr_v[98] = 0x5A316CA0L;
            arr_v[99] = 0x6FF78D2BL;
            arr_v[100] = 0x4C81989BL;
            arr_v[101] = 1307031631L;
            arr_v[102] = 465736800L;
            arr_v[103] = 0x29F38EF2L;
            arr_v[104] = 0x3B448721L;
            arr_v[105] = 0x5E1B1907L;
            arr_v[106] = 453065044L;
            arr_v[107] = 0x31AF432DL;
            arr_v[108] = 0xFFFFFFFFD4349FC4L;
            arr_v[109] = 0xFFFFFFFFAED597DBL;
            arr_v[110] = -1709007373L;
            arr_v[0x6F] = 0xFFFFFFFFEEEFCA02L;
            arr_v[0x70] = 0xFFFFFFFFAE0F2A7BL;
            arr_v[0x71] = 0xFFFFFFFF9ECBFA5DL;
            arr_v[0x72] = 0xFFFFFFFFE620637EL;
            arr_v[0x73] = 0xFFFFFFFF830F5311L;
            arr_v[0x74] = 0xFFFFFFFFE57E9719L;
            arr_v[0x75] = 0x49298AE3L;
            arr_v[0x76] = 0x2CB601DBL;
            arr_v[0x77] = -1871032930L;
            arr_v[120] = 0x43DC3300L;
            arr_v[0x79] = 0x2AB39F64L;
            arr_v[0x7A] = 0x10D4E478L;
            arr_v[0x7B] = 1155782590L;
            arr_v[0x7C] = 0xB882F1CL;
            arr_v[0x7D] = 572118071L;
            arr_v[0x7E] = 1799493080L;
            arr_v[0x7F] = 0x5B6CF520L;
        }

        public a(Context context0) {
            SharedPreferences.Editor sharedPreferences$Editor0 = bcq.a(context0).edit();
            long[] arr_v = a.a;
            byte[] arr_b = new byte[((int)arr_v[0]) ^ 1740198604];
            arr_b[((int)arr_v[1]) ^ 0x208589E2] = ((int)arr_v[2]) ^ 0x3B75CCCA;
            arr_b[((int)arr_v[3]) ^ 458944170] = ((int)arr_v[4]) ^ 0x6D7591A8;
            arr_b[((int)arr_v[5]) ^ 0x6922B450] = ((int)arr_v[6]) ^ 0x28CBCAE2;
            arr_b[((int)arr_v[7]) ^ 0x19602762] = ((int)arr_v[8]) ^ 0x1C929927;
            arr_b[4] = ((int)arr_v[9]) ^ 0xE060F48;
            arr_b[5] = ((int)arr_v[10]) ^ 2101749188;
            arr_b[((int)arr_v[11]) ^ 182770824] = ((int)arr_v[12]) ^ 0x6FCB226E;
            arr_b[((int)arr_v[13]) ^ 0x40D055BC] = ((int)arr_v[14]) ^ 0x19450058;
            arr_b[((int)arr_v[15]) ^ 0x756B0B19] = 5;
            arr_b[((int)arr_v[16]) ^ 0x6BF752A3] = ((int)arr_v[17]) ^ 0x5DA1F504;
            arr_b[((int)arr_v[18]) ^ 0xB9DC06C] = ((int)arr_v[19]) ^ 0x1E889FF4;
            arr_b[((int)arr_v[20]) ^ 0x34B0A7BA] = ((int)arr_v[21]) ^ 1766035007;
            arr_b[((int)arr_v[22]) ^ 1725392081] = ((int)arr_v[23]) ^ 0x6F52B055;
            arr_b[((int)arr_v[24]) ^ 305045612] = ((int)arr_v[25]) ^ 2115298400;
            arr_b[((int)arr_v[26]) ^ 1778047907] = ((int)arr_v[27]) ^ 1130987500;
            arr_b[((int)arr_v[28]) ^ 316191046] = ((int)arr_v[29]) ^ 0x41F860DC;
            arr_b[((int)arr_v[30]) ^ 130235047] = ((int)arr_v[0x1F]) ^ 0x41808E99;
            arr_b[((int)arr_v[0x20]) ^ 0x6D0138DE] = ((int)arr_v[33]) ^ 1953102062;
            arr_b[((int)arr_v[19]) ^ 0x1E889FF4] = ((int)arr_v[34]) ^ 0x495DBCC;
            arr_b[((int)arr_v[35]) ^ 0xB7787C7] = ((int)arr_v[36]) ^ 0x13864520;
            arr_b[((int)arr_v[37]) ^ 0x2BD2DD85] = ((int)arr_v[38]) ^ 0x5AE87FD4;
            arr_b[((int)arr_v[39]) ^ 709660002] = ((int)arr_v[40]) ^ 0x45D01BC6;
            arr_b[((int)arr_v[41]) ^ 0x7897FFCC] = ((int)arr_v[42]) ^ 0xEBBB90C;
            arr_b[((int)arr_v[43]) ^ 0x3BFDF876] = ((int)arr_v[44]) ^ 0x5F297E6;
            arr_b[((int)arr_v[45]) ^ 0x9F81453] = ((int)arr_v[46]) ^ 0x2DB20037;
            arr_b[((int)arr_v[0x2F]) ^ 1987660133] = ((int)arr_v[0x30]) ^ 0x45B58EA5;
            arr_b[((int)arr_v[49]) ^ 0x152E5EDF] = ((int)arr_v[50]) ^ 0x62FF589;
            arr_b[((int)arr_v[51]) ^ 1150722501] = ((int)arr_v[52]) ^ 0xE198D09;
            arr_b[((int)arr_v[53]) ^ 85602507] = ((int)arr_v[54]) ^ 0x62F86FCF;
            arr_b[((int)arr_v[55]) ^ 0x4C7BC1D4] = ((int)arr_v[56]) ^ 0x1FB160ED;
            arr_b[((int)arr_v[57]) ^ 0x4C7FA418] = ((int)arr_v[58]) ^ 0x51FF1BAC;
            arr_b[((int)arr_v[42]) ^ 0xEBBB90C] = ((int)arr_v[7]) ^ 0x19602762;
            arr_b[((int)arr_v[59]) ^ 0x5F372398] = ((int)arr_v[25]) ^ 2115298400;
            arr_b[((int)arr_v[60]) ^ 0x59FDFF0D] = ((int)arr_v[59]) ^ 0x5F372398;
            arr_b[((int)arr_v[9]) ^ 0xE060F48] = ((int)arr_v[40]) ^ 0x45D01BC6;
            arr_b[((int)arr_v[61]) ^ 762625305] = ((int)arr_v[62]) ^ 0xED68CFD;
            arr_b[((int)arr_v[0x3F]) ^ 0x2F936D97] = ((int)arr_v[0x40]) ^ 0x1C017841;
            arr_b[((int)arr_v[65]) ^ 1180089524] = ((int)arr_v[66]) ^ 2001388638;
            arr_b[((int)arr_v[67]) ^ 0x1BAD1730] = ((int)arr_v[14]) ^ 0x19450058;
            arr_b[((int)arr_v[38]) ^ 0x5AE87FD4] = ((int)arr_v[4]) ^ 0x6D7591A8;
            arr_b[((int)arr_v[68]) ^ 0x1996CFC7] = ((int)arr_v[69]) ^ 0x2560D55;
            arr_b[((int)arr_v[70]) ^ 0x1874AC6] = ((int)arr_v[71]) ^ 760723605;
            arr_b[((int)arr_v[72]) ^ 317170873] = ((int)arr_v[30]) ^ 130235047;
            arr_b[((int)arr_v[73]) ^ 2112083061] = ((int)arr_v[74]) ^ 0x46B8254D;
            arr_b[((int)arr_v[75]) ^ 780017766] = ((int)arr_v[76]) ^ 0x4AB5C09D;
            arr_b[((int)arr_v[77]) ^ 0x4B2D2C2] = ((int)arr_v[78]) ^ 0x587030F;
            arr_b[((int)arr_v[0x4F]) ^ 2013260010] = ((int)arr_v[80]) ^ 205372699;
            arr_b[((int)arr_v[81]) ^ 0x502DDCEC] = ((int)arr_v[43]) ^ 0x3BFDF876;
            arr_b[((int)arr_v[82]) ^ 0x314A2728] = ((int)arr_v[83]) ^ 0x4C0E6EC;
            arr_b[((int)arr_v[84]) ^ 0x635912C9] = ((int)arr_v[85]) ^ 0x4CE0D70C;
            arr_b[((int)arr_v[86]) ^ 0x350B0DE] = ((int)arr_v[87]) ^ 0x534A6C30;
            arr_b[((int)arr_v[88]) ^ 0x24590322] = ((int)arr_v[89]) ^ 0x27F5BCB0;
            arr_b[((int)arr_v[90]) ^ 0x239003DE] = ((int)arr_v[91]) ^ 1545861009;
            arr_b[((int)arr_v[92]) ^ 0x61F8EF96] = ((int)arr_v[93]) ^ 0x2235DF87;
            arr_b[((int)arr_v[87]) ^ 0x534A6C30] = ((int)arr_v[94]) ^ 0x4F30329F;
            arr_b[((int)arr_v[0x5F]) ^ 831032504] = ((int)arr_v[0x60]) ^ 0x5DDCFBB;
            arr_b[((int)arr_v[97]) ^ 1419257510] = ((int)arr_v[98]) ^ 0x5A316CE3;
            arr_b[((int)arr_v[89]) ^ 0x27F5BCB0] = ((int)arr_v[99]) ^ 0x6FF78D7B;
            arr_b[((int)arr_v[100]) ^ 0x4C8198A1] = ((int)arr_v[85]) ^ 0x4CE0D70C;
            arr_b[((int)arr_v[101]) ^ 1307031668] = ((int)arr_v[90]) ^ 0x239003DE;
            arr_b[((int)arr_v[102]) ^ 0x1BC2945C] = ((int)arr_v[99]) ^ 0x6FF78D7B;
            arr_b[((int)arr_v[103]) ^ 0x29F38ECF] = ((int)arr_v[104]) ^ 0x3B448743;
            arr_b[((int)arr_v[105]) ^ 0x5E1B1939] = ((int)arr_v[56]) ^ 0x1FB160ED;
            arr_b[((int)arr_v[106]) ^ 453065067] = ((int)arr_v[107]) ^ 0x31AF436B;
            long[] arr_v1 = a.a;
            byte[] arr_b1 = new byte[((int)arr_v1[82]) ^ 0x314A2728];
            arr_b1[((int)arr_v1[1]) ^ 0x208589E2] = ((int)arr_v1[21]) ^ 1766035007;
            arr_b1[((int)arr_v1[3]) ^ 458944170] = ((int)arr_v1[94]) ^ 0x4F30329F;
            arr_b1[((int)arr_v1[5]) ^ 0x6922B450] = ((int)arr_v1[108]) ^ 0x2BCB6018;
            arr_b1[((int)arr_v1[7]) ^ 0x19602762] = ((int)arr_v1[109]) ^ 0x512A683D;
            arr_b1[4] = ((int)arr_v1[0x2F]) ^ 1987660133;
            arr_b1[5] = ((int)arr_v1[88]) ^ 0x24590322;
            arr_b1[((int)arr_v1[11]) ^ 182770824] = ((int)arr_v1[34]) ^ 0x495DBCC;
            arr_b1[((int)arr_v1[13]) ^ 0x40D055BC] = ((int)arr_v1[110]) ^ 1709007363;
            arr_b1[((int)arr_v1[15]) ^ 0x756B0B19] = ((int)arr_v1[0x6F]) ^ 0x111035FC;
            arr_b1[((int)arr_v1[16]) ^ 0x6BF752A3] = ((int)arr_v1[0x70]) ^ 0x51F0D5C9;
            arr_b1[((int)arr_v1[18]) ^ 0xB9DC06C] = ((int)arr_v1[10]) ^ 2101749188;
            arr_b1[((int)arr_v1[20]) ^ 0x34B0A7BA] = ((int)arr_v1[81]) ^ 0x502DDCEC;
            arr_b1[((int)arr_v1[22]) ^ 1725392081] = ((int)arr_v1[0x71]) ^ 0x613405A0;
            arr_b1[((int)arr_v1[24]) ^ 305045612] = ((int)arr_v1[0x72]) ^ 434085037;
            arr_b1[((int)arr_v1[26]) ^ 1778047907] = ((int)arr_v1[0x73]) ^ 0x7CF0ACFE;
            arr_b1[((int)arr_v1[28]) ^ 316191046] = ((int)arr_v1[0x1F]) ^ 0x41808E99;
            arr_b1[((int)arr_v1[30]) ^ 130235047] = 0x6922B450 ^ ((int)arr_v1[5]);
            arr_b1[((int)arr_v1[0x20]) ^ 0x6D0138DE] = ((int)arr_v1[59]) ^ 0x5F372398;
            arr_b1[((int)arr_v1[19]) ^ 0x1E889FF4] = ((int)arr_v1[90]) ^ 0x239003DE;
            arr_b1[((int)arr_v1[35]) ^ 0xB7787C7] = ((int)arr_v1[43]) ^ 0x3BFDF876;
            arr_b1[((int)arr_v1[37]) ^ 0x2BD2DD85] = ((int)arr_v1[0x74]) ^ 0x1A8168FE;
            arr_b1[((int)arr_v1[39]) ^ 709660002] = ((int)arr_v1[104]) ^ 0x3B448743;
            arr_b1[((int)arr_v1[41]) ^ 0x7897FFCC] = ((int)arr_v1[0x74]) ^ 0x1A8168FE;
            arr_b1[((int)arr_v1[43]) ^ 0x3BFDF876] = ((int)arr_v1[0x75]) ^ 0x49298AB6;
            arr_b1[((int)arr_v1[45]) ^ 0x9F81453] = ((int)arr_v1[0x76]) ^ 0x2CB601A4;
            arr_b1[((int)arr_v1[0x2F]) ^ 1987660133] = ((int)arr_v1[0x77]) ^ 0x6F85B23E;
            arr_b1[((int)arr_v1[49]) ^ 0x152E5EDF] = 5;
            arr_b1[((int)arr_v1[51]) ^ 1150722501] = ((int)arr_v1[99]) ^ 0x6FF78D7B;
            arr_b1[((int)arr_v1[53]) ^ 85602507] = ((int)arr_v1[97]) ^ 1419257510;
            arr_b1[((int)arr_v1[55]) ^ 0x4C7BC1D4] = ((int)arr_v1[104]) ^ 0x3B448743;
            arr_b1[((int)arr_v1[57]) ^ 0x4C7FA418] = ((int)arr_v1[109]) ^ 0x512A683D;
            arr_b1[((int)arr_v1[42]) ^ 0xEBBB90C] = ((int)arr_v1[120]) ^ 1138504530;
            arr_b1[((int)arr_v1[59]) ^ 0x5F372398] = ((int)arr_v1[89]) ^ 0x27F5BCB0;
            arr_b1[((int)arr_v1[60]) ^ 0x59FDFF0D] = ((int)arr_v1[0x79]) ^ 0x2AB39F1C;
            arr_b1[((int)arr_v1[9]) ^ 0xE060F48] = ((int)arr_v1[0x7A]) ^ 0x10D4E420;
            arr_b1[((int)arr_v1[61]) ^ 762625305] = ((int)arr_v1[0x7B]) ^ 0x44E3D7E9;
            arr_b1[((int)arr_v1[0x3F]) ^ 0x2F936D97] = ((int)arr_v1[87]) ^ 0x534A6C30;
            arr_b1[((int)arr_v1[65]) ^ 1180089524] = ((int)arr_v1[83]) ^ 0x4C0E6EC;
            arr_b1[((int)arr_v1[67]) ^ 0x1BAD1730] = ((int)arr_v1[0x7C]) ^ 0xB882F65;
            arr_b1[((int)arr_v1[38]) ^ 0x5AE87FD4] = ((int)arr_v1[91]) ^ 1545861009;
            arr_b1[((int)arr_v1[68]) ^ 0x1996CFC7] = ((int)arr_v1[89]) ^ 0x27F5BCB0;
            arr_b1[((int)arr_v1[70]) ^ 0x1874AC6] = ((int)arr_v1[0x7D]) ^ 572118108;
            arr_b1[((int)arr_v1[72]) ^ 317170873] = ((int)arr_v1[0x7E]) ^ 1799493040;
            arr_b1[((int)arr_v1[73]) ^ 2112083061] = ((int)arr_v1[83]) ^ 0x4C0E6EC;
            arr_b1[((int)arr_v1[75]) ^ 780017766] = ((int)arr_v1[0x7F]) ^ 0x5B6CF54F;
            arr_b1[((int)arr_v1[77]) ^ 0x4B2D2C2] = ((int)arr_v1[0x7A]) ^ 0x10D4E420;
            arr_b1[((int)arr_v1[0x4F]) ^ 2013260010] = ((int)arr_v1[0x79]) ^ 0x2AB39F1C;
            arr_b1[((int)arr_v1[81]) ^ 0x502DDCEC] = ((int)arr_v1[89]) ^ 0x27F5BCB0;
            sharedPreferences$Editor0.putString("dffa98fe-8bf6-4ed7-8d80-bb1a83c91fbb", "SBhXcwoAiLTNIyLK").apply();
        }
    }

    final class b implements Thread.UncaughtExceptionHandler {
        private static long[] a;
        final App a;

        static {
            long[] arr_v = new long[80];
            b.a = arr_v;
            arr_v[0] = 0x5BF4B3E7L;
            arr_v[1] = 0x6A645218L;
            arr_v[2] = 0x36EDBDC0L;
            arr_v[3] = 0xFFFFFFFFA5E54A8EL;
            arr_v[4] = 0x7D14D8B0L;
            arr_v[5] = 0x453FBDA3L;
            arr_v[6] = 0xDE9E738L;
            arr_v[7] = 0xFFFFFFFFAC42905CL;
            arr_v[8] = 0x4C7108ABL;
            arr_v[9] = 0x6A086466L;
            arr_v[10] = 0xFFFFFFFFB67DF98BL;
            arr_v[11] = 0xFFFFFFFFE1FB9561L;
            arr_v[12] = 0x62C64BDDL;
            arr_v[13] = -745035146L;
            arr_v[14] = 163960894L;
            arr_v[15] = 0x2D1C36E6L;
            arr_v[16] = 0x757B0232L;
            arr_v[17] = 1036302695L;
            arr_v[18] = 0x47AF1F0DL;
            arr_v[19] = 0x42818B9AL;
            arr_v[20] = 0x2A3111A0L;
            arr_v[21] = 0x37F13F71L;
            arr_v[22] = 0xFFFFFFFFB740224FL;
            arr_v[23] = 0x62F290DCL;
            arr_v[24] = 0x65B10F95L;
            arr_v[25] = 0x5FE54641L;
            arr_v[26] = -1341050477L;
            arr_v[27] = 0x11CE3ABAL;
            arr_v[28] = 0xFFFFFFFFA591036BL;
            arr_v[29] = 1260179931L;
            arr_v[30] = 0x50120225L;
            arr_v[0x1F] = 0x3750EAA0L;
            arr_v[0x20] = 0x6D203C67L;
            arr_v[33] = 0x1A8824F2L;
            arr_v[34] = 0x43D3A122L;
            arr_v[35] = 748106513L;
            arr_v[36] = 0x70639AC5L;
            arr_v[37] = 0xD0F6564L;
            arr_v[38] = 0x5CBAF037L;
            arr_v[39] = 2009281982L;
            arr_v[40] = 0x2E77601BL;
            arr_v[41] = 106714314L;
            arr_v[42] = 0x6E13DEFAL;
            arr_v[43] = 0x31AEB91EL;
            arr_v[44] = 1048996722L;
            arr_v[45] = 0x3B72C731L;
            arr_v[46] = 732005048L;
            arr_v[0x2F] = 0x7CDBAC0EL;
            arr_v[0x30] = 1901371023L;
            arr_v[49] = 0x3E4122D0L;
            arr_v[50] = 0x632BFAEAL;
            arr_v[51] = 1551266906L;
            arr_v[52] = 0x4023E0CBL;
            arr_v[53] = 1046270831L;
            arr_v[54] = 0x414062FCL;
            arr_v[55] = 0xDD8FEE4L;
            arr_v[56] = 330180787L;
            arr_v[57] = 0x3433377BL;
            arr_v[58] = 0xFFFFFFFFCAFE69A8L;
            arr_v[59] = 0xFFFFFFFFAB4CCF91L;
            arr_v[60] = 2099652743L;
            arr_v[61] = 0xFFFFFFFFDC49C7EEL;
            arr_v[62] = 1188210376L;
            arr_v[0x3F] = 0x396CEA70L;
            arr_v[0x40] = 1467435009L;
            arr_v[65] = -1419005491L;
            arr_v[66] = 0x56100421L;
            arr_v[67] = 0x2C1F853CL;
            arr_v[68] = 0xFFFFFFFFB369431DL;
            arr_v[69] = 1392403092L;
            arr_v[70] = -2031779113L;
            arr_v[71] = 0x70483387L;
            arr_v[72] = 1824700252L;
            arr_v[73] = 1501205965L;
            arr_v[74] = 0x30C757EDL;
            arr_v[75] = 0x5BB26BBDL;
            arr_v[76] = 2032777339L;
            arr_v[77] = 686395730L;
            arr_v[78] = 0x2EFAE0BCL;
            arr_v[0x4F] = 1035017291L;
        }

        private b() {
        }

        b(com.mistral.jon.App.1 app$10) {
        }

        // Detected as a lambda implementation
        private static void a() [...]

        // Detected as a lambda implementation
        private static void a(Throwable throwable0) [...]

        @Override
        public void uncaughtException(Thread thread0, Throwable throwable0) {
            throwable0.printStackTrace();
            if(throwable0 instanceof ayo) {
                azb.e(App.this);
                new Handler().postDelayed(() -> System.exit(((int)b.a[6]) ^ 233432890), b.a[0] ^ 0x5BF4B00FL);
                return;
            }

            new Thread(() -> {
                ayp.a().a(throwable0.getMessage(), "APP", ((int)b.a[12]) ^ 0x62C64BDB);
                String s = ayy.a(throwable0);
                ayp.a().a(s, "APP", ((int)b.a[12]) ^ 0x62C64BDB);
                try {
                    Thread.sleep(b.a[0x4F] ^ 1035014083L);
                }
                catch(InterruptedException unused_ex) {
                }

                System.exit(((int)b.a[6]) ^ 233432890);
            }).start();
        }
    }

    public interface c {
        void a();

        void a(MediaProjectionManager arg1, Intent arg2, int arg3);
    }

    private static Application a;
    private static c a;
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        App.a = arr_v;
        arr_v[0] = 0x50FF3377L;
        arr_v[1] = 0xBA12068L;
    }

    public static Application a() {
        return App.a;
    }

    public static void a() {
        c app$c0 = App.a;
        if(app$c0 != null) {
            app$c0.a();
        }
    }

    public static void a(MediaProjectionManager mediaProjectionManager0, Intent intent0, int v) {
        if(v == -1) {
            c app$c0 = App.a;
            if(app$c0 != null) {
                app$c0.a(mediaProjectionManager0, intent0, -1);
            }
        }
    }

    public static void a(c app$c0) {
        App.a = app$c0;
    }

    @Override  // android.app.Application
    public void onCreate() {
        super.onCreate();
        App.a = this;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        if(bcq.a(this).getBoolean("390930af-59ff-40e5-8dab-f303d89c05f3", ((boolean)(((int)App.a[0]) ^ 0x50FF3377)))) {
            System.exit(((int)App.a[1]) ^ 0xBA12069);
            return;
        }

        azu.a(this, ((boolean)(((int)App.a[0]) ^ 0x50FF3377)));
        azk.a(this);
        new a(this);
        Thread.setDefaultUncaughtExceptionHandler(new b(this, null));
        bcn bcn0 = new bcn(bcq.a(this));
        bcn0.a("a983fc39", OvService.class);
        bcn0.a("a063ff84", WebViewService.class);
    }

    class com.mistral.jon.App.1 {
    }

}

