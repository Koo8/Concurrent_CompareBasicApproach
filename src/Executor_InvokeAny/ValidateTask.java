package Executor_InvokeAny;

import java.util.concurrent.Callable;

public class ValidateTask implements Callable<Boolean> {
    private UserValidator userValidator;
    private String userName;
    private String userPassword;
    public ValidateTask(UserValidator userValidator, String userName, String userPassword){
        this.userValidator = userValidator;
        this.userName = userName;
        this.userPassword = userPassword;
    }


    @Override
    public Boolean call() throws Exception {
        boolean result =
        userValidator.validateUser(userName, userPassword);
        if(!result ){
            System.out.println(userValidator.getName() +" couldn't find the user " + userName);
            throw new Exception("Error validating user");
        }
        System.out.println(userValidator.getName()+ " found user "+ userName);
        return result;
    }
}
