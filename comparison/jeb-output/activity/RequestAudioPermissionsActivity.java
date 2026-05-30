package com.mistral.jon.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RequestAudioPermissionsActivity extends AppCompatActivity {
    private static long[] a;

    static {
        long[] arr_v = new long[2];
        RequestAudioPermissionsActivity.a = arr_v;
        arr_v[0] = 0x38CCF5CBL;
        arr_v[1] = 0x2ECDCD89L;
    }

    private void f() {
        long[] arr_v = RequestAudioPermissionsActivity.a;
        String[] arr_s = new String[((int)arr_v[0]) ^ 0x38CCF5CA];
        arr_s[((int)arr_v[1]) ^ 0x2ECDCD89] = "android.permission.RECORD_AUDIO";
        this.requestPermissions(arr_s, 1002);
    }

    @Override  // androidx.fragment.app.FragmentActivity
    public void onActivityResult(int v, int v1, Intent intent0) {
        if(v == 1002) {
            this.finish();
        }
        super.onActivityResult(v, v1, intent0);
    }

    @Override  // androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle0) {
        super.onCreate(bundle0);
        this.f();
    }
}

