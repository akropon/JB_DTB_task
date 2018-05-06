package dtb_task1;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class task1 {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Executor executor1 = new Executor(taskManager, 1);
        Executor executor2 = new Executor(taskManager, 2);
        Executor executor3 = new Executor(taskManager, 3);
        Executor executor4 = new Executor(taskManager, 4);
        Executor[] executors = {executor1, executor2, executor3, executor4};
        for (int i=0; i<executors.length; i++)
            taskManager.addExecutor(executors[i]);
        
        
        for (int i=0; i<executors.length; i++)
            System.out.println("Executor["+i+"] thread id = "+executors[i].getId());
        
        
        for (int i=0; i<executors.length; i++) 
            executors[i].start();
        taskManager.start();
        
        MyTask[] tasks = new MyTask[40];
        for (int i=0; i<tasks.length; i++)
            tasks[i] = new MyTask(i, 300);
        
        for (int i=0; i<tasks.length/2; i++)
            taskManager.addTask(tasks[i]);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(task1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (int i=tasks.length/2; i<tasks.length; i++)
            taskManager.addTask(tasks[i]);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(task1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        taskManager.needToEnd();
        for (Executor executor : taskManager.getExecutors())
            executor.needToEnd();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(task1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private static class MyTask implements Runnable {
        int id;
        long millis;

        public MyTask(int id, long millis) {
            this.id = id;
            this.millis = millis;
        }
        
        
        
        @Override
        public void run() {
            System.out.println(String.format("Task №%2d: thread=%2d, Begin", id, Thread.currentThread().getId()));
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(task1.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(String.format("Task №%2d: thread=%2d, End", id, Thread.currentThread().getId()));
        }
        
    }
    
}
