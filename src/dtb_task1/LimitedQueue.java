package dtb_task1;

import java.lang.reflect.Array;

public class LimitedQueue<T> {
    private final int _maxSize;
    private final T[] _elements;
    private int _size;
    private int _headIndex;
    private int _tailIndex;
    

    public LimitedQueue(Class<T> clazz, int maxSize) {
        if (maxSize<1) 
            throw new IllegalArgumentException("maxSize is "+maxSize+", but shoult be > 0");
        
        _maxSize = maxSize;
        _elements = (T[])Array.newInstance(clazz, _maxSize);
        _size = 0;
        _headIndex = 0;
        _tailIndex = 0;
    }

    public int getMaxSize() {
        return _maxSize;
    }
    
    public int getSize() {
        return _size;
    }
    
    public boolean isFull() {
        return _size == _maxSize;
    }
    
    public T getElement() {
        if (_size>0) {
            T result = _elements[_headIndex];
            _size--;
            _headIndex = (_headIndex+1)%_maxSize;
            return result;
        } else {
            return null;
        }
    }
    
    public boolean putElement(T element) {
        if (_size<_maxSize) {
            _size++;
            _elements[_tailIndex] = element;
            _tailIndex = (_tailIndex+1)%_maxSize;
            return true;
        } else {
            return false;
        }
    }
    
    public void clear() {
        for (int i=0; i<_maxSize; i++) 
            _elements[i] = null;
        _size = 0;
        _headIndex = 0;
        _tailIndex = 0;
    }
    
    /**
     * Загруженность очереди
     * 
     * @return степень заполненности очереди 
     *         в диапазоне от 0 до максимального размера очереди
     */
    public double fullness() {
        return (double)_size / _maxSize;
    }
}
