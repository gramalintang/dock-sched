package com.docksched.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash password dengan PBKDF2-HMAC-SHA256 (pustaka bawaan JDK); password tidak disimpan sebagai teks biasa (DS-NF-06).
 * Format tersimpan: pbkdf2$iterasi$salt(base64)$hash(base64)
 */
public final class PasswordUtil {

    private static final int ITERASI = 120_000;
    private static final int PANJANG_KUNCI_BIT = 256;
    private static final int PANJANG_SALT = 16;
    private static final SecureRandom ACAK = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(char[] password) {
        byte[] salt = new byte[PANJANG_SALT];
        ACAK.nextBytes(salt);
        byte[] h = pbkdf2(password, salt, ITERASI);
        return "pbkdf2$" + ITERASI + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(h);
    }

    public static boolean verify(char[] password, String tersimpan) {
        if (password == null || tersimpan == null) {
            return false;
        }
        try {
            String[] bagian = tersimpan.split("\\$");
            if (bagian.length != 4 || !"pbkdf2".equals(bagian[0])) {
                return false;
            }
            int iterasi = Integer.parseInt(bagian[1]);
            byte[] salt = Base64.getDecoder().decode(bagian[2]);
            byte[] diharapkan = Base64.getDecoder().decode(bagian[3]);
            return MessageDigest.isEqual(diharapkan, pbkdf2(password, salt, iterasi));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterasi) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterasi, PANJANG_KUNCI_BIT);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritma PBKDF2 tidak tersedia di JDK ini", e);
        } finally {
            spec.clearPassword();
        }
    }
}
