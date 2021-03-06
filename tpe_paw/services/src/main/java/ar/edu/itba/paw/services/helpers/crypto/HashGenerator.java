package ar.edu.itba.paw.services.helpers.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class HashGenerator {

    private HashGenerator() {
    }

    public static String generateRecoveryToken(String code){
        return process(code.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateSecretKey(String email, String pass){
        return process((email + pass).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a base 64 representation of the SHA-256 encryption of data
     * @param data Data bytes to be processed
     * @return Base64 url-safe encoding
     */
    private static String process(byte[] data){
        try {
            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            byte[] userPassHash = sha256Digest.digest(data);
            return new String(Base64.getUrlEncoder().encode(userPassHash));
        } catch (NoSuchAlgorithmException e) {
            // Error, return null
        }
        return null;
    }
}
