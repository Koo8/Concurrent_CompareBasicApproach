import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * This program run a few threads to search a few file directories for a specific file extension
 * files that have been modified within the last 24 hours.
 * Each thread has 3 stages to complete: to get ready, to search directory and collect results, to show results
 * after all phases are reached by all thread, check if the phaser is terminated.
 */
public class FileSearch_Phaser implements Runnable {
    private String filePath;
    private String fileExtension;
    // each thread needs to register with the same phaser.
    private Phaser phaser;
    private List<String> results;

    public FileSearch_Phaser(String filePath, String fileExtension, Phaser phaser) {
        this.filePath = filePath;
        this.fileExtension = fileExtension;
        this.phaser = phaser;
        results = new ArrayList<>();
    }


    @Override
    public void run() {
        phaser.arriveAndAwaitAdvance(); // to wait for all threads to be ready to start
        // check directory - deregister those that have not result
        doSearch();


    }

    private void showInfo() {
        System.out.println("In Thread "+ Thread.currentThread().getName());
        for (int i = 0; i <results.size() ; i++) {
            System.out.println(results.get(i));

        }
    }

    private void doSearch() {
        File file = new File(filePath);
        if (file.isDirectory()) {
            directoryProcess(file);  // all files end with "log" will be collected
        }
        // deregister those empty thread
        if(results.isEmpty()) {
            phaser.arriveAndDeregister();
            System.out.println(Thread.currentThread().getName() + " is deRegistered" );

        }else {
            // use filter to modify the result Then show the info 
            modificationFilter();
            showInfo();
        }
    }

    private void modificationFilter() {
        List<String> newResult = new ArrayList<>();
        for (int i = 0; i <results.size() ; i++) {
           File file = new File(results.get(i));
           long lastModify = file.lastModified();
           long currentTime = new Date().getTime();
           if((currentTime - lastModify)< TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS)){
              newResult.add(results.get(i));
           }
        }
        results = newResult;
    }

    private void directoryProcess(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            //System.out.println("process directory");
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    directoryProcess(files[i]);
                } else {
                    fileProcess(files[i]);
                }
            }
        }
    }

    private void fileProcess(File file) {
        String thisPath = file.getAbsolutePath();
        if (thisPath.endsWith(fileExtension)){
            results.add(thisPath);
        }
//        if (file.getName().endsWith(fileExtension)){
//            results.add(file.getAbsolutePath());
//        }
    }

    public static void main(String[] args) {
        Phaser phaser = new Phaser(3);
        FileSearch_Phaser r1 = new FileSearch_Phaser("C:\\Windows", "log", phaser);
        FileSearch_Phaser r2 = new FileSearch_Phaser("C:\\Program Files", "log", phaser);
        FileSearch_Phaser r3 = new FileSearch_Phaser("D:\\", "log", phaser);

        Thread t1 = new Thread(r1, "t1");
        Thread t2 = new Thread(r2, "t2");
        Thread t3 = new Thread(r3, "t3");
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // check if phaser is terminated
        System.out.println(phaser.isTerminated() + " for this program phaser terminate");


    }
}
