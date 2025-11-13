import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.lang.Double;

/**
 * Simple console expense tracker.
 * - Uses Java's built-in serialization for persistence (users_data.ser)
 * - Safe loading that checks types to avoid unchecked casts
 * - Basic user registration/login and add/view operations
 */
public class ExpenseTracker3Console {

    private static final String STORAGE_FILE = "users_data.ser";

    // ---------------------- Model classes ----------------------

    static class Transaction implements Serializable {
        private static final long serialVersionUID = 1L;
        String category;
        double amount;
        LocalDate date;

        Transaction(String category, double amount, LocalDate date) {
            this.category = category;
            this.amount = amount;
            this.date = date;
        }

        @Override
        public String toString() {
            return String.format("%s | %.2f | %s", category, amount, date);
        }
    }

    static class RecurringExpense implements Serializable {
        private static final long serialVersionUID = 1L;
        String category;
        double amount;
        String frequency; // descriptive only

        RecurringExpense(String category, double amount, String frequency) {
            this.category = category;
            this.amount = amount;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f) - %s", category, amount, frequency);
        }
    }

    static class Budget implements Serializable {
        private static final long serialVersionUID = 1L;
        double monthlyTarget = 0;
        double yearlyTarget = 0;
    }

    static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        String username;
        String password;
        List<Transaction> incomes = new ArrayList<>();
        List<Transaction> expenses = new ArrayList<>();
        List<RecurringExpense> recurring = new ArrayList<>();
        Budget budget = new Budget();

        User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // ---------------------- Storage (safe) ----------------------

    /**
     * Loads users from the storage file in a type-safe way.
     * If the file does not exist or content is invalid, returns an empty map.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    static Map<String, User> loadUsers() {
        File f = new File(STORAGE_FILE);
        if (!f.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (!(obj instanceof Map)) {
                System.out.println("Storage format mismatch — starting fresh.");
                return new HashMap<>();
            }

            Map<?, ?> raw = (Map<?, ?>) obj;
            Map<String, User> result = new HashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet()) {
                Object k = e.getKey();
                Object v = e.getValue();
                if (k instanceof String && v instanceof User) {
                    result.put((String) k, (User) v);
                } else {
                    // skip invalid entries
                }
            }
            return result;
        } catch (Exception ex) {
            System.out.println("Could not load storage (corrupt/missing). Starting fresh.");
            return new HashMap<>();
        }
    }

    static void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_FILE))) {
            oos.writeObject(users);
        } catch (IOException ex) {
            System.out.println("Error saving users: " + ex.getMessage());
        }
    }

    // ---------------------- UI / Main ----------------------

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, User> users = loadUsers();

        System.out.println("=== ExpenseTracker3Console ===");

        User current = null;
        while (current == null) {
            System.out.println("\n1) Register\n2) Login\n3) Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            if (ch.equals("1")) {
                System.out.print("Username: ");
                String u = sc.nextLine().trim();
                if (u.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                    continue;
                }
                if (users.containsKey(u)) {
                    System.out.println("Username already exists.");
                    continue;
                }
                System.out.print("Password: ");
                String p = sc.nextLine();
                users.put(u, new User(u, p));
                saveUsers(users);
                System.out.println("Registered. You can now login.");
            } else if (ch.equals("2")) {
                System.out.print("Username: ");
                String u = sc.nextLine().trim();
                System.out.print("Password: ");
                String p = sc.nextLine();
                User candidate = users.get(u);
                if (candidate == null || candidate.password == null || !candidate.password.equals(p)) {
                    System.out.println("Invalid credentials.");
                } else {
                    current = candidate;
                    System.out.println("Login successful. Welcome " + current.username + "!");
                }
            } else if (ch.equals("3")) {
                System.out.println("Goodbye.");
                saveUsers(users);
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        // logged in menu
        while (true) {
            System.out.println("\n--- Menu (" + current.username + ") ---");
            System.out.println("1) Add Income");
            System.out.println("2) Add Expense");
            System.out.println("3) Add Recurring Expense");
            System.out.println("4) Set Targets (monthly/yearly)");
            System.out.println("5) View Summary");
            System.out.println("6) List Incomes");
            System.out.println("7) List Expenses");
            System.out.println("8) Logout");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();

            try {
                switch (c) {
                    case "1": {
                        System.out.print("Category/source: ");
                        String cat = sc.nextLine().trim();
                        System.out.print("Amount: ");
                        double amt = parseDoubleSafe(sc.nextLine().trim());
                        current.incomes.add(new Transaction(cat, amt, LocalDate.now()));
                        saveUsers(users);
                        System.out.println("Income added.");
                        break;
                    }
                    case "2": {
                        System.out.print("Category: ");
                        String cat = sc.nextLine().trim();
                        System.out.print("Amount: ");
                        double amt = parseDoubleSafe(sc.nextLine().trim());
                        current.expenses.add(new Transaction(cat, amt, LocalDate.now()));
                        saveUsers(users);
                        System.out.println("Expense added.");
                        break;
                    }
                    case "3": {
                        System.out.print("Category (e.g., Rent): ");
                        String cat = sc.nextLine().trim();
                        System.out.print("Amount: ");
                        double amt = parseDoubleSafe(sc.nextLine().trim());
                        System.out.print("Frequency (Monthly/Weekly): ");
                        String freq = sc.nextLine().trim();
                        current.recurring.add(new RecurringExpense(cat, amt, freq));
                        saveUsers(users);
                        System.out.println("Recurring expense saved.");
                        break;
                    }
                    case "4": {
                        System.out.print("Monthly target amount: ");
                        current.budget.monthlyTarget = parseDoubleSafe(sc.nextLine().trim());
                        System.out.print("Yearly target amount: ");
                        current.budget.yearlyTarget = parseDoubleSafe(sc.nextLine().trim());
                        saveUsers(users);
                        System.out.println("Targets updated.");
                        break;
                    }
                    case "5": {
                        double totalIncome = current.incomes.stream().mapToDouble(t -> t.amount).sum();
                        double totalExpense = current.expenses.stream().mapToDouble(t -> t.amount).sum();
                        System.out.println("\n--- Summary ---");
                        System.out.printf("Total Income : %.2f%n", totalIncome);
                        System.out.printf("Total Expense: %.2f%n", totalExpense);
                        System.out.printf("Balance      : %.2f%n", (totalIncome - totalExpense));
                        System.out.printf("Monthly Target: %.2f%n", current.budget.monthlyTarget);
                        System.out.printf("Yearly Target : %.2f%n", current.budget.yearlyTarget);
                        break;
                    }
                    case "6": {
                        System.out.println("\n--- Incomes ---");
                        if (current.incomes.isEmpty()) System.out.println("(none)");
                        for (Transaction t : current.incomes) System.out.println(t);
                        break;
                    }
                    case "7": {
                        System.out.println("\n--- Expenses ---");
                        if (current.expenses.isEmpty()) System.out.println("(none)");
                        for (Transaction t : current.expenses) System.out.println(t);
                        System.out.println("\n--- Recurring ---");
                        if (current.recurring.isEmpty()) System.out.println("(none)");
                        for (RecurringExpense r : current.recurring) System.out.println(r);
                        break;
                    }
                    case "8": {
                        System.out.println("Logging out...");
                        saveUsers(users);
                        return;
                    }
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number. Try again.");
            } catch (Exception ex) {
                System.out.println("An error occurred: " + ex.getMessage());
            }
        }
    }

    private static double parseDoubleSafe(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        return Double.parseDouble(s);
    }
}
