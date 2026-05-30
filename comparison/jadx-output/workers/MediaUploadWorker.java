package com.mistral.jon.workers;

import android.content.Context;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.File;
import java.io.IOException;
import p001z.AbstractC0802qj;
import p001z.WorkRequestImpl;
import p001z.ayp;
import p001z.bcq;
import p001z.bcr;
import p001z.bcy;
import p001z.bdf;

/* JADX INFO: loaded from: classes.dex */
public class MediaUploadWorker extends Worker {

    /* JADX INFO: renamed from: a */
    private final Context context;

    /* JADX INFO: renamed from: a */
    private bcy bcyVar;

    /* JADX INFO: renamed from: a */
    private static long[] f771a = {1370434793, 1300854226, 350249880, 446815003, -1024391718, 1722740231, -1676587548, 2081748634, -765464249, 1420249193, -1902930660, 1467914936, -272836756, 1517244961, -1289883628, 957521534, 318014022, 1790329583, -1331577235, 739823630, 633786435, -280035722, 2085119106, -454935307, 1567043836, -988504015, 945559764, 415313385, 550355459, 1112556718, 1356818535, 211265303, 636573640, 720368856, 103358131, 1205909772, 1209095690, 1914415329, 1848272235, 1940870657, 873420817, 1885333265, 1032217395, 615638814, 218296918, 299570318, 1635004803, 19415644, 927757318, 981722452, 1439901422, 384367900, 663763359, 642476732, 1730088904, 515681566, 1272099046, 1757456521, 1861324194, -116179656, -403038872, 1360505486, -1159166453, 432855928, 123462508, -2085896338, 1117044474, 1096839526, 624834863, -536388554, 350146443, -741082637, 981524933, 1310352586, 1035822881, 132116812, 635978006, 1547970432, 1363342546, 234627993, 889446608, 254856671, 583641670, 401563862, -273976268, -1448397903, 158845909, -1219061927, -844297469, -1031051667, -1545433760, 809426115, 1825876062, 1280519441, 1021813787, 339123013, 505396636, 2062660762, 1124611116, 528717573, 1532643627, -336183378, 414157494, -489368623, -529087738, -561859650, -237833723, 793195209, -1705182920, 462169276, 425492592, -714347954, 1274334196, 2043955284, 1582454761, 1211000649, 1407046085, 614139960, 1897831902, 950567776, 156451301, 800509631, 2015205906, 2086137163, 710346752, 1389201342, 71769437, -1036206996, -36651097, -1922682188, 618719301, -932491935, -1532507665, -1017716945, 1401309386, 1954676652, 478258352, 749177595, -1950262702, 744118351, -285768851, -737224464, -1157518820, -788216741, -652812917, -1474839164, -738768654, 795931175, -1069195506, -429310700, 1793074878, -1679564254, -1719798109, -999015964, 459280780, 326965874, -1907737012, -902984961, -669446906, -1499398282, -1513827656, -1097672959, 1102691991, -950484353, -1915075234, 1963993825, -1739052867, -2869778, 1730477446, -503934825, -1242446682, 1807826440, -629155717, -645319695, -172642801, -1465060281, -869776561, -859982786, -1041778904, 1533760392, 2051741471, -150233547, -551460996, -1580526316, -1068360757, -1751506978, -220733813, -1490631065, -1842503189, 668448007, -690425223, -1749340584, -1026535858, -672434191, -229643663, -1291363032, -620272075, -2131078, -491330866, -1689970485, -1705826352, -1321052974, 5214572, -1104803100, -300996268, -198128473, -1702602861, -2003062162, 1587633367, -311203334, 1321305872};

    /* JADX INFO: renamed from: e */
    private static final String str5 = "MediaUploadWorker";

    /* JADX INFO: renamed from: a */
    public static final String str = "filename";

    /* JADX INFO: renamed from: b */
    public static final String str2 = "type";

    /* JADX INFO: renamed from: c */
    public static final String str3 = "video";

    /* JADX INFO: renamed from: d */
    public static final String str4 = "accessibility";

    /* JADX INFO: String decrypt: "instance"; "token"; "type" */
    public MediaUploadWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.bcyVar = null;
        this.context = context;
        String str6 = bcq.m4272a(context).getString("instance", "");
        try {
            bcy bcyVar = new bcy(bcr.m4555b());
            this.bcyVar = bcyVar;
            bcyVar.m4571a("token", str6);
            this.bcyVar.m4571a("type", getInputData().getString(str2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: renamed from: a */
    public static void m1118a(String str1, String str6) throws Throwable {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        AbstractC0802qj.getInstance().enqueue(new WorkRequestImpl.a(MediaUploadWorker.class).setConstraints(constraints).setInputData(new Data.Builder().putString(str, str1).putString(str2, str6).build()).build());
    }

    /* JADX INFO: String decrypt: "Start uploading [%s] %s"; "/"; "Upload file %s not found"; "media"; "Uploading %s success"; "/"; "Uploading %s failed"; "Uploading %s exception: %s" */
    @Override // androidx.work.Worker
    public ListenableWorker.Result doWork() {
        if (this.bcyVar == null) {
            return ListenableWorker.Result.retry();
        }
        String str1 = getInputData().getString(str);
        ayp aypVar = ayp.m4254a();
        Object[] objArr1 = new Object[2];
        objArr1[0] = getInputData().getString(str2);
        objArr1[1] = str1;
        String str6 = String.format("Start uploading [%s] %s", objArr1);
        String str7 = str5;
        aypVar.m4259a(str6, str7);
        try {
            File file = str1.startsWith("/") ? new File(str1) : new File(bdf.m4588a(this.context, str1));
            if (!file.exists()) {
                Object[] objArr5 = new Object[1];
                objArr5[0] = str1;
                ayp.m4254a().m4259a(String.format("Upload file %s not found", objArr5), str7);
                return ListenableWorker.Result.success();
            }
            this.bcyVar.m4570a("media", file);
            if (this.bcyVar.m4572a()) {
                Object[] objArr4 = new Object[1];
                objArr4[0] = str1;
                ayp.m4254a().m4259a(String.format("Uploading %s success", objArr4), str7);
                if (!str1.startsWith("/") && file.exists()) {
                    try {
                        file.delete();
                    } catch (Exception unused) {
                    }
                }
                return ListenableWorker.Result.success();
            }
            Object[] objArr3 = new Object[1];
            objArr3[0] = str1;
            ayp.m4254a().m4259a(String.format("Uploading %s failed", objArr3), str5);
            return ListenableWorker.Result.retry();
        } catch (IOException e) {
            Object[] objArr2 = new Object[2];
            objArr2[0] = str1;
            objArr2[1] = e.getMessage();
            ayp.m4254a().m4259a(String.format("Uploading %s exception: %s", objArr2), str5);
            e.printStackTrace();
        }
    }
}
