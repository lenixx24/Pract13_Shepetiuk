public class TransferService {
    static void main() {
       Account ac1 = new Account(6, 200);
       Account ac2 = new Account(2, 150);
       Thread t1 = new Thread(()->{
           transfer(ac1, ac2, 100);
           System.out.println("1: ac1: "+ac1.getBalance()+", ac2: "+ac2.getBalance());
       });
        Thread t2 = new Thread(()->{
            transfer(ac2, ac1, 50);
            System.out.println("2: ac1: "+ac1.getBalance()+", ac2: "+ac2.getBalance());
        });
        t1.start();
        t2.start();
        try{
        t1.join();
        t2.join();
        } catch (InterruptedException ignore) {}
        System.out.println("After transfers:");
        // ac1 = 200-100+50; ac2 = 150+100-50
        System.out.println("ac1: "+ac1.getBalance()+", ac2: "+ac2.getBalance());
    }
    public static void transfer(Account from ,Account to,int amount){
        if(from.getId()== to.getId()) System.out.println("You can not transfer to the same account");
            Account firstLock = from.getId()>to.getId()? to: from;
            Account secondLock = from.getId()>to.getId()? to: from;
        synchronized (firstLock) {
            System.out.println( Thread.currentThread().getName()+" locked on account "+ firstLock.getId());
            try { Thread.sleep(50); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            synchronized (secondLock) {
                System.out.println( Thread.currentThread().getName()+" locked on account "+ firstLock.getId());
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.add(amount);
                    System.out.println("Successful transfer from "+from.getId()+" to "+to.getId());
                } else {
                    System.out.printf("Not enough money on "+from.getId());
                }
            }
        }

    }
}
