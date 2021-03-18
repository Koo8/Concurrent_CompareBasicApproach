package Various_BankAccount_Approach;

import java.math.BigDecimal;

/**
 *  volatile keyword guarantees visibility of changes to variables across threads.
 *  if a variable is volatile, its cached value in CPU is the same as in main memory.
 */

public class BankAccountSynVolatile {

    // volatile is necessary -> one thread may catch the balance and make it hidden from main thread.
    // so that getBalance() may get a dated data
    private volatile BigDecimal balance;

    BankAccountSynVolatile(BigDecimal balance) {
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

    // multiple threads can read the balance - not a sync block
    public BigDecimal getBalance() {
        return balance;
    }


}
