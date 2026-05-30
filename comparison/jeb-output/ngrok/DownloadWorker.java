package com.mistral.jon.ngrok;

import android.content.Context;
import android.text.TextUtils;
import androidx.work.Constraints.Builder;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ListenableWorker.Result;
import androidx.work.NetworkType;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import z.bde;
import z.bdg;
import z.qf.a;
import z.qf;
import z.qj;

public class DownloadWorker extends Worker {
    private static final String a;
    private static long[] a;
    private final Context a;
    private static final String b;
    private static final String c;

    static {
        long[] arr_v = new long[101];
        DownloadWorker.a = arr_v;
        arr_v[0] = 1454204971L;
        arr_v[1] = 0x75F2AB29L;
        arr_v[2] = 0x740E6BL;
        arr_v[3] = 0xFFFFFFFFA25A5D41L;
        arr_v[4] = 1161005614L;
        arr_v[5] = 1737712024L;
        arr_v[6] = 0xBE7688L;
        arr_v[7] = 0x281D15ECL;
        arr_v[8] = 0x6316F200L;
        arr_v[9] = -102254891L;
        arr_v[10] = 0x74E68C25L;
        arr_v[11] = 2100678268L;
        arr_v[12] = 680400365L;
        arr_v[13] = 0x32E801F9L;
        arr_v[14] = 0x50918433L;
        arr_v[15] = 0xFFFFFFFF94FD3481L;
        arr_v[16] = 0xFFFFFFFF9EC11308L;
        arr_v[17] = 0x1F1DA24BL;
        arr_v[18] = 0x3B9A9914L;
        arr_v[19] = 0x1312332CL;
        arr_v[20] = 0x637D0CE9L;
        arr_v[21] = 0x654E0CDCL;
        arr_v[22] = 0xFFFFFFFFBD8F0645L;
        arr_v[23] = 1436959530L;
        arr_v[24] = 181872801L;
        arr_v[25] = 1725720209L;
        arr_v[26] = 0xFFFFFFFFD830353DL;
        arr_v[27] = 0x45AA6510L;
        arr_v[28] = 0x3174E220L;
        arr_v[29] = 0x3430B03CL;
        arr_v[30] = 0x92782DDL;
        arr_v[0x1F] = 618106345L;
        arr_v[0x20] = 0xDA2FF15L;
        arr_v[33] = 0x450A2DBCL;
        arr_v[34] = 0x56020215L;
        arr_v[35] = 0x4D529AD1L;
        arr_v[36] = 0x2F7B3887L;
        arr_v[37] = 0x37D7E7CAL;
        arr_v[38] = 0x329EE444L;
        arr_v[39] = 0x75C10798L;
        arr_v[40] = 0x36F5F0A3L;
        arr_v[41] = 1729900362L;
        arr_v[42] = 0xD1BAD0EL;
        arr_v[43] = 0x29FBF9F1L;
        arr_v[44] = 0x8074508L;
        arr_v[45] = 520400088L;
        arr_v[46] = 47422908L;
        arr_v[0x2F] = 0x42CF38E8L;
        arr_v[0x30] = 500043151L;
        arr_v[49] = 0x22F2724L;
        arr_v[50] = 0x235D035FL;
        arr_v[51] = 0x72314D3BL;
        arr_v[52] = 0x36727BF0L;
        arr_v[53] = 1806420969L;
        arr_v[54] = 0x1BC60E27L;
        arr_v[55] = 0x50BBA190L;
        arr_v[56] = 0x483B276AL;
        arr_v[57] = 0x70505FCAL;
        arr_v[58] = 0x75915164L;
        arr_v[59] = 1753030885L;
        arr_v[60] = 0x58BA83FCL;
        arr_v[61] = 0xFFFFFFFFD10399F2L;
        arr_v[62] = -831660649L;
        arr_v[0x3F] = 0xFFFFFFFFB9D09790L;
        arr_v[0x40] = -1306773018L;
        arr_v[65] = 891610532L;
        arr_v[66] = -1498106926L;
        arr_v[67] = 0xFFFFFFFFDC01B6B1L;
        arr_v[68] = 1031107642L;
        arr_v[69] = 1937708701L;
        arr_v[70] = 0x65F68905L;
        arr_v[71] = 0xFFFFFFFFDDED0AAEL;
        arr_v[72] = -43576650L;
        arr_v[73] = 411820920L;
        arr_v[74] = 0x71016BC6L;
        arr_v[75] = 0xC8FCE70L;
        arr_v[76] = 1459182004L;
        arr_v[77] = 0x30BAC341L;
        arr_v[78] = 0x26B4772FL;
        arr_v[0x4F] = 0x523F724CL;
        arr_v[80] = 0x41099F4FL;
        arr_v[81] = 1551442570L;
        arr_v[82] = 0x6B034B90L;
        arr_v[83] = 847856073L;
        arr_v[84] = 0x7BD7FE0AL;
        arr_v[85] = 0xFFFFFFFFBB93ED47L;
        arr_v[86] = 0xFFFFFFFFF1AF1958L;
        arr_v[87] = -1306998000L;
        arr_v[88] = 2126983500L;
        arr_v[89] = -501529435L;
        arr_v[90] = -2053183128L;
        arr_v[91] = -633109660L;
        arr_v[92] = 0x5FF00222L;
        arr_v[93] = 0x1FC8C76L;
        arr_v[94] = 0x4EE069F7L;
        arr_v[0x5F] = 0x4CAA0E39L;
        arr_v[0x60] = 0x67EB61C6L;
        arr_v[97] = 0x2F4CE239L;
        arr_v[98] = 1123940000L;
        arr_v[99] = 0x5EF5C4FBL;
        arr_v[100] = 2020882073L;
        byte[] arr_b = new byte[((int)arr_v[2]) ^ 0x740E4B];
        arr_b[((int)arr_v[0]) ^ 1454204971] = ((int)arr_v[3]) ^ 0x5DA5A2F4;
        arr_b[((int)arr_v[4]) ^ 1161005615] = ((int)arr_v[5]) ^ 1737712046;
        arr_b[((int)arr_v[6]) ^ 0xBE768A] = ((int)arr_v[7]) ^ 0x281D15D9;
        arr_b[((int)arr_v[8]) ^ 0x6316F203] = ((int)arr_v[3]) ^ 0x5DA5A2F4;
        arr_b[4] = ((int)arr_v[9]) ^ 102254869;
        arr_b[5] = ((int)arr_v[10]) ^ 1961266220;
        arr_b[((int)arr_v[11]) ^ 2100678266] = ((int)arr_v[12]) ^ 680400297;
        arr_b[((int)arr_v[13]) ^ 0x32E801FE] = ((int)arr_v[2]) ^ 0x740E4B;
        arr_b[((int)arr_v[14]) ^ 0x5091843B] = ((int)arr_v[15]) ^ 0x6B02CB25;
        arr_b[((int)arr_v[10]) ^ 1961266220] = ((int)arr_v[16]) ^ 0x613EEC86;
        arr_b[((int)arr_v[17]) ^ 0x1F1DA241] = ((int)arr_v[18]) ^ 0x3B9A997E;
        arr_b[((int)arr_v[19]) ^ 0x13123327] = ((int)arr_v[20]) ^ 0x637D0C99;
        arr_b[((int)arr_v[21]) ^ 0x654E0CD0] = ((int)arr_v[22]) ^ 0x4270F9F6;
        arr_b[((int)arr_v[23]) ^ 0x55A64327] = ((int)arr_v[24]) ^ 181872840;
        arr_b[((int)arr_v[25]) ^ 0x66DC669F] = ((int)arr_v[26]) ^ 0x27CFCA84;
        arr_b[((int)arr_v[27]) ^ 0x45AA651F] = ((int)arr_v[28]) ^ 829743702;
        arr_b[((int)arr_v[29]) ^ 875606060] = ((int)arr_v[30]) ^ 0x92782A8;
        arr_b[((int)arr_v[0x1F]) ^ 618106360] = ((int)arr_v[0x20]) ^ 0xDA2FF71;
        arr_b[((int)arr_v[33]) ^ 0x450A2DAE] = ((int)arr_v[34]) ^ 0x56020264;
        arr_b[((int)arr_v[35]) ^ 1297259202] = ((int)arr_v[36]) ^ 0x2F7B38E8;
        arr_b[((int)arr_v[37]) ^ 0x37D7E7DE] = ((int)arr_v[38]) ^ 0x329EE43E;
        arr_b[((int)arr_v[39]) ^ 0x75C1078D] = ((int)arr_v[40]) ^ 0x36F5F0FA;
        arr_b[((int)arr_v[41]) ^ 1729900380] = ((int)arr_v[42]) ^ 0xD1BAD65;
        arr_b[((int)arr_v[43]) ^ 0x29FBF9E6] = ((int)arr_v[44]) ^ 0x807454A;
        arr_b[((int)arr_v[45]) ^ 520400064] = ((int)arr_v[46]) ^ 0x2D39DEB;
        arr_b[((int)arr_v[0x2F]) ^ 0x42CF38F1] = ((int)arr_v[0x30]) ^ 500043246;
        arr_b[((int)arr_v[49]) ^ 0x22F273E] = ((int)arr_v[50]) ^ 0x235D0331;
        arr_b[((int)arr_v[51]) ^ 0x72314D20] = ((int)arr_v[52]) ^ 0x36727BA6;
        arr_b[((int)arr_v[53]) ^ 1806420981] = ((int)arr_v[54]) ^ 0x1BC60E60;
        arr_b[((int)arr_v[55]) ^ 0x50BBA18D] = ((int)arr_v[56]) ^ 1211836201;
        arr_b[((int)arr_v[57]) ^ 0x70505FD4] = ((int)arr_v[58]) ^ 0x75915113;
        arr_b[((int)arr_v[59]) ^ 1753030906] = ((int)arr_v[60]) ^ 0x58BA839B;
        DownloadWorker.a = "name";
        long[] arr_v1 = DownloadWorker.a;
        byte[] arr_b1 = new byte[((int)arr_v1[2]) ^ 0x740E4B];
        arr_b1[((int)arr_v1[0]) ^ 1454204971] = ((int)arr_v1[61]) ^ 0x2EFC6629;
        arr_b1[((int)arr_v1[4]) ^ 1161005615] = ((int)arr_v1[62]) ^ 831660562;
        arr_b1[((int)arr_v1[6]) ^ 0xBE768A] = ((int)arr_v1[0x3F]) ^ 0x462F6862;
        arr_b1[((int)arr_v1[8]) ^ 0x6316F203] = ((int)arr_v1[0x40]) ^ 1306773020;
        arr_b1[4] = ((int)arr_v1[65]) ^ 891610568;
        arr_b1[5] = ((int)arr_v1[66]) ^ 1498106938;
        arr_b1[((int)arr_v1[11]) ^ 2100678266] = ((int)arr_v1[67]) ^ 0x23FE493B;
        arr_b1[((int)arr_v1[13]) ^ 0x32E801FE] = ((int)arr_v1[68]) ^ 1031107706;
        arr_b1[((int)arr_v1[14]) ^ 0x5091843B] = ((int)arr_v1[69]) ^ 0x737F16D4;
        arr_b1[((int)arr_v1[10]) ^ 1961266220] = -1;
        arr_b1[((int)arr_v1[17]) ^ 0x1F1DA241] = ((int)arr_v1[70]) ^ 0x65F68978;
        arr_b1[((int)arr_v1[19]) ^ 0x13123327] = ((int)arr_v1[71]) ^ 0x2212F54B;
        arr_b1[((int)arr_v1[21]) ^ 0x654E0CD0] = ((int)arr_v1[15]) ^ 0x6B02CB25;
        arr_b1[((int)arr_v1[23]) ^ 0x55A64327] = ((int)arr_v1[72]) ^ 0x298ED06;
        arr_b1[((int)arr_v1[25]) ^ 0x66DC669F] = ((int)arr_v1[60]) ^ 0x58BA839B;
        arr_b1[((int)arr_v1[27]) ^ 0x45AA651F] = ((int)arr_v1[35]) ^ 1297259202;
        arr_b1[((int)arr_v1[29]) ^ 875606060] = ((int)arr_v1[24]) ^ 181872840;
        arr_b1[((int)arr_v1[0x1F]) ^ 618106360] = ((int)arr_v1[73]) ^ 411820845;
        arr_b1[((int)arr_v1[33]) ^ 0x450A2DAE] = ((int)arr_v1[46]) ^ 0x2D39DEB;
        arr_b1[((int)arr_v1[35]) ^ 1297259202] = ((int)arr_v1[74]) ^ 0x71016BF4;
        arr_b1[((int)arr_v1[37]) ^ 0x37D7E7DE] = ((int)arr_v1[75]) ^ 210751012;
        arr_b1[((int)arr_v1[39]) ^ 0x75C1078D] = ((int)arr_v1[76]) ^ 0x56F95984;
        arr_b1[((int)arr_v1[41]) ^ 1729900380] = ((int)arr_v1[58]) ^ 0x75915113;
        arr_b1[((int)arr_v1[43]) ^ 0x29FBF9E6] = ((int)arr_v1[77]) ^ 0x30BAC376;
        arr_b1[((int)arr_v1[45]) ^ 520400064] = ((int)arr_v1[78]) ^ 0x26B4774A;
        arr_b1[((int)arr_v1[0x2F]) ^ 0x42CF38F1] = ((int)arr_v1[0x4F]) ^ 0x523F723E;
        arr_b1[((int)arr_v1[49]) ^ 0x22F273E] = ((int)arr_v1[69]) ^ 0x737F16D4;
        arr_b1[((int)arr_v1[51]) ^ 0x72314D20] = ((int)arr_v1[80]) ^ 0x41099F3C;
        arr_b1[((int)arr_v1[53]) ^ 1806420981] = ((int)arr_v1[81]) ^ 0x5C7922C1;
        arr_b1[((int)arr_v1[55]) ^ 0x50BBA18D] = ((int)arr_v1[44]) ^ 0x807454A;
        arr_b1[((int)arr_v1[57]) ^ 0x70505FD4] = ((int)arr_v1[82]) ^ 0x6B034BF3;
        arr_b1[((int)arr_v1[59]) ^ 1753030906] = ((int)arr_v1[83]) ^ 0x328941AF;
        DownloadWorker.b = "arch";
        long[] arr_v2 = DownloadWorker.a;
        byte[] arr_b2 = new byte[((int)arr_v2[2]) ^ 0x740E4B];
        arr_b2[((int)arr_v2[0]) ^ 1454204971] = ((int)arr_v2[84]) ^ 0x7BD7FE2D;
        arr_b2[((int)arr_v2[4]) ^ 1161005615] = ((int)arr_v2[85]) ^ 1147933402;
        arr_b2[((int)arr_v2[6]) ^ 0xBE768A] = ((int)arr_v2[51]) ^ 0x72314D20;
        arr_b2[((int)arr_v2[8]) ^ 0x6316F203] = ((int)arr_v2[86]) ^ 0xE50E6C2;
        arr_b2[4] = ((int)arr_v2[87]) ^ 1306997897;
        arr_b2[5] = ((int)arr_v2[88]) ^ 0x7EC73161;
        arr_b2[((int)arr_v2[11]) ^ 2100678266] = ((int)arr_v2[15]) ^ 0x6B02CB25;
        arr_b2[((int)arr_v2[13]) ^ 0x32E801FE] = ((int)arr_v2[89]) ^ 501529389;
        arr_b2[((int)arr_v2[14]) ^ 0x5091843B] = ((int)arr_v2[90]) ^ 2053183115;
        arr_b2[((int)arr_v2[10]) ^ 1961266220] = ((int)arr_v2[91]) ^ 0x25BC7CD0;
        arr_b2[((int)arr_v2[17]) ^ 0x1F1DA241] = ((int)arr_v2[92]) ^ 0x5FF0025C;
        arr_b2[((int)arr_v2[19]) ^ 0x13123327] = ((int)arr_v2[93]) ^ 0x1FC8C54;
        arr_b2[((int)arr_v2[21]) ^ 0x654E0CD0] = ((int)arr_v2[41]) ^ 1729900380;
        arr_b2[((int)arr_v2[23]) ^ 0x55A64327] = ((int)arr_v2[94]) ^ 1323330014;
        arr_b2[((int)arr_v2[25]) ^ 0x66DC669F] = ((int)arr_v2[40]) ^ 0x36F5F0FA;
        arr_b2[((int)arr_v2[27]) ^ 0x45AA651F] = ((int)arr_v2[0x5F]) ^ 0x4CAA0E16;
        arr_b2[((int)arr_v2[29]) ^ 875606060] = ((int)arr_v2[54]) ^ 0x1BC60E60;
        arr_b2[((int)arr_v2[0x1F]) ^ 618106360] = ((int)arr_v2[28]) ^ 829743702;
        arr_b2[((int)arr_v2[33]) ^ 0x450A2DAE] = ((int)arr_v2[76]) ^ 0x56F95984;
        arr_b2[((int)arr_v2[35]) ^ 1297259202] = ((int)arr_v2[0x60]) ^ 0x67EB61AE;
        arr_b2[((int)arr_v2[37]) ^ 0x37D7E7DE] = ((int)arr_v2[75]) ^ 210751012;
        arr_b2[((int)arr_v2[39]) ^ 0x75C1078D] = ((int)arr_v2[97]) ^ 0x2F4CE241;
        arr_b2[((int)arr_v2[41]) ^ 1729900380] = ((int)arr_v2[98]) ^ 0x42FDF6F1;
        arr_b2[((int)arr_v2[43]) ^ 0x29FBF9E6] = ((int)arr_v2[0x60]) ^ 0x67EB61AE;
        arr_b2[((int)arr_v2[45]) ^ 520400064] = ((int)arr_v2[99]) ^ 0x5EF5C4B5;
        arr_b2[((int)arr_v2[0x2F]) ^ 0x42CF38F1] = ((int)arr_v2[0x60]) ^ 0x67EB61AE;
        arr_b2[((int)arr_v2[49]) ^ 0x22F273E] = ((int)arr_v2[78]) ^ 0x26B4774A;
        arr_b2[((int)arr_v2[51]) ^ 0x72314D20] = ((int)arr_v2[77]) ^ 0x30BAC376;
        arr_b2[((int)arr_v2[53]) ^ 1806420981] = ((int)arr_v2[100]) ^ 2020882088;
        arr_b2[((int)arr_v2[55]) ^ 0x50BBA18D] = ((int)arr_v2[30]) ^ 0x92782A8;
        arr_b2[((int)arr_v2[57]) ^ 0x70505FD4] = ((int)arr_v2[0x30]) ^ 500043246;
        arr_b2[((int)arr_v2[59]) ^ 1753030906] = ((int)arr_v2[18]) ^ 0x3B9A997E;
        DownloadWorker.c = "ll";
    }

    public DownloadWorker(Context context0, WorkerParameters workerParameters0) {
        super(context0, workerParameters0);
        this.a = context0;
    }

    public static void a(Context context0, String s, String s1, boolean z) {
        if(TextUtils.isEmpty(s)) {
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(DownloadWorker.a[1] ^ 0x75F2A8C1L);
            }
            catch(InterruptedException interruptedException0) {
                interruptedException0.printStackTrace();
            }
            Constraints constraints0 = new Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            Data data0 = new androidx.work.Data.Builder().putString("name", s).putString("arch", s1).putBoolean("ll", z).build();
            qf qf0 = (qf)((a)((a)new a(DownloadWorker.class).setInputData(data0)).setConstraints(constraints0)).build();
            qj.getInstance(context0).enqueue(qf0);
        }).start();
    }

    // Detected as a lambda implementation
    private static void a(String s, String s1, boolean z, Context context0) [...]

    @Override  // androidx.work.Worker
    public Result doWork() {
        try {
            String s = this.getInputData().getString("name");
            String s1 = this.getInputData().getString("arch");
            boolean z = this.getInputData().getBoolean("ll", ((boolean)(((int)DownloadWorker.a[0]) ^ 1454204971)));
            if(s != null && bde.a(s, s1)) {
                if(z) {
                    bdg.a(this.a, s);
                }
                return Result.success();
            }
        }
        catch(Exception exception0) {
            exception0.printStackTrace();
        }
        return Result.failure();
    }
}

