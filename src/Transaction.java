import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {

    public static class Output {
        private String address; // The address of the recipient
        private long amount; // The amount of cryptocurrency in this UTXO

        public Output(String address, long amount) {
            this.address = address;
            this.amount = amount;
        }

        public String getAddress() {
            return address;
        }

        public long getAmount() {
            return amount;
        }
    }

    private String transactionId;
    private List<UTXO> inputs = new ArrayList<>();
    private List<Output> outputs = new ArrayList<>();
    private byte[] signature;

    // For coinbase/genesis transactions
    public Transaction(String receiver, long amount) {
        this.transactionId = UUID.randomUUID().toString();
        this.outputs.add(new Output(receiver, amount));
    }

    // For normal transactions
    public Transaction(List<UTXO> inputs, List<Output> outputs) {
        this.transactionId = UUID.randomUUID().toString();
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public List<UTXO> getInputs() {
        return inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void signTransaction(Wallet senderWallet) {
        if (senderWallet.getPublicKey() == null) {
            throw new IllegalStateException("No public key found for sender");
        }
        StringBuilder data = new StringBuilder();
        for (UTXO in : inputs) {
            data.append(in.getTransactionId()).append(in.getOutputIndex());
        }
        for (Output out : outputs) {
            data.append(out.getAddress()).append(out.getAmount());
        }
        this.signature = senderWallet.signData(data.toString().getBytes());
    }

    public boolean verifyTransaction(PublicKey senderPublicKey) {
        if (signature == null) {
            return false;
        }
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(senderPublicKey);
            StringBuilder data = new StringBuilder();
            for (UTXO in : inputs) {
                data.append(in.getTransactionId()).append(in.getOutputIndex());
            }
            for (Output out : outputs) {
                data.append(out.getAddress()).append(out.getAmount());
            }
            sig.update(data.toString().getBytes());
            return sig.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(transactionId).append("\n");
        sb.append("Inputs:\n");
        for (UTXO in : inputs) {
            sb.append("  ").append(in.getTransactionId()).append(":").append(in.getOutputIndex()).append("\n");
        }
        sb.append("Outputs:\n");
        for (Output out : outputs) {
            sb.append("  ").append(out.getAddress()).append(" -> ").append(out.getAmount()).append("\n");
        }
        return sb.toString();
    }
}
