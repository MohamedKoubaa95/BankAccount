import java.time.LocalDate;


public class Transaction {
  private String type;
  private Double  amount;
  private LocalDate date;
  private Double balance;

    public Transaction(String type, Double amount, LocalDate date, Double balance) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":" + '"'+type +'"'+
                ", \"amount\":" + amount +
                ", \"date\":\"" + date +'"'+
                ", \"balance\":" + balance +
                '}';
    }
}
