package com.mistral.jon.fcm;

import android.content.Context;
import com.google.firebase.messaging.FirebaseMessagingService;
import java.util.Map;
import z.axg;
import z.ayp;

public class MessagingService extends FirebaseMessagingService {
    private static final String a = "MessagingService";
    private final ayp a;

    static {
    }

    public MessagingService() {
        this.a = ayp.a();
    }

    @Override  // com.google.firebase.messaging.FirebaseMessagingService
    public void a(axg axg0) {
        Map map0 = axg0.a();
        this.a.a(map0);
    }

    @Override  // com.google.firebase.messaging.FirebaseMessagingService
    public void b(String s) {
        this.a.a(s);
    }

    @Override  // android.app.Service
    public void onCreate() {
        Context context0 = this.getApplicationContext();
        this.a.a(context0);
    }

    @Override  // com.google.firebase.messaging.EnhancedIntentService
    public void onDestroy() {
        this.a.a();
        super.onDestroy();
    }
}

