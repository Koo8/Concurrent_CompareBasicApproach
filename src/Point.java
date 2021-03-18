import java.util.concurrent.locks.StampedLock;

public class Point {
    private static final int RETRIES = 5;
    private final StampedLock sl = new StampedLock();
    private int x, y;
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    // approach 1: use sync keyword for move()
//    public synchronized void move(int deltaX, int deltaY){
//        x += deltaX;
//        y += deltaY;
//    }
    // approach 2: use StampedLock
    public void move(int deltaX, int deltaY){
        // acquire the stamp, then update when guarded by the lock
        long stamp = sl.writeLock();
        try {
           x += deltaX;
           y += deltaY;
        }finally {
            sl.unlockWrite(stamp);// unlock the lock in the finally block
        }
    }
    // approach 1: sync keyword
//    public synchronized double distanceFromOrigin(){
//        return Math.hypot(x, y);
//    }

    // approach 2: optimistic read
//    public double distanceFromOrigin(){
//        // step 1
//        long stamp = sl.tryOptimisticRead();
//        // step 2 -- after acquire the optimistic stamp, reading the fields into local variables localX and localY.
//        int localX = x;
//        int localY = y;
//        // step 3 -- validate the stamp
//        if (!sl.validate(stamp)) {
//            // step 4
//            stamp = sl.readLock(); // if writeLock() newly released the lock, read the update
//            try {
//                localX = x;
//                localY = y;
//            }finally {
//                sl.unlockRead(stamp);
//            }
//        }
//        return Math.hypot(localX, localY);
//    }

    // approach 3:
    //Here is another idiom that retries a number of times before defaulting
    // to the pessimistic read version. It uses the trick in Java where we break out
    // to a label, thus jumping out of a code block.
    public double distanceFromOrigin() {
         int localX, localY;
         out: // use label
         {
           // try a few times to do an optimistic read
             for (int i = 0; i <RETRIES ; i++) {
                  // acquire the stamp
                 long stamp = sl.tryOptimisticRead();
                 // read data
                 localX = x;
                 localY = y;
                 if (sl.validate(stamp)){
                     break out; // once get the stamp, jump out of the loop
                 }
             }

             // pessimistic read
             long stamp = sl.readLock();
             try {
                 localX = x;
                 localY = y;
             }finally {
                 sl.unlockRead(stamp);
             }
         }
         return Math.hypot(localX, localY);
    }
    

    public synchronized boolean isitMoved(int oldX, int oldY, int newX, int newY) {
        if(x == oldX && y == oldY) {
            x = newX;
            y = newY;
            return true;
        } else {
            return false;
        }
    }
}
