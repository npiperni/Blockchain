import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Miner {

    private Queue<Transaction> pool;

    public Miner() {
        this.pool = new LinkedList<>();
    }
    public void mine(Blockchain blockChain) {
        while (!pool.isEmpty()) {
            Block block = createBlock(blockChain);
            System.out.println("Mining block with index " + block.getIndex() + "...");
            proofOfWork(block, blockChain.getDifficulty());
            System.out.println("Block mined with hash: " + HashUtils.toHex(block.getHash()));
            blockChain.addToChain(block);
        }
    }

    private Block createBlock(Blockchain blockChain) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        while (!pool.isEmpty()) {
            transactions.add(pool.remove());
        }
        return new Block(blockChain.getChainSize(), transactions, blockChain.getLastBlockHash());
    }

    private void proofOfWork(Block block, int difficulty) {
        var sha256 = HashUtils.getMessageDigest();
        sha256.update(block.getAsByteArray());
        block.setHash(sha256.digest());
        while (HashUtils.toInteger(block.getHash()).compareTo(BigDecimal.valueOf(Math.pow(16, 64 - difficulty)).toBigInteger()) > 0) {
            block.incrementNonce();
            sha256.reset();
            sha256.update(block.getAsByteArray());
            block.setHash(sha256.digest());
        }
    }

    public void addToPool(Transaction... transactions) {
        pool.addAll(Arrays.asList(transactions));
    }

}
