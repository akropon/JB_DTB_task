package dtb_task1;

import java.lang.reflect.Array;

/**
 * Ограниченная очередь.
 * Основана на массиве с головным и хвостовым индексом.
 * Добавление и удаление элемента производится за О(1),
 * никаких выделений памяти не производится.
 * 
 * @author akropon
 * @param <T> - тип хранимых объектов
 */
public class LimitedQueue<T> {
    private final int _maxSize;
    private final T[] _elements;
    private int _size;
    private int _headIndex;
    private int _tailIndex;
    
    /**
     * Конструктор
     * 
     * @param clazz - класс хранимых объектов
     * @param maxSize - максимальный размер очереди
     */
    public LimitedQueue(Class<T> clazz, int maxSize) {
        if (maxSize<1) 
            throw new IllegalArgumentException("maxSize is "+maxSize+", but shoult be > 0");
        
        _maxSize = maxSize;
        _elements = (T[])Array.newInstance(clazz, _maxSize);
        _size = 0;
        _headIndex = 0;
        _tailIndex = 0;
    }

    /**
     * Получить максимальный размер очереди
     * 
     * @return максимальный размер очереди
     */
    public int getMaxSize() {
        return _maxSize;
    }
    
    /**
     * Получить текущий размер очереди
     * 
     * @return текущий размер очереди
     */
    public int getSize() {
        return _size;
    }
    
    /**
     * Проверить, полная ли очередь
     * 
     * @return true, если очередь полная, false иначе
     */
    public boolean isFull() {
        return _size == _maxSize;
    }
    
    /**
     * Получить элемент из головы очереди с удалением этого элемента из очереди.
     * 
     * @return элемент из головы очереди, null-если очередь пуста
     */
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
    
    /**
     * Поместить элемент в конец очереди
     * 
     * @param element элемент
     * @return true - если успешно, false, если очередь переполнена и поместить
     *              этот элемент не удалось
     */
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
    
    /**
     * Удалить все элементы из очереди.
     */
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
