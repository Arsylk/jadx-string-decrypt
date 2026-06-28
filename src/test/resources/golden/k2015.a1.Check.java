package k2015.a1;

import java.io.PrintStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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
            char[] charArray = str.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                charArray[i] = (char) (charArray[i] ^ 'g');
            }
            try {
                Constructor<?> constructor = String.class.getConstructor(char[].class);
                constructor.setAccessible(true);
                return (String) constructor.newInstance(charArray);
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
        BigDecimal bigDecimalValueOf;
        BigDecimal bigDecimalValueOf2;
        String str2;
        String str3;
        try {
            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
            Field field = MathContext.class.getField("DECIMAL64");
            try {
                Method method = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(field, true);
                try {
                    MathContext mathContext = (MathContext) field.get(null);
                    try {
                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                        Method method2 = Integer.class.getMethod("parseInt", (Class[]) new Class[]{String.class});
                        Method method3 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                        method3.setAccessible(true);
                        method3.invoke(method2, true);
                        jIntValue = Integer.valueOf((int) Integer.valueOf((int) Integer.parseInt(str)).intValue()).intValue();
                        try {
                            Method method4 = BigDecimal.class.getMethod("valueOf", (Class[]) new Class[]{long.class, int.class});
                            Method method5 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                            method5.setAccessible(true);
                            method5.invoke(method4, true);
                            bigDecimalValueOf = BigDecimal.valueOf(0L, 0);
                            try {
                                Method method6 = BigDecimal.class.getMethod("valueOf", (Class[]) new Class[]{long.class, int.class});
                                Method method7 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                method7.setAccessible(true);
                                method7.invoke(method6, true);
                                bigDecimalValueOf2 = BigDecimal.valueOf(4L, 0);
                            } catch (InvocationTargetException e) {
                                try {
                                    throw e.getTargetException();
                                } catch (InvocationTargetException e2) {
                                    throw e2.getTargetException();
                                }
                            }
                        } catch (InvocationTargetException e3) {
                            try {
                                throw e3.getTargetException();
                            } catch (InvocationTargetException e4) {
                                throw e4.getTargetException();
                            }
                        }
                    } catch (InvocationTargetException e5) {
                        try {
                            throw e5.getTargetException();
                        } catch (InvocationTargetException e6) {
                            throw e6.getTargetException();
                        }
                    }
                    try {
                        Field field2 = System.class.getField("out");
                        try {
                            Method method8 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                            method8.setAccessible(true);
                            method8.invoke(field2, true);
                            try {
                                if (((PrintStream) field2.get(null)) == null) {
                                    try {
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method9 = String.class.getMethod("equals", (Class[]) new Class[]{Object.class});
                                        Method method10 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method10.setAccessible(true);
                                        method10.invoke(method9, true);
                                        return Boolean.valueOf((boolean) Boolean.valueOf((boolean) "9527".equals(str)).booleanValue()).booleanValue();
                                    } catch (InvocationTargetException e7) {
                                        try {
                                            throw e7.getTargetException();
                                        } catch (InvocationTargetException e8) {
                                            throw e8.getTargetException();
                                        }
                                    }
                                }
                                String string = null;
                                int i = 1;
                                BigDecimal bigDecimalAdd = bigDecimalValueOf;
                                String str4 = str;
                                long j = jIntValue;
                                while (true) {
                                    int i2 = i;
                                    str2 = string;
                                    if (i2 >= 1001) {
                                        break;
                                    }
                                    if (i2 % 1 == 0) {
                                        try {
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            Method method11 = String.class.getMethod("toString", (Class[]) null);
                                            Method method12 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method12.setAccessible(true);
                                            method12.invoke(method11, true);
                                            str3 = str4;
                                            string = str4.toString();
                                        } catch (InvocationTargetException e9) {
                                            try {
                                                throw e9.getTargetException();
                                            } catch (InvocationTargetException e10) {
                                                throw e10.getTargetException();
                                            }
                                        }
                                    } else {
                                        str3 = str2;
                                        string = str2;
                                    }
                                    long j2 = j + i2;
                                    try {
                                        Method method13 = BigDecimal.class.getMethod("valueOf", (Class[]) new Class[]{long.class, int.class});
                                        Method method14 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method14.setAccessible(true);
                                        method14.invoke(method13, true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method15 = BigDecimal.class.getMethod("divide", (Class[]) new Class[]{BigDecimal.class, MathContext.class});
                                        Method method16 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method16.setAccessible(true);
                                        method16.invoke(method15, true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method17 = BigDecimal.class.getMethod("add", (Class[]) new Class[]{BigDecimal.class, MathContext.class});
                                        Method method18 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method18.setAccessible(true);
                                        method18.invoke(method17, true);
                                        Method method19 = BigDecimal.class.getMethod("valueOf", (Class[]) new Class[]{long.class, int.class});
                                        Method method20 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method20.setAccessible(true);
                                        method20.invoke(method19, true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method21 = BigDecimal.class.getMethod("divide", (Class[]) new Class[]{BigDecimal.class, MathContext.class});
                                        Method method22 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method22.setAccessible(true);
                                        method22.invoke(method21, true);
                                        Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                        Method method23 = BigDecimal.class.getMethod("add", (Class[]) new Class[]{BigDecimal.class, MathContext.class});
                                        Method method24 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method24.setAccessible(true);
                                        method24.invoke(method23, true);
                                        i = i2 + 4;
                                        bigDecimalAdd = bigDecimalAdd.add(bigDecimalValueOf2.divide((BigDecimal) BigDecimal.valueOf(i2, 0), mathContext), mathContext).add(bigDecimalValueOf2.divide((BigDecimal) BigDecimal.valueOf(-(i2 + 2), 0), mathContext), mathContext);
                                        str4 = str3;
                                        j = j2;
                                    } catch (InvocationTargetException e11) {
                                        try {
                                            throw e11.getTargetException();
                                        } catch (InvocationTargetException e12) {
                                            throw e12.getTargetException();
                                        }
                                    }
                                }
                                try {
                                    Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                    Method method25 = String.class.getMethod("hashCode", (Class[]) null);
                                    Method method26 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                    method26.setAccessible(true);
                                    method26.invoke(method25, true);
                                    if ((Integer.valueOf((int) Integer.valueOf((int) str4.hashCode()).intValue()).intValue() & 15) < 0) {
                                        if (jIntValue > 8) {
                                            str2 = str4;
                                        }
                                        try {
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            Method method27 = String.class.getMethod("equals", (Class[]) new Class[]{Object.class});
                                            Method method28 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method28.setAccessible(true);
                                            method28.invoke(method27, true);
                                            return Boolean.valueOf((boolean) Boolean.valueOf((boolean) "1290".equals(str2)).booleanValue()).booleanValue();
                                        } catch (InvocationTargetException e13) {
                                            try {
                                                throw e13.getTargetException();
                                            } catch (InvocationTargetException e14) {
                                                throw e14.getTargetException();
                                            }
                                        }
                                    }
                                    try {
                                        Method method29 = BigDecimal.class.getMethod("scaleByPowerOfTen", (Class[]) new Class[]{int.class});
                                        Method method30 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method30.setAccessible(true);
                                        method30.invoke(method29, true);
                                        Method method31 = BigDecimal.class.getMethod("longValue", (Class[]) null);
                                        Method method32 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                        method32.setAccessible(true);
                                        method32.invoke(method31, true);
                                        long jLongValue = Long.valueOf((long) Long.valueOf((long) bigDecimalAdd.scaleByPowerOfTen((int) (7 + ((((1 + j) * j) * (2 + j)) % 6))).longValue()).longValue()).longValue();
                                        try {
                                            Class.forName("k2015.a1.Check").getDeclaredMethod("access$_T15566", Object.class, String.class).setAccessible(true);
                                            Method method33 = String.class.getMethod("equals", (Class[]) new Class[]{Object.class});
                                            Method method34 = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
                                            method34.setAccessible(true);
                                            method34.invoke(method33, true);
                                            Boolean.valueOf((boolean) Boolean.valueOf((boolean) "4680".equals(str2)).booleanValue()).booleanValue();
                                            return (jLongValue % 1000000) + 124750 == j;
                                        } catch (InvocationTargetException e15) {
                                            try {
                                                throw e15.getTargetException();
                                            } catch (InvocationTargetException e16) {
                                                throw e16.getTargetException();
                                            }
                                        }
                                    } catch (InvocationTargetException e17) {
                                        try {
                                            throw e17.getTargetException();
                                        } catch (InvocationTargetException e18) {
                                            throw e18.getTargetException();
                                        }
                                    }
                                } catch (InvocationTargetException e19) {
                                    try {
                                        throw e19.getTargetException();
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