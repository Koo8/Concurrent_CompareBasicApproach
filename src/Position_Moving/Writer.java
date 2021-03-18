package Position_Moving;

import Position_Moving.Position;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class Writer implements Runnable {
    private final Position position;
    private final StampedLock sl;

    public Writer (Position p, StampedLock sl) {
        position = p;
        this.sl = sl;
    }

    //Implement the run() method. In a loop that we will repeat 10 times, get the lock
    //in write mode, change the value of the two attributes of the position object,
    //suspend the execution of the thread for a second, release the lock (in the finally
    //section of a try...catch...finally structure to release the lock in any
    //circumstance), and suspend the thread for a second:
    @Override
    public void run() {
        for (int i = 0; i <10 ; i++) {
             long stamp = sl.writeLock();

             // after get the writeLock, use try block for task
            try{
                position.setX(position.getX()+1);
                position.setY(position.getY()+ 1);
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                sl.unlockWrite(stamp);
                System.out.println("Writer: " + i + "=> lock is released. " + stamp );
            }

            // suspend the thread for  a bit longer
            try{
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
