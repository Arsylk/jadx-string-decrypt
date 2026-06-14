package k2015.a1;

import java.io.PrintStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/* loaded from: classes.dex */
public class Check {
    private static /* synthetic */ String access$_T11306(Object obj, String str) {
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = (char) (charArray[i] ^ 't');
        }
        return new String(charArray);
    }

    /* JADX INFO: String decrypt: "java.lang.String"; "toCharArray"; "[C" (+1 more) */
    private static /* synthetic */ String access$_T15566(Object obj, String str) throws Throwable {
        try {
            Method method = String.class.getMethod("toCharArray", null);
            method.setAccessible(true);
            char[] cArr = (char[]) method.invoke(str, new Object[0]);
            for (int i = 0; i < cArr.length; i++) {
                cArr[i] = (char) (cArr[i] ^ 'g');
            }
            try {
                Constructor<?> constructor = String.class.getConstructor(char[].class);
                constructor.setAccessible(true);
                return (String) constructor.newInstance(cArr);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } catch (InvocationTargetException e2) {
            throw e2.getTargetException();
        }
    }

    /* JADX INFO: String decrypt: "java.lang.StringBuilder"; "java.lang.String"; "reverse"; "java.lang.Object"; "toString"; "java.lang.Class"; "forName"; "java.lang.System"; "out"; "k2015.a1.Check"; "access$_T15566"; "getField" (+913 more) */
    public static boolean check(String str) throws Throwable {
        long jIntValue;
        BigDecimal bigDecimal;
        BigDecimal bigDecimal2;
        String str2;
        String str3;
        try {
            StringBuilder.class.getConstructor(String.class).setAccessible(true);
            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
            Object.class.getMethod("toString", null).setAccessible(true);
            Method method = Class.class.getMethod("forName", String.class);
            method.setAccessible(true);
            Class.forName("java.lang.System").getField("out").setAccessible(true);
            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
            Method method2 = Class.class.getMethod("getField", String.class);
            method2.setAccessible(true);
            Field field = (Field) method2.invoke((Class) method.invoke(null, "java.math.MathContext"), "DECIMAL64");
            try {
                Method method3 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                method3.setAccessible(true);
                method3.invoke(field, true);
                try {
                    Method method4 = Field.class.getMethod("get", Object.class);
                    method4.setAccessible(true);
                    MathContext mathContext = (MathContext) method4.invoke(field, null);
                    try {
                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                        Object.class.getMethod("toString", null).setAccessible(true);
                        Method method5 = Class.class.getMethod("forName", String.class);
                        method5.setAccessible(true);
                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                        Method method6 = Class.class.getMethod("forName", String.class);
                        method6.setAccessible(true);
                        Method method7 = Class.class.getMethod("getMethod", String.class, Class[].class);
                        method7.setAccessible(true);
                        Method method8 = (Method) method7.invoke((Class) method5.invoke(null, "java.lang.Integer"), "parseInt", new Class[]{(Class) method6.invoke(null, "java.lang.String")});
                        Method method9 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                        method9.setAccessible(true);
                        method9.invoke(method8, true);
                        Method method10 = Method.class.getMethod("invoke", Object.class, Object[].class);
                        method10.setAccessible(true);
                        Method method11 = Integer.class.getMethod("intValue", null);
                        method11.setAccessible(true);
                        jIntValue = ((Integer) method11.invoke((Integer) method10.invoke(method8, null, new Object[]{str}), new Object[0])).intValue();
                        try {
                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                            Method method12 = Class.class.getMethod("forName", String.class);
                            method12.setAccessible(true);
                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                            Long.class.getField("TYPE").setAccessible(true);
                            Integer.class.getField("TYPE").setAccessible(true);
                            Method method13 = Class.class.getMethod("getMethod", String.class, Class[].class);
                            method13.setAccessible(true);
                            Method method14 = (Method) method13.invoke((Class) method12.invoke(null, "java.math.BigDecimal"), "valueOf", new Class[]{long.class, int.class});
                            Method method15 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                            method15.setAccessible(true);
                            method15.invoke(method14, true);
                            Long.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                            Method method16 = Integer.class.getMethod("valueOf", Integer.TYPE);
                            method16.setAccessible(true);
                            Method method17 = Method.class.getMethod("invoke", Object.class, Object[].class);
                            method17.setAccessible(true);
                            bigDecimal = (BigDecimal) method17.invoke(method14, null, new Object[]{0, (Integer) method16.invoke(null, 0)});
                            try {
                                BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                Method method18 = Class.class.getMethod("forName", String.class);
                                method18.setAccessible(true);
                                BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                Long.class.getField("TYPE").setAccessible(true);
                                Integer.class.getField("TYPE").setAccessible(true);
                                Method method19 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                method19.setAccessible(true);
                                Method method20 = (Method) method19.invoke((Class) method18.invoke(null, "java.math.BigDecimal"), "valueOf", new Class[]{long.class, int.class});
                                Method method21 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                method21.setAccessible(true);
                                method21.invoke(method20, true);
                                Long.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                Method method22 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                method22.setAccessible(true);
                                Method method23 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                method23.setAccessible(true);
                                bigDecimal2 = (BigDecimal) method23.invoke(method20, null, new Object[]{4, (Integer) method22.invoke(null, 0)});
                            } catch (InvocationTargetException e) {
                                try {
                                    Method method24 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                    method24.setAccessible(true);
                                    throw ((Throwable) method24.invoke(e, new Object[0]));
                                } catch (InvocationTargetException e2) {
                                    throw e2.getTargetException();
                                }
                            }
                        } catch (InvocationTargetException e3) {
                            try {
                                Method method25 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                method25.setAccessible(true);
                                throw ((Throwable) method25.invoke(e3, new Object[0]));
                            } catch (InvocationTargetException e4) {
                                throw e4.getTargetException();
                            }
                        }
                    } catch (InvocationTargetException e5) {
                        try {
                            Method method26 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                            method26.setAccessible(true);
                            throw ((Throwable) method26.invoke(e5, new Object[0]));
                        } catch (InvocationTargetException e6) {
                            throw e6.getTargetException();
                        }
                    }
                    try {
                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                        Method method27 = Class.class.getMethod("forName", String.class);
                        method27.setAccessible(true);
                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                        Method method28 = Class.class.getMethod("getField", String.class);
                        method28.setAccessible(true);
                        Field field2 = (Field) method28.invoke((Class) method27.invoke(null, "java.lang.System"), "out");
                        try {
                            Method method29 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                            method29.setAccessible(true);
                            method29.invoke(field2, true);
                            try {
                                Method method30 = Field.class.getMethod("get", Object.class);
                                method30.setAccessible(true);
                                if (((PrintStream) method30.invoke(field2, null)) == null) {
                                    try {
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method31 = Class.class.getMethod("forName", String.class);
                                        method31.setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method32 = Class.class.getMethod("forName", String.class);
                                        method32.setAccessible(true);
                                        Method method33 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method33.setAccessible(true);
                                        Method method34 = (Method) method33.invoke((Class) method31.invoke(null, "java.lang.String"), "equals", new Class[]{(Class) method32.invoke(null, "java.lang.Object")});
                                        Method method35 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method35.setAccessible(true);
                                        method35.invoke(method34, true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method36 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method36.setAccessible(true);
                                        Method method37 = Boolean.class.getMethod("booleanValue", null);
                                        method37.setAccessible(true);
                                        return ((Boolean) method37.invoke((Boolean) method36.invoke(method34, "9527", new Object[]{str}), new Object[0])).booleanValue();
                                    } catch (InvocationTargetException e7) {
                                        try {
                                            Method method38 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                            method38.setAccessible(true);
                                            throw ((Throwable) method38.invoke(e7, new Object[0]));
                                        } catch (InvocationTargetException e8) {
                                            throw e8.getTargetException();
                                        }
                                    }
                                }
                                String str4 = null;
                                int i = 1;
                                BigDecimal bigDecimal3 = bigDecimal;
                                String str5 = str;
                                long j = jIntValue;
                                while (true) {
                                    int i2 = i;
                                    str2 = str4;
                                    if (i2 >= 1001) {
                                        break;
                                    }
                                    if (i2 % 1 == 0) {
                                        try {
                                            Class.forName("java.lang.System").getField("err").setAccessible(true);
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            Method method39 = Class.class.getMethod("forName", String.class);
                                            method39.setAccessible(true);
                                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                            Method method40 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                            method40.setAccessible(true);
                                            Method method41 = (Method) method40.invoke((Class) method39.invoke(null, "java.lang.String"), "toString", null);
                                            Method method42 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method42.setAccessible(true);
                                            method42.invoke(method41, true);
                                            Method method43 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                            method43.setAccessible(true);
                                            str3 = str5;
                                            str4 = (String) method43.invoke(method41, str5, new Object[0]);
                                        } catch (InvocationTargetException e9) {
                                            try {
                                                Method method44 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                                method44.setAccessible(true);
                                                throw ((Throwable) method44.invoke(e9, new Object[0]));
                                            } catch (InvocationTargetException e10) {
                                                throw e10.getTargetException();
                                            }
                                        }
                                    } else {
                                        str3 = str2;
                                        str4 = str2;
                                    }
                                    long j2 = j + i2;
                                    try {
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method45 = Class.class.getMethod("forName", String.class);
                                        method45.setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Long.class.getField("TYPE").setAccessible(true);
                                        Integer.class.getField("TYPE").setAccessible(true);
                                        Method method46 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method46.setAccessible(true);
                                        Method method47 = (Method) method46.invoke((Class) method45.invoke(null, "java.math.BigDecimal"), "valueOf", new Class[]{long.class, int.class});
                                        Method method48 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method48.setAccessible(true);
                                        method48.invoke(method47, true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method49 = Class.class.getMethod("forName", String.class);
                                        method49.setAccessible(true);
                                        Class.forName("java.lang.System").getField("out").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method50 = Class.class.getMethod("forName", String.class);
                                        method50.setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method51 = Class.class.getMethod("forName", String.class);
                                        method51.setAccessible(true);
                                        Method method52 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method52.setAccessible(true);
                                        Method method53 = (Method) method52.invoke((Class) method49.invoke(null, "java.math.BigDecimal"), "divide", new Class[]{(Class) method50.invoke(null, "java.math.BigDecimal"), (Class) method51.invoke(null, "java.math.MathContext")});
                                        Method method54 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method54.setAccessible(true);
                                        method54.invoke(method53, true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method55 = Class.class.getMethod("forName", String.class);
                                        method55.setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("out").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method56 = Class.class.getMethod("forName", String.class);
                                        method56.setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method57 = Class.class.getMethod("forName", String.class);
                                        method57.setAccessible(true);
                                        Method method58 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method58.setAccessible(true);
                                        Method method59 = (Method) method58.invoke((Class) method55.invoke(null, "java.math.BigDecimal"), "add", new Class[]{(Class) method56.invoke(null, "java.math.BigDecimal"), (Class) method57.invoke(null, "java.math.MathContext")});
                                        Method method60 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method60.setAccessible(true);
                                        method60.invoke(method59, true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method61 = Class.class.getMethod("forName", String.class);
                                        method61.setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Long.class.getField("TYPE").setAccessible(true);
                                        Integer.class.getField("TYPE").setAccessible(true);
                                        Method method62 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method62.setAccessible(true);
                                        Method method63 = (Method) method62.invoke((Class) method61.invoke(null, "java.math.BigDecimal"), "valueOf", new Class[]{long.class, int.class});
                                        Method method64 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method64.setAccessible(true);
                                        method64.invoke(method63, true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method65 = Class.class.getMethod("forName", String.class);
                                        method65.setAccessible(true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method66 = Class.class.getMethod("forName", String.class);
                                        method66.setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method67 = Class.class.getMethod("forName", String.class);
                                        method67.setAccessible(true);
                                        Method method68 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method68.setAccessible(true);
                                        Method method69 = (Method) method68.invoke((Class) method65.invoke(null, "java.math.BigDecimal"), "divide", new Class[]{(Class) method66.invoke(null, "java.math.BigDecimal"), (Class) method67.invoke(null, "java.math.MathContext")});
                                        Method method70 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method70.setAccessible(true);
                                        method70.invoke(method69, true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method71 = Class.class.getMethod("forName", String.class);
                                        method71.setAccessible(true);
                                        Class.forName("java.lang.System").getField("out").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method72 = Class.class.getMethod("forName", String.class);
                                        method72.setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method73 = Class.class.getMethod("forName", String.class);
                                        method73.setAccessible(true);
                                        Method method74 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method74.setAccessible(true);
                                        Method method75 = (Method) method74.invoke((Class) method71.invoke(null, "java.math.BigDecimal"), "add", new Class[]{(Class) method72.invoke(null, "java.math.BigDecimal"), (Class) method73.invoke(null, "java.math.MathContext")});
                                        Method method76 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method76.setAccessible(true);
                                        method76.invoke(method75, true);
                                        Method method77 = Long.class.getMethod("valueOf", Long.TYPE);
                                        method77.setAccessible(true);
                                        Method method78 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                        method78.setAccessible(true);
                                        Method method79 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method79.setAccessible(true);
                                        Method method80 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method80.setAccessible(true);
                                        Method method81 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method81.setAccessible(true);
                                        Method method82 = Long.class.getMethod("valueOf", Long.TYPE);
                                        method82.setAccessible(true);
                                        Method method83 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                        method83.setAccessible(true);
                                        Method method84 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method84.setAccessible(true);
                                        Method method85 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method85.setAccessible(true);
                                        Method method86 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method86.setAccessible(true);
                                        Object[] objArr = {(BigDecimal) method81.invoke(method59, bigDecimal3, new Object[]{(BigDecimal) method80.invoke(method53, bigDecimal2, new Object[]{(BigDecimal) method79.invoke(method47, null, new Object[]{(Long) method77.invoke(null, Long.valueOf(i2)), (Integer) method78.invoke(null, 0)}), mathContext}), mathContext}), new Object[]{(BigDecimal) method85.invoke(method69, bigDecimal2, new Object[]{(BigDecimal) method84.invoke(method63, null, new Object[]{(Long) method82.invoke(null, Long.valueOf(-(i2 + 2))), (Integer) method83.invoke(null, 0)}), mathContext}), mathContext}};
                                        i = i2 + 4;
                                        bigDecimal3 = (BigDecimal) method86.invoke(method75, objArr);
                                        str5 = str3;
                                        j = j2;
                                    } catch (InvocationTargetException e11) {
                                        try {
                                            Method method87 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                            method87.setAccessible(true);
                                            throw ((Throwable) method87.invoke(e11, new Object[0]));
                                        } catch (InvocationTargetException e12) {
                                            throw e12.getTargetException();
                                        }
                                    }
                                }
                                try {
                                    Class.forName("java.lang.System").getField("err").setAccessible(true);
                                    Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                    Method method88 = Class.class.getMethod("forName", String.class);
                                    method88.setAccessible(true);
                                    BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                    BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                    BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                    BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                    String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                    Method method89 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                    method89.setAccessible(true);
                                    Method method90 = (Method) method89.invoke((Class) method88.invoke(null, "java.lang.String"), "hashCode", null);
                                    Method method91 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                    method91.setAccessible(true);
                                    method91.invoke(method90, true);
                                    Method method92 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                    method92.setAccessible(true);
                                    Method method93 = Integer.class.getMethod("intValue", null);
                                    method93.setAccessible(true);
                                    if ((((Integer) method93.invoke((Integer) method92.invoke(method90, str5, new Object[0]), new Object[0])).intValue() & 15) < 0) {
                                        if (jIntValue > 8) {
                                            str2 = str5;
                                        }
                                        try {
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Method method94 = Class.class.getMethod("forName", String.class);
                                            method94.setAccessible(true);
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Class.forName("java.lang.System").getField("err").setAccessible(true);
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            Method method95 = Class.class.getMethod("forName", String.class);
                                            method95.setAccessible(true);
                                            Method method96 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                            method96.setAccessible(true);
                                            Method method97 = (Method) method96.invoke((Class) method94.invoke(null, "java.lang.String"), "equals", new Class[]{(Class) method95.invoke(null, "java.lang.Object")});
                                            Method method98 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method98.setAccessible(true);
                                            method98.invoke(method97, true);
                                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                            Method method99 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                            method99.setAccessible(true);
                                            Method method100 = Boolean.class.getMethod("booleanValue", null);
                                            method100.setAccessible(true);
                                            return ((Boolean) method100.invoke((Boolean) method99.invoke(method97, "1290", new Object[]{str2}), new Object[0])).booleanValue();
                                        } catch (InvocationTargetException e13) {
                                            try {
                                                Method method101 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                                method101.setAccessible(true);
                                                throw ((Throwable) method101.invoke(e13, new Object[0]));
                                            } catch (InvocationTargetException e14) {
                                                throw e14.getTargetException();
                                            }
                                        }
                                    }
                                    try {
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method102 = Class.class.getMethod("forName", String.class);
                                        method102.setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Integer.class.getField("TYPE").setAccessible(true);
                                        Method method103 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method103.setAccessible(true);
                                        Method method104 = (Method) method103.invoke((Class) method102.invoke(null, "java.math.BigDecimal"), "scaleByPowerOfTen", new Class[]{int.class});
                                        Method method105 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method105.setAccessible(true);
                                        method105.invoke(method104, true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method106 = Class.class.getMethod("forName", String.class);
                                        method106.setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method107 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method107.setAccessible(true);
                                        Method method108 = (Method) method107.invoke((Class) method106.invoke(null, "java.math.BigDecimal"), "longValue", null);
                                        Method method109 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method109.setAccessible(true);
                                        method109.invoke(method108, true);
                                        Method method110 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                        method110.setAccessible(true);
                                        Method method111 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method111.setAccessible(true);
                                        Method method112 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method112.setAccessible(true);
                                        Method method113 = Long.class.getMethod("longValue", null);
                                        method113.setAccessible(true);
                                        long jLongValue = ((Long) method113.invoke((Long) method112.invoke(method108, (BigDecimal) method111.invoke(method104, bigDecimal3, new Object[]{(Integer) method110.invoke(null, Integer.valueOf((int) (7 + ((((1 + j) * j) * (2 + j)) % 6))))}), new Object[0]), new Object[0])).longValue();
                                        try {
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Method method114 = Class.class.getMethod("forName", String.class);
                                            method114.setAccessible(true);
                                            Class.forName("java.lang.System").getField("err").setAccessible(true);
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Method method115 = Class.class.getMethod("forName", String.class);
                                            method115.setAccessible(true);
                                            Method method116 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                            method116.setAccessible(true);
                                            Method method117 = (Method) method116.invoke((Class) method114.invoke(null, "java.lang.String"), "equals", new Class[]{(Class) method115.invoke(null, "java.lang.Object")});
                                            Method method118 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method118.setAccessible(true);
                                            method118.invoke(method117, true);
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Method method119 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                            method119.setAccessible(true);
                                            Method method120 = Boolean.class.getMethod("booleanValue", null);
                                            method120.setAccessible(true);
                                            ((Boolean) method120.invoke((Boolean) method119.invoke(method117, "4680", new Object[]{str2}), new Object[0])).booleanValue();
                                            return (jLongValue % 1000000) + 124750 == j;
                                        } catch (InvocationTargetException e15) {
                                            try {
                                                Method method121 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                                method121.setAccessible(true);
                                                throw ((Throwable) method121.invoke(e15, new Object[0]));
                                            } catch (InvocationTargetException e16) {
                                                throw e16.getTargetException();
                                            }
                                        }
                                    } catch (InvocationTargetException e17) {
                                        try {
                                            Method method122 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                            method122.setAccessible(true);
                                            throw ((Throwable) method122.invoke(e17, new Object[0]));
                                        } catch (InvocationTargetException e18) {
                                            throw e18.getTargetException();
                                        }
                                    }
                                } catch (InvocationTargetException e19) {
                                    try {
                                        Method method123 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                        method123.setAccessible(true);
                                        throw ((Throwable) method123.invoke(e19, new Object[0]));
                                    } catch (InvocationTargetException e20) {
                                        throw e20.getTargetException();
                                    }
                                }
                            } catch (InvocationTargetException e21) {
                                throw e21.getTargetException();
                            }
                        } catch (InvocationTargetException e22) {
                            throw e22.getTargetException();
                        }
                    } catch (InvocationTargetException e23) {
                        throw e23.getTargetException();
                    }
                } catch (InvocationTargetException e24) {
                    throw e24.getTargetException();
                }
            } catch (InvocationTargetException e25) {
                throw e25.getTargetException();
            }
        } catch (InvocationTargetException e26) {
            throw e26.getTargetException();
        }
    }
}