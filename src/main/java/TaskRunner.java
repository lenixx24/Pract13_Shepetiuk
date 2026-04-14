import java.util.ArrayList;
import java.util.List;

record Order(long id, int totalCents){}
public class TaskRunner {
    public static void runAndWait(List<Runnable> tasks) {
        List<Thread> threads = new ArrayList<>();
        for (Runnable task : tasks) {
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted");
            }
        }
    }
    static void main() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, 20));
        orders.add(new Order(2, 80));
        orders.add(new Order(3, 40));
        orders.add(new Order(4, 10));
        orders.add(new Order(6, 150));
        orders.add(new Order(5, 30));
        int[] sum=new int[1];
        Runnable sumCount =()->{
            sum[0]=0;
            for(Order o: orders) sum[0]+=o.totalCents();
        };
        int[] max=new int[1];
        Runnable maxCount = ()->{
            max[0]=orders.getFirst().totalCents();
            for(Order o: orders)
                if(o.totalCents()>max[0]) max[0]=o.totalCents();
        };
        List<Runnable> runnableList = new ArrayList<>();
        runnableList.add(sumCount); runnableList.add(maxCount);
        runAndWait(runnableList);
        System.out.println("Sum: "+sum[0]+", Max: "+max[0]);
    }
}
