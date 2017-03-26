import java.util.List;
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
    private GuiRunnable mUI;
    private Car lastCar;


    private boolean dayIsOver = false;
    private boolean isFair = true;

    private List<Integer> poolParking;

    private BlockingQueue<Car> exitQueue;
    private ParkingManagement parking;

    public Exit(int id, int queue_size, boolean isFair, BlockingQueue<Car> exitQueue, ParkingManagement parking, GuiRunnable mUI, List<Integer> poolParking){
        this.id = id;
        this.queueSize = queue_size;
        this.isFair = isFair;
        this.exitQueue = exitQueue;
        this.parking = parking;
        this.mUI = mUI;
        this.poolParking = poolParking;
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
                        if (!car.isABadCarParkerAKA4x4People()) {
                            parking.tryLeaveParking(car, id, false);
                            car.setTryingToLeave(false);
                            exitQueue.poll();
                            mUI.restoreParkingSlot(car.getLocationInParking());
                            poolParking.set(car.getLocationInParking(), 0);
                        } else {
                            parking.tryLeaveParking(car, id, true);
                            car.setTryingToLeave(false);
                            exitQueue.poll();
                            mUI.restoreParkingSlot(car.getLocationInParking());
                            mUI.restoreParkingSlot(car.getMorePositionInParking());
                            poolParking.set(car.getLocationInParking(), 0);
                            poolParking.set(car.getMorePositionInParking(), 0);
                        }
                        System.out.println("Driver " + Integer.toString(car.getId()) + " (" +  car.getDriver() +") managed to leave the parking at Exit " + Integer.toString(id));
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
