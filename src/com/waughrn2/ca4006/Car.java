package com.waughrn2.ca4006;

import java.util.concurrent.*;

/**
 * Created by iNfecteD on 22/03/2017.
 */
public class Car implements Callable<Integer> {
    private int id;
    private String driver;
    private int randomProblemFactor;
    private int durationStay;
    private int state;
    private ParkingManagement parking;
    private int carLocation = 0;

    private int locationInParking;

    private GuiRunnable mUI;

    private int randomlyAssignedEntrance = 0;


    private  boolean inQueue = false;
    private boolean stillLooking = true;
    private boolean tryingToLeave = true;

    Car(int id, String driver, int randomProblemFactor, int durationStay, ParkingManagement parking, GuiRunnable mUI, int randomlyAssignedEntrance){
        this.driver                     = driver;
        this.randomProblemFactor        = randomProblemFactor;
        this.durationStay               = durationStay;
        this.parking                    = parking;
        this.id                         = id;
        this.mUI                        = mUI;
        this.randomlyAssignedEntrance   = randomlyAssignedEntrance;
        carLocation = randomlyAssignedEntrance;
    }

    String getDriver() {
        return driver;
    }

    void setDriver(String driver) {
        this.driver = driver;
    }

    int getrandomProblemFactor() {
        return randomProblemFactor;
    }

    void setrandomProblemFactor(int randomProblemFactor) {
        this.randomProblemFactor = randomProblemFactor;
    }

    int getState() {
        return state;
    }

    void setState(int state) {
        this.state = state;
    }

    public boolean isStillLooking() {
        return stillLooking;
    }

    void setStillLooking(boolean stillLooking) {
        this.stillLooking = stillLooking;
    }

    @Override
    public Integer call() throws Exception {
        return findQueue();
    }

    public int findQueue(){
        int foundAPlace = 0;
        while (stillLooking) {
            if (inQueue) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Entrance entrance = parking.getListEntrancesCallable().get(carLocation);
                    int nbEntrances = parking.getListEntrancesCallable().size();

                    boolean success = entrance.getEntranceQueue().offer(this, 100, TimeUnit.MILLISECONDS);

                    if (!success) { //Car timed out and is going to next entrance to try to get a spot
                        if (carLocation < nbEntrances - 1) {
                            carLocation++;
                            findQueue();
                        } else {
                            carLocation = 0;
                            findQueue();
                        }
                    } else {
                        System.out.println("CAR IN QUEUE");
                        inQueue = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        foundAPlace = goToLecture();
        return foundAPlace;
    }

    private int goToLecture(){
        try {
            Thread.sleep(5000*randomProblemFactor);
            return tryToLeaveParking();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return 0;
    }

    private int tryToLeaveParking() {
        inQueue = false;
        while (tryingToLeave) {
            if (inQueue) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Exit exit = parking.getListExitCallable().get(carLocation);
                    int nbEntrances = parking.getListExitCallable().size();

                    boolean success = exit.getExitQueue().offer(this, 100, TimeUnit.MILLISECONDS);

                    if (!success) { //Car timed out and is going to next entrance to try to get a spot
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return 1;
    }

    public void setTryingToLeave(boolean isTryingToLeave){
        tryingToLeave = isTryingToLeave;
    }

    public int getId(){
        return id;
    }

    public int getLocationInParking() {
        return locationInParking;
    }

    public void setLocationInParking(int locationInParking) {
        this.locationInParking = locationInParking;
    }

    public void nextQueue(){
        inQueue = false;
        int nbEntrances = parking.getListExitCallable().size();
        if (carLocation < nbEntrances - 1) {
            carLocation++;
            findQueue();
        } else {
            carLocation = 0;
            findQueue();
        }
    }
}
