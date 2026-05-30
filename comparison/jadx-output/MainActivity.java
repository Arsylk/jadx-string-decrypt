package com.mistral.jon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.mistral.jon.activity.IntroActivity;
import p001z.azb;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static final String str = "MainActivity";

    /* JADX INFO: renamed from: a */
    private static long[] f709a = {583029645, 1104127437, 1748946473, 1819476102, -476286850, 794954972, 665963240, 1613938145, -1894897320, 1357660264, 1811633038, 2100619566, -1677634989, 1294137824, 871329431, 1056217832, -1064183018, 801581169, 1242138191, 2048151515, 350458505, 1368366247, 678824638, 1621729513, -1806955651, 1127648293, -1695275118, 1118691975, 703111750, 1083985815, -1980242608, 1063836435, 1510249002, 1801772217, -1485901640, 529260384, -1809394039, 101424701, 1202688832, 1267126171, 912002811, 826371184, 1664427864, 595950531, -968769201, 933398336, -964924631, 1973103604, -480057194, 864982349, -1325734233, 1275794915, 933810794, 1716176824, 636979836, 611826019, 1562932432, 1186359066, 916098867, -909126895, 872012805, 1524378418, 980987177, 1492227956, 279786315, 380558726, 2130999939, 1197743920, 902150863, 426109265, 2009003388, 1014874967, 1520332905, 1515626135, 1815189613, 904574519, 2106570239, 670106707, 165046567, 381805100, 1515967012, 1272135829, 1462884135, 1850045368, 1278912260, 1540666997, 2139480508, 693785715, 1606003244, 563747177, -1128508328, -1742645914, 44814790, 1523966134, -1482172719, -943100869, -3520044, 804033117, 1480738738, 342319450, 524081477, 191531538, 606899425, -269277138, -650482949, -716245346, 648658079, 846788152, -1153034262, -1637506394, -114544724, -730202097, -684448818, -867251494, 184943685, -1831572475, 1363737187, -1276458224, 1574865551, -719505877, 1331745944, -2036798726, -205648122, 1467114788, -439832036, -2134420611, -107081298, -687557689, -482076175, -648111534, -703268914, 2066651874, 608829772, 2044575256, 1410855164, 328842369, 1646961575, 1130930171, 1025459458, 1628056938, 651089785, 1642553447, 853534333, 206666486, -2074248047, -1806532380, -1350155957, -659256077, 1380588998, -1017852473, -658799146, -2048090935, 983186949, 1751781292, 687029061, 1928492311, 842978823, -292368023, -1917589182, -1524657805, -846714635, -1140737318, -592160766, -1066471531, 338533364, 843539743, 7909924};

    public static /* synthetic */ void lambda$rU5FWQXZ1ZOby6LkiKHeCgy0ghM(MainActivity mainActivity) {
        mainActivity.finish();
    }

    /* JADX INFO: String decrypt: "ACTION_SHOW_TRANSPARENT"; "a11y"; "android.permission.POST_NOTIFICATIONS"; "fcm" */
    /* JADX WARN: Code duplicated, block: B:13:0x0665  */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(2131427357);
        if (Build.VERSION.SDK_INT < 29) {
            azb.m4323c(getApplicationContext());
        }
        if ("ACTION_SHOW_TRANSPARENT".equals(getIntent().getAction())) {
            new Handler().post(new $$Lambda$MainActivity$rU5FWQXZ1ZOby6LkiKHeCgy0ghM(this));
            return;
        }
        boolean zM4322b = azb.m4322b((Context) this);
        if (azb.m4322b((Context) this)) {
            startActivity(new Intent(this, (Class<?>) IntroActivity.class));
        } else {
            if (bcq.m4272a(this).getBoolean("a11y", false)) {
                startActivity(new Intent(this, (Class<?>) IntroActivity.class));
            }
        }
        if (!zM4322b) {
            if (Build.VERSION.SDK_INT >= 33) {
                String str2 = "android.permission.POST_NOTIFICATIONS";
                if (checkSelfPermission(str2) != 0) {
                    String[] strArr = new String[1];
                    strArr[0] = str2;
                    requestPermissions(strArr, 101);
                    return;
                }
            }
        }
        if (bcq.m4272a(this).contains("fcm")) {
            new Handler().post(new $$Lambda$MainActivity$rU5FWQXZ1ZOby6LkiKHeCgy0ghM(this));
        } else {
            new Handler().postDelayed(new $$Lambda$MainActivity$rU5FWQXZ1ZOby6LkiKHeCgy0ghM(this), 5000L);
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    /* JADX INFO: String decrypt: "a11y" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        if (!azb.m4322b((Context) this)) {
            if (!bcq.m4272a(this).getBoolean("a11y", false)) {
                return;
            }
        }
        new Handler().post(new $$Lambda$MainActivity$rU5FWQXZ1ZOby6LkiKHeCgy0ghM(this));
    }
}
