import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

record SupportTicket(long id, String customer, String topic){}
public class TicketService {
    public static final SupportTicket POISON_PILL = new SupportTicket(-1, "??", "??");
    static void main() throws InterruptedException {
        BlockingQueue<SupportTicket> queue=new LinkedBlockingQueue<>();
        ConcurrentHashMap<String,Integer> topicsCount=new ConcurrentHashMap<>();
        int consumersAmount=4;
        Thread producer = new Thread(new TicketProducer(queue, consumersAmount), "Producer");

        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < consumersAmount; i++) {
            Thread consumer = new Thread(new TicketConsumer(queue, topicsCount), "Consumer-" + (i + 1));
            consumers.add(consumer);
            consumer.start();
        }

        producer.start();

        producer.join();
        for (Thread consumer : consumers) {
            consumer.join();
        }

        System.out.println("Result: ");
        topicsCount.forEach((topic, count) ->
                System.out.println("Topic: " + topic + " -> Amount: " + count)
        );
    }
}
class TicketProducer implements Runnable{
    private final BlockingQueue<SupportTicket> queue;
    private final int consumersAmount;

    public TicketProducer(BlockingQueue<SupportTicket> queue, int consumersAmount) {
        this.queue = queue;
        this.consumersAmount = consumersAmount;
    }

    @Override
    public void run() {
        try {
            queue.put(new SupportTicket(1, "Anna", "a"));
            queue.put(new SupportTicket(2, "Matvei", "b"));
            queue.put(new SupportTicket(3, "Masha", "c"));
            queue.put(new SupportTicket(4, "Dasha", "a"));
            queue.put(new SupportTicket(5, "Dima", "a"));
            queue.put(new SupportTicket(6, "Kolya", "b"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for(int i=0; i<consumersAmount; i++){
            try {
                queue.put(TicketService.POISON_PILL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
class TicketConsumer implements Runnable{
    private final BlockingQueue<SupportTicket> queue;
    ConcurrentHashMap<String,Integer> topicsCount;

    TicketConsumer(BlockingQueue<SupportTicket> queue, ConcurrentHashMap<String,Integer> topicsCount) {
        this.queue = queue;
        this.topicsCount=topicsCount;
    }

    @Override
    public void run() {

            try {
                while(true){
                SupportTicket st = queue.take();
                if(st==TicketService.POISON_PILL){
                    System.out.println(Thread.currentThread().getName()+": got POISON PILL");
                    break;
                }
                System.out.println(Thread.currentThread().getName()+": on ticket "+st.id());
                topicsCount.merge(st.topic(), 1, Integer::sum);
                Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


    }
}
