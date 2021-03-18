package Executor_Submit_Callable_Future;

import java.util.concurrent.Callable;

public class FactorialCalculator implements Callable<Integer> {

    private int number;
    FactorialCalculator(int number){
        this.number = number;
    }
    @Override
    public Integer call() throws Exception {
        int result = 1;
        if (number == 0 || number == 1) result = 1;
        else {
            for (int i = 2; i <=number; i++) {
              result *=i;
            }
        }
        System.out.println(Thread.currentThread().getName() + " for number "+ number + " the result is "+ result);
        return result;
    }
}
