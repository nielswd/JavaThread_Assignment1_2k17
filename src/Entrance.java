import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

/**
 * Created by iNfecteD on 22/03/2017.
 */
    public class Entrance implements Callable<Integer> {


    private int id;
    private int moneyReceived = 0;
    private int priceStudent = 4;
    private int priceTeacher = 2;



    private boolean dayIsOver = false;
    private boolean parkedAlone = false;

    private Car lastCar = null;
    private UIDesign mUI;

    private BlockingQueue<Car> entranceQueue;
    private ParkingManagement parking;

    private List<Integer> poolParking;

    private int totalParkedCar = 0;

    private String[] data = {"Entrance 1", "1000", "1000", "0.00€", "0", "0"};

    private Lock writeLock;
    private Lock readLock;

    Entrance(int id, int priceStudent, int priceTeacher, BlockingQueue<Car> entranceQueue, ParkingManagement parking, UIDesign mUI,
             List<Integer> poolParking, Lock writeLock, Lock readLock) {
        this.id             = id;
        this.priceStudent   = priceStudent;
        this.priceTeacher   = priceTeacher;
        this.entranceQueue  = entranceQueue;
        this.parking        = parking;
        this.mUI            = mUI;
        this.poolParking    = poolParking;
        this.readLock       = readLock;
        this.writeLock      = writeLock;
    }

    BlockingQueue<Car> getEntranceQueue() {
        return entranceQueue;
    }

    private void addMoney(int amount){
        this.moneyReceived += amount;
    }

    public int getTotalParkedCar() {
        return totalParkedCar;
    }

    public void setDayIsOver(boolean dayIsOver) {
        this.dayIsOver = dayIsOver;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Entrance " + Integer.toString(id) + " created. Total capacity: " + Integer.toString(entranceQueue.remainingCapacity()));
        data[0] = "Entrance " + Integer.toString(id + 1);
        data[1] = Integer.toString(entranceQueue.remainingCapacity());
        return manageEntrance();
    }

    private int manageEntrance() {
        while (!dayIsOver) {
            if (entranceQueue.size() > 0) {
                Car car = entranceQueue.peek();
                if (car != null && lastCar != car) {
                    lastCar = car;
                    try {
                        Thread.sleep(car.getrandomProblemFactor() * 10);
                        boolean isAbleToPark = tryAcquireParkingSlot(car);

                        if (isAbleToPark && !car.isABadCarParkerAKA4x4People()) {
                            parkCurrentCar(car);
                        } else if (isAbleToPark && car.isABadCarParkerAKA4x4People()) {
                            help4x4DriverToPark(car);
                        } else {
                            car.nextQueue();
                        }
                    } catch (InterruptedException interrupted1) {
                        interrupted1.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }
            sleepToSaveCPU();
        }
        return 0;
    }

    private void sleepToSaveCPU() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void updateUIEntranceData(){
            data[2] = Integer.toString(entranceQueue.remainingCapacity());
            data[3] = Integer.toString(moneyReceived) + ".00€";
            data[4] = "Not implemented";
            data[5] = "Not implemented";
            mUI.updateTableEntrance(id, data);
    }

    private boolean tryAcquireParkingSlot(Car currentCar){
        boolean isAbleToPark;
        try {
            if (currentCar.isABadCarParkerAKA4x4People()) {
                isAbleToPark = parking.tryAcquireParkingSlot(currentCar, true);
            } else {
                isAbleToPark = parking.tryAcquireParkingSlot(currentCar, false);
            }
        } catch (InterruptedException acquiringInterrupted){
            isAbleToPark = false;
            Thread.currentThread().interrupt();
        }
//        System.out.println("IsAbleToPark: " + isAbleToPark);
        if (isAbleToPark){
            currentCar.setGotASpot(true);
        }
        return isAbleToPark;
    }

    boolean carTryToFindSpotAlone(Car currentCar){
        parkedAlone = true;
        boolean copyOfIsAbleToPark = false;
        if (currentCar != lastCar) {
            lastCar = currentCar;
            try {
                Thread.sleep(currentCar.getrandomProblemFactor() * 50);
                boolean isAbleToPark = tryAcquireParkingSlot(currentCar);
                copyOfIsAbleToPark = isAbleToPark;

                if (isAbleToPark && !currentCar.isABadCarParkerAKA4x4People()) {
                    parkCurrentCarAlone(currentCar);
                } else if (isAbleToPark && currentCar.isABadCarParkerAKA4x4People()) {
                    help4x4DriverToParkAlone(currentCar);
                } else {
                    currentCar.nextQueue();
                }
            } catch (InterruptedException interrupted1) {
                interrupted1.printStackTrace();
                Thread.currentThread().interrupt();
            }
            return  copyOfIsAbleToPark;
        }
        return copyOfIsAbleToPark;
    }

    private void parkCurrentCarAlone(Car currentCar){
        totalParkedCar += 1;
        int availableSlotPos = poolParking.indexOf(0);
        if (parkedAlone) {
            readLock.lock();
            try {
                availableSlotPos = poolParking.indexOf(0);
                if (availableSlotPos == -1) {
                    System.out.println("-1 ...");
                    parkCurrentCar(currentCar);
                } else {
                    poolParking.set(availableSlotPos, 1);
                    currentCar.setLocationInParking(availableSlotPos);

                    updateParkingSlotAndAddMoney(currentCar, availableSlotPos);


                    currentCar.setStillLooking(false);
                    entranceQueue.poll();
                    updateUIEntranceData();
                    System.out.println("Driver " + Integer.toString(currentCar.getId()) + " (" +  currentCar.getDriver() +") managed to park via Entrance " + Integer.toString(id));
                }
            } finally {
                readLock.unlock();
            }
        }
    }

    private void parkCurrentCar(Car currentCar){
        totalParkedCar += 1;

        int availableSlotPos = poolParking.lastIndexOf(0);
        poolParking.set(availableSlotPos, 1);
        currentCar.setLocationInParking(availableSlotPos);

        updateParkingSlotAndAddMoney(currentCar, availableSlotPos);
        updateUIEntranceData();

        currentCar.setStillLooking(false);
        entranceQueue.poll();

        System.out.println("Driver " + Integer.toString(currentCar.getId()) + " (" +  currentCar.getDriver() +") managed to park via Entrance " + Integer.toString(id));
    }

    private void help4x4DriverToParkAlone(Car current4x4Driver){
        totalParkedCar += 1;
        int availableSlotPos = poolParking.indexOf(0);
        if (parkedAlone) {
            writeLock.lock();
            try {
                availableSlotPos = poolParking.indexOf(0);
                int moreAvailableSlotPos = 0;
                if (availableSlotPos == -1) {
                    help4x4DriverToParkAlone(current4x4Driver);
                } else if ((availableSlotPos + 1) < poolParking.size() && poolParking.get(availableSlotPos + 1) == 0){
                    moreAvailableSlotPos = availableSlotPos + 1;
                    poolParking.set(availableSlotPos, 1);
                    poolParking.set(availableSlotPos + 1, 1);
                    current4x4Driver.setLocationInParking(availableSlotPos);
                    current4x4Driver.setMorePositionInParking(availableSlotPos + 1);
                    updateAndPrint(current4x4Driver, availableSlotPos, moreAvailableSlotPos);
                    current4x4Driver.setPrintedItOnUI(true);
                } else if ((availableSlotPos - 1) < poolParking.size() && poolParking.get(availableSlotPos - 1) == 0) {
                    moreAvailableSlotPos = availableSlotPos - 1;
                    poolParking.set(availableSlotPos, 1);
                    poolParking.set(availableSlotPos - 1, 1);
                    current4x4Driver.setLocationInParking(availableSlotPos);
                    current4x4Driver.setMorePositionInParking(availableSlotPos - 1);
                    updateAndPrint(current4x4Driver, availableSlotPos, moreAvailableSlotPos);
                    current4x4Driver.setPrintedItOnUI(true);
                }else{
                    //System.out.println("Car " + current4x4Driver.getId() + " got a spot but couldn't update the UI");
//                    try {
//                        parking.tryLeaveParking(current4x4Driver, true);
//                        totalParkedCar -= 1;
//                        current4x4Driver.nextQueue();
//                    } catch (InterruptedException r){
//                        r.printStackTrace();
//                        Thread.currentThread().interrupt();
//                    }
                    tryAcquireParkingSlot(current4x4Driver);
                }
            } finally {
                writeLock.unlock();
            }
        }

    }

    private void help4x4DriverToPark(Car current4x4Driver){
        totalParkedCar += 1;

        int availableSlotPos = poolParking.lastIndexOf(0);
        int moreAvailableSlotPos = 0;

        if ((availableSlotPos + 1) < poolParking.size() && poolParking.get(availableSlotPos + 1) == 0){
            moreAvailableSlotPos = availableSlotPos + 1;
            poolParking.set(availableSlotPos, 1);
            poolParking.set(availableSlotPos + 1, 1);
            current4x4Driver.setLocationInParking(availableSlotPos);
            current4x4Driver.setMorePositionInParking(availableSlotPos + 1);
        } else if ((availableSlotPos - 1) < poolParking.size() && poolParking.get(availableSlotPos - 1) == 0){
            moreAvailableSlotPos = availableSlotPos  - 1;
            poolParking.set(availableSlotPos, 1);
            poolParking.set(availableSlotPos - 1, 1);
            current4x4Driver.setLocationInParking(availableSlotPos);
            current4x4Driver.setMorePositionInParking(availableSlotPos - 1);
        }

        updateParkingSlotAndAddMoney4x4(current4x4Driver, availableSlotPos, moreAvailableSlotPos);
        updateUIEntranceData();

        current4x4Driver.setStillLooking(false);
        entranceQueue.poll();

        System.out.println("Driver " + Integer.toString(current4x4Driver.getId()) + " (" +  current4x4Driver.getDriver() +") finally managed to park via Entrance " + Integer.toString(id));
    }

    private void updateAndPrint(Car current4x4Driver, int availableSlotPos, int moreAvailableSlotPos){
        updateParkingSlotAndAddMoney4x4(current4x4Driver, availableSlotPos, moreAvailableSlotPos);

        current4x4Driver.setStillLooking(false);
        entranceQueue.poll();
        updateUIEntranceData();
        System.out.println("Driver " + Integer.toString(current4x4Driver.getId()) + " (" +  current4x4Driver.getDriver() +") finally managed to park via Entrance " + Integer.toString(id));
    }

    private void updateParkingSlotAndAddMoney4x4(Car currentCar, int availableSlotPos, int moreAvailableSlotPos){
        if (currentCar.getDriver().equals("teacher")){
            addMoney(priceTeacher);
            mUI.updateParkingSlot(availableSlotPos, false, true);
            mUI.updateParkingSlot(moreAvailableSlotPos, false, true);
        } else {
            addMoney(priceStudent);
            mUI.updateParkingSlot(availableSlotPos, true, true);
            mUI.updateParkingSlot(moreAvailableSlotPos, true, true);
        }
    }

    private void updateParkingSlotAndAddMoney(Car currentCar, int availableSlotPos){
        if (currentCar.getDriver().equals("teacher")){
            addMoney(priceTeacher);
            mUI.updateParkingSlot(availableSlotPos, false, false);
        } else {
            addMoney(priceStudent);
            mUI.updateParkingSlot(availableSlotPos, true, false);
        }
    }
}
