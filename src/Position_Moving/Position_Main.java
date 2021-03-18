package Position_Moving;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Position_Main {

    public static void main(String[] args) {
        Position position = new Position();
        StampedLock sl = new StampedLock();
        Reader readerThread = new Reader(position, sl);
        Writer writerThread = new Writer(position, sl);
        OptimisticReader optReader = new OptimisticReader(position, sl);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(writerThread);
        executorService.submit(readerThread); // NOTE: reader finished reading b4 opReader can start in this order
        executorService.submit(optReader);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }
}
