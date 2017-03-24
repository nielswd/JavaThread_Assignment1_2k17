package com.waughrn2.ca4006;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Created by iNfecteD on 22/03/2017.
 */
    public class Entrance implements Callable<Integer> {


    private int id;
    private int queueSize;
    private int moneyReceived;
    private int priceStudent;
    private int priceTeacher;


    private boolean dayIsOver = false;
    private boolean isFair = true;

    private Car lastCar = null;
    private boolean isFirstCar = true;


    private BlockingQueue<Car> entranceQueue;
    private ParkingManagement parking;

    public Entrance(int id, int queue_size, int priceStudent, int priceTeacher, boolean isFair, BlockingQueue<Car> entranceQueue, ParkingManagement parking) {
        this.id = id;
        this.queueSize = queue_size;
        this.priceStudent = priceStudent;
        this.priceTeacher = priceTeacher;
        this.isFair = isFair;
        this.entranceQueue = entranceQueue;
        this.parking = parking;
    }

    public boolean isDayOver() {
        return dayIsOver;
    }

    public void setDayIsOver(boolean dayIsOver) {
        this.dayIsOver = dayIsOver;
    }

    public BlockingQueue<Car> getEntranceQueue() {
        return entranceQueue;
    }

    public void setEntranceQueue(BlockingQueue<Car> entranceQueue) {
        this.entranceQueue = entranceQueue;
    }

    private void onCarArrived(boolean isAStudent) {

    }

    private void onCarLeave(boolean isAStudent) {

        payEntrance(isAStudent);
    }

    private void payEntrance(boolean isAStudent) {
        if (isAStudent) {
            moneyReceived += priceStudent;
        } else {
            moneyReceived += priceTeacher;
        }
    }

    public int getMoneyReceived() {
        return moneyReceived;
    }

    public void setMoneyReceived(int moneyReceived) {
        this.moneyReceived = moneyReceived;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Entrance " + Integer.toString(id) + " created. Total capacity: " + Integer.toString(entranceQueue.remainingCapacity()));
        manageEntrance();
        return 0;
    }

    private int manageEntrance() {
        while (!dayIsOver) {
            if (entranceQueue.size() > 0) {
                Car car = entranceQueue.peek();
                if (car != null && lastCar != car) {
                    lastCar = car;
                    try {
                        Thread.sleep(car.getrandomProblemFactor() * 10);
                        boolean isAbleToPark = parking.tryAcquireParkingSlot(car);
                        if (isAbleToPark) {
                            car.setStillLooking(false);
                            entranceQueue.poll();
                            System.out.println("Driver " + car.getDriver() + " managed to park at entrance " + id);
                        } else {
                        }
                    } catch (InterruptedException interrupted1) {
                        interrupted1.printStackTrace();
                    }
                } else {

                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return 0;
    }
}
