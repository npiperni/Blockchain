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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    // Generate a wallet address from the public key
    public String getAddress() {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] pubKeyHash = sha256.digest(publicKey.getEncoded());

             MessageDigest ripeMD160 = MessageDigest.getInstance("RIPEMD160");
             byte[] addressBytes = ripeMD160.digest(pubKeyHash);

            return Base64.getEncoder().encodeToString(addressBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
