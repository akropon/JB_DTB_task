package systemTest;

import dtb_task1.Executor;
import dtb_task1.TaskManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author akropon
 */
public class SystemTest {
    TaskManager taskManager;
    Executor executor1;
    Executor executor2;
    Executor executor3;
    Executor executor4;
    Executor[] executors;
    
    @Before
    public void setUp() {
        taskManager = new TaskManager();
        executor1 = new Executor(taskManager, 1);
        executor2 = new Executor(taskManager, 2);
        executor3 = new Executor(taskManager, 3);
        executor4 = new Executor(taskManager, 4);
        executors = new Executor[] {executor1, executor2, executor3, executor4};
    }
    
    @After
    public void tearDown() {
        taskManager.needToEnd();
        for (Executor executor : executors)
            executor.needToEnd();
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
    }
    
    
    /**
     * 4 исполнителя. 8 "длительных" задач подаются 
     *  c небольшими промежутками во времени в раз на общую очередь. 
     * За "небольшой промежуток времени" менеджер должен успеть распределить
     *  задачу в очередь нужного исполнителя, а тот в свою очередь должен успеть 
     *  ее от туда забрать на исполнение.
     * Таким образом 1я задача через очередь первого исполнителя 
     *   провалится на исполнение в первый исполнитель,
     * 2я попадет в очередь первого исполнителя, тем самым заняв ее полностью.
     * 3я попадет в очередь 2го исполнителя и провалится на исполнение
     * 4я втанет в очереди 2го исполнителя
     * и т.д.
     * 
     * Таким образом в каждом исполнителе в итоге обработаются по 2 задачи,
     * это здесь и проверяется.
     */
    @Test
    public void test1() {
        for (Executor executor : executors)
            taskManager.addExecutor(executor);
        
        final long[] threadsID = new long[executors.length];
        final int[] threadsTaskCounter = new int[executors.length];
        for (int i=0; i<executors.length; i++)
            threadsID[i] = executors[i].getId();
        
        Runnable task = () -> {
            try {
                Thread.sleep(400);
            } catch (InterruptedException ex) {
                Logger.getLogger(SystemTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for (int i=0; i<executors.length; i++)
                if (threadsID[i]==Thread.currentThread().getId()) {
                    synchronized (threadsTaskCounter) {
                        threadsTaskCounter[i]++;
                        return; 
                    }
                }
        };
        
        
        for (int i=0; i<executors.length; i++)
            executors[i].start();
        taskManager.start();
        
        for (int i=0; i<8; i++) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                fail("InterruptedException: "+ex);
            }
            taskManager.addTask(task);
        }
        
        // в каждом исполнителе надо выполнить максимум 2 задачи по 400мс,
        //   значит, если подождать 1000мс, то все задачи должны выполниться
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        // Проверяем предположение
        for (int i=0; i<executors.length; i++)
            assertEquals(2, threadsTaskCounter[i]);
    }
    
    
    
    /**
     * Нагрузочный тест. 
     * 
     * На общую очередь сразу подается 100 задач.
     * Каждая задача вызывает Thread.sleep(...) на 100мс 
     *  и не выполняет других долгих действий. Следовательно, можно 
     *  считать, что 1 задача выполняется за 100мс. 
     * Если бы 100 таких задач выполнялись в одном потоке, 
     *  то по времени это заняло бы около 10 секунд.
     * Но это 100 задач будут исполняться в многопоточной
     *  системе с 4мя исполнителями.
     * Так как каждая задача использует метод Thread.sleep(...), то
     *  она не грузит процессор, а следовательно не мешает выполняться другим
     *  задачам. Тогда можно считать, что задачи выполняются параллельно
     *  и независимо. Следовательно в 4х потоках эти задачи выполнятся примерно
     *  в 4 раза быстрее, т.е. за 2.5 секунд.
     * Чтобы проверить правильность утверждения, запустим данную систему на 3с
     *  и проверим, выполнились ли все задачи.
     */
    @Test
    public void test2() {
        for (Executor executor : executors)
            taskManager.addExecutor(executor);
        
        final int[] taskCounter = new int[1];
        
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(SystemTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            synchronized (taskCounter) {
                taskCounter[0]++;
            }
        };
        
        
        for (int i=0; i<100; i++) {
            taskManager.addTask(task);
        }
        
        for (int i=0; i<executors.length; i++)
            executors[i].start();
        taskManager.start();
        
        // Ждем 2 секунды
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        // Проверяем предположение (за 2 секунды точно не успеем)
        assertNotEquals(100, taskCounter[0]);
        
        // ждем еще 1 секунду (В итоге будет 3 секунды со старта)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        // Проверяем предположение (за 3 должны успеть)
        assertEquals(100, taskCounter[0]);
    }
}
