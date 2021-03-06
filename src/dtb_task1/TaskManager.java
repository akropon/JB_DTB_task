package dtb_task1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Менеджер задач.
 * 
 * Хранит в себе Общую очередь задач.
 * Распределяет задачи из общей очереди по Исполнителям по принципу
 *   наименьшей загруженности.
 * 
 * @author akropon
 */
public class TaskManager extends Thread{
    /** 
     * Notificator - объект для синхронизации и  уведомления потока данного класса
     */ 
    public final Object notificator = new Object();
    
    /**
     * флаг "неизменности состояния очередей Исполнителей"
     */
    public boolean someExecutorTookNewTask;
    
    private final LinkedList<Runnable> _commonTaskQueue = new LinkedList<>();
    private final ArrayList<Executor> _executors = new ArrayList<>();
    private boolean _mayWork = true;

    /**
     * Добавить еще один Исполнитель.
     * Добавлять исполнители можно только перед стартом потока.
     * 
     * @param executor - Исполнитель
     * @return - true, если успешно, false, если Исполнитель добавлен не был.
     */
    public boolean addExecutor(Executor executor) {
        if (this.getState() == Thread.State.NEW) {
            _executors.add(executor);
            return true; 
        } else
            return false;
    }

    /**
     * Уведомить поток о том, что пора завершиться
     */
    public void needToEnd(/*boolean waitForExecutingAllTasks*/) {
        synchronized(notificator) {
            _mayWork = false;
            notificator.notifyAll();
        }
    }

    /**
     * Код исполнения потока Менеджера задач
     */
    @Override
    public void run() {
        double minFullness;
        Executor executorWithMinFullness;
        double fullness;
        Runnable task;
        
        MAINCYCLE:
        while (_mayWork) {
            
            someExecutorTookNewTask = false;
            
            executorWithMinFullness = null;
            minFullness = 2; // 2 точно больше, чем максимально возможное fullness
            for (Executor executor : _executors) {
                fullness = executor.getTaskQueue().fullness();
                if (fullness < minFullness) {
                    executorWithMinFullness = executor;
                    minFullness = fullness;
                }
            }
            
            if (executorWithMinFullness.getTaskQueue().getSize() 
                    == executorWithMinFullness.getTaskQueue().getMaxSize()) {
                // значит, все очереди забиты
                // теперь надо проверить, что ничего не изменилось за время проверки
                synchronized(notificator) {
                    if (!someExecutorTookNewTask) {
                        // действительно ничего не изменилось, можно войти в режим ожидания
                        try {
                            notificator.wait();
                            if (!_mayWork) break MAINCYCLE;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    // одна из очередей все же освободилась, нужно повторить проверку    
                }
            } else {
                // нашли наименее загруженную очередь, причем она не заполнена до конца
                synchronized(notificator) {
                    if (_commonTaskQueue.isEmpty()) {
                        // входящих задач нет, можно войти в режим ожидания
                        try {
                            notificator.wait();
                            
                            
                            
                            if (!_mayWork) break MAINCYCLE;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // вышли их ожидания, следует начать все сначала
                        continue;
                    } else {
                        // задачи в общей очереди есть, берем одну
                        task = _commonTaskQueue.removeFirst();
                    }
                }
                
                // добавляем задачу в нужную очередь и уведомляем исполнителя на случай, если он ожидает
                synchronized (executorWithMinFullness.getTaskQueue()) {
                    executorWithMinFullness.getTaskQueue().putElement(task);
                    executorWithMinFullness.getTaskQueue().notifyAll();
                }
            }
        }
        
        // debug
        // System.out.println("TaskManager (thread="+Thread.currentThread().getId()+"): Завершаюсь.");
        
    }

    /**
     * Получить список Исполнителей
     * 
     * @return список Исполнителей
     */
    public ArrayList<Executor> getExecutors() {
        return _executors;
    }

    /**
     * Получить Общую очередь задач
     * 
     * @return Общая очередь задач
     */
    public LinkedList<Runnable> getCommonTaskQueue() {
        return _commonTaskQueue;
    }
    
    /**
     * Метод для добавления новой задачи в Общую очередь из внешнего потока
     * 
     * @param task - задача
     */
    public void addTask(Runnable task) {
        synchronized (notificator) {
            _commonTaskQueue.addLast(task);
            notificator.notifyAll();
        }
    }
    
}
