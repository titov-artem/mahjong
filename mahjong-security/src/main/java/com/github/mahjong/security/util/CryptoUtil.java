package com.github.mahjong.security.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtil {

    private static final String ALGORITHM = "Blowfish";

    public static int getMaxKeyLengthBytes() throws GeneralSecurityException {
        // TODO this not working on Ubuntu. It returns just 2147483640 / 8
        // return Cipher.getMaxAllowedKeyLength(ALGORITHM) / 8;
        return 16;
    }

    public static String encryptString(final String source, final String passphrase) throws GeneralSecurityException {
        byte[] encryptedName = encrypt(source.getBytes(), passphrase);
        byte[] encodedBytes = Base64.getEncoder().encode(encryptedName);
        return new String(encodedBytes);
    }

    public static String decryptString(String source, String passphrase) throws GeneralSecurityException {
        byte[] decodedBytes = Base64.getDecoder().decode(source.getBytes());
        byte[] decryptedName = decrypt(decodedBytes, passphrase);
        return new String(decryptedName);
    }

    private static byte[] encrypt(final byte[] input, final String passphrase) throws GeneralSecurityException {
        return applyCryptAction(input, passphrase, Cipher.ENCRYPT_MODE);
    }

    private static byte[] decrypt(final byte[] input, final String passphrase) throws GeneralSecurityException {
        return applyCryptAction(input, passphrase, Cipher.DECRYPT_MODE);
    }

    private static byte[] applyCryptAction(byte[] input, String passphrase, int mode) throws GeneralSecurityException {
        final SecretKey key = new SecretKeySpec(passphrase.getBytes(), ALGORITHM);
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key);
        return cipher.doFinal(input);
    }

}
