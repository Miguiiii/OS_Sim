/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package Structures;
import java.util.Iterator;
/**
 *
 * @author Miguel
 */
public class HashMap<K, V> implements Iterable<HashNode<K, V>> {

    private ArrayList<List<HashNode<K, V>>> buckets;
    private int capacity;
    private int size;

    public HashMap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.buckets = new ArrayList(capacity);
        
    }

    public ArrayList<List<HashNode<K, V>>> getBuckets() {
        return buckets;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return size;
    }
    
    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode())%capacity;
    }
    
    public V getValueOfKey(K key) {
        int index = getBucketIndex(key);
        List<HashNode<K, V>> bucket = getBuckets().getElmenetAtIndex(index);
        if (bucket == null) {
            System.out.println("A Value with this Key does not exist");
            return null;
        }
        for (int i = 0; i < bucket.getLength(); i++) {
            if (bucket.getElmenetAtIndex(i).getKey() == key) {
                return bucket.getElmenetAtIndex(i).getValue();
            }
        }
        System.out.println("A Value with this Key does not exist");
        return null;
    }
    
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        List<HashNode<K, V>> bucket = getBuckets().getElmenetAtIndex(index);
        if (bucket == null) {
            bucket = new List();
            HashNode<K, V> node = new HashNode(key, value);
            bucket.insertBegin(node);
            getBuckets().insertAtIndex(bucket, index);
        } else {
            int i = 0;
            for (; i < bucket.getLength(); i++) {
                if (bucket.getElmenetAtIndex(i).getKey() == key) {
                    HashNode<K, V> entry = bucket.getElmenetAtIndex(i);
                    entry.setValue(value);
                    break;
                }
            }
            if (i == bucket.getLength()) {
                HashNode<K, V> entry = new HashNode(key, value);
                bucket.insertBegin(entry);
            }
        }
        size++;
    }
    
    public V deleteEntry(K key) {
        int index = getBucketIndex(key);
        List<HashNode<K, V>> bucket = getBuckets().getElmenetAtIndex(index);
        if (bucket == null) {
            System.out.println("An Entry with this Key does not exist");
            return null;
        }
        int i = 0;
        for (; i < bucket.getLength(); i++) {
            if (bucket.getElmenetAtIndex(i).getKey() == key) {
                HashNode<K, V> entry = bucket.getElmenetAtIndex(i);
                return entry.getValue();
            }
        }
        System.out.println("An Entry with this Key does not exist");
        return null;
    }
    
    @Override
    public Iterator<HashNode<K, V>> iterator() {
        List<HashNode<K, V>> hash = new List();
        for (List<HashNode<K, V>> b:getBuckets()) {
            for (HashNode<K, V> node:b) {
                hash.insertFinal(node);
            }
        }
        return new IteratorList(hash);
    }
    
}