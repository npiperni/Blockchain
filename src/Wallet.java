import java.security.*;
import java.util.Base64;

public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Wallet() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] signData(byte[] data) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generate a wallet address from the public key
    public String getAddress() {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] pubKeyHash = sha256.digest(publicKey.getEncoded());

            return Base64.getEncoder().encodeToString(pubKeyHash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
