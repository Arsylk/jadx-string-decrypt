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
            Class.class.getMethod("forName", String.class).setAccessible(true);
            Class.forName("java.lang.System").getField("out").setAccessible(true);
            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
            Method method = Class.class.getMethod("getField", String.class);
            method.setAccessible(true);
            Field field = (Field) method.invoke(Class.forName("java.math.MathContext"), "DECIMAL64");
            try {
                Method method2 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                method2.setAccessible(true);
                method2.invoke(field, true);
                try {
                    Method method3 = Field.class.getMethod("get", Object.class);
                    method3.setAccessible(true);
                    MathContext mathContext = (MathContext) method3.invoke(field, null);
                    try {
                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                        Object.class.getMethod("toString", null).setAccessible(true);
                        Class.class.getMethod("forName", String.class).setAccessible(true);
                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                        Class.class.getMethod("forName", String.class).setAccessible(true);
                        Method method4 = Class.class.getMethod("getMethod", String.class, Class[].class);
                        method4.setAccessible(true);
                        Method method5 = (Method) method4.invoke(Integer.class, "parseInt", new Class[]{String.class});
                        Method method6 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                        method6.setAccessible(true);
                        method6.invoke(method5, true);
                        Method method7 = Method.class.getMethod("invoke", Object.class, Object[].class);
                        method7.setAccessible(true);
                        Method method8 = Integer.class.getMethod("intValue", null);
                        method8.setAccessible(true);
                        jIntValue = ((Integer) method8.invoke((Integer) method7.invoke(method5, null, new Object[]{str}), new Object[0])).intValue();
                        try {
                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                            Class.class.getMethod("forName", String.class).setAccessible(true);
                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                            Long.class.getField("TYPE").setAccessible(true);
                            Integer.class.getField("TYPE").setAccessible(true);
                            Method method9 = Class.class.getMethod("getMethod", String.class, Class[].class);
                            method9.setAccessible(true);
                            Method method10 = (Method) method9.invoke(BigDecimal.class, "valueOf", new Class[]{long.class, int.class});
                            Method method11 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                            method11.setAccessible(true);
                            method11.invoke(method10, true);
                            Long.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                            Method method12 = Integer.class.getMethod("valueOf", Integer.TYPE);
                            method12.setAccessible(true);
                            Method method13 = Method.class.getMethod("invoke", Object.class, Object[].class);
                            method13.setAccessible(true);
                            bigDecimal = (BigDecimal) method13.invoke(method10, null, new Object[]{0, (Integer) method12.invoke(null, 0)});
                            try {
                                BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                Class.class.getMethod("forName", String.class).setAccessible(true);
                                BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                Long.class.getField("TYPE").setAccessible(true);
                                Integer.class.getField("TYPE").setAccessible(true);
                                Method method14 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                method14.setAccessible(true);
                                Method method15 = (Method) method14.invoke(BigDecimal.class, "valueOf", new Class[]{long.class, int.class});
                                Method method16 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                method16.setAccessible(true);
                                method16.invoke(method15, true);
                                Long.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                Method method17 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                method17.setAccessible(true);
                                Method method18 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                method18.setAccessible(true);
                                bigDecimal2 = (BigDecimal) method18.invoke(method15, null, new Object[]{4, (Integer) method17.invoke(null, 0)});
                            } catch (InvocationTargetException e) {
                                try {
                                    Method method19 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                    method19.setAccessible(true);
                                    throw ((Throwable) method19.invoke(e, new Object[0]));
                                } catch (InvocationTargetException e2) {
                                    throw e2.getTargetException();
                                }
                            }
                        } catch (InvocationTargetException e3) {
                            try {
                                Method method20 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                method20.setAccessible(true);
                                throw ((Throwable) method20.invoke(e3, new Object[0]));
                            } catch (InvocationTargetException e4) {
                                throw e4.getTargetException();
                            }
                        }
                    } catch (InvocationTargetException e5) {
                        try {
                            Method method21 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                            method21.setAccessible(true);
                            throw ((Throwable) method21.invoke(e5, new Object[0]));
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
                        Class.class.getMethod("forName", String.class).setAccessible(true);
                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                        Method method22 = Class.class.getMethod("getField", String.class);
                        method22.setAccessible(true);
                        Field field2 = (Field) method22.invoke(Class.forName("java.lang.System"), "out");
                        try {
                            Method method23 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                            method23.setAccessible(true);
                            method23.invoke(field2, true);
                            try {
                                Method method24 = Field.class.getMethod("get", Object.class);
                                method24.setAccessible(true);
                                if (((PrintStream) method24.invoke(field2, null)) == null) {
                                    try {
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Method method25 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method25.setAccessible(true);
                                        Method method26 = (Method) method25.invoke(String.class, "equals", new Class[]{Object.class});
                                        Method method27 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method27.setAccessible(true);
                                        method27.invoke(method26, true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Method method28 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method28.setAccessible(true);
                                        Method method29 = Boolean.class.getMethod("booleanValue", null);
                                        method29.setAccessible(true);
                                        return ((Boolean) method29.invoke((Boolean) method28.invoke(method26, "9527", new Object[]{str}), new Object[0])).booleanValue();
                                    } catch (InvocationTargetException e7) {
                                        try {
                                            Method method30 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                            method30.setAccessible(true);
                                            throw ((Throwable) method30.invoke(e7, new Object[0]));
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
                                            Class.class.getMethod("forName", String.class).setAccessible(true);
                                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                            Method method31 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                            method31.setAccessible(true);
                                            Method method32 = (Method) method31.invoke(String.class, "toString", null);
                                            Method method33 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method33.setAccessible(true);
                                            method33.invoke(method32, true);
                                            Method method34 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                            method34.setAccessible(true);
                                            str3 = str5;
                                            str4 = (String) method34.invoke(method32, str5, new Object[0]);
                                        } catch (InvocationTargetException e9) {
                                            try {
                                                Method method35 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                                method35.setAccessible(true);
                                                throw ((Throwable) method35.invoke(e9, new Object[0]));
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
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Long.class.getField("TYPE").setAccessible(true);
                                        Integer.class.getField("TYPE").setAccessible(true);
                                        Method method36 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method36.setAccessible(true);
                                        Method method37 = (Method) method36.invoke(BigDecimal.class, "valueOf", new Class[]{long.class, int.class});
                                        Method method38 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method38.setAccessible(true);
                                        method38.invoke(method37, true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("out").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Method method39 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method39.setAccessible(true);
                                        Method method40 = (Method) method39.invoke(BigDecimal.class, "divide", new Class[]{BigDecimal.class, Class.forName("java.math.MathContext")});
                                        Method method41 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method41.setAccessible(true);
                                        method41.invoke(method40, true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("out").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Method method42 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method42.setAccessible(true);
                                        Method method43 = (Method) method42.invoke(BigDecimal.class, "add", new Class[]{BigDecimal.class, Class.forName("java.math.MathContext")});
                                        Method method44 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method44.setAccessible(true);
                                        method44.invoke(method43, true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Long.class.getField("TYPE").setAccessible(true);
                                        Integer.class.getField("TYPE").setAccessible(true);
                                        Method method45 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method45.setAccessible(true);
                                        Method method46 = (Method) method45.invoke(BigDecimal.class, "valueOf", new Class[]{long.class, int.class});
                                        Method method47 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method47.setAccessible(true);
                                        method47.invoke(method46, true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("err").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Method method48 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method48.setAccessible(true);
                                        Method method49 = (Method) method48.invoke(BigDecimal.class, "divide", new Class[]{BigDecimal.class, Class.forName("java.math.MathContext")});
                                        Method method50 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method50.setAccessible(true);
                                        method50.invoke(method49, true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Class.forName("java.lang.System").getField("out").setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        Method method51 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method51.setAccessible(true);
                                        Method method52 = (Method) method51.invoke(BigDecimal.class, "add", new Class[]{BigDecimal.class, Class.forName("java.math.MathContext")});
                                        Method method53 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method53.setAccessible(true);
                                        method53.invoke(method52, true);
                                        Long.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        Method method54 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                        method54.setAccessible(true);
                                        Method method55 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method55.setAccessible(true);
                                        Method method56 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method56.setAccessible(true);
                                        Method method57 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method57.setAccessible(true);
                                        Long.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        Method method58 = Integer.class.getMethod("valueOf", Integer.TYPE);
                                        method58.setAccessible(true);
                                        Method method59 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method59.setAccessible(true);
                                        Method method60 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method60.setAccessible(true);
                                        Method method61 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method61.setAccessible(true);
                                        Object[] objArr = {(BigDecimal) method57.invoke(method43, bigDecimal3, new Object[]{(BigDecimal) method56.invoke(method40, bigDecimal2, new Object[]{(BigDecimal) method55.invoke(method37, null, new Object[]{Long.valueOf((Object) i2), (Integer) method54.invoke(null, 0)}), mathContext}), mathContext}), new Object[]{(BigDecimal) method60.invoke(method49, bigDecimal2, new Object[]{(BigDecimal) method59.invoke(method46, null, new Object[]{Long.valueOf((Object) (-(i2 + 2))), (Integer) method58.invoke(null, 0)}), mathContext}), mathContext}};
                                        i = i2 + 4;
                                        bigDecimal3 = (BigDecimal) method61.invoke(method52, objArr);
                                        str5 = str3;
                                        j = j2;
                                    } catch (InvocationTargetException e11) {
                                        try {
                                            Method method62 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                            method62.setAccessible(true);
                                            throw ((Throwable) method62.invoke(e11, new Object[0]));
                                        } catch (InvocationTargetException e12) {
                                            throw e12.getTargetException();
                                        }
                                    }
                                }
                                try {
                                    Class.forName("java.lang.System").getField("err").setAccessible(true);
                                    Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                    Class.class.getMethod("forName", String.class).setAccessible(true);
                                    BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                    BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                    BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                    BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                    String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                    Method method63 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                    method63.setAccessible(true);
                                    Method method64 = (Method) method63.invoke(String.class, "hashCode", null);
                                    Method method65 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                    method65.setAccessible(true);
                                    method65.invoke(method64, true);
                                    Method method66 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                    method66.setAccessible(true);
                                    Method method67 = Integer.class.getMethod("intValue", null);
                                    method67.setAccessible(true);
                                    if ((((Integer) method67.invoke((Integer) method66.invoke(method64, str5, new Object[0]), new Object[0])).intValue() & 15) < 0) {
                                        if (jIntValue > 8) {
                                            str2 = str5;
                                        }
                                        try {
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Class.class.getMethod("forName", String.class).setAccessible(true);
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Class.forName("java.lang.System").getField("err").setAccessible(true);
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            Class.class.getMethod("forName", String.class).setAccessible(true);
                                            Method method68 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                            method68.setAccessible(true);
                                            Method method69 = (Method) method68.invoke(String.class, "equals", new Class[]{Object.class});
                                            Method method70 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method70.setAccessible(true);
                                            method70.invoke(method69, true);
                                            BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                            BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                            BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                            BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                            String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                            Method method71 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                            method71.setAccessible(true);
                                            Method method72 = Boolean.class.getMethod("booleanValue", null);
                                            method72.setAccessible(true);
                                            return ((Boolean) method72.invoke((Boolean) method71.invoke(method69, "1290", new Object[]{str2}), new Object[0])).booleanValue();
                                        } catch (InvocationTargetException e13) {
                                            try {
                                                Method method73 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                                method73.setAccessible(true);
                                                throw ((Throwable) method73.invoke(e13, new Object[0]));
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
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Integer.class.getField("TYPE").setAccessible(true);
                                        Method method74 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method74.setAccessible(true);
                                        Method method75 = (Method) method74.invoke(BigDecimal.class, "scaleByPowerOfTen", new Class[]{int.class});
                                        Method method76 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method76.setAccessible(true);
                                        method76.invoke(method75, true);
                                        BigInteger.class.getConstructor(byte[].class).setAccessible(true);
                                        BigInteger.class.getMethod("valueOf", Long.TYPE).setAccessible(true);
                                        BigInteger.class.getMethod("divide", BigInteger.class).setAccessible(true);
                                        BigInteger.class.getMethod("toByteArray", null).setAccessible(true);
                                        String.class.getConstructor(byte[].class, String.class).setAccessible(true);
                                        Class.class.getMethod("forName", String.class).setAccessible(true);
                                        StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                        StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                        Object.class.getMethod("toString", null).setAccessible(true);
                                        Method method77 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                        method77.setAccessible(true);
                                        Method method78 = (Method) method77.invoke(BigDecimal.class, "longValue", null);
                                        Method method79 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method79.setAccessible(true);
                                        method79.invoke(method78, true);
                                        Integer.class.getMethod("valueOf", Integer.TYPE).setAccessible(true);
                                        Method method80 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method80.setAccessible(true);
                                        Method method81 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                        method81.setAccessible(true);
                                        Method method82 = Long.class.getMethod("longValue", null);
                                        method82.setAccessible(true);
                                        long jLongValue = ((Long) method82.invoke((Long) method81.invoke(method78, (BigDecimal) method80.invoke(method75, bigDecimal3, new Object[]{Integer.valueOf((Object) ((int) (7 + ((((1 + j) * j) * (2 + j)) % 6))))}), new Object[0]), new Object[0])).longValue();
                                        try {
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Class.class.getMethod("forName", String.class).setAccessible(true);
                                            Class.forName("java.lang.System").getField("err").setAccessible(true);
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Class.class.getMethod("forName", String.class).setAccessible(true);
                                            Method method83 = Class.class.getMethod("getMethod", String.class, Class[].class);
                                            method83.setAccessible(true);
                                            Method method84 = (Method) method83.invoke(String.class, "equals", new Class[]{Object.class});
                                            Method method85 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method85.setAccessible(true);
                                            method85.invoke(method84, true);
                                            StringBuilder.class.getConstructor(String.class).setAccessible(true);
                                            StringBuilder.class.getMethod("reverse", null).setAccessible(true);
                                            Object.class.getMethod("toString", null).setAccessible(true);
                                            Method method86 = Method.class.getMethod("invoke", Object.class, Object[].class);
                                            method86.setAccessible(true);
                                            Method method87 = Boolean.class.getMethod("booleanValue", null);
                                            method87.setAccessible(true);
                                            ((Boolean) method87.invoke((Boolean) method86.invoke(method84, "4680", new Object[]{str2}), new Object[0])).booleanValue();
                                            return (jLongValue % 1000000) + 124750 == j;
                                        } catch (InvocationTargetException e15) {
                                            try {
                                                Method method88 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                                method88.setAccessible(true);
                                                throw ((Throwable) method88.invoke(e15, new Object[0]));
                                            } catch (InvocationTargetException e16) {
                                                throw e16.getTargetException();
                                            }
                                        }
                                    } catch (InvocationTargetException e17) {
                                        try {
                                            Method method89 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                            method89.setAccessible(true);
                                            throw ((Throwable) method89.invoke(e17, new Object[0]));
                                        } catch (InvocationTargetException e18) {
                                            throw e18.getTargetException();
                                        }
                                    }
                                } catch (InvocationTargetException e19) {
                                    try {
                                        Method method90 = Class.forName("java.lang.reflect.InvocationTargetException").getMethod("getTargetException", null);
                                        method90.setAccessible(true);
                                        throw ((Throwable) method90.invoke(e19, new Object[0]));
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