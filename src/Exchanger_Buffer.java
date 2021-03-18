import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * To exchange info between producer and consumer at a certain point,
 * we need an Exchanger and two buffers for data storage.
 */

public class Exchanger_Buffer {

    public static void main(String[] args) {
        List<String> bufferForProducer = new ArrayList<>();
        List<String> bufferForConsumer = new ArrayList<>();
        Exchanger<List<String>> exchanger = new Exchanger<List<String>>();

        Producer producer = new Producer(bufferForProducer, exchanger);
        Consumer consumer = new Consumer(bufferForConsumer, exchanger);
        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(consumer);
        t1.start();
        t2.start();

    }
}

class Producer implements Runnable {
    private List<String> buffer;
    private Exchanger<List<String>> exchanger;

    public Producer(List<String> bufferForProducer, Exchanger<List<String>> exchanger) {
        buffer = bufferForProducer;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for (int cycle = 1; cycle <= 10; cycle++){
            System.out.printf("Producer: Cycle %d\n",cycle);
            // for each cycle, create 10 messages, then do exchange().
            for (int j=0; j<10; j++){
                String message="Event "+(((cycle-1)*10)+j);
                System.out.printf("Producer: %s\n",message);
                buffer.add(message);
            }
            try {
                buffer=exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Producer: bufferSize is "+buffer.size());
        }

    }

}

class Consumer implements Runnable {
    private List<String> buffer;
    private Exchanger<List<String>> exchanger;

    public Consumer(List<String> bufferForConsumer, Exchanger<List<String>> exchanger) {
        buffer = bufferForConsumer;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for (int cycle = 1; cycle <= 10; cycle++) {
            System.out.printf("Consumer: Cycle %d\n", cycle);
            // for each cycle, do exchange() to receive data
            try {
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("      ***** Consumer: bufferSize is "+buffer.size());
            for (int j=0; j<10; j++){
                String message=buffer.get(0);
                System.out.println("      Consumer: "+ message);
                buffer.remove(0); // messages are removed so nothing will be exchanged to producer
            }
        }
    }
}

