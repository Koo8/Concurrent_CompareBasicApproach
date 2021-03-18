package ForkJoin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * from Java 9 concurrency cookbook P242
 * search for files with a determined extension inside
 * a folder and its subFolders.
 * For each subfolder inside that folder, it will send a new task
 * to the ForkJoinPool class in an asynchronous way. For each file inside that folder, the task
 * will check the extension of the file and add it to the result list if it proceeds. When a task is
 * completed, it will insert the result lists of all its child tasks in its result task.
 */

class ForderProcessor extends CountedCompleter<List<String>> {
    private String path, extension;
    private List<ForderProcessor> taskList;// = new ArrayList<>();
    private List<String> resultList; //= new ArrayList<>();

    ForderProcessor(CountedCompleter<?> cc, String path, String extension) {  // this is for subClass to have a parent task CC
        super(cc);
        this.path = path;
        this.extension = extension;
    }

    ForderProcessor(String path, String extension){
        this.path = path;
        this.extension = extension;
    }

    @Override
    public void compute() {
        resultList = new ArrayList<>();
        taskList = new ArrayList<>();
        File file = new File(path);
        File[] contents = file.listFiles();
        //System.out.println("in Computer for file " + file.getName());

        if (contents != null) {// if not an empty folder
            // check each content, is is a subFolder, create a new task
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isDirectory()) {
                    ForderProcessor task = new ForderProcessor(this, contents[i].getAbsolutePath(), extension);
                    task.fork();// start new folder search asynchronisely
                    // one fork() needs one pending count
                    addToPendingCount(1);
                    // add to task array
                    taskList.add(task);
                }
                // otherwise search the file of its extension to see if match
                else {
                    if (checkFile(contents[i].getName())) {
                        // add to file list
                       // System.out.println("in 54 line: the file is with extension ");
                        resultList.add(contents[i].getAbsolutePath());
                       // System.out.println(resultList.size() + " is the resultSize in if block on Line56");
                    }
                }
            }
        }
//        if(taskList.size()> 50){
//            System.out.println(file.getAbsolutePath() + " has " + taskList.size() + " tasks running>>>");
//        }
        // bofore return
        tryComplete();
    }

    private boolean checkFile(String fileName) {
        return fileName.endsWith(extension);
    }

    public List<String> getResultList() {
        return this.resultList;
    }

    @Override  // onCompletion, combine all subTasks result together
    public void onCompletion(CountedCompleter<?> caller) {

            for (ForderProcessor subTasks : taskList) {
                if(subTasks.getResultList().size()> 0){
                    resultList.addAll(subTasks.getResultList());
                }

            } 
    }
}

public class CC_ForderProcessor {
    public static void main(String[] args) {

        ForkJoinPool pool = new ForkJoinPool();
        ForderProcessor ccSystem = new ForderProcessor(null, "C:\\Program Files", "log");
        pool.execute(ccSystem);   // arrange asynchronous execution of the task

        do {
            System.out.println("--------------------");
            System.out.printf("Main: Active Threads: %d\n",
                    pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n",
                    pool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n",
                    pool.getStealCount());
            System.out.println("************************");
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (!ccSystem.isDone());
        System.out.println(ccSystem.isDone() + " for is Done");
        pool.shutdown();
        List<String> printResult = ccSystem.getResultList(); // ccSystem.join() return the result of the computation. but this program compute() return void, so join() won't equal to the resultList
        System.out.println(printResult );
        // TODO: nonPointer exception
        System.out.println("There are " + printResult.size() + " file with the extension found");



    }
}


