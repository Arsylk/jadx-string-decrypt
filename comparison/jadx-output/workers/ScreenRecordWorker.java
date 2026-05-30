package com.mistral.jon.workers;

import android.content.Context;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.mistral.jon.services.a11y.AcService;
import p001z.ayp;
import p001z.bcz;
import p001z.bda;
import p001z.bdb;
import p001z.bdc;
import p001z.bdd;

/* JADX INFO: loaded from: classes.dex */
public class ScreenRecordWorker extends Worker {

    /* JADX INFO: renamed from: a */
    private final Context context;

    /* JADX INFO: renamed from: a */
    private static long[] f773a = {459034689, 206429879, 1352469696, 124182340, 212020903, 1612879877, -1337876296, 1219709359, 423482700, -561262902, -1940260774, 1351299203, -1674545060, 372335602, -712625690, 2083046688, 2144419777, 941335114, -464328512, 782506934, -1615446977, 896429249, 560091620, 1991452258, 1270464270, 1781375363, 398720492, 1922820735, 1253055576, 1850584383, 2050946285, -1079961593, 539893017, 887957907, 1346350566, 1480937643, 147308666, -1350963815, 1680762472, 954091122, 778315368, 1341479029, 2080517986, -1588590533, 868926899, -1260012744, 1090584470, 722804404, -669344612, 540781459, -1816333890, 1116532243, -1508630549, 1140301479, -1097979800, 1791701870, 1689128837, 1424157381, 2099203955, -1169686759, 553706180, -1153975472, 1578622500, 602528005, 1092051811, -1212921312, 1499066724, 1553661284, -1835337715, 1899326226, 956824824, 709495906, -1454250781, 1081064152, 1138094157, 1539299855, -558104619, 1702280999, 440484395, 347598349, -372657881, 506763162, 24302913, 1434896767, 2107559255, 984349555, 52171895, 1030793728, 1883081773, 1946378091, 1242877233, 26554217, 662396816, 1092461366, 1125476520, 1711260737, 616316116, 1485949186, 1472283693, 1160939180, 1355808668, 119661856, 175049341, 872467206, 23548191, 459934282, 1054412995, 1164453921, 98271081, -2114103043, -759029640, 132806963, -1232287067, -1163809492, -848895010, -1364786173, 616247125, 2095167957, -597639383, -1319647216, -950602979, -129299603, 891985453, -1827017572, -238002855, 70791511, -1374572391, -1650920423, -248460808, -1769616754, 1923864324, 1364535625, 731985041, 2066607204, 1173510379, -1452993564, 1334188574, 587514371, -716855556, 1531841557, -2006276774, -715537425, -1545552310, 652312608, -1708801206, -744084153, -338154411, -321122235, 1256569694, -2021571310, 706803755, 805781976, 569154176, -196996194, -312634550, 1174381795, -1244269977, -1478312803, 436923590, 504630321, 305286622, -1851741330, -1715051564, -69188333, -1810220300, -201987816, -1403294875, -300159784, 149715165, 94840896};

    /* JADX INFO: renamed from: a */
    private static final String str = "ScreenRecordWorker";

    /* JADX INFO: renamed from: b */
    private static final String str2 = "filename";

    /* JADX INFO: renamed from: c */
    private static final String str3 = "timeout";

    /* JADX INFO: renamed from: a */
    private static boolean f772a = false;

    /* JADX INFO: renamed from: a */
    public static bcz bczVar = null;

    public ScreenRecordWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.context = context;
    }

    /* JADX INFO: String decrypt: "doWork: ScreenCaptureDataHolder.data="; "doWork: Starting screen recording" */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.work.Worker
    public ListenableWorker.Result doWork() throws Throwable {
        ayp aypVar = ayp.m4254a();
        String str1 = "doWork: ScreenCaptureDataHolder.data=" + bdb.intent;
        String str4 = str;
        aypVar.m4259a(str1, str4);
        if (bdb.intent == null || f772a) {
            return ListenableWorker.Result.success();
        }
        f772a = true;
        int i = getInputData().getInt(str3, 10000);
        String str5 = getInputData().getString(str2);
        bdc bdcVar = AcService.acService.bdcVar;
        bdcVar.m4581a(str5);
        bdcVar.m4580a(bdb.f1594a, bdb.intent);
        ayp.m4254a().m4259a("doWork: Starting screen recording", str4);
        bdd.m4585a(this.context, bdcVar, str5, i, bczVar);
        f772a = false;
        bda.f1592a = false;
        return ListenableWorker.Result.success();
    }
}
