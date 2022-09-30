import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The worker class defines our plant's workers. There are two types of workers in our plant. The processing worker
 * has a specific set of parameters that the other workers do not have. Methods that the worker has
 * access to are quite simple, startThread(), stopThread(), waitToStop(), and run().
 */
public class Worker implements Runnable {

    //Instance variables
    //Volatile makes sure that the variable is not cached. Ensures that our threads don't grab an outdated variable.
    private volatile boolean timeToWork;
    private final Thread thread;

    //Incoming and outgoing queues that we pass to the workers.
    private BlockingQueue<Orange> incomingQ;
    private BlockingQueue<Orange> outgoingQ;

    private Plant plant;

    //The average worker constructor
    public Worker(int threadNum, BlockingQueue<Orange> incomingQ, BlockingQueue<Orange> outgoingQ) {
        this.incomingQ = incomingQ;
        this.outgoingQ = outgoingQ;
        thread = new Thread(this, "Worker[" + threadNum + "]");
    }

    //The special worker constructor.
    public Worker(int threadNum, BlockingQueue<Orange> incomingQ, Plant plant) {
        this.incomingQ = incomingQ;
        this.outgoingQ = null;
        this.plant = plant;
        thread = new Thread(this, "Worker[" + threadNum + "]");
        thread.setDaemon(true);
    }

    /**
     * Starts the worker's thread. We call this function within the Plant once the workers have been created.
     */
    public void startThread() {
        timeToWork = true;
        thread.start();
    }

    /**
     * Stops the worker's thread.
     */
    public void stopThread() {
        timeToWork = false;
    }

    /**
     * Waits for the threads to die by using thread.join().
     */
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * This is what happens when the worker thread starts running. Once we call startThread().
     */
    public void run() {
        while (timeToWork) {
            //Grab an orange from the incoming queue.
            Orange o = incomingQ.remove();
            //Run our process on the orange.
            o.runProcess();
            //If the worker has an outgoing queue as a parameter, add it to that queue.
            if (outgoingQ != null) {
                outgoingQ.add(o);
                //If it doesn't, it's being passed to the processing worker which calls process orange.
            } else {
                plant.processOrange();
            }
        }
    }
}
