package com.mistral.jon.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import p001z.axg;
import p001z.ayp;

/* JADX INFO: loaded from: classes.dex */
public class MessagingService extends FirebaseMessagingService {

    /* JADX INFO: renamed from: a */
    private static final String str = "MessagingService";

    /* JADX INFO: renamed from: a */
    private final ayp aypVar = ayp.m4254a();

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    /* JADX INFO: renamed from: a */
    public void mo994a(axg axgVar) {
        this.aypVar.m4261a(axgVar.m4057a());
    }

    @Override // com.google.firebase.messaging.FirebaseMessagingService
    /* JADX INFO: renamed from: b */
    public void mo995b(String str2) {
        this.aypVar.m4258a(str2);
    }

    @Override // android.app.Service
    public void onCreate() {
        this.aypVar.m4256a(getApplicationContext());
    }

    @Override // com.google.firebase.messaging.EnhancedIntentService, android.app.Service
    public void onDestroy() {
        this.aypVar.m4255a();
        super.onDestroy();
    }
}
