import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) {
        try {
            List<Transaction> transactions = new ArrayList<>();

            JSONParser jsonParser = new JSONParser();

            FileReader accountFile = new FileReader("./src/resources/BankAccount.json");
            Object accountObj = jsonParser.parse(accountFile);
            JSONObject accountJson = (JSONObject) accountObj;

            FileReader transactionFile = new FileReader("./src/resources/Transactions.json");
            JSONArray transactionsJson;
            try {
                Object transactionsObject = jsonParser.parse(transactionFile);
                transactionsJson = (JSONArray) transactionsObject;
            } catch (Exception e) {
                transactionsJson = new JSONArray();
            }

            if (!transactionsJson.isEmpty()) {
                transactionsJson.forEach(item -> parseTransactions((JSONObject) item, transactions));
            }


            System.out.println("Please chose one the following commands number :\n" +
                    "1-Deposit\t  2-Withdraw\t  3-Print Statement 4-Print Transactions ");
            BankAccount account = new BankAccount((Double) accountJson.get("balance"));

            while (true) {
                FileWriter accountWriter;
                FileWriter transactionWriter;
                Scanner reader = new Scanner(System.in);
                System.out.print("Enter a command: ");
                String command = reader.nextLine();
                command = command.replaceAll(" ", "");
                if (isValidCommand(command)) {
                    switch (command) {
                        case "1": {
                            depositMoney(transactions, accountJson, transactionsJson, account, reader);
                            break;
                        }
                        case "2": {
                            withdrawMoney(transactions, accountJson, transactionsJson, account, reader);
                            break;
                        }
                        case "3": {
                            System.out.println("your balance at  " + LocalDate.now() + " is  " + account.getBalance());
                            break;
                        }
                        case "4": {
                            if (transactions.isEmpty()) {
                                System.out.println("You have not done any transactions yet");
                            } else {
                                for (Transaction item : transactions) {
                                    System.out.println(item.toString());
                                }

                            }
                            break;
                        }
                    }


                } else {
                    System.out.println("please enter a valid command");
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private static void withdrawMoney(List<Transaction> transactions, JSONObject accountJson, JSONArray transactionsJson, BankAccount account, Scanner reader) throws IOException {
        System.out.print("Enter a amount: ");
        String stringAmount = reader.nextLine();
        if (isValidAmount(stringAmount)) {
            Double amount = Double.parseDouble(stringAmount);
            if (amount <= account.getBalance()) {
                account.withdrawAmount(amount);
                updateBalance(accountJson, account);
                Transaction transaction = new Transaction("WITHDRAWAL", amount, LocalDate.now(), account.getBalance());
                transactions.add(transaction);
                addTransactionToJson(transactionsJson, transaction);
            } else {
                System.out.println("Your balance is insufficient");

            }
        } else {
            System.out.println("Please enter a valid amount");
            withdrawMoney(transactions, accountJson, transactionsJson, account, reader);
        }
    }

    private static void depositMoney(List<Transaction> transactions, JSONObject accountJson, JSONArray transactionsJson, BankAccount account, Scanner reader) throws IOException {
        System.out.print("Enter a amount: ");
        String stringAmount = reader.nextLine();
        if (isValidAmount(stringAmount)) {
            Double amount = Double.parseDouble(stringAmount);
            account.depositAmount(amount);
            updateBalance(accountJson, account);
            Transaction transaction = new Transaction("DEPOSIT", amount, LocalDate.now(), account.getBalance());
            transactions.add(transaction);
            addTransactionToJson(transactionsJson, transaction);

        } else {
            System.out.println("Please enter a valid amount");
            depositMoney(transactions, accountJson, transactionsJson, account, reader);
        }
    }

    private static void addTransactionToJson(JSONArray transactionsJson, Transaction transaction) throws IOException {
        FileWriter transactionWriter;
        transactionsJson.add(transaction);
        transactionWriter = new FileWriter("./src/resources/Transactions.json");
        transactionWriter.write(transactionsJson.toJSONString());
        transactionWriter.flush();
        transactionWriter.close();
    }

    private static void updateBalance(JSONObject accountJson, BankAccount account) throws IOException {
        FileWriter accountWriter;
        if (accountJson.get("balance") != null) {
            accountJson.replace("balance", account.getBalance());
        } else {
            accountJson.put("balance", account.getBalance());
        }
        accountWriter = new FileWriter("./src/resources/BankAccount.json");
        accountWriter.write(accountJson.toJSONString());
        accountWriter.flush();
        accountWriter.close();
    }

    static Boolean isValidAmount(String amount) {
        try {
            Double.parseDouble(amount);
            return true;

        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    static boolean isValidCommand(String command) {
        try {
            int commandNumber = Integer.parseInt(command);
            return commandNumber > 0 && commandNumber < 5;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    static void parseTransactions(JSONObject item, List<Transaction> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime;
        try {
            dateTime = LocalDate.parse((String) item.get("date"), formatter);
        } catch (DateTimeParseException e) {
            dateTime = null;
        }
        transactions.add(new Transaction((String) item.get("type"), (Double) item.get("amount"), dateTime, (Double) item.get("balance")));
    }

}
