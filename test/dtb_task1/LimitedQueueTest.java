package dtb_task1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author akropon
 */
public class LimitedQueueTest {
    
    LimitedQueue<Integer> sQueue;
    int queueMaxLength;
    
    public LimitedQueueTest() {
    }
    
    @Before
    public void setUp() {
        queueMaxLength = 4;
        sQueue = new LimitedQueue<>(Integer.class, queueMaxLength);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getMaxSize method, of class LimitedQueue.
     */
    @Test
    public void testGetMaxSize() {
        System.out.println("getMaxSize");
        assertEquals(queueMaxLength, sQueue.getMaxSize());
    }

    /**
     * Test of getSize method, of class LimitedQueue.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        
        assertEquals(0, sQueue.getSize());
        
        for (int i=0; i<queueMaxLength; i++) {
            sQueue.putElement(i);
            assertEquals(i+1, sQueue.getSize());
        }
    }

    /**
     * Test of isFull method, of class LimitedQueue.
     */
    @Test
    public void testIsFull() {
        System.out.println("isFull");
        
        assertEquals(0, sQueue.getSize());
        
        for (int i=0; i<queueMaxLength-1; i++) {
            sQueue.putElement(i);
            assertFalse(sQueue.isFull());
        }
        sQueue.putElement(1);
        assertTrue(sQueue.isFull());
    }

    /**
     * Test of putElement method, of class LimitedQueue.
     */
    @Test
    public void testPutElement() {
        System.out.println("putElement");
        
        assertEquals(0, sQueue.getSize());
        
        assertTrue(sQueue.putElement(0));
        assertEquals(1, sQueue.getSize());
        
        for (int i=1; i<queueMaxLength; i++) {
            assertTrue(sQueue.putElement(i));
            assertEquals(i+1, sQueue.getSize());
        }
        
        assertFalse(sQueue.putElement(100));
        assertEquals(queueMaxLength, sQueue.getSize());
    }

    /**
     * Test of getElement method, of class LimitedQueue.
     */
    @Test
    public void testGetElement() {
        assertNull(sQueue.getElement());
        
        sQueue.putElement(1);
        assertEquals(Integer.valueOf(1), sQueue.getElement());
        assertNull(sQueue.getElement());
        
        sQueue.putElement(2);
        sQueue.putElement(3);
        assertEquals(Integer.valueOf(2), sQueue.getElement());
        assertEquals(Integer.valueOf(3), sQueue.getElement());
        assertNull(sQueue.getElement());
        
        for (int i=0; i<queueMaxLength; i++)
            sQueue.putElement(i);
        for (int i=0; i<queueMaxLength; i++)
            assertEquals(Integer.valueOf(i), sQueue.getElement());
        assertNull(sQueue.getElement());
    }

    /**
     * Test of clear method, of class LimitedQueue.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        
        
        for (int i=0; i<queueMaxLength/2; i++)
            sQueue.putElement(i);
        
        sQueue.clear();
        assertEquals(0, sQueue.getSize());
    }

    /**
     * Test of fullness method, of class LimitedQueue.
     */
    @Test
    public void testFullness() {
        System.out.println("fullness");
        
        assertEquals(0, sQueue.fullness(), queueMaxLength / 4.0);
        for (int i=0; i<queueMaxLength; i++) {
            sQueue.putElement(i);
            assertEquals((i+1.0)/queueMaxLength, 
                    sQueue.fullness(), 
                    queueMaxLength / 4.0);
        }
    }
    
}
