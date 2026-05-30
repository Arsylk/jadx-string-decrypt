package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import com.mistral.jon.App;
import p001z.ayp;

/* JADX INFO: loaded from: classes.dex */
public class ScpActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static final String str = "ScpActivity";

    /* JADX INFO: renamed from: a */
    private static long[] f725a = {1003281805, 574352208, -2131918017, 1803639288, 994041162, 1362035681, 1236495024, 1310597956, -2125050791, -192596843, 2006340029, -1809310552, 414738503, -824763307, 2133322044, 556130739, 659904034, 780805765, 2007373745, 1096009153, 782656996, 1462327072, 11249243, 737031950, 874514694, 1752145743, -1411462583, 667038125, 1303339280, 1012854564, 1673654292, 500497440, 298883102, 1000769311, 764415786, 1642582913, 173808540, 1058539801, 1308422300, 989429132, 1435893007, 1195874512, 1556748977, 2112427212, 1110796301, 1544005635, 1717836161, 1644199812, 944681142, 1687762271, 72213914, 1877091308, 552152332, 519264443, 465762930, 1715243725, 2033360756, 1284581886, 1671429652, 1574880405, 2136249109, -467867855, -1032158523, -2071691387, 579652369, 1861287581, -2141569241, -1300883468, 1961907144, -1812745121, -342265105, 2010805755, 1943114307, 1355140248, -1016141095, 1022430626, -649869191, 2010166320, 735453870, -2065750489, 1895734685, 1479843070, 596774345, 219114202, 1164360225, 1229616611, 1419491585, 440655801, 1204910006, 1604078666, 1076064743, 95345222, 1192088248, 956178453, 349842172, 13842029, 1610730955, 1110834540, 1181166873, 528783134, 932571058, 7061646, -960100407, -288358201, 72111237, 1350372001, -229092133, -712227320, -2100555264, 475528537, 245298272, -1737430965, -515594823, -986155429, -1471355007, -1393001008, 627549562, -1791374580, -1936611534, -1122476829, -54131474, 77089572, 1600489436, 2069260290, -960694859, 381463799, -1795835296, -974053223, 941556267, 2069065489, -1045891387, -1352594020, -914089410, -772521898, 768058468, -1247952869, 352088352, -1998485547, -1665310916, 1005383, 493127889, 728832684, 1396929930, -947966994, -1822469591, -1557457278, -274409006, -1783162270, -176730071, -1368391077, -748456014, -1790243661, -1205230164, -1813671063, -711135222, -911226016, 223055681, 1767343622, 800494425};

    /* JADX INFO: renamed from: a */
    private MediaProjectionManager mediaProjectionManager;

    /* JADX INFO: String decrypt: "Activity result. RequestCode="; " ResultCode="; " Intent="; "Permission denied"; "Permission granted" */
    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        String str2 = str;
        ayp.m4254a().m4259a("Activity result. RequestCode=" + i + " ResultCode=" + i2 + " Intent=" + intent, str2);
        if (i == 1) {
            ayp aypVar = ayp.m4254a();
            if (i2 == 0) {
                aypVar.m4259a("Permission denied", str2);
                App.m1004a();
            } else {
                aypVar.m4259a("Permission granted", str2);
                App.m1005a(this.mediaProjectionManager, intent, i2);
            }
        }
        finish();
    }

    /* JADX INFO: String decrypt: "Create" */
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ayp.m4254a().m4259a("Create", str);
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService("media_projection");
        this.mediaProjectionManager = mediaProjectionManager;
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent().addFlags(65536), 1);
    }
}
