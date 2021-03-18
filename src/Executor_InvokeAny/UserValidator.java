package Executor_InvokeAny;

import java.util.Random;

public class UserValidator {

    private String name;
    public UserValidator(String name ){ // name could be database name etc.
        this.name = name;
    }

    public boolean validateUser(String userName, String userPassword){
       // System.out.print("\nstart to validate the user "+ userName);
        Random random = new Random();
        try{
            long duration = (long) (Math.random()*10);
            System.out.print(name+ " that takes " + duration + "seconds\n" );
            Thread.sleep(duration/100); // to save time of running this program

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return random.nextBoolean();
    }

    public String getName() {
        return name;
    }
}
