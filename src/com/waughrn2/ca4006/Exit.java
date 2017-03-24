package com.waughrn2.ca4006;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Created by iNfecteD on 22/03/2017.
 */
public class Exit implements Callable<Integer> {


    private int id;
    private int queueSize;
    private int moneyReceived;
    private int priceStudent;
    private int priceTeacher;

    private Car lastCar;


    private boolean dayIsOver = false;
    private boolean isFair = true;



    private BlockingQueue<Car> exitQueue;
    private ParkingManagement parking;

    public Exit(int id, int queue_size, boolean isFair, BlockingQueue<Car> exitQueue, ParkingManagement parking){
        this.id = id;
        this.queueSize = queue_size;
        this.isFair = isFair;
        this.exitQueue = exitQueue;
        this.parking = parking;
    }

    public boolean isDayOver() {
        return dayIsOver;
    }

    void setDayIsOver(boolean dayIsOver) {
        this.dayIsOver = dayIsOver;
    }

    public BlockingQueue<Car> getExitQueue() {
        return exitQueue;
    }

    public void setExitQueue(BlockingQueue<Car> entranceQueue) {
        this.exitQueue = entranceQueue;
    }

    private void onCarArrived(boolean isAStudent){

    }

    private void onCarLeave(boolean isAStudent){

    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Exit "+ Integer.toString(id) + " created. Total capacity: " + Integer.toString(exitQueue.remainingCapacity()));
        return manageEntrance();
    }

    private int manageEntrance() {
        while (!dayIsOver) {
            if (exitQueue.size() > 0) {
                Car car = exitQueue.peek();
                if (car != null && car != lastCar) {
                    lastCar = car;
                    try {
                        Thread.sleep(car.getrandomProblemFactor() * 10);
                        parking.tryLeaveParking(car, id);
                        car.setTryingToLeave(false);
                        exitQueue.poll();

                    } catch (InterruptedException interrupted1) {
                        interrupted1.printStackTrace();
                    }
                }

            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
