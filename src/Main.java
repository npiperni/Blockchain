import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Blockchain chain = new Blockchain(3);


        System.out.println("----------------------Welcome-to-Blockchain-Explorer----------------------");

        boolean running = true;
        while (running) {
            System.out.println("1: View Blockchain");
            System.out.println("2: Add Block(s)");
            System.out.println("3: Change Difficulty");
            System.out.println("4: Exit");
            System.out.print("Enter option: ");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.next()) {
                case "1":
                    chain.displayChain();
                    break;
                case "2":
                    addBlocks(chain);
                    break;
                case "3":
                    changeDifficulty(chain);
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.print("\nBad input, try again\n");
            }
            System.out.println();
        }

    }

    private static void changeDifficulty(Blockchain blockchain) {
        System.out.println("The difficulty represents the number of leading zeroes needed for a block's hash to be considered valid");
        System.out.println("A higher difficulty requires more processing power to mine blocks");
        System.out.println(STR."The current difficulty is: \{blockchain.getDifficulty()}");

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
        System.out.println(STR."Difficulty set to \{blockchain.getDifficulty()}");
    }

    private static void addBlocks(Blockchain blockchain) {
        System.out.println("How many blocks do you want to add?");
        int num;
        do {
            System.out.print("Number of blocks: ");
            try {
                num = new Scanner(System.in).nextInt();
            } catch (InputMismatchException e) {
                num = 0;
            }
            if (num < 1) {
                System.out.println("Enter a valid positive number");
            }
        } while (num < 1);

        String[] data = new String[num];
        for (int i = 1; i <= num; i++) {
            System.out.print(STR."Enter text data for block \{i}: ");
            data[i-1] = new Scanner(System.in).nextLine();
        }

        System.out.print("Confirm and mine block(s)?[y/n]: ");
        if (new Scanner(System.in).next().equals("y")) {
            Miner miner = new Miner();
            miner.addToPool(data);
            miner.mine(blockchain);
        } else {
            System.out.println("Operation Cancelled");
        }
    }
}
