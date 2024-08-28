import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    protected static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected static String toHex(byte[] bytes) {
        BigInteger i = toInteger(bytes);
        return String.format("%0" + (bytes.length << 1) + "X", i);
    }

    protected static BigInteger toInteger(byte[] bytes) {
        return new BigInteger(1, bytes);
    }
}

