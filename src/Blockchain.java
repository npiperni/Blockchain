import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedList;

public class Blockchain {

    private int difficulty;
    private LinkedList<Block> chain;

    public Blockchain(int difficulty) {
        System.out.println("Initializing Blockchain...");
        this.difficulty = difficulty;
        this.chain = new LinkedList<>();
        createFirstBlock();
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

    private void createFirstBlock() {
        System.out.println("Creating Genesis Block...");
        Miner miner = new Miner();
        miner.addToPool("Genesis Block");
        miner.mine(this);
    }

}
