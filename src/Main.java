import java.util.*;

public class Main {

    // A real blockchain node wouldn't store wallets in memory like this, but for simplicity I want to allow users to create wallets using the same interface
    private static final Map<String, Wallet> wallets = new HashMap<>();
    private static Wallet minerWallet = new Wallet();

    public static void main(String[] args) {
        wallets.put(minerWallet.getAddress(), minerWallet);

        Blockchain chain = new Blockchain(3, minerWallet.getAddress());

        System.out.println("----------------------Welcome-to-Blockchain-Explorer----------------------");

        System.out.println("Your miner wallet address is: " + minerWallet.getAddress());
        System.out.println("You can set a different wallet for mining in the options menu");

        boolean running = true;
        while (running) {
            System.out.println("1: View Blockchain");
            System.out.println("2: Create Wallet");
            System.out.println("3: View Wallet Balance");
            System.out.println("4: Connect Mining Wallet");
            System.out.println("5: Send Money");
            System.out.println("6: Change Mining Difficulty");
            System.out.println("7: Exit");
            System.out.print("Enter option: ");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.next()) {
                case "1":
                    chain.displayChain();
                    break;
                case "2":
                    createWallet();
                    break;
                case "3":
                    viewWalletBalance(chain);
                    break;
                case "4":
                    connectMiningWallet();
                    break;
                case "5":
                    sendMoney(chain);
                    break;
                case "6":
                    changeDifficulty(chain);
                    break;
                case "7":
                    running = false;
                    break;
                default:
                    System.out.print("\nBad input, try again\n");
            }
            System.out.println();
        }

    }

    private static void createWallet() {
        Wallet wallet = new Wallet();
        String address = wallet.getAddress();
        wallets.put(address, wallet);
        System.out.println("Wallet created with address: " + address);
    }

    private static void connectMiningWallet() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter wallet address: ");
        String address = scanner.nextLine();
        Wallet wallet = wallets.get(address);
        if (wallet == null) {
            System.out.println("Wallet not found. You must own this wallet to set it as the mining wallet.");
            return;
        }
        minerWallet = wallet;
        System.out.println("Mining wallet set to: " + address);
        System.out.println("This wallet will be used for mining rewards.");
    }

    private static void viewWalletBalance(Blockchain blockchain) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter wallet address: ");
        String address = scanner.nextLine();
        Wallet wallet = wallets.get(address);
        if (wallet == null) {
            System.out.println("Wallet not found.");
            return;
        }
        long balance = 0;
        for (Map.Entry<UTXO, Transaction.Output> entry : blockchain.getUTXOs(address).entrySet()) {
            balance += entry.getValue().getAmount();
        }
        System.out.println("Balance: " + balance);
    }

    private static void changeDifficulty(Blockchain blockchain) {
        System.out.println("The difficulty represents the number of leading zeroes needed for a block's hash to be considered valid");
        System.out.println("A higher difficulty requires more processing power to mine blocks");
        System.out.println("The current difficulty is: " + blockchain.getDifficulty());

        System.out.print("Enter new difficultly: ");
        int num;
        do {
            try {
                num = new Scanner(System.in).nextInt();
            } catch (InputMismatchException e) {
                num = 0;
            }
            if (num < 1) {
                System.out.print("Please enter a number bigger than 0: ");
            }
        } while (num < 1);
        blockchain.setDifficulty(num);
        System.out.println("Difficulty set to " + blockchain.getDifficulty());
    }

    private static void sendMoney(Blockchain blockchain) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your wallet address: ");
        String senderAddress = scanner.nextLine();
        Wallet senderWallet = wallets.get(senderAddress);
        if (senderWallet == null) {
            System.out.println("Wallet not found. You must own this wallet to send money.");
            return;
        }

        System.out.print("Enter receiver address: ");
        String receiver = scanner.nextLine();
        System.out.print("Enter amount: ");
        long amount;
        try {
            amount = scanner.nextLong();
            if (amount <= 0) throw new InputMismatchException();
        } catch (InputMismatchException e) {
            System.out.println("Invalid amount.");
            return;
        }

        // Step 1: Find enough UTXOs for sender
        List<UTXO> inputs = new ArrayList<>();
        long totalCollected = 0;

        for (Map.Entry<UTXO, Transaction.Output> entry : blockchain.getUTXOs(senderAddress).entrySet()) {
            UTXO utxo = entry.getKey();
            Transaction.Output output = entry.getValue();

            inputs.add(new UTXO(utxo.getTransactionId(), utxo.getOutputIndex()));
            totalCollected += output.getAmount();
            if (totalCollected >= amount) break;
        }


        if (totalCollected < amount) {
            System.out.println("Insufficient funds.");
            return;
        }

        // Step 2: Create outputs
        List<Transaction.Output> outputs = new ArrayList<>();
        outputs.add(new Transaction.Output(receiver, amount)); // Send to receiver

        long change = totalCollected - amount;
        if (change > 0) {
            outputs.add(new Transaction.Output(senderAddress, change)); // Send change back
        }

        // Step 3: Create, sign, and verify transaction
        Transaction tx = new Transaction(inputs, outputs);
        tx.signTransaction(senderWallet);

        System.out.print("Confirm and mine block(s)?[y/n]: ");
        if (new Scanner(System.in).next().equals("y")) {
            // Pretend this is when the transaction reaches the network and needs to be verified
            if (!tx.verifyTransaction(senderWallet.getPublicKey())) {
                System.out.println("Transaction verification failed. Invalid signature.");
                return;
            }
            Miner miner = new Miner();
            miner.addToPool(tx);
            miner.mine(blockchain, minerWallet.getAddress());

            // Step 4: Update UTXO set manually (since Miner doesn't do it)
            blockchain.updateUTXOs(tx);
        } else {
            System.out.println("Operation Cancelled");
        }
    }

}
