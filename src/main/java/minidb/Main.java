package minidb;

import minidb.database.Database;
import minidb.parser.Parser;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Database database = new Database("minidb");
        Parser parser = new Parser(database);
        Scanner scanner = new Scanner(System.in);

        System.out.println("MiniDB started. Type EXIT to quit.");

        while (true) {
            System.out.print("minidb> ");
            String line = scanner.nextLine();
            if (line == null) {
                break;
            }
            String trimmed = line.trim();
            if (trimmed.equalsIgnoreCase("EXIT") || trimmed.equalsIgnoreCase("QUIT")) {
                break;
            }
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                System.out.println(parser.execute(trimmed));
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Goodbye.");
    }
}
