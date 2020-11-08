
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final LinkedList<Integer> buffer;
    private final int LIMIT;
    private final ReentrantLock lock;
    private final Condition producerCondition;
    private final Condition consumerCondition;
    private final Condition otherProducerCondition;
    private final Condition otherConsumerCondition;
    private boolean isProducerConditionalEmpty = true;
    private boolean isConsumerConditionalEmpty = true;
    private int countProducerConditional = 0;
    private int countConsumerConditional = 0;
    private int countProducerOthersConditional = 0;
    private int countConsumerOthersConditional = 0;

    public Buffer(int m) {
        this.buffer = new LinkedList<Integer>();
        this.LIMIT = m * 2;
        this.lock = new ReentrantLock();
        this.producerCondition = lock.newCondition();
        this.consumerCondition = lock.newCondition();
        this.otherProducerCondition = lock.newCondition();
        this.otherConsumerCondition = lock.newCondition();

    }

    public  void put(int[] toProduce) {
        lock.lock();

        try {
            countProducerOthersConditional++;
            while(lock.hasWaiters(producerCondition)){
                System.out.println("PRODUCER id: " + Thread.currentThread().getId() + " is waiting on otherProducerCondition; " + countProducerOthersConditional);
                otherProducerCondition.await();
            }
            if(countProducerOthersConditional > 0){
                countProducerOthersConditional--;
            }
            countProducerConditional++;
            while( buffer.size() + toProduce.length  > LIMIT ) {
                System.out.println("PRODUCER id: " + Thread.currentThread().getId() + " is waiting producerCondition; " + countProducerConditional);
                isProducerConditionalEmpty = false;
                producerCondition.await();

            }
            if(countProducerConditional > 0){
                countProducerConditional--;
            }


            isProducerConditionalEmpty = true;
            for( int j = 0; j<toProduce.length; j++){
                buffer.add(toProduce[j]);
            }

            System.out.println("Producer id: " + Thread.currentThread().getId() + " put: " + toProduce.length );
            System.out.println("Buffer size: " + buffer.size());


            otherProducerCondition.signal();
            consumerCondition.signal();
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void get(int toConsume) {

        lock.lock();

        try {

            countConsumerOthersConditional++;
            while(lock.hasWaiters(consumerCondition)){
                System.out.println("CONSUMER id: " + Thread.currentThread().getId() + " is waiting on otherConsumerCondition; " + countConsumerOthersConditional);
                otherConsumerCondition.await();
            }
            if(countConsumerOthersConditional > 0){
                countConsumerOthersConditional--;
            }

            countConsumerConditional++;
            while( buffer.size() - toConsume < 0 ) {
                System.out.println("CONSUMER id: " + Thread.currentThread().getId() + " is waiting on consumerCondition; " + countConsumerConditional);
                isConsumerConditionalEmpty = false;
                consumerCondition.await();
            }
            if(countConsumerConditional > 0){
                countConsumerConditional--;
            }
            isConsumerConditionalEmpty = true;
            for( int j = 0; j < toConsume; j++ ) {
                buffer.removeFirst();
            }
            System.out.println("CONSUMER id: " + Thread.currentThread().getId() + " get: " + toConsume);
            System.out.println("Buffer size:" + buffer.size());


            otherConsumerCondition.signal();
            producerCondition.signal();
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}





















































































//
//
//public class ProdConsRandLockManagerImpl implements ProdConsManager {
//
//    private final LinkedList<Integer> buffer;
//    private final int LIMIT;
//    private final Random rand;
//    private final Lock lock;
//    private final Condition producerCondition;
//    private final Condition consumerCondition;
//    private final Condition bigProducerCondition;
//    private final Condition bigConsumerCondition;
//    private final int m;
//
//    public ProdConsRandLockManagerImpl( int maxProductions, int m ) {
//
//        this.buffer = new LinkedList<Integer>();
//        this.LIMIT = 2*m;
//        this.rand = new Random();
//        this.lock = new ReentrantLock();
//        this.producerCondition = lock.newCondition();
//        this.consumerCondition = lock.newCondition();
//        this.bigProducerCondition = lock.newCondition();
//        this.bigConsumerCondition = lock.newCondition();
//        this.m = m;
//    }
//
//    public void produce() throws InterruptedException {
//        int i = 0;
//        lock.lock();
//
//        try {
//            while ( true ) {
//                int toProduce = rand.nextInt( m );
//                while( buffer.size() + toProduce  > LIMIT ) {
//                    System.out.println("PRODUCER IS WATING " + Thread.currentThread().getName() );
//
//                    if( toProduce > 0.5 * m ) {
//                        bigProducerCondition.await();
//                    }
//                    else {
//                        producerCondition.await();
//                    }
//
//                }
//                for( int j = 0; j < toProduce; j++ ) {
//                    int val = rand.nextInt(1000);
//                    buffer.add(val);
//                }
//                System.out.println("I'm producer " + Thread.currentThread().getName() );
//                System.out.println("Buffer size: " + buffer.size());
//
//                bigConsumerCondition.signal();
//                consumerCondition.signalAll();
//            }
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    @Override
//    public void consume() throws InterruptedException {
//        int i = 0;
//
//        lock.lock();
//
//        try {
//            while( true ) {
//                int toConsume = rand.nextInt( m );
//                while( buffer.size() - toConsume < 0 ) {
//                    System.out.println("CONSUMER IS WATING " + Thread.currentThread().getName());
//
//                    if( toConsume > 0.5 * m ) {
//                        bigConsumerCondition.await();
//                    }
//                    else {
//                        consumerCondition.await();
//                    }
//
//                }
//                for( int j = 0; j < toConsume; j++ ) {
//                    buffer.removeFirst();
//                }
//                System.out.println("I'm consumer " + Thread.currentThread().getName() );
//                System.out.println("Buffer size:" + buffer.size());
//
//                bigProducerCondition.signal();
//                producerCondition.signalAll();
//
//            }
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }
//    }
//}
