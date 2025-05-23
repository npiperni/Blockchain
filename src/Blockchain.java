import java.math.BigDecimal;
import java.util.*;

public class Blockchain {

    private int difficulty;
    private final LinkedList<Block> chain;
    private static final Map<UTXO, Transaction.Output> utxoSet = new HashMap<>();

    public Blockchain(int difficulty, String genesisAddress) {
        System.out.println("Initializing Blockchain...");
        this.difficulty = difficulty;
        this.chain = new LinkedList<>();
        createFirstBlock(genesisAddress);
    }

    private boolean validate(Block block) {
        var sha256 = HashUtils.getMessageDigest();
        sha256.update(block.getAsByteArray());
        byte[] hash = sha256.digest();
        return Arrays.equals(block.getHash(), hash) &&
                HashUtils.toInteger(hash).compareTo(
                        BigDecimal.valueOf(Math.pow(16, 64 - difficulty)).toBigInteger()) < 0 &&
                Arrays.equals(block.getPreviousHash(), getLastBlockHash());
    }

    public void addToChain(Block block) {
        if (validate(block)) {
            chain.add(block);
        } else {
            throw new RuntimeException("Block Rejected");
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getChainSize() {
        return chain.size();
    }

    public byte[] getLastBlockHash() {
        if (chain.isEmpty()) {
            return HexFormat.of().parseHex("0000000000000000000000000000000000000000000000000000000000000000");
        }
        return chain.getLast().getHash();
    }

    public void displayChain() {
        if (chain.isEmpty()) {
            System.out.println("There are no blocks in this chain");
            return;
        }
        for (Block block: chain) {
            System.out.println("==================================================================================");
            System.out.println(block);
        }
        System.out.println("==================================================================================");
    }

    private void createFirstBlock(String genesisAddress) {
        System.out.println("Creating Genesis Block...");
        Miner miner = new Miner();
        miner.mine(this, genesisAddress);
    }

    public Map<UTXO, Transaction.Output> getUTXOs(String address) {
        Map<UTXO, Transaction.Output> result = new HashMap<>();
        for (Map.Entry<UTXO, Transaction.Output> entry : utxoSet.entrySet()) {
            Transaction.Output output = entry.getValue();
            if (output.getAddress().equals(address)) {
                result.put(entry.getKey(), output);
            }
        }
        return result;
    }

    public void updateUTXOs(Transaction... transactions) {
        for (Transaction transaction : transactions) {
            for (UTXO input : transaction.getInputs()) {
                utxoSet.remove(input);
            }
            for (int i = 0; i < transaction.getOutputs().size(); i++) {
                utxoSet.put(new UTXO(transaction.getTransactionId(), i), transaction.getOutputs().get(i));
            }
        }
    }

}
