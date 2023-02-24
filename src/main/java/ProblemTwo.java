import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/* Problem 2: Minotaur's Crystal Vase
 * We have N guests (N threads, let N = 100)
 * We have one room with one door (Single object with a lock)
 * Strategy 1 (Spin-Lock?)
 *  Any guess can stop by and check if locked
 *  Each thread has a loop and tries to visit the room
 *  Pros: Simple to implement (like problem 1), not bad for few guests
 *  Cons: Large groups waiting, no guarantee if able to visit or when
 * Strategy 2 (Flag)
 *  "Available/Busy" Sign set by guests upon entering and leaving
 *  Separate flag that threads check before attempting to visit
 *  Pros: Not everyone is trying to show up at once
 *  Cons: Still no guaranteed entry
 * Strategy 3 (Queue Based Lock)
 *  Line in a queue, exiting guests inform next guest in line to enter (able to enter multiple times)
 *  Thread queue: each thread always tries to enter queue, wait once inside, exiting thread notifies front thread
 *  Pros: Everyone is guaranteed entry at some point since everyone can enter the queue (FIFO Fairness)
 *      and it will be their turn eventually
 *  Cons: Some contention can still occur due to
 *
 * Strategy 3 is best because it guarantees everyone will be able to enter in a timely manner (starvation-free), even if
 *  there could be delays from the exiting thread not informing the next in line or the first few threads being able to
 * enter the queue, visit the room, and enter the queue again before other threads enter queue.
 */
public class ProblemTwo {
    static int numGuests = 100;
    static int maxVisits = 2;

    public static void main(String[] args) throws InterruptedException {
        guestThread[] allGuests = new guestThread[numGuests];
        for (int i = 0; i < allGuests.length; i++) {
            allGuests[i] = new guestThread();
        }
        for (Thread thread : allGuests) {
            thread.start();//welcome to the party!
        }
        for (Thread thread : allGuests) {
            thread.join();//wait until every guest has visited the room maxVisits times
        }
        System.out.println("Every guest has visited the vase room "+maxVisits+" times");
    }

    public static class vaseRoom {
        static vaseQueue roomQueue = new vaseQueue(numGuests);
        public static void admireVase() throws InterruptedException {
            roomQueue.lock();//enter room or queue
            try {
                //wow, what a pretty vase!
                /*   .--------.
                     \        /
                      )      (
                     /        \
                   ,'          `.
                  /              \
                 /                \
                (                  )
                 \                /
                  `.            ,'
                    `.________,'        */
            } finally {
                roomQueue.unlock();//after admiring the vase unlock the door and let next guest in
            }
        }
    }

    public static class vaseQueue implements Lock {
        ThreadLocal<Integer> mySlotIndex = ThreadLocal.withInitial(() -> 0);
        AtomicInteger tail;
        boolean[] flag;
        int size;

        public vaseQueue(int capacity) {
            size = capacity;//fine since we know the number of guests
            tail = new AtomicInteger(0);
            flag = new boolean[capacity];
            flag[0] = true;//so the first guest in the line just enters and doesn't wait to be told to go in
        }

        @Override
        public void lock() {//when trying to enter room...
            int slot = tail.getAndIncrement() % size;//get into the circular line
            mySlotIndex.set(slot);//memorize where I was in the line
            while (!flag[slot]) {Thread.onSpinWait();}//if it's not my turn, wait and explore rest of the party
        }

        @Override
        public void unlock() {//when leaving the room...
            int slot = mySlotIndex.get();//remember my spot
            flag[slot] = false;//circular queue so reset it for next entry
            flag[(slot+1) % size ] = true;//inform next in line they can see the vase
        }

        @Override
        public void lockInterruptibly() {

        }
        @Override
        public boolean tryLock() {
            return false;
        }
        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            return false;
        }
        @Override
        public Condition newCondition() {
            return null;
        }
    }

    public static class guestThread extends Thread {
        public void run() {
            int numVisits = 0;
            while (numVisits < maxVisits) {//until I've visited the vase as much as I want
                try {
                    vaseRoom.admireVase();//try to admire the vase
                    numVisits++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
