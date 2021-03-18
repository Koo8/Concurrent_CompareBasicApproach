package IncomeTax;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IncomeTaxApp {
    private static final int NUMOFTaxPayers = 100;
    private static IncomeTaxDept incomeTaxDept = new IncomeTaxDept(1000, NUMOFTaxPayers);
    // run multiple threads to speed up the process
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
    public static void main(String[] args) {
        registerTaxPayers();
        double time = System.currentTimeMillis();
        for (TaxPayer t:incomeTaxDept.getTaxPayerList()){
            // each payer run one thread
            executor.submit(()-> {
                incomeTaxDept.payTax(t);
                incomeTaxDept.getTaxReturn(t);
//                System.out.println("Total revenue is "+ incomeTaxDept.getTotalRevenuePessimistic()+ "  --> " + t.getName());
//                System.out.println("Total Opti revenue " + incomeTaxDept.getTotalRevenueOptimistic() + " ==> " + t.getName());
            });

        }

        executor.shutdown();
//        if (executor.isShutdown()) {
//            System.out.println("Is Shut down");
//            double newTime = System.currentTimeMillis();
////            System.out.println("Total revenue Pessi :" + incomeTaxDept.getTotalRevenuePessimistic());
////            System.out.println("Total revenue Opti :" + incomeTaxDept.getTotalRevenueOptimistic());
//            System.out.println("Elapsed "+ (newTime - time));
//        }
        // without this try block, the following isTerminated() can't be true ?
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (executor.isTerminated()) {
            System.out.println("Is terminated");
            double newTime1 = System.currentTimeMillis();
            System.out.println("Total revenue Pessi :" + incomeTaxDept.getTotalRevenuePessimistic());
            System.out.println("Total revenue Opti :" + incomeTaxDept.getTotalRevenueOptimistic());
            System.out.println("Elapsed "+ (newTime1 - time-1000));
        }

    }

    private static void registerTaxPayers() {

        for (int i = 0; i <NUMOFTaxPayers ; i++) {
            TaxPayer taxPayer = new TaxPayer();
            taxPayer.setName("T"+ i);
            taxPayer.setSsn("ssn-" + i);
            taxPayer.setTaxAmount(2000);
            incomeTaxDept.registerTaxPayer(taxPayer);
        }
    }
}
