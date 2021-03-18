package Various_BankAccount_Approach;

import java.util.concurrent.locks.StampedLock;

/**
 * Compare this with "volatile" approach. If memory visibility is the only concern,
 * this one is an overkill
 */

public class BankAccountStampedLock {
    private final StampedLock sl = new StampedLock();
    private long balance;

    public BankAccountStampedLock(long balance) {
        this.balance = balance;
    }

    public void deposit(long amount) {
        long stamp = sl.writeLock(); //Exclusively acquires the lock, blocking if necessary
        try {
            balance += amount;
        } finally {
            sl.unlockWrite(stamp); // if the lock state matches the stamp, release the exclusive lock
        }
    }

    public void withdraw(long amount) {
        long stamp = sl.writeLock();
        try {
            balance -= amount;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    public long getBalance() {
        //pessimistic, not sure if data was updated again, but to read.
        long stamp = sl.readLock();   //Non-exclusively acquires the lock, blocking if necessary
        try {
            return balance;
        } finally {
            sl.unlockRead(stamp);
        }
    }
    //The use of
    // *   optimistic read mode for short read-only code segments often reduces
    // *   contention and improves throughput.
    // the benefit of optimistic lock you can acquire it and read the values and then check if
    // there is any change in the values, if there is then only you need to go through
    // the blocking read lock.
    public long getBalanceOptimisticRead() {
        // this stamp can be validated later.
        // if return 0, the lock is exclusively locked.
        long stamp = sl.tryOptimisticRead();
        long balance = this.balance;
        // only when write lock is acquired by some thread after optimistic lock is acquired, that means new input has been updated,
        if (!sl.validate(stamp)) {  // true,if the lock has not been acquired in write mode since obtaining a given stamp,, false, when 0
            // then acquire the readLock() to read
            stamp = sl.readLock();
            try {
                balance = this.balance;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return balance;
    }
}
