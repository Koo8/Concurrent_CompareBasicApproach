package BasicExecutor_RejectedHandler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedTaskHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println("OOPS!! "+r.toString()+ " is rejected");
        System.out.println(executor.isTerminating() + " for executor is terminating");
        System.out.println(executor.isTerminated() + " for exeecutor is terminated." );
    }
}
