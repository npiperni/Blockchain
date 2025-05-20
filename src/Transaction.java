import java.util.UUID;

public class Transaction {
    private String transactionId;
    private String sender;
    private String receiver;
    private long amount;

    public Transaction(String sender, String receiver, long amount) {
        this.transactionId = UUID.randomUUID().toString(); // Generate a unique ID
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "ID: " + transactionId + "\n" +
                "Sender: " + sender + "\n" +
                "Receiver: " + receiver + "\n" +
                "Amount: " + amount;
    }
}
