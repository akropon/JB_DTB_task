package dtb_task1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author akropon
 */
public class TaskManagerTest {
    
    TaskManager taskManager;
    Executor executor1;
    Executor executor2;
    Executor executor3;
    Executor executor4;
    Executor[] executors;
    
    public TaskManagerTest() {
    }
    
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
     * Test of addExecutor method, of class TaskManager.
     */
    @Test
    public void testAddExecutor() {
        System.out.println("addExecutor");
        
        assertEquals(0, taskManager.getExecutors().size());
        
        taskManager.addExecutor(executor1);
        assertEquals(1, taskManager.getExecutors().size());
        assertEquals(executor1, taskManager.getExecutors().get(0));

        taskManager.addExecutor(executor2);
        assertEquals(2, taskManager.getExecutors().size());
        assertEquals(executor1, taskManager.getExecutors().get(0));
        assertEquals(executor2, taskManager.getExecutors().get(1));
    }

    /**
     * Test of needToEnd method, of class TaskManager.
     */
    @Test
    public void testNeedToEnd_noCommonTasks() {
        System.out.println("needToEnd");
        
        taskManager.addExecutor(executor1);
        taskManager.addExecutor(executor2);
        taskManager.start();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(taskManager.isAlive());
        
        taskManager.needToEnd();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertFalse(taskManager.isAlive());
    }

    @Test
    public void testNeedToEnd_allExecutorsAreFull() {
        System.out.println("needToEnd");
        
        // заполняем очереди исполнителей полностью
        executor1.getTaskQueue().putElement(() -> {
        });
        executor2.getTaskQueue().putElement(() -> {
        }); 
        executor2.getTaskQueue().putElement(() -> {
        });
        
        // кладем задачу в общую очередь
        taskManager.addTask(() -> {
        });
        
        taskManager.addExecutor(executor1);
        taskManager.addExecutor(executor2);
        taskManager.start();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(taskManager.isAlive());
        
        taskManager.needToEnd();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertFalse(taskManager.isAlive());
    }
    
    @Test
    public void testNeedToEnd_inManagmentProcess() {
        System.out.println("needToEnd");
        
        taskManager.addExecutor(executor1);
        taskManager.addExecutor(executor2);
        taskManager.addExecutor(executor3);
        taskManager.addExecutor(executor4);
        taskManager.start();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(taskManager.isAlive());
        
        // добавлением задачи провоцируем менеджер выйти из режима ожидания
        taskManager.addTask(() -> {
        });
        // и останавливаем менеджер
        taskManager.needToEnd();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertFalse(taskManager.isAlive());
    }
    
    /**
     * Test of run method, of class TaskManager.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        
        System.out.println("needToEnd");
        
        taskManager.addExecutor(executor1);
        
        assertFalse(taskManager.isAlive());
        
        taskManager.start();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(taskManager.isAlive());
    }

    /**
     * Test of getExecutors method, of class TaskManager.
     */
    @Test
    public void testGetExecutors() {
        System.out.println("getExecutors");
        
        assertNotNull(taskManager.getExecutors());
        assertEquals(0, taskManager.getExecutors().size());
    }

    /**
     * Test of getCommonTaskQueue method, of class TaskManager.
     */
    @Test
    public void testGetCommonTaskQueue() {
        System.out.println("getCommonTaskQueue");
        
        assertNotNull(taskManager.getExecutors());
        assertEquals(0, taskManager.getExecutors().size());
    }

    /**
     * Test of addTask method, of class TaskManager.
     */
    @Test
    public void testAddTask_withoutRunning() {
        System.out.println("addTask");
        
        assertEquals(0, taskManager.getCommonTaskQueue().size());
        
        Runnable task1 = () -> {
        };
        Runnable task2 = () -> {
        };
        
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertEquals(task1, taskManager.getCommonTaskQueue().removeFirst());
        assertEquals(task2, taskManager.getCommonTaskQueue().removeFirst());
        
    }
    
    @Test
    public void testAddTask_withRunning() {
        System.out.println("addTask");
        
        taskManager.addExecutor(executor4);
        assertEquals(0, taskManager.getCommonTaskQueue().size());
        
        taskManager.start();
        
        Runnable task1 = () -> {
        };
        Runnable task2 = () -> {
        };
        
        // добавляем задачи в общую очередь
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        
        // Ждем, чтобы задачи распределились из общей очереди в очередь исполнителя
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        // проверяем, что задач в общей очереди нет, зато они есть в очереди исполнителя
        
        assertEquals(0, taskManager.getCommonTaskQueue().size());
        
        assertEquals(task1, executor4.getTaskQueue().getElement());
        assertEquals(task2, executor4.getTaskQueue().getElement());
    }
    
}
