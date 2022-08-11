public class BankAccount {
    private Double balance ;

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBalance() {
        return balance;
    }

    public BankAccount(Double balance) {
        this.balance = balance;
    }

    void depositAmount(Double amount){
        this.balance += amount;
    }

    void withdrawAmount(Double amount){
        this.balance -= amount;
    }
}
