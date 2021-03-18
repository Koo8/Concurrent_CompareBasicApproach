package Position_Moving;

import Position_Moving.Position;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Reader implements Runnable{
    private final Position position;
    private final StampedLock lock;

    public Reader(Position p, StampedLock lock){
        position = p;
        this.lock = lock;
    }

    // In a loop that we will repeat 50 times, get
    //control of the lock in read mode, write the values of the position object in the
    //console, and suspend the thread for 200 milliseconds. Finally, release the lock
    //using the finally block of a try...catch...finally structure:
    @Override
    public void run() {
        for (int i = 0; i <20 ; i++) {
            long stamp = lock.readLock();
            try{
                System.out.println("0Reader: "+ i + "->" + stamp + " "+ position.getX() + ", " + position.getY());
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlockRead(stamp);
                System.out.println("0Reader: "+ i + " released " + stamp);
               // System.out.println("Position_Moving.Reader: " + stamp + " is released");
            }
        }
    }
}
