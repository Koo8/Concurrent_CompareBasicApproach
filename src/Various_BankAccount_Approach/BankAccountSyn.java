package Various_BankAccount_Approach;

import java.math.BigDecimal;

/**
 * Synchronized blocks to prevent this bankAccount accessed simultaneously
 * TO compare this approach with others in this project
 * For ReentrantLock and ReentrantReadWriteLock -> they are not better than sync, except it can time waits
 * and tryLock(). 
 * 
 */

public class BankAccountSyn {
    private BigDecimal balance;
    BankAccountSyn(BigDecimal balance){
        this.balance = balance;
    }
    // only one thread can update the balance
    public synchronized void deposit(BigDecimal newDeposit) {
        balance.add(newDeposit);
    }
    // only one thread an update the balance
    public synchronized void withdraw(BigDecimal newWithdrawal) {
        balance.subtract(newWithdrawal);
    }
    // only one thread can read the balance -> this can be changed using volatile keywords
    public synchronized BigDecimal getBalance() {
        return balance;
    }
}
