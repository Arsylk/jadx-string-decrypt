package com.mistral.jon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mistral.jon.services.a11y.AcService;
import p001z.bdb;

/* JADX INFO: loaded from: classes.dex */
public class UscActivity extends AppCompatActivity {

    /* JADX INFO: renamed from: a */
    private static final String str = "UscActivity";

    /* JADX INFO: renamed from: a */
    private int f729a;

    /* JADX INFO: renamed from: d */
    private boolean f730d = false;

    /* JADX INFO: renamed from: e */
    private String str5 = "";

    /* JADX INFO: renamed from: a */
    private static long[] f728a = {691802085, 1885279140, 1698018924, 1009355550, 1019828438, 458078152, 156491132, 1477472502, -2098566508, 537727047, -718320076, 1179359662, -882368766, 217252414, -434716636, 1401558551, 484902445, 1167114982, -1499278338, 1415824423, 909544153, 1498117668, -977819800, 382765375, 833207649, 574399138, -1697102407, 663935081, 1148177620, -2078995240, 1593109307, 2118666821, 2140241011, 482399616, 121874018, 1073554972, 1485352122, 346924836, 1469378724, 365682806, 1170446414, 137839219, 524705235, 492808992, 205077704, 881647994, 1732278754, 71394913, 231868276, 1946772531, 325976668, 382222786, 1128235546, 1646407704, 112604449, 477864681, 412048354, 992014944, 860825164, 331997119, 636600651, 1918485863, 1499569569, 1313842447, -1399891434, 368328598, -667332719, 128308442, -911553390, 16064845, -784323573, -1189604720, 324156111, 1948217992, 898425867, 1157830349, 350102028, 1201604833, 777001470, 125704093, 924084147, 1734048225, 107387631, 451711967, -698249642, -1232191134, -81591793, -365172145, 1311050871, 1925434091, -1334941531, -703564476, -1514637253, -1139837906, 378218384, -694545022, -1823152110, 511438216, 44633888, 38990017, 1023714632, 362121023, 1505415629, 1580003479};

    /* JADX INFO: renamed from: b */
    private static final String str2 = "record_screen";

    /* JADX INFO: renamed from: c */
    private static final String str3 = "fn";

    /* JADX INFO: renamed from: d */
    private static final String str4 = "timeout";

    /* JADX INFO: renamed from: a */
    public static InterfaceC0121a aVar = null;

    /* JADX INFO: renamed from: com.mistral.jon.activity.UscActivity$a */
    public interface InterfaceC0121a {
    }

    /* JADX INFO: renamed from: a */
    public static void m1041a(Context context, boolean z2, String str5, int i) {
        Intent intent = new Intent(context.getApplicationContext(), (Class<?>) UscActivity.class);
        intent.addFlags(268435456);
        intent.addFlags(65536);
        intent.addFlags(67108864);
        intent.addFlags(8388608);
        intent.putExtra(str2, z2);
        intent.putExtra(str3, str5);
        intent.putExtra(str4, i);
        context.startActivity(intent);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 1) {
            super.onActivityResult(i, i2, intent);
            return;
        }
        if (i2 == -1) {
            bdb.mediaProjectionManager = AcService.acService.bdcVar.getMediaProjectionManager();
            bdb.f1594a = i2;
            bdb.intent = intent;
        }
        finish();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f730d = getIntent().getBooleanExtra(str2, false);
        this.str5 = getIntent().getStringExtra(str3);
        this.f729a = getIntent().getIntExtra(str4, 10000);
        Intent intent = AcService.acService.bdcVar.getMediaProjectionManager().createScreenCaptureIntent();
        intent.addFlags(65536);
        startActivityForResult(intent, 1);
    }
}
