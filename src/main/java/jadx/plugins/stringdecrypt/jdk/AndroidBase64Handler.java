package jadx.plugins.stringdecrypt.jdk;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.jetbrains.annotations.Nullable;

import jadx.core.dex.info.MethodInfo;

/**
 * Folds {@code android.util.Base64} on constant inputs. Android's {@code Base64} is part of the
 * framework, not the host JDK, so — unlike {@link CipherHandler} and {@link JavaUtilBase64Handler} —
 * it cannot be reflected; this handler re-implements the flag semantics on top of
 * {@link java.util.Base64}. That matters because the common Android string obfuscator hides a literal
 * as {@code new String(cipher.doFinal(Base64.decode(s, 0)), UTF_8)}: without a {@code Base64} fold the
 * interpreter refuses at the very first call and the whole helper stays opaque.
 *
 * <p>
 * Flag bits mirror {@code android.util.Base64}: {@code NO_PADDING=1}, {@code NO_WRAP=2},
 * {@code CRLF=4}, {@code URL_SAFE=8}, {@code NO_CLOSE=16}. Decoding is deliberately lenient (matching
 * the framework): any non-alphabet character — whitespace, the {@code DEFAULT} line wrapping, stray
 * bytes — is ignored, padding is recomputed from length, and {@code URL_SAFE} maps {@code -_} back to
 * {@code +/}. Encoding is folded only for {@code NO_WRAP}; the wrapped forms are refused rather than
 * risk emitting a literal whose line breaks differ from the framework's.
 */
public final class AndroidBase64Handler implements JdkClassHandler {

	private static final int NO_PADDING = 1;
	private static final int NO_WRAP = 2;
	private static final int URL_SAFE = 8;

	@Override
	public String targetClass() {
		return "android.util.Base64";
	}

	@Override
	public @Nullable Object invoke(MethodInfo call, @Nullable Object instance, Object[] args) {
		try {
			switch (call.getName()) {
				case "decode":
					return decode(args);
				case "encode":
					return encode(args);
				case "encodeToString":
					byte[] enc = encode(args);
					return enc == null ? null : new String(enc, StandardCharsets.ISO_8859_1);
				default:
					return null;
			}
		} catch (Throwable t) {
			return null; // any malformed input -> refuse (call left as-is), same contract as the reflective base
		}
	}

	/** {@code decode(String,int)} / {@code decode(byte[],int)} / {@code decode(byte[],int,int,int)}. */
	private static @Nullable byte[] decode(Object[] args) {
		if (args.length == 2) {
			String s = toAsciiString(args[0]);
			Integer flags = intArg(args[1]);
			if (s == null || flags == null) {
				return null;
			}
			return decode(s, flags);
		}
		if (args.length == 4) {
			byte[] in = asByteArray(args[0]);
			Integer off = intArg(args[1]);
			Integer len = intArg(args[2]);
			Integer flags = intArg(args[3]);
			if (in == null || off == null || len == null || flags == null) {
				return null;
			}
			if (off < 0 || len < 0 || off + len > in.length) {
				return null;
			}
			return decode(new String(in, off, len, StandardCharsets.ISO_8859_1), flags);
		}
		return null;
	}

	private static @Nullable byte[] decode(String s, int flags) {
		boolean urlSafe = (flags & URL_SAFE) != 0;
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (urlSafe) {
				if (c == '-') {
					c = '+';
				} else if (c == '_') {
					c = '/';
				}
			}
			if (isBase64Alphabet(c)) {
				sb.append(c);
			}
			// any other char (whitespace, line wrap, padding handled below) is ignored, as Android does
		}
		String body = sb.toString().replace("=", "");
		int mod = body.length() % 4;
		if (mod == 1) {
			return null; // not a valid base64 length: Android would throw -> refuse
		}
		StringBuilder padded = new StringBuilder(body);
		for (int i = mod; i != 0 && i < 4; i++) {
			padded.append('=');
		}
		return Base64.getDecoder().decode(padded.toString());
	}

	/** {@code encode(byte[],int)} / {@code encode(byte[],int,int,int)} — folded only for NO_WRAP. */
	private static @Nullable byte[] encode(Object[] args) {
		byte[] in;
		int flags;
		if (args.length == 2) {
			in = asByteArray(args[0]);
			Integer f = intArg(args[1]);
			if (in == null || f == null) {
				return null;
			}
			flags = f;
		} else if (args.length == 4) {
			byte[] full = asByteArray(args[0]);
			Integer off = intArg(args[1]);
			Integer len = intArg(args[2]);
			Integer f = intArg(args[3]);
			if (full == null || off == null || len == null || f == null) {
				return null;
			}
			if (off < 0 || len < 0 || off + len > full.length) {
				return null;
			}
			in = new byte[len];
			System.arraycopy(full, off, in, 0, len);
			flags = f;
		} else {
			return null;
		}
		if ((flags & NO_WRAP) == 0) {
			return null; // refuse the line-wrapped forms rather than risk a mis-wrapped literal
		}
		Base64.Encoder enc = (flags & URL_SAFE) != 0 ? Base64.getUrlEncoder() : Base64.getEncoder();
		if ((flags & NO_PADDING) != 0) {
			enc = enc.withoutPadding();
		}
		return enc.encode(in);
	}

	private static boolean isBase64Alphabet(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
				|| c == '+' || c == '/' || c == '=';
	}

	private static @Nullable String toAsciiString(Object v) {
		if (v instanceof String) {
			return (String) v;
		}
		byte[] b = asByteArray(v);
		return b == null ? null : new String(b, StandardCharsets.ISO_8859_1);
	}

	private static byte @Nullable [] asByteArray(Object v) {
		return v instanceof byte[] ? (byte[]) v : null;
	}

	private static @Nullable Integer intArg(Object v) {
		return v instanceof Number ? ((Number) v).intValue() : null;
	}
}
