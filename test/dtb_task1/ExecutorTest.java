package dtb_task1;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author akropon
 */
public class ExecutorTest {
    
    TaskManager taskManager;
    Executor sExecutor;
    int sQueueMaxSize;
    
    public ExecutorTest() {
    }
    
    @Before
    public void setUp() {
        taskManager = new TaskManager();
        sQueueMaxSize = 10;
        sExecutor = new Executor(taskManager, sQueueMaxSize);
    }
    
    @After
    public void tearDown() {
    }
    

    /**
     * Test of getTaskQueue method, of class Executor.
     */
    @Test
    public void testGetTaskQueue() {
        LimitedQueue<Runnable> queue = sExecutor.getTaskQueue();
        assertNotNull(queue);
        assertEquals(queue.getMaxSize(), sQueueMaxSize);
        assertEquals(queue.getSize(), 0);
    }

    /**
     * Test of needToEnd method, of class Executor.
     */
    @Test
    public void testNeedToEnd() {
        System.out.println("testNeedToEnd");
        sExecutor.start();
        sExecutor.needToEnd();
        try {
            sExecutor.join(200);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        assertFalse(sExecutor.isAlive());
    }
    
    @Test
    public void testNeedToEnd1() {
        System.out.println("testNeedToEnd1");
        
        sExecutor.getTaskQueue().putElement(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                fail("InterruptedException: "+ex);
            }
        });
        
        sExecutor.start();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        sExecutor.needToEnd();
        
        try {
            sExecutor.join(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(sExecutor.isAlive());
        
        try {
            sExecutor.join(200);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertFalse(sExecutor.isAlive());
    }

    /**
     * Test of run method, of class Executor.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        
        final boolean[] step = {false, false};
        
        Runnable task1 = () -> {
            step[0] = true;
        };
        Runnable task2 = () -> {
            step[1] = true;
        };
        
        sExecutor.getTaskQueue().putElement(task1);
        sExecutor.getTaskQueue().putElement(task2);
        sExecutor.start();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(step[0]);
        assertTrue(step[1]);
        
        step[0] = false;
        step[1] = false;
        
        synchronized(sExecutor.getTaskQueue()) {
            sExecutor.getTaskQueue().putElement(task1);
            sExecutor.getTaskQueue().notifyAll();
        }
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(step[0]);
        assertFalse(step[1]);
        
        
        synchronized(sExecutor.getTaskQueue()) {
            sExecutor.getTaskQueue().putElement(task2);
            sExecutor.getTaskQueue().notifyAll();
        }
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            fail("InterruptedException: "+ex);
        }
        
        assertTrue(step[0]);
        assertTrue(step[1]);
        
    }
    
}
