package com.mistral.jon.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.mistral.jon.main.views.TransparentWebView;
import p001z.bcp;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class WebViewActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static long[] f732a = {673838733, 1088801787, 1088377963, 1652611075, 1406312731, 1295298158, 325641413, 2054007898, 1896538267, 62507041, -118073027, -1457186014, 1268387586, 1553284285, 912048728, 1166613520, 2033587464, 1148296886, -2073768077, 173196566, -2112933170, -1942115829, 588231657, 980064926, 1364064405, 211226215, -1850485407, 1228912125, -201848562, 1504963555, -1994063784, 369837308, 1426333835, 2141669376, -1942628003, 235539191, 1424704236, -705755186, 2107222773, -1512302156, 543704451, 1096558370, 376340803, -1768276366, 1222036435, 921433407, -831184412, 1482060003, -332208275, 4993599, -1469579376, 905255971, 789067872, 1752867547, 31586145, 976747353, -494340142, 2113036821, 870617701, 1229734579, 1527796802, 1407849425, 1494302214, 1053050293, 1902766148, 508474466, 120077586, 980348439, 172624864, 1703936249, 276580094, 233032329, 759857811, 1047560286, 194916366, 1610684983, 834392362, 1641702807, 668923235, 248336808, 1340674608, 1247517337, 1114701139, 1451474859, 1749576217, -729703810, -861286452, -506204513, 1772724525, 1205418883, -1515651144, -823564084, -1353983573, 747074888, -60241343, 1427329629, 2090816809, 1290121209, 597681565, 1834476247, 1923316071, 1929892510, 276994977, 921018470, -684695571, -495701866, -1336959541, -1562066718, -1364722462, -155926208, -652211765, -1351450049, 2058508097, 1981759814, 478743248, 1993376207, 1116798229, 1284309538, 1185884379, 1060218010, 2141435536, 1407614973, 1553488027, -1603507693, -261360885, 72497770, -1858075, 559336169, -631065165, 1600498497, 1302320652, 134113948, 1174635287, 1068067758, 1908643723, -179236916, -1630191127, -1889214941, -1818783932, -1593173054, -1304887599, -78668227, -1402018214, -1738421984, 506435365, 2124340882, 754578111, 457889081};

    /* JADX INFO: renamed from: a */
    public static boolean f731a = false;

    /* JADX INFO: renamed from: com.mistral.jon.activity.WebViewActivity$a */
    static class C0122a {

        /* JADX INFO: renamed from: a */
        private static long[] f733a = {1558939327, 702631717, -908800719, 961101743, 498259892, 691381262, 2088479150, 321389108, 973071424, 1070087236, 1332930785, 523833614, 344294459, 1812523469, -333197292, 1617308437, -1762827068, 1155644097, 1363686186, 1757250818, 607721424, 1884591759, 760372985, 1727995333, -1595286337, 1954800238, 1616830943, 609697755, -561700067, 1526431651, -1766943856, 771280937, 722506958, 1557304338, 1301359603, 1133653433, 143207393, 1645457838, 523054125, 423723682, 740016429, 115345563, 884795720, 1842973123, 35101032, 1713078426, 1078689049, 1796598526, 1725607452, 1597677846, 416837556, 82734071, 1888944797, 1076387760, 325048707, 1101685628, 1554611045, 877200929, -2106333837, 701095482, -662079937, 1870692974, -1607341682, 1668295276, -1785508170, -183864380, -1530755730, -1087601946, -731018576, -1311785790, 670122465, 1047502816, 746910499, 961142381, 454634668, 141925306, 765852229, 1967937217, 1269019168, 894472654, 822878414};

        /* JADX INFO: renamed from: a */
        private static final String str = "WebViewService";

        /* JADX INFO: renamed from: b */
        private static final String str2 = "start_service";

        /* JADX INFO: renamed from: a */
        static /* synthetic */ String getStr2() {
            return str2;
        }

        /* JADX INFO: renamed from: b */
        static /* synthetic */ String getStr() {
            return str;
        }
    }

    /* JADX INFO: renamed from: a */
    public static Intent m1042a(Context context) {
        Intent intent = new Intent(context, (Class<?>) WebViewActivity.class);
        intent.addFlags(1350631424);
        return intent;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private void m1043a() {
        bcp.m4271a(getApplicationContext(), C0122a.getStr(), null, true);
    }

    /* JADX INFO: renamed from: a */
    public static void m1044a(Context context) {
        context.startActivity(m1042a(context));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private /* synthetic */ boolean m1045a(View view, MotionEvent event) {
        finish();
        return false;
    }

    /* JADX INFO: String decrypt: "config:dialog:html:notification"; "${APP_NAME}"; "${TEXT}"; "utf8"; "text/html" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    private void m1046b() {
        ((NotificationManager) getSystemService("notification")).cancel(10);
        f731a = true;
        String str = bcq.m4272a(this).getString("config:dialog:html:notification", "");
        if (str != null) {
            str = str.replace("${APP_NAME}", getString(2131558433)).replace("${TEXT}", getString(2131558449));
        }
        TransparentWebView transparentWebView = new TransparentWebView(this);
        transparentWebView.loadData(str, "text/html", "utf8");
        transparentWebView.setOnTouchListener(new View.OnTouchListener() { // from class: com.mistral.jon.activity.-$$Lambda$WebViewActivity$UjQZF-UxS9UNmfb2c-y1lDn-kbI
            public /* synthetic */ $$Lambda$WebViewActivity$UjQZFUxS9UNmfb2cy1lDnkbI() {
            }

            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent event) {
                return WebViewActivity.m10590lambda$UjQZFUxS9UNmfb2cy1lDnkbI(this.f$0, view, event);
            }
        });
        setContentView(transparentWebView);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: c */
    private void m1047c() {
        Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
        intent.setFlags(1887436800);
        startActivity(intent);
        f731a = false;
    }

    /* JADX INFO: renamed from: lambda$UjQZF-UxS9UNmfb2c-y1lDn-kbI */
    public static /* synthetic */ boolean m10590lambda$UjQZFUxS9UNmfb2cy1lDnkbI(WebViewActivity webViewActivity, View view, MotionEvent event) {
        return webViewActivity.m1045a(view, event);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        Window window = getWindow();
        window.setFlags(1024, 1024);
        window.setWindowAnimations(0);
        if (getIntent() == null || !C0122a.getStr2().equals(getIntent().getAction())) {
            m1043a();
            m1046b();
        } else {
            m1043a();
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        m1047c();
    }
}
