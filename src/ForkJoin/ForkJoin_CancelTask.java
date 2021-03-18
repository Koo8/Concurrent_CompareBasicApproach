package ForkJoin;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * A limitation of the fork/join framework is that it doesn't allow the cancellation of all the
 * tasks that are in ForkJoinPool.
 * The ForkJoinTask class provides the cancel() method that allows you to cancel a task if
 * it hasn't been executed yet. This is a very important point. If the task has begun its
 * execution, a call to the cancel() method has no effect.
 * To overcome that limitation, you implemented the
 * TaskManager class. It stores all the tasks that have been sent to the pool. It has a method
 * that cancels all the tasks it has stored.
 * For this program, we are only interested in one occurrence of the number, once find it,
 * we cancel the other tasks through TaskManager.
 */
public class ForkJoin_CancelTask {
}

class ArrayGenerator {
    public int[] generateArray(int size){
        int[] array = new int[size];
        Random rand = new Random();
        for (int i = 0; i <size; i++) {
           array[i] = rand.nextInt(10);
        }
        return array;
    }
}

// for storing all tasks executed in ForkJoinPool,
// ALSO, for cancelling all tasks even after tasks have been sent to the pool
class TaskManager{
    ///TODO: STUDY CONCURRENTLINKEDDEQUE.OOOO
    private final ConcurrentLinkedDeque<SearchNumberTask> tasks;
    public TaskManager (){
        // constructor generates an empty concurrentLinkedDeque 
        tasks = new ConcurrentLinkedDeque<>();
    }
    public void addTask(SearchNumberTask task){
        tasks.add(task);
    }
    public void cancelTasks(SearchNumberTask cancelTask){  // the parameter is the task that wants to cancel all other tasks
        for(SearchNumberTask task: tasks){
            if (task != cancelTask){
                task.cancel(true);
                task.logCancelMessage();  // identify each task that is cancelled
            }
        }
    }

}

// for Searching for a number within an integer array, once found, cancel all other SearchNumberTask using TaskManager
class SearchNumberTask extends RecursiveTask<Integer>{
    private int[] array;
    private int start, end, number;
    private TaskManager taskManager;
    private final int NOT_FOUND = -1;
    public SearchNumberTask (int[] array, int start, int end, int number, TaskManager taskManager){
        this.array = array;
        this.start = start;
        this.end = end;
        this.number = number;
        this.taskManager = taskManager;
    }

    @Override
    protected Integer compute() {
        // identify the task range
        System.out.println("Task: "+ start + " : "+ end);
        // divide the task if the range is bigger than 10 numbers
        if (end -start > 10){
            // TODO: launchNewTask()    P260
        }
         // TODO: task return integer
        return 0;
        
    }

    public void logCancelMessage() {
    }
}
