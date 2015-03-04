// Adapted from http://stackoverflow.com/questions/3110547/java-how-to-create-new-entry-key-value
// Map.Entry cannot be instantiated because it's a generic type
// Used to create entry object to store Key, Value pair

import java.util.Map;

public class MyEntry<K, V> implements Map.Entry<K, V>{
	private K key;
    private V value;

    public MyEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
