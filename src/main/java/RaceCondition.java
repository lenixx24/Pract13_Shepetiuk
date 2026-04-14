public class RaceCondition {
    static void main() throws InterruptedException {
            UnsafeInventory unsafe1 = new UnsafeInventory(100);
            SynchronizedInventory synchronized1 = new SynchronizedInventory(100);
            System.out.println("Unsafe: ");
            runInventoryThreads(unsafe1, 60);
            System.out.println("Available: "+unsafe1.available());
            System.out.println("Synchronized: ");
            runInventoryThreads(synchronized1, 60);
            System.out.println("Available: "+synchronized1.available());
            stressTest(400, 60);

    }
    public static void stressTest(int it, int amount) throws InterruptedException {
        int fails=0;
        UnsafeInventory unsafe = new UnsafeInventory(100);
        for(int i=0; i<it; i++){
            runInventoryThreads(unsafe, amount);
            if(unsafe.available()<0)  fails++;
            unsafe.setAmount(100);
        }
        System.out.println(fails+" fails from "+it+" tries");
    }
    public static void runInventoryThreads(Inventory inventory, int amount) throws InterruptedException {
        Thread thread1 = new Thread(()->{
           inventory.reserve(amount);
        }, "thread1");
        Thread thread2 = new Thread(()->{
            inventory.reserve(amount);
        }, "thread2");
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

    }
}

class UnsafeInventory implements Inventory {
    int itemsAmount;
    UnsafeInventory(int itemsAmount){
        this.itemsAmount=itemsAmount;
    }
    @Override
    public void reserve(int amount) {
        if(itemsAmount>=amount){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            itemsAmount-=amount;
        }
    }

    @Override
    public int available() {
        return itemsAmount;
    }

    public void setAmount(int i) {
        this.itemsAmount=i;
    }
}
class SynchronizedInventory implements Inventory {
    private int itemsAmount;
    public SynchronizedInventory(int itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

    @Override
    public synchronized int available() {
        return itemsAmount;
    }

    @Override
    public synchronized void reserve(int amount) {
        if (itemsAmount >= amount) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            itemsAmount -= amount;
        }
    }
}
