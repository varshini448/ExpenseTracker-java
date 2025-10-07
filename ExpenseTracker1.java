import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

class Expense {
    private LocalDate date;       // yyyy-MM-dd
    private String category;
    private double amount;
    private String description;

    public Expense(String date, String category, double amount, String description) {
        this.date = LocalDate.parse(date); // parse date string
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }
    public String getCategory() {
        return category;
    }
    public double getAmount() {
        return amount;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("Expense[date=%s, category=%s, amount=%.2f, description=%s]",
                             date, category, amount, description);
    }
}

class Income {
    private LocalDate date;
    private String source;
    private double amount;

    public Income(String date, String source, double amount) {
        this.date = LocalDate.parse(date);
        this.source = source;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }
    public String getSource() {
        return source;
    }
    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("Income[date=%s, source=%s, amount=%.2f]", date, source, amount);
    }
}

public class ExpenseTracker1 {
    private List<Expense> expenses;
    private List<Income> incomes;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        incomes = new ArrayList<>();
    }

    public void addExpense(String date, String category, double amount, String description) {
        if (amount < 0) {
            System.out.println("Error: Expense amount must be non-negative.");
            return;
        }
        Expense e = new Expense(date, category, amount, description);
        expenses.add(e);
        System.out.println("Expense added.");
    }

    public void addIncome(String date, String source, double amount) {
        if (amount < 0) {
            System.out.println("Error: Income amount must be non-negative.");
            return;
        }
        Income inc = new Income(date, source, amount);
        incomes.add(inc);
        System.out.println("Income added.");
    }

    public double getTotalExpenses() {
        double sum = 0;
        for (Expense e : expenses) sum += e.getAmount();
        return sum;
    }

    public double getTotalIncome() {
        double sum = 0;
        for (Income inc : incomes) sum += inc.getAmount();
        return sum;
    }

    public double getSavings() {
        return getTotalIncome() - getTotalExpenses();
    }

    // ----- Monthly and Yearly Calculations -----
    public double getMonthlyExpenses(int year, int month) {
        double sum = 0;
        for (Expense e : expenses) {
            if (e.getDate().getYear() == year && e.getDate().getMonthValue() == month) sum += e.getAmount();
        }
        return sum;
    }

    public double getYearlyExpenses(int year) {
        double sum = 0;
        for (Expense e : expenses) {
            if (e.getDate().getYear() == year) sum += e.getAmount();
        }
        return sum;
    }

    public double getMonthlyIncome(int year, int month) {
        double sum = 0;
        for (Income inc : incomes) {
            if (inc.getDate().getYear() == year && inc.getDate().getMonthValue() == month) sum += inc.getAmount();
        }
        return sum;
    }

    public double getYearlyIncome(int year) {
        double sum = 0;
        for (Income inc : incomes) {
            if (inc.getDate().getYear() == year) sum += inc.getAmount();
        }
        return sum;
    }

    // ----- Listing -----
    public void listExpenses() {
        if (expenses.isEmpty()) System.out.println("No expenses recorded.");
        else {
            System.out.println("Expenses:");
            for (Expense e : expenses) System.out.println("  " + e);
            System.out.printf("Total Expenses: %.2f\n", getTotalExpenses());
        }
    }

    public void listIncomes() {
        if (incomes.isEmpty()) System.out.println("No incomes recorded.");
        else {
            System.out.println("Incomes:");
            for (Income inc : incomes) System.out.println("  " + inc);
            System.out.printf("Total Income: %.2f\n", getTotalIncome());
        }
    }

    public void showSummary() {
        System.out.println("===== Summary =====");
        System.out.printf("Total Income   : %.2f\n", getTotalIncome());
        System.out.printf("Total Expenses : %.2f\n", getTotalExpenses());
        System.out.printf("Savings        : %.2f\n", getSavings());
        System.out.println("===================");
    }

    // ----- Main -----
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. List Incomes");
            System.out.println("4. List Expenses");
            System.out.println("5. Show Summary");
            System.out.println("6. Exit");
            System.out.println("7. Monthly Summary");
            System.out.println("8. Yearly Summary");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter date (yyyy-mm-dd): ");
                        String incDate = sc.nextLine().trim();
                        System.out.print("Enter income source: ");
                        String source = sc.nextLine().trim();
                        System.out.print("Enter amount: ");
                        double incAmt = Double.parseDouble(sc.nextLine().trim());
                        tracker.addIncome(incDate, source, incAmt);
                        break;

                    case "2":
                        System.out.print("Enter date (yyyy-mm-dd): ");
                        String expDate = sc.nextLine().trim();
                        System.out.print("Enter category: ");
                        String cat = sc.nextLine().trim();
                        System.out.print("Enter amount: ");
                        double expAmt = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("Enter description: ");
                        String desc = sc.nextLine().trim();
                        tracker.addExpense(expDate, cat, expAmt, desc);
                        break;

                    case "3":
                        tracker.listIncomes();
                        break;

                    case "4":
                        tracker.listExpenses();
                        break;

                    case "5":
                        tracker.showSummary();
                        break;

                    case "6":
                        System.out.println("Exiting. Bye!");
                        sc.close();
                        System.exit(0);
                        break;

                    case "7": // Monthly summary
                        System.out.print("Enter year (yyyy): ");
                        int mYear = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Enter month (1-12): ");
                        int mMonth = Integer.parseInt(sc.nextLine().trim());
                        System.out.printf("Monthly Income: %.2f\n", tracker.getMonthlyIncome(mYear, mMonth));
                        System.out.printf("Monthly Expenses: %.2f\n", tracker.getMonthlyExpenses(mYear, mMonth));
                        System.out.printf("Monthly Savings: %.2f\n", tracker.getMonthlyIncome(mYear, mMonth) - tracker.getMonthlyExpenses(mYear, mMonth));
                        break;

                    case "8": // Yearly summary
                        System.out.print("Enter year (yyyy): ");
                        int yYear = Integer.parseInt(sc.nextLine().trim());
                        System.out.printf("Yearly Income: %.2f\n", tracker.getYearlyIncome(yYear));
                        System.out.printf("Yearly Expenses: %.2f\n", tracker.getYearlyExpenses(yYear));
                        System.out.printf("Yearly Savings: %.2f\n", tracker.getYearlyIncome(yYear) - tracker.getYearlyExpenses(yYear));
                        break;

                    default:
                        System.out.println("Invalid choice. Please choose again.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Error: Please enter valid numeric amount.");
            } catch (DateTimeParseException dtpe) {
                System.out.println("Error: Please enter date in yyyy-mm-dd format.");
            }
        }
    }
}
