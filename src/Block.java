import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Block {

    private final int index;
    private byte[] hash;
    private final byte[] previousHash;
    private int nonce;
    private final List<Transaction> transactions;
    private final long timestamp;

    public Block(int index, List<Transaction> transactions, byte[] previousHash) {
        this.index = index;
        this.hash = HashUtils.getMessageDigest().digest();
        this.previousHash = previousHash;
        this.nonce = 0;
        this.transactions = transactions;
        this.timestamp = new Date().getTime();
    }

    public byte[] getAsByteArray() {
        return (HashUtils.toHex(previousHash) + transactions + String.valueOf(timestamp) + String.valueOf(nonce)).getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getHash() {
        return Arrays.copyOf(hash, hash.length);
    }

    public void setHash(byte[] hash) {
        this.hash = Arrays.copyOf(hash, hash.length);
    }

    public int getIndex() {
        return index;
    }

    public void incrementNonce() {
        nonce++;
    }

    public byte[] getPreviousHash() {
        return Arrays.copyOf(previousHash, previousHash.length);
    }

    @Override
    public String toString() {
        StringBuilder transactions = new StringBuilder(100);
        for (Transaction t : this.transactions) {
            transactions.append(t).append("\n");
        }
        return "Index: " + index + "\n" +
                "Hash: " + HashUtils.toHex(hash) + "\n" +
                "Previous Hash: " + HashUtils.toHex(previousHash) + "\n" +
                "Nonce: " + nonce + "\n" +
                "Transactions:\n" + transactions +
                "Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date(timestamp));
    }

}
