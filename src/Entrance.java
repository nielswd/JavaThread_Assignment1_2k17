import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Created by iNfecteD on 22/03/2017.
 */
    public class Entrance implements Callable<Integer> {


    private int id;
    private int moneyReceived = 0;
    private int priceStudent = 4;
    private int priceTeacher = 2;



    private boolean dayIsOver = false;

    private Car lastCar = null;
    private UIDesign mUI;

    private BlockingQueue<Car> entranceQueue;
    private ParkingManagement parking;

    private List<Integer> poolParking;

    private int totalParkedCar = 0;

    private String[] data = {"Entrance 1", "1000", "1000", "0.00€", "0", "0"};

    Entrance(int id, int priceStudent, int priceTeacher, BlockingQueue<Car> entranceQueue, ParkingManagement parking, UIDesign mUI,
                        List<Integer> poolParking) {
        this.id             = id;
        this.priceStudent   = priceStudent;
        this.priceTeacher   = priceTeacher;
        this.entranceQueue  = entranceQueue;
        this.parking        = parking;
        this.mUI            = mUI;
        this.poolParking    = poolParking;
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
                            totalParkedCar += 1;
                        } else if (isAbleToPark && car.isABadCarParkerAKA4x4People()) {
                            help4x4DriverToPark(car);
                            totalParkedCar += 1;
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

    public boolean tryAcquireParkingSlot(Car currentCar){
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
        return isAbleToPark;
    }

    boolean carTryToFindSpotAlone(Car currentCar){
        if (currentCar != lastCar) {
            lastCar = currentCar;
            boolean isAbleToPark = false;
            try {
                Thread.sleep(currentCar.getrandomProblemFactor() * 50);
                isAbleToPark = tryAcquireParkingSlot(currentCar);

                if (isAbleToPark && !currentCar.isABadCarParkerAKA4x4People()) {
                    parkCurrentCar(currentCar);
                    totalParkedCar += 1;
                } else if (isAbleToPark && currentCar.isABadCarParkerAKA4x4People()) {
                    help4x4DriverToPark(currentCar);
                    totalParkedCar += 1;
                } else {
                    currentCar.nextQueue();
                }
            } catch (InterruptedException interrupted1) {
                interrupted1.printStackTrace();
                Thread.currentThread().interrupt();
            }
            return isAbleToPark;
        }
        return false;
    }

    private void parkCurrentCar(Car currentCar){
        totalParkedCar += 1;

        int availableSlotPos = poolParking.lastIndexOf(0);
        if (availableSlotPos == -1){
            System.out.println("-1 bitch...");
        }
        poolParking.set(availableSlotPos, 1);
        currentCar.setLocationInParking(availableSlotPos);

        updateParkingSlotAndAddMoney(currentCar, availableSlotPos);


        currentCar.setStillLooking(false);
        entranceQueue.poll();
        updateUIEntranceData();
        System.out.println("Driver " + Integer.toString(currentCar.getId()) + " (" +  currentCar.getDriver() +") managed to park via Entrance " + Integer.toString(id));
    }

    private void help4x4DriverToPark(Car current4x4Driver){
        totalParkedCar += 1;

        int availableSlotPos = poolParking.lastIndexOf(0);
        int moreAvailableSlotPos = 0;
        if (availableSlotPos == -1){
            System.out.println("-1 bitch...4x4");
        }
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
