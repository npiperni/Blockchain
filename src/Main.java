import java.util.*;

public class Main {

    // A real blockchain node wouldn't store wallets in memory like this, but for simplicity I want to allow users to create wallets using the same interface
    private static final Map<String, Wallet> wallets = new HashMap<>();

    public static void main(String[] args) {

        Blockchain chain = new Blockchain(3);

        System.out.println("----------------------Welcome-to-Blockchain-Explorer----------------------");


        boolean running = true;
        while (running) {
            System.out.println("1: View Blockchain");
            System.out.println("2: Create Wallet");
            System.out.println("3: Send Money");
            System.out.println("4: Change Difficulty");
            System.out.println("5: Exit");
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
                    sendMoney(chain);
                    break;
                case "4":
                    changeDifficulty(chain);
                    break;
                case "5":
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

        Transaction tx = new Transaction(senderAddress, receiver, amount);
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
            miner.mine(blockchain);
        } else {
            System.out.println("Operation Cancelled");
        }
    }
}
