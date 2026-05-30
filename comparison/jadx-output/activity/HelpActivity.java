package com.mistral.jon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.mistral.jon.main.views.TransparentWebView;
import p001z.azb;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class HelpActivity extends Activity {

    /* JADX INFO: renamed from: c */
    private static final String str3 = "HelpActivity";

    /* JADX INFO: renamed from: b */
    private boolean f717b = false;

    /* JADX INFO: renamed from: c */
    private boolean f718c = false;

    /* JADX INFO: renamed from: d */
    private String str4;

    /* JADX INFO: renamed from: a */
    private static long[] f716a = {758576868, 1480005578, 1923288987, 703051469, 1286941340, 1280074891, -928450376, 1932436945, 450390346, 1415011672, -437417076, 1450266178, -810674622, 1831070760, -396362637, 377645498, 743737152, 169815515, -436063968, 198282050, 1801409776, 534068626, -1492386372, 211918596, 457959744, 1812711958, -1018357480, 8399054, -1782472959, 1322771767, 1946670991, 1729560986, 2142939669, -148087054, 82267630, -51149387, 1904368926, 521088178, 1401082023, -2109856697, 177729916, 237125702, -1820600883, 780039877, 225956280, 234737431, -1324610835, 1863676563, 1088775304, -887819092, 812132751, 729205668, -1882309603, 1084486889, -989326774, -1023409472, -1313046439, 2042805543, -1924963896, 1676080738, 293757644, 1124628765, 888967667, 1356997482, 1941952040, 1079029578, 874168506, 291136982, 199499116, 395211934, 1370431618, 891290471, 422723210, 1913187154, 241048221, 870950740, 754458672, 1482167020, 1237459215, 989118426, 488497321, 494928092, 983637111, 1242600698, 2145335577, 1410837833, -693626642, -257005317, -682307602, -348760343, -1048630628, -264278824, -995239648, -2060464513, -1660863207, -1907458163, -183488014, -108211521, 593811731, 489149136, -1962822295, 505793495, 2009848114, 1407865360, 1884901889, 231380160, 1428703921, 1942394869, 1196149446, 2080365194, 717333403, 572813537, 1336081278, -1011065244, -874581505, 1653313486, 911348073, -1096625834, -1850676578, 1312949410, 1741048135, 652971771, 153232944, 1490889162, 1800585136, -777751228, -1733585885, -2048833903, -445283738, -268211889, 1622553805, 1512962577, 1847263071, 1133736422, 29009389, 1076953223, 1271858688, -1438795382, 1567566013, 1732649846, 568279204, 1678326880, 659202604, -573957702, 431934789, 1696568228, 1338511532, 1639171856, 899506573, 504657738, 839968948, 296771496, 1184002528, -120045395, -1421218735, -1703052663, -1109604770, -1488876075, -809179049, -1445277696, -1824221881, -2119891477, 1655019686, 211941678, -1649380665, -1916361229, -2147220252, 1226590733, -274337691, 154655327, 121671112, 1122977333, 5729546, 1865432635, -782453898, -2014364393, -1165199630, -83004846, -653975288, -1133357343, -451017858, -835822774, 1588993109, -949502524, -1006414621, -339926755, -1375866044, -661066719, -794795160, -1251297623, -1165286371, -455804856, 812561688};

    /* JADX INFO: renamed from: a */
    public static final String str = "22a70813-51cf-4a0d-97e4-dfc840a0adcb";

    /* JADX INFO: renamed from: b */
    public static final String str2 = "e2ccb186-307a-4a15-9590-bc82e0d3a03b";

    /* JADX INFO: renamed from: a */
    public static boolean f715a = false;

    /* JADX INFO: String decrypt: "config:dialog:html:notification"; "${APP_NAME}"; "${TEXT}"; "utf8"; "text/html" */
    /* JADX INFO: renamed from: a */
    private void m1022a() {
        int i;
        int i2;
        String str1 = bcq.m4272a(this).getString("config:dialog:html:notification", "");
        if (str1 != null) {
            String str4 = str1.replace("${APP_NAME}", getString(2131558433));
            String str5 = "${TEXT}";
            if (str2.equals(this.str4)) {
                i = 1490889162;
                i2 = 667953658;
            } else {
                i = 1800585136;
                i2 = 341819266;
            }
            str1 = str4.replace(str5, getString(i ^ i2));
        }
        TransparentWebView transparentWebView = new TransparentWebView(this);
        transparentWebView.loadData(str1, "text/html", "utf8");
        transparentWebView.setOnTouchListener(new View.OnTouchListener() { // from class: com.mistral.jon.activity.-$$Lambda$HelpActivity$ifyNiMdelWuMiocmzVZ3-95rbw0
            public /* synthetic */ $$Lambda$HelpActivity$ifyNiMdelWuMiocmzVZ395rbw0() {
            }

            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent event) {
                return HelpActivity.m10585lambda$ifyNiMdelWuMiocmzVZ395rbw0(this.f$0, view, event);
            }
        });
        setContentView(transparentWebView);
    }

    /* JADX INFO: renamed from: a */
    public static void m1023a(Context context, String str4) {
        if (azb.m4322b(context)) {
            return;
        }
        Intent intent = new Intent(context, (Class<?>) HelpActivity.class);
        intent.addFlags(1350631424);
        intent.setAction(str4);
        context.startActivity(intent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private /* synthetic */ boolean m1024a(View view, MotionEvent event) {
        if (!this.f718c) {
            this.f718c = true;
            if (str2.equals(this.str4)) {
                finish();
            } else {
                this.f717b = true;
                azb.m4321b((Context) this);
                new Handler().postDelayed(new Runnable() { // from class: com.mistral.jon.activity.-$$Lambda$HelpActivity$13S8PMcQjnmRuGULHVxI5LeG5g8
                    public /* synthetic */ $$Lambda$HelpActivity$13S8PMcQjnmRuGULHVxI5LeG5g8() {
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        HelpActivity.lambda$13S8PMcQjnmRuGULHVxI5LeG5g8(this.f$0);
                    }
                }, 1000L);
                new Handler().postDelayed(new Runnable() { // from class: com.mistral.jon.activity.-$$Lambda$HelpActivity$2m6ABMPYzNgsuJPAbaZAWAADHa8
                    public /* synthetic */ $$Lambda$HelpActivity$2m6ABMPYzNgsuJPAbaZAWAADHa8() {
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        HelpActivity.lambda$2m6ABMPYzNgsuJPAbaZAWAADHa8(this.f$0);
                    }
                }, 1500L);
            }
        }
        return false;
    }

    /* JADX INFO: renamed from: b */
    private /* synthetic */ void m1025b() {
        m1023a(this, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: c */
    private static /* synthetic */ void m1026c() {
        f715a = true;
    }

    public static /* synthetic */ void lambda$13S8PMcQjnmRuGULHVxI5LeG5g8(HelpActivity helpActivity) {
        helpActivity.m1025b();
    }

    public static /* synthetic */ void lambda$2m6ABMPYzNgsuJPAbaZAWAADHa8(HelpActivity helpActivity) {
        helpActivity.finish();
    }

    /* JADX INFO: renamed from: lambda$ie6Od4TX8FNgNCtapTMLa6Bab-U */
    public static /* synthetic */ void m10584lambda$ie6Od4TX8FNgNCtapTMLa6BabU() {
        m1026c();
    }

    /* JADX INFO: renamed from: lambda$ifyNiMdelWuMiocmzVZ3-95rbw0 */
    public static /* synthetic */ boolean m10585lambda$ifyNiMdelWuMiocmzVZ395rbw0(HelpActivity helpActivity, View view, MotionEvent event) {
        return helpActivity.m1024a(view, event);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        Window window = getWindow();
        window.setFlags(1024, 1024);
        window.setWindowAnimations(0);
        Intent intent = getIntent();
        this.str4 = intent.getAction() == null ? str : intent.getAction();
        m1022a();
        new Handler().postDelayed(new Runnable() { // from class: com.mistral.jon.activity.-$$Lambda$HelpActivity$ie6Od4TX8FNgNCtapTMLa6Bab-U
            private /* synthetic */ $$Lambda$HelpActivity$ie6Od4TX8FNgNCtapTMLa6BabU() {
            }

            @Override // java.lang.Runnable
            public final void run() {
                HelpActivity.m10584lambda$ie6Od4TX8FNgNCtapTMLa6BabU();
            }
        }, 500L);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Activity
    protected void onStop() {
        if (str.equals(this.str4) && !this.f717b) {
            this.f717b = true;
            azb.m4321b((Context) this);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            m1023a(this, str2);
        }
        f715a = false;
        super.onStop();
    }
}
