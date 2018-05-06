package dtb_task1;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Executor extends Thread{
    private final TaskManager _taskManager;
    private final LimitedQueue<Runnable> _taskQueue;
    private boolean _mayWork;
    
    public Executor(TaskManager taskManager, int queueSize) {
        _taskManager = taskManager;
        _taskQueue = new LimitedQueue<>(Runnable.class, queueSize);
        _mayWork = true;
    }

    
//    public boolean addTask(Runnable task) {
//        if (_taskQueue.isFull())
//            return false;
//        else {
//            _taskQueue.putElement(task);
//            return true;
//        }
//    }

    public LimitedQueue<Runnable> getTaskQueue() {
        return _taskQueue;
    }
    
    public void needToEnd() {
        synchronized(_taskQueue) {
            _mayWork = false;
            _taskQueue.notifyAll();
        }
    }
    
    @Override
    public void run() {
        Runnable task;
        
        MAINCYCLE:
        while (_mayWork) {
        
            synchronized(_taskQueue) {
                while (_taskQueue.getSize() == 0)
                    try {
                        _taskQueue.wait();
                        if (!_mayWork) break MAINCYCLE;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                task = _taskQueue.getElement();
            }
            
            synchronized(_taskManager.notificator) {
                _taskManager.someExecutorTookNewTask = true;
                _taskManager.notificator.notify();
            }
            
            // поток не должен останавливаться из-за ошибок в исполняемой задаче
            try {
                task.run();
            } catch (Exception ex) {
                Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, "exception in task", ex);
            }
        } 
        
        System.out.println("Executor (thread="+Thread.currentThread().getId()+"): Завершаюсь.");
    }
    
}
