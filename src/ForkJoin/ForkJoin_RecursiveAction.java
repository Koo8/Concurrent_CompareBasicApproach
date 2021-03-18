package ForkJoin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *  ForkJoin Framework is to solve problems that can be broken into smaller tasks using
 * the divide and conquer technique. Extended from AbstractExecutorService, with its
 * work stealing algorithm( only ForkJoinTask has this feature, if it is a Runnable,
 * the pool won't have this feature), it is more efficient.
 * Two operations are involved : fork and join. This framework is not suitable for I/O operation.
 * Mainly this is done through two class: ForkJoinPool and ForkJoinTask. ForkJoinPool is the
 * executorService for running ForkJoinTasks(abstract class), which has three subclasses:
 * 1.RecursiveAction for tasks with no return result,
 * 2.RecursiveTask for tasks that return one result,
 * 3.CountedCompleter for tasks that launch a completion action when all the subTasks have finished.
 */ //  p222 - p232    Concurrent cookbook

public class ForkJoin_RecursiveAction {
    //static volatile int counter = 0;

    public static void main(String[] args) {
        Counter counter = new Counter();
        ForkJoinPool pool = new ForkJoinPool();      // with parallelism equal to Runtime#availableProcessors
        List<Product> list = new GenerateProductList(1000).getList();

        UpdatePriceTask task = new UpdatePriceTask(0, 1000,list, 0.20,counter);
        pool.submit(task);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();
        //System.out.println(list.get(0).getPrice());

//        if (pool.isTerminating()){
//           for (int i = 0; i < list.size(); i++) {
//                System.out.print(list.get(i).getPrice() + " ");
//                if (i%10 ==0 && i!= 0) System.out.println();
//            }
            System.out.println(list.get(0).getPrice());
//        }

        System.out.println(counter.getCount() + " times ");

    }
}

class UpdatePriceTask extends RecursiveAction {
    private int first, last;
    private List<Product> list;
    private double increment;
    private Counter counter;


    public UpdatePriceTask (int first, int last, List<Product> list, double increment,Counter counter){
        this.first = first;
        this.last = last;
        this.list = list;
        this.increment = increment;
        this.counter = counter;
    }

    @Override
    protected void compute() {  // not using fork() join()
        counter.increment();
        int threshold = 10;
        // use the fork then join algorithm
        if((last-first)<threshold) {
            doUpdate(first, last, list);
        } else {
            int middle = (first + last) /2;
            UpdatePriceTask task1 = new UpdatePriceTask(first, middle, list, increment, counter);
            UpdatePriceTask task2 = new UpdatePriceTask(middle, last, list, increment, counter);
            invokeAll(task1, task2);
        }
    }

    private void doUpdate(int first, int last, List<Product> list) {
        for (int i = first; i <last ; i++) {
            list.get(i).setPrice(list.get(i).getPrice()*(1+increment));
        }
    }


}

class Product {
    private String name;
    private double price;

    public Product(String name, double price){
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

class GenerateProductList{
    private int size;
    private List<Product> list;

    public GenerateProductList (int size) {
        this.size = size;
        list = new ArrayList<>();
        toGenerateList(size);
    }

    private void toGenerateList(int size) {

        for (int i = 0; i <size; i++) {
           Product p = new Product(("pro-"+ i),10);
           list.add(p);
        }
    }

    public List<Product> getList() {
        return list;
    }
}

class Counter {
    private int count;
    Counter (){
        count= 0;
    }
    public int increment(){
        return count++;
    }

    public int getCount() {
        return count;
    }
}
