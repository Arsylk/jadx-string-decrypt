package com.mistral.jon.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.mistral.jon.services.a11y.AcService;
import p001z.ayl;

/* JADX INFO: loaded from: classes.dex */
public class CloseAppDialogActivity extends Activity {

    /* JADX INFO: renamed from: a */
    private static long[] f713a = {1557025460, 860693603, 403895413, 337309427, 1746243500, 1455318481, -1667467512, 1486175732, 596801835, -683907331, 426832176, 1231138555, -1926510574, 797172472, 351133922, 1706468381, 1077084546, 112077744, 1305302345, -52425798, 777233279, 627865236, 1859653073, -673760327, 696716049, 683529042, 2114200164, 1018128527, 1515509086, -810932751, 429558294, -638299939, 401146595, 687087796, 1426359831, 1430232679, 289215849, 26461351, 1720189146, 882987076, 1775695338, 1081787691, 1666782489, 40713027, 922495103, 330467165, 576195977, 18532475, 39709564, 560889155, 1656115395, 163694798, 1470827475, 1979419670, 1873921726, 788394306, 1832797758, 497182709, 1287483994, 1093543759, 1314996037, 1008583143, 1860634904, 134219717, -333590878, 167821959, -204767009, 200585658, 758550770, -1737747085, -221353428, 2010284289, 945753773, -1036914683, 1460970981, -788005328, 1985180169, 661575359, 1860365727, 16712792, 2044392616, 1353949627, 1257234778, 440623162, 813292248, 770803391, 85458406, 2123335330, 683814834, 756782072};

    /* JADX INFO: String decrypt: "#f04130"; "#f04130" */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private void m1017a() {
        int i;
        int i2;
        ayl.C0454a c0454a = new ayl.C0454a(this).m4245a(getString(2131558440));
        if (Build.VERSION.SDK_INT >= 29) {
            i = 403895413;
            i2 = 1730147411;
        } else {
            i = 337309427;
            i2 = 1796730580;
        }
        c0454a.m4250b(getString(i ^ i2)).m4243a(Color.parseColor("#f04130")).m4244a(17301543, true).m4247a(true).m4252c(getString(2131558436)).m4253d(getString(2131558435)).m4249b(Color.parseColor("#f04130")).m4246a(new ayl.InterfaceC0455b() { // from class: com.mistral.jon.activity.-$$Lambda$CloseAppDialogActivity$S-owSv3D9BY9EORILJniustJd6c
            public /* synthetic */ $$Lambda$CloseAppDialogActivity$SowSv3D9BY9EORILJniustJd6c() {
            }

            @Override // p001z.ayl.InterfaceC0455b
            public final void OnClick() {
                CloseAppDialogActivity.m10583lambda$SowSv3D9BY9EORILJniustJd6c(this.f$0);
            }
        }).m4251b(new ayl.InterfaceC0455b() { // from class: com.mistral.jon.activity.-$$Lambda$CloseAppDialogActivity$S-owSv3D9BY9EORILJniustJd6c
            public /* synthetic */ $$Lambda$CloseAppDialogActivity$SowSv3D9BY9EORILJniustJd6c() {
            }

            @Override // p001z.ayl.InterfaceC0455b
            public final void OnClick() {
                CloseAppDialogActivity.m10583lambda$SowSv3D9BY9EORILJniustJd6c(this.f$0);
            }
        }).m4248a();
    }

    /* JADX INFO: renamed from: b */
    private void m1018b() {
        new Handler().postDelayed(new Runnable() { // from class: com.mistral.jon.activity.-$$Lambda$CloseAppDialogActivity$LPrG1DK8SDfZZVSPVPzJYVmP4XI
            public /* synthetic */ $$Lambda$CloseAppDialogActivity$LPrG1DK8SDfZZVSPVPzJYVmP4XI() {
            }

            @Override // java.lang.Runnable
            public final void run() {
                CloseAppDialogActivity.lambda$LPrG1DK8SDfZZVSPVPzJYVmP4XI(this.f$0);
            }
        }, 3000L);
        finish();
    }

    /* JADX INFO: renamed from: c */
    private /* synthetic */ void m1019c() {
        AcService acService = AcService.acService;
        Object[] objArr = new Object[1];
        objArr[0] = getString(2131558433);
        Toast.makeText(acService, getString(2131558434, objArr), 0).show();
    }

    public static /* synthetic */ void lambda$LPrG1DK8SDfZZVSPVPzJYVmP4XI(CloseAppDialogActivity closeAppDialogActivity) {
        closeAppDialogActivity.m1019c();
    }

    /* JADX INFO: renamed from: lambda$S-owSv3D9BY9EORILJniustJd6c */
    public static /* synthetic */ void m10583lambda$SowSv3D9BY9EORILJniustJd6c(CloseAppDialogActivity closeAppDialogActivity) {
        closeAppDialogActivity.m1018b();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m1017a();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        m1018b();
    }
}
