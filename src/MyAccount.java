import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Класс для счета
 */
public class MyAccount {
    private String name;
    private double expenses;//расход
    private double earnings;//доход
    private double balance; //баланс
    private double startBalance; //начальный баланс
    private GregorianCalendar gregorianCalendar;

    public MyAccount(String name, double expense, double earning, double startBalance, double balance, GregorianCalendar gregorianCalendar){
        this.name = name;
        this.expenses = expense;
        this.earnings = earning;
        this.startBalance = startBalance;
        this.balance = balance;
        this.gregorianCalendar = gregorianCalendar;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public GregorianCalendar getGregorianCalendar(){
        return gregorianCalendar;
    }

    public double getExpenses() {
        return expenses;
    }

    public double getEarnings() {
        return earnings;
    }

    public double getStartBalance() {
        return startBalance;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    @Override
    public String toString() {

        return name + "/" + balance + "/" +
                gregorianCalendar.get(Calendar.DAY_OF_MONTH) + "." +
                gregorianCalendar.get(Calendar.MONTH) + "." +
                gregorianCalendar.get(Calendar.YEAR);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
