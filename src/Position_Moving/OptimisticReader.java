package Position_Moving;

import Position_Moving.Position;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class OptimisticReader implements Runnable {
    private final Position position;
    private final StampedLock lock;

    public OptimisticReader ( Position p, StampedLock sl){
        position = p;
        lock = sl;
    }

    //. First obtain the stamp of the lock in the
    //optimistic read mode using the tryOptimisticRead() method. Then, repeat
    //the loop 100 times. In the loop, validate whether you can access data using the
    //validate() method. If this method returns true, write the values of the position
    //object in the console. Otherwise, write a message in the console and get another
    //stamp using the tryOptimisticRead() method again. Then, suspend the
    //thread for 200 milliseconds:
    @Override
    public void run() {

        for (int i = 0; i <50 ; i++) {
            long stamp = lock.tryOptimisticRead();
            // local variable to read data   -NOTE:  always
            int x = position.getX();
            int y = position.getY();
            if (lock.validate(stamp)) {
                System.out.println("OptimizedReader: "+ i+ "==> " + stamp + " " + x + ", " + y);
            } else {
                System.out.println("OptimizedReader: " + stamp + " not freed");
            }

            // suspend thread for 300 milliseconds
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

