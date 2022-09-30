import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The plant class handles starting our multiple plants and gathering oranges.
 * <p>
 * The plant class implements runnable in order to execute threads for creating out plants.
 * <p>
 * Included are numerous methods such as startPlant, stopPlant, waitToStop, delay, run, and a couple of getters, setters, and incrementing methods.
 */
public class Plant implements Runnable {

    //Runtime. Runs for 2 seconds.
    public static final long PROCESSING_TIME = 2 * 1000;

    //Final variables. Decides number of plants, number of workers in each plant, and how many oranges are required in each bottle of orange juice.
    private static final int NUM_PLANTS = 3;
    private static final int NUM_WORKERS = 4;
    public final int ORANGES_PER_BOTTLE = 3;

    //Instance variables.
    private int orangesProvided;
    private int orangesProcessed;

    private final Thread thread;

    private volatile boolean timeToWork;

    private Worker[] workers;

    //Initialize/define plant queues.
    private BlockingQueue<Orange> peelQ = new LinkedBlockingQueue<>();
    private BlockingQueue<Orange> squeezeQ = new LinkedBlockingQueue<>();
    private BlockingQueue<Orange> bottleQ = new LinkedBlockingQueue<>();
    private BlockingQueue<Orange> processQ = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        //Create array for plants and start them up.
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(i);
            plants[i].startPlant();
        }

        //Delay for a specific time (PROCESSING_TIME) to allow the threads to work.
        delay(PROCESSING_TIME, "Plant malfunction");

        //Stop the plants and wait for them to stop.
        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }

        //Summarize results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        System.out.println("");
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles + " Bottles of OJ " +
                ", wasted " + totalWasted + " oranges");

    }

    /**
     * The delay method takes a specified time and error message as parameters and allows our threads to
     * run for that specified time.
     *
     * @param time
     * @param errMsg
     */
    private static void delay(long time, String errMsg) {
        //Math.max returns the larger number of the two provided
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    //Plant constructor
    Plant(int threadNum) {
        thread = new Thread(this, "Plant[" + threadNum + "]");
    }

    /**
     * The startPlant() method initializes our instance variables and creates the workers, and start the plant's thread.
     * This method also calls the Worker's startThread() method which starts the worker threads.
     * Each plant currently has 4 workers.
     * Each worker knows which queues it should add to and remove from.
     */
    public void startPlant() {
        orangesProcessed = 0;
        orangesProvided = 0;
        timeToWork = true;
        workers = new Worker[NUM_WORKERS];
        workers[0] = new Worker(0, peelQ, squeezeQ);
        workers[1] = new Worker(1, squeezeQ, bottleQ);
        workers[2] = new Worker(2, bottleQ, processQ);
        workers[3] = new Worker(3, processQ, this);
        thread.start();
        for (Worker w : workers) {
            w.startThread();
        }
    }

    /**
     * The stopPlant() method simply turns timeToWork to false which stops the plans and stops the threads.
     */
    public void stopPlant() {
        timeToWork = false;
        thread.stop();
    }

    /**
     * The waitToStop() method runs thread.join() which waits for the threads to die before continuing.
     */
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * The run() method runs when the plant thread is started. While timeToWork is true the plant adds oranges to the peelQ.
     * Once timeToWork is false the plant stops the workers and waits for the threads to die.
     */
    public void run() {
        System.out.print(Thread.currentThread().getName() + " working ");
        while (timeToWork) {
            peelQ.add(new Orange());
            orangesProvided++;
            orangesProcessed++;
            System.out.print(".");
        }

        //Stops workers and waits for threads to die.
        for (Worker w : workers) {
            w.stopThread();
        }

        for (Worker w : workers) {
            w.waitToStop();
        }

        System.out.println("");
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    /**
     * Increments orangeProcessed variable.
     */
    public void processOrange() {
        orangesProcessed++;
    }

    /**
     * Getter function for the orangesProvided variable.
     *
     * @return
     */
    public int getProvidedOranges() {
        return orangesProvided;
    }

    /**
     * Getter function for the orangesProcessed variable.
     *
     * @return
     */
    public int getProcessedOranges() {
        return orangesProcessed;
    }

    /**
     * Returns orangesProcessed/ORANGES_PER_BOTTLE which is the amount of bottles processed.
     *
     * @return
     */
    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    /**
     * Checks for the remainder of oranges after bottling.
     *
     * @return
     */
    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}