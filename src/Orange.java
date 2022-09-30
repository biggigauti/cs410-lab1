/**
 * The orange class defines what our orange is and what states our orange can take. Our orange class is made up of
 * a couple of methods that do the work required to change orange states.
 */
public class Orange {
    public enum State {
        //The five different states of our orange.
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

        //Instance variables
        private static final int finalIndex = State.values().length - 1;

        final int timeToComplete;

        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        /**
         * getNext() puts our orange into the next state.
         *
         * @return
         */
        State getNext() {
            //Since state is an "enum" .ordinal returns the oranges current state. The logic below check if the orange is at
            //its final state and if it isn't the orange changes to the next state.
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }

    private State state;

    //Orange constructor. Initializes the state to "Fetched" and runs doWork()
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    /**
     * runProcess is called once the orange has been created. This method calls doWork which
     * runs for the amount of time necessary to "do the work" and then gets the next state of the orange.
     */
    public void runProcess() {
        //Checks if the orange has already been processed, if not, do the work and then call getNext() which
        //changes the orange's state.
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    /**
     * Do "the work" to the orange. Simply waits for the specified amount of time needed.
     */
    private void doWork() {
        //Sleep for "timeToComplete" amount. The timeToComplete varies based on the orange's state.
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }
}