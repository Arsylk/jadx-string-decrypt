package com.mistral.jon.lib;

/* JADX INFO: loaded from: classes.dex */
public class VncSessionConfig {
    private int mConSocketPort;
    private String mName;
    private float mPreScaleFactor;
    private String mPw;
    private int mVncPort;

    /* JADX INFO: renamed from: com.mistral.jon.lib.VncSessionConfig$a */
    public static class C0123a {

        /* JADX INFO: renamed from: a */
        private static long[] f735a = {863371295, 57618991, -533830310, 1375055419, 1421786591, 597750526, 248220183, 869315835, -940062271, -2104753261, 13332621, 1749889098, -629984563, 150817494, 376216095, 195192454, -1202336407, 730196848, 1726190435, 1488369941, 288800698, 525558219, -1969661164, 237070186, -1592137078, 1488872710, 611850082, 60508011, 404362313, 979671981, -334667383, 997469671, 2063358199, 841129616, 1092383550, 2097438906, 1275957424, 2043555752, 2022423514, 384633637, 228269078, 503131545, 812580690, 401625752, 2095090511, 824470453, 418812817, 584662920, 1478601702, 312622908, 2085091348, 1593138299, 870512762, 981147689, 1840400996, 1185249629, 682710659, 1615558347, 283840760, 774422207};

        /* JADX INFO: renamed from: b */
        private String str2;

        /* JADX INFO: renamed from: a */
        private String str = "VNC_SESSION";

        /* JADX INFO: renamed from: a */
        private int f736a = 4800;

        /* JADX INFO: renamed from: c */
        private int f738c = 5901;

        /* JADX INFO: renamed from: b */
        private int f737b = 100;

        /* JADX INFO: renamed from: a */
        public C0123a m1051a(int i) {
            this.f737b = i;
            return this;
        }

        /* JADX INFO: renamed from: a */
        public C0123a m1052a(String str) {
            this.str = str;
            return this;
        }

        /* JADX INFO: renamed from: a */
        public VncSessionConfig m1053a() {
            return new VncSessionConfig(this.str, this.str2, this.f736a, this.f738c, this.f737b, null);
        }

        /* JADX INFO: renamed from: b */
        public C0123a m1054b(int i) {
            this.f738c = i;
            return this;
        }

        /* JADX INFO: renamed from: b */
        public C0123a m1055b(String str) {
            this.str2 = str;
            return this;
        }
    }

    private VncSessionConfig(String str1, String str2, int i, int i2, int i3) {
        this.mName = str1;
        this.mPw = str2;
        this.mConSocketPort = i;
        this.mVncPort = i2;
        this.mPreScaleFactor = i3 / 100.0f;
    }

    VncSessionConfig(String str1, String str2, int i, int i2, int i3, VncSessionConfig vncSessionConfig) {
        this(str1, str2, i, i2, i3);
    }

    public int getConSocketPort() {
        return this.mConSocketPort;
    }

    public String getName() {
        return this.mName;
    }

    public String getPw() {
        return this.mPw;
    }

    public float getScaleFactor() {
        return this.mPreScaleFactor;
    }

    public int getVncPort() {
        return this.mVncPort;
    }
}
