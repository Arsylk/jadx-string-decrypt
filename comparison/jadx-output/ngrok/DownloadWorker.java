package com.mistral.jon.ngrok;

import android.content.Context;
import android.text.TextUtils;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import p001z.AbstractC0802qj;
import p001z.WorkRequestImpl;
import p001z.bde;
import p001z.bdg;

/* JADX INFO: loaded from: classes.dex */
public class DownloadWorker extends Worker {

    /* JADX INFO: renamed from: a */
    private final Context context;

    /* JADX INFO: renamed from: a */
    private static long[] f740a = {1454204971, 1978837801, 7605867, -1571136191, 1161005614, 1737712024, 12482184, 672994796, 1662448128, -102254891, 1961266213, 2100678268, 680400365, 854065657, 1351713843, -1795345279, -1631513848, 522035787, 999987476, 319959852, 1669139689, 1699613916, -1114700219, 1436959530, 181872801, 1725720209, -667929283, 1168794896, 829743648, 875606076, 153584349, 618106345, 228785941, 1158294972, 1442972181, 1297259217, 796604551, 936896458, 849273924, 1975584664, 922087587, 1729900362, 219917582, 704379377, 134694152, 520400088, 47422908, 1120876776, 500043151, 36644644, 593298271, 1915833659, 913472496, 1806420969, 465964583, 1354473872, 1211836266, 1884315594, 1972457828, 1753030885, 1488618492, -788293134, -831660649, -1177512048, -1306773018, 891610532, -1498106926, -603867471, 1031107642, 1937708701, 1710655749, -571667794, -43576650, 411820920, 1895918534, 210751088, 1459182004, 817546049, 649361199, 1379889740, 1091149647, 1551442570, 1795378064, 847856073, 2077752842, -1147933369, -240182952, -1306998000, 2126983500, -501529435, -2053183128, -633109660, 1609564706, 33328246, 1323330039, 1286213177, 1743479238, 793567801, 1123940000, 1593165051, 2020882073};

    /* JADX INFO: renamed from: a */
    private static final String str = "name";

    /* JADX INFO: renamed from: b */
    private static final String str2 = "arch";

    /* JADX INFO: renamed from: c */
    private static final String str3 = "ll";

    public DownloadWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.context = context;
    }

    /* JADX INFO: renamed from: a */
    public static void m1056a(Context context, String str1, String str4, boolean z2) {
        if (TextUtils.isEmpty(str1)) {
            return;
        }
        new Thread(new Runnable(str1, str4, z2, context) { // from class: com.mistral.jon.ngrok.-$$Lambda$DownloadWorker$o5FMEgmvT3LvmQqMwwFv54BLFXY
            public final /* synthetic */ String f$0;
            public final /* synthetic */ String f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ Context f$3;

            public /* synthetic */ $$Lambda$DownloadWorker$o5FMEgmvT3LvmQqMwwFv54BLFXY(String str5, String str6, boolean z3, Context context2) {
                this.f$0 = str5;
                this.f$1 = str6;
                this.f$2 = z3;
                this.f$3 = context2;
            }

            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                DownloadWorker.lambda$o5FMEgmvT3LvmQqMwwFv54BLFXY(this.f$0, this.f$1, this.f$2, this.f$3);
            }
        }).start();
    }

    /* JADX INFO: renamed from: a */
    private static /* synthetic */ void m1057a(String str1, String str4, boolean z2, Context context) throws Throwable {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AbstractC0802qj.getInstance(context).enqueue(new WorkRequestImpl.a(DownloadWorker.class).setInputData(new Data.Builder().putString(str, str1).putString(str2, str4).putBoolean(str3, z2).build()).setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build());
    }

    public static /* synthetic */ void lambda$o5FMEgmvT3LvmQqMwwFv54BLFXY(String str1, String str4, boolean z2, Context context) throws Throwable {
        m1057a(str1, str4, z2, context);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.work.Worker
    public ListenableWorker.Result doWork() {
        try {
            String str1 = getInputData().getString(str);
            String str4 = getInputData().getString(str2);
            boolean z2 = getInputData().getBoolean(str3, false);
            if (str1 != null && bde.m4587a(str1, str4)) {
                if (z2) {
                    bdg.m4589a(this.context, str1);
                }
                return ListenableWorker.Result.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ListenableWorker.Result.failure();
    }
}
