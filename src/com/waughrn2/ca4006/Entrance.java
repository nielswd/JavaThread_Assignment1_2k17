package com.waughrn2.ca4006;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Created by iNfecteD on 22/03/2017.
 */
    public class Entrance implements Callable<Integer> {


    private int id;
    private int queueSize;
    private int moneyReceived = 0;
    private int priceStudent = 4;
    private int priceTeacher = 2;


    private boolean dayIsOver = false;
    private boolean isFair = true;

    private Car lastCar = null;
    private boolean isFirstCar = true;
    private GuiRunnable mUI;

    private BlockingQueue<Car> entranceQueue;
    private ParkingManagement parking;

    private List<Integer> poolParking;

    private String[] data = {"Entrance 1", "1000", "1000", "0.00€", "0", "0"};

    public Entrance(int id, int queue_size, int priceStudent, int priceTeacher, boolean isFair, BlockingQueue<Car> entranceQueue, ParkingManagement parking, GuiRunnable mUI,
                        List<Integer> poolParking) {
        this.id             = id;
        this.queueSize      = queue_size;
        this.priceStudent   = priceStudent;
        this.priceTeacher   = priceTeacher;
        this.isFair         = isFair;
        this.entranceQueue  = entranceQueue;
        this.parking        = parking;
        this.mUI            = mUI;
        this.poolParking    = poolParking;
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

    public void addMoney(int amount){
        this.moneyReceived += amount;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Entrance " + Integer.toString(id) + " created. Total capacity: " + Integer.toString(entranceQueue.remainingCapacity()));
        data[0] = "Entrance " + Integer.toString(id + 1);
        data[1] = Integer.toString(entranceQueue.remainingCapacity());
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
                            int availableSlotPos = poolParking.lastIndexOf(0);
                            poolParking.set(availableSlotPos, 1);
                            car.setLocationInParking(availableSlotPos);
                            if (car.getDriver().equals("teacher")){
                                addMoney(priceTeacher);
                                mUI.updateParkingSlot(availableSlotPos, false);
                            } else {
                                addMoney(priceStudent);
                                mUI.updateParkingSlot(availableSlotPos, true);
                            }
                            updateUIEntranceData();
                            car.setStillLooking(false);
                            entranceQueue.poll();
                            System.out.println("Driver " + car.getDriver() + " managed to park at entrance " + id);
                        } else {
                            car.nextQueue();
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

    private void updateUIEntranceData(){
            data[2] = Integer.toString(entranceQueue.remainingCapacity());
            data[3] = Integer.toString(moneyReceived) + ".00€";
            data[4] = "Not implemented";
            data[5] = "Not implemented";
            mUI.updateTableEntrance(id, data);
    }
}
