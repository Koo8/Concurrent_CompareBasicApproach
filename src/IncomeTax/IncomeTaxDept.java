package IncomeTax;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.StampedLock;

public class IncomeTaxDept {

    private List<TaxPayer> taxPayerList;
    private double totalRevenue;
    // for concurrent protection, use stampedLock
    private final StampedLock sl = new StampedLock();


    public IncomeTaxDept(long oriRevenue, int numOfTaxPayer) {
        totalRevenue = oriRevenue; // double includes long, not vice versa
        taxPayerList = new ArrayList<>(numOfTaxPayer);
    }
    // writeLock to write paid tax by taxPayer
    public void payTax(TaxPayer taxPayer) {
        double taxPaid = taxPayer.getTaxAmount();
        // write this into totalRevenue
        long writeStamp = sl.writeLock();
        // NOTE: always use try block, put unlock into the finally block
        try {
            totalRevenue += taxPaid;
           // System.out.println("Total Revenue is" + totalRevenue);
        }finally {
            sl.unlockWrite(writeStamp);
        }
    }

    // writeLock to write returned amount to taxPayer
    public double getTaxReturn(TaxPayer taxPayer) {
        double return10Percent = taxPayer.getTaxAmount() * 10/100;
        // write to update totalRevenue
        long writeStamp = sl.writeLock();
        try {
            totalRevenue -= return10Percent;
        }finally {
            sl.unlockWrite(writeStamp);
        }
        return return10Percent;
    }
    // readLock 
    public double getTotalRevenuePessimistic() {
        long readStamp = sl.readLock();
        try{
            return totalRevenue;
        } finally {
            sl.unlockRead(readStamp);
        }
    }

    // optimisticRead
    public double getTotalRevenueOptimistic(){
        long optStamp = sl.tryOptimisticRead();
        // must use local variable
        double theRevenue = totalRevenue; // to read the data
        //calling validate(stamp) method to ensure that stamp is valid, if not then acquiring the read lock
        if(!sl.validate(optStamp)) {
            optStamp = sl.readLock(); // if optimistic read lock can't secure, use pessimistic read
            try {
                theRevenue = totalRevenue;
            }finally {
                sl.unlockRead(optStamp);
            }
        }


       return theRevenue;
    }

    // after read, if you need to write to data, use convertToWriteLock(), check if successful, when fail directly use writeLock() to get the lock
     // code omitted


    public void registerTaxPayer (TaxPayer taxPayer){
        taxPayerList.add(taxPayer);
    }

    public List<TaxPayer> getTaxPayerList() {
        return taxPayerList;
    }
}
