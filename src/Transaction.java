import java.security.PublicKey;
import java.security.Signature;
import java.util.UUID;

public class Transaction {
    private String transactionId;
    private String sender;
    private String receiver;
    private long amount;
    private byte[] signature;

    public Transaction(String sender, String receiver, long amount) {
        this.transactionId = UUID.randomUUID().toString(); // Generate a unique ID
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public void signTransaction(Wallet senderWallet) {
        if (senderWallet.getPublicKey() == null) {
            throw new IllegalStateException("No public key found for sender");
        }
        String data = sender + receiver + amount;
        this.signature = senderWallet.signData(data.getBytes());
    }

    public boolean verifyTransaction(PublicKey senderPublicKey) {
        if (signature == null || sender == null || receiver == null) {
            return false;
        }
        // Verify the signature using the sender's public key
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(senderPublicKey);
            sig.update((sender + receiver + amount).getBytes());
            return sig.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ID: " + transactionId + "\n" +
                "Sender: " + sender + "\n" +
                "Receiver: " + receiver + "\n" +
                "Amount: " + amount;
    }
}
