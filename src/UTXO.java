import java.util.Objects;

public class UTXO {
    private final String transactionId; // The ID of the transaction that created this UTXO
    private final int outputIndex; // The index of the output in the transaction

    public UTXO(String transactionId, int outputIndex) {
        this.transactionId = transactionId;
        this.outputIndex = outputIndex;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getOutputIndex() {
        return outputIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UTXO utxo = (UTXO) o;
        return outputIndex == utxo.outputIndex &&
                transactionId.equals(utxo.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, outputIndex);
    }

    @Override
    public String toString() {
        return transactionId + ":" + outputIndex;
    }
}
