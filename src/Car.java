import java.util.concurrent.*;

/**
 * Created by nielswd on 22/03/2017.
 */

/**
 * Car objects are Callable<Integer> Objects. That way, we can monitor their state (especially their death/end of park trip)
 * Their lifecycle is the following : Created in CarParkManagement -> call() by Executor. Then call() -> findQueue()
 * which is an infinite loop till they get a spot in the parking. In this loop, they try to find an entrance and go
 * into its queue. If the entrance's queue is full, they'll timeout and go to another queue (nobody likes to wait in a stuck queue).
 * If they manage to get into an entrance's queue, they will ask for a parking spot. If parking is full, they will stay at their position and
 * keep asking after a small sleep. Only first car of queue will be taken if a spot is free. If they get a spot, their main loop is broken and they sleep while their owner go to Lecture.
 * When sleep time is done, they will do kind of the same loop to be able to leave the parking environment (and die by interrupt... :(   )
 *
 * Random values : possible issue at entrange/exit due to parking card problem : int randomProblemFactor, which is... random (crazy, I know), multiply by 10 (they are quick at solving problems)
 */
public class Car implements Callable<Integer> {
    private int id;
    private String driver;
    private int randomProblemFactor;
    private int durationStay;
    private boolean useQueue;

    private ParkingManagement parking;

    private int carLocation             = 0;
    private int locationInParking       = 0;
    private int morePositionInParking   = 0;

    private boolean inQueue                     = false;
    private boolean stillLooking                = true;
    private boolean tryingToLeave               = true;

    private boolean isABadCarParkerAKA4x4People = false;
    private boolean gotASpot = false;
    private boolean printedItOnUI = false;

    Car(int id, String driver, int randomProblemFactor, int durationStay, ParkingManagement parking, int randomlyAssignedEntrance, boolean isABadCarParkerAKA4x4People, boolean useQueue){
        this.driver                         = driver;
        this.randomProblemFactor            = randomProblemFactor;
        this.durationStay                   = durationStay;
        this.parking                        = parking;
        this.id                             = id;
        this.carLocation                    = randomlyAssignedEntrance;
        this.isABadCarParkerAKA4x4People    = isABadCarParkerAKA4x4People;
        this.useQueue                       = useQueue;

    }

    void setStillLooking(boolean stillLooking) {
        this.stillLooking = stillLooking;
    }
    void setTryingToLeave(boolean isTryingToLeave){
        tryingToLeave = isTryingToLeave;
    }
    void setLocationInParking(int locationInParking) {
        this.locationInParking = locationInParking;
    }
    void setMorePositionInParking(int morePositionInParking) {
        this.morePositionInParking = morePositionInParking;
    }
    int getMorePositionInParking() {
        return morePositionInParking;
    }
    int getrandomProblemFactor() {
        return randomProblemFactor;
    }
    int getId(){
        return id;
    }
    int getLocationInParking() {
        return locationInParking;
    }
    boolean isABadCarParkerAKA4x4People() {
        return isABadCarParkerAKA4x4People;
    }
    String getDriver() {
        return driver;
    }

    @Override
    public Integer call() throws Exception {
        return findQueue();
    }

    private int findQueue(){
        while (stillLooking) {
            if (inQueue) {
                sleepToSaveCPUDuringQueue();
            } else {
                sleepToSaveCPUDuringQueue();
                if (!useQueue) {
                    carParkDontUseQueue();
                } else {
                    carParkUseQueue();
                }

                sleepToSaveCPUDuringQueue();
            }
        }
        return goToLecture();
    }

    private void carParkDontUseQueue(){
        Entrance entrance = parking.getListEntrancesCallable().get(carLocation);
        boolean gotSpot = entrance.carTryToFindSpotAlone(this);
        actionAfterEnteringParkingAlone(gotSpot);
    }
    private void carParkUseQueue(){
        try {
        Entrance entrance = parking.getListEntrancesCallable().get(carLocation);

        boolean gotSpot = entrance.getEntranceQueue().offer(this, 100, TimeUnit.MILLISECONDS);

        actionAfterAskingForEntrance(gotSpot);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void actionAfterEnteringParkingAlone(boolean gotSpot){
        if (!gotSpot) {
            nextQueue();
        } else {
            System.out.println("Driver " + Integer.toString(getId()) + " (" +  getDriver() +") entered the queue at Entrance " + Integer.toString(carLocation + 1));
            inQueue = true;
        }
    }

    private void actionAfterAskingForEntrance(boolean gotSpot){

        int nbEntrances = parking.getListEntrancesCallable().size();

        if (!gotSpot) { //Car timed out and is going to next entrance to try to get a spot
            if (carLocation < nbEntrances - 1) {
                carLocation++;
                findQueue();
            } else {
                carLocation = 0;
                findQueue();
            }
        } else {
            System.out.println("Driver " + Integer.toString(getId()) + " (" +  getDriver() +") entered the queue at Entrance " + Integer.toString(carLocation + 1));
            inQueue = true;
        }
    }

    private void sleepToSaveCPUDuringQueue() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int goToLecture(){
        try {
            Thread.sleep(5000*randomProblemFactor);
            return tryToLeaveParking();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return  0;
        }
    }

    private int tryToLeaveParking() {
        inQueue = false;
        while (tryingToLeave) {
            if (inQueue) {
                sleepToSaveCPUDuringQueue();
            } else {
                try {
                    Exit exit = parking.getListExitCallable().get(carLocation);

                    boolean canLeave = exit.getExitQueue().offer(this, 100, TimeUnit.MILLISECONDS);

                    actionAfterAskingToLeave(canLeave);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sleepToSaveCPUDuringQueue();
            }
        }
        Thread.currentThread().interrupt(); //"Kill" Thread to free CPU at end of car trip
        return 1;
    }

    private void actionAfterAskingToLeave(boolean canLeave){
        int nbEntrances = parking.getListExitCallable().size();
        if (!canLeave) { //Car timed out and is going to next entrance to try to get a spot
            if (carLocation < nbEntrances - 1) {
                carLocation++;
                tryToLeaveParking();
            } else {
                carLocation = 0;
                tryToLeaveParking();
            }
        } else {
            inQueue= true;
        }
    }

    void nextQueue(){
        inQueue = false;
            int nbEntrances = parking.getListEntrancesCallable().size();
            if (carLocation < nbEntrances - 1) {
                carLocation++;
                findQueue();
            } else {
                carLocation = 0;
                findQueue();
            }
    }

    public boolean isGotASpot() {
        return gotASpot;
    }

    public void setGotASpot(boolean gotASpot) {
        this.gotASpot = gotASpot;
    }

    public boolean isPrintedItOnUI() {
        return printedItOnUI;
    }

    public void setPrintedItOnUI(boolean printedItOnUI) {
        this.printedItOnUI = printedItOnUI;
    }
}
