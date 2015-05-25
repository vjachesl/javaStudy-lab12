package lab12;
import java.util.Random;
/**
 * Created by viacheslav on 20.05.15.
 На основании шаблона Producer-Consumer 1 реализовать блокирующий кольцевой буфер на N
 элементов 2 . Если буфер пуст, то читающие потоки должны ждать появления в нем элементов.
 Если буфер заполнен, то пишущие потоки должны ждать удаление элементов.
 *
 */

public class ProdConsumer {
    // Creating special Object for blocking producers or consumer.
    static Object monitor = new Object();

    // Running unit - it will Form new Circle Queue, 3 producers treads and 1 consumer thread;
    public static void main(String[] args) {
        MyCircleData data = new MyCircleData(16);
        Produser prod1 = new Produser("#1",data, monitor);
        Produser prod2 = new Produser("#2",data, monitor);
        Produser prod3 = new Produser("#3",data, monitor);
        Consumer cons = new Consumer(data, monitor);
        prod1.start();
        prod2.start();
        prod3.start();
        cons.start();
    }
}
    // Class for producer - Each produser has the same link to the monitor and to the data container
    // it transfered from running module throw constructors
    class Produser extends Thread {
        MyCircleData data;
        Object monitor;
        String id;

        // construct one producer
        public Produser(String id, MyCircleData data, Object monitor) {
            this.id = id;
            this.data = data;
            this.monitor = monitor;
        }
        // specific method for numbers generation
        public int generateData (){
            Random rnd = new Random();
            return rnd.nextInt();
        }
        @Override
        public void run() {
            while (true){
                    // all tasks will make in the sincronized block to ensure data container overflow
                    synchronized (monitor){
                    System.out.print("Produser "+id+" was put:  ");
                    Integer dataInt = generateData();
                    System.out.println(dataInt);
                    if (!data.isFull()) data.insert(dataInt);
                    monitor.notify();
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();  }

                    }
            }
        }
    }

   //  Class for consumer - Each consumer has the same link to the monitor and to the data container
   // it transfered from running module throw constructors
   class Consumer extends Thread {
       MyCircleData data;
        Object monitor;

        // Constructor
        public Consumer( MyCircleData data, Object monitor) {
            this.data = data;
            this.monitor = monitor;
        }

       // method for taking data - it will called from syncronized block in the run method
        public int getDataStoreData () {
            return (Integer) data.remove();

        }
        @Override
        public void run() {
            while (true){
                synchronized (monitor) {
                    // all tasks will make in the sincronized block to ensure data null pointer Exception
                    if (!data.isEmpty()) {
                        System.out.print("Consumer was pick:  ");
                        System.out.println(getDataStoreData()); }
                    monitor.notify();
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    // Circle Queue data container
    class MyCircleData {
        private int maxSize; // size for data
        private Object [] queArray;
        private int beginPointer;
        private int endPointer;
        private int nItems; // item's quantity, was putted into the data store

        // Construct new data structure with the specific size
        public MyCircleData(int maxSize) {
            this.maxSize = maxSize;
            queArray = new Object [maxSize];
            beginPointer = 0;
            endPointer = -1;
            nItems = 0;
        }

        // insert new element into data structure
        public void insert(Object j) {
            if(isFull()) throw new IllegalStateException("Queue is full");
            if (endPointer == maxSize - 1)  endPointer = -1;
            queArray[++endPointer] = j;
            nItems++;  }

        // removing the first element from the data Structure
        public Object remove() {
            if(isEmpty()) throw new IllegalStateException("Queue is empty");
            Object temp = queArray[beginPointer++];
            if (beginPointer == maxSize) beginPointer = 0;
            nItems--;
            return temp;
        }

        // take without removing the first element from the data Structure;
        public Object peekFront() {
            return queArray[beginPointer];
        }

        // check is the circle queue is empty;
        public boolean isEmpty() {
            return (nItems == 0);
        }

        // check is the circle queue is empty;
        public boolean isFull() {
            return (nItems == maxSize);
        }

        // returns the stored elements quantity ;
        public int size() {
            return nItems;
        }
    }

