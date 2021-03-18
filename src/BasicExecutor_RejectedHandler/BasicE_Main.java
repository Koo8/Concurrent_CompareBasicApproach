package BasicExecutor_RejectedHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class BasicE_Main {

    public static void main(String[] args) {
        // create threadpoolexecutor
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // for this executor to have customized reaction to rejected tasks instead of throw execptions
        RejectedTaskHandler  handler = new RejectedTaskHandler();
        executor.setRejectedExecutionHandler(handler);
        for (int i = 0; i < 10; i++) {
            Task task = new Task("T" + i);
            executor.execute(task);
        }
        executor.shutdown();

        // tasks added to executor will get rejected
        Task rejectedTask = new Task("OoPs");
        executor.execute(rejectedTask);
        
    }
}
