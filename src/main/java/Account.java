public class Account {
    private int id;
    private int balance;
    public Account(int id, int balance){
        this.id=id;
        this.balance=balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }
    public void withdraw(int amount){
        this.balance-=amount;
    }
    public void add(int amount){
        this.balance+=amount;
    }
}
