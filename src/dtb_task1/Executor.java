package dtb_task1;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс Исполнителя.
 * 
 * Хранит в себе очередь задач.
 * Исполняет задачи по одной, забирая их из очереди задач.
 * 
 * @author akropon
 */
public class Executor extends Thread{
    private final TaskManager _taskManager;
    private final LimitedQueue<Runnable> _taskQueue;
    private boolean _mayWork;
    
    /**
     * Конструктор 
     * 
     * @param taskManager - Менеджер задач
     * @param queueSize - максимальный размер прикрепленной очереди задач Исполнителя
     */
    public Executor(TaskManager taskManager, int queueSize) {
        _taskManager = taskManager;
        _taskQueue = new LimitedQueue<>(Runnable.class, queueSize);
        _mayWork = true;
    }

    /**
     * Получить Очередь задач Исполнителя
     * @return 
     */
    public LimitedQueue<Runnable> getTaskQueue() {
        return _taskQueue;
    }
    
    /**
     * Уведомить потом Исполнителя о том, что пора завершаться
     */
    public void needToEnd() {
        synchronized(_taskQueue) {
            _mayWork = false;
            _taskQueue.notifyAll();
        }
    }
    
    /**
     * Код исполнения потока Исполнителя
     */
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
        
        //debug
        //System.out.println("Executor (thread="+Thread.currentThread().getId()+"): Завершаюсь.");
    }
    
}
