import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by nielswd on 23/03/2017.
 */
public class ParkingManagement implements Callable<Integer> {
    private int maxSlot;
    private boolean fairness;
    private int nbEntranceExit;
    private int queueSize;

    private int totalParkedCar = 0;
    private int totalCarInParking = 0;

    private UIDesign mUI;

    private List<Entrance> listEntrancesCallable    = new ArrayList<Entrance>();
    private List<Exit> listExitCallable             = new ArrayList<Exit>();



    private BlockingQueue<Car> mParkingQueue;

    /**
     * List of entrances, to let cars keep a track of their position
     */
    private List<ArrayBlockingQueue<Car>> listEntrances = new ArrayList<>();

    /**
     * List of exits, to let cars keep a track of their position
     */
    private List<ArrayBlockingQueue<Car>> listExits     = new ArrayList<>();


    private List<Integer> poolParking                   = Collections.synchronizedList(new ArrayList<Integer>());

    /**
     * ThreadPool entrances.
     */
    private ExecutorService mExecutorEntrances;

    /**
     * ThreadPool exits.
     */
    private ExecutorService mExecutorExits;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();

    ParkingManagement(int maxSlot, int nbEntranceExit, boolean fairness, UIDesign mUI, int queueSize){
        this.maxSlot            = maxSlot;
        this.fairness           = fairness;
        this.nbEntranceExit     = nbEntranceExit;
        this.mUI                = mUI;
        this.queueSize          = queueSize;

        setupParking();
    }

    private void setupParking(){
        fillPoolParking();
        initEntrancesAndExitQueues();
        createEntrances();
        createExits();

        mParkingQueue = new ArrayBlockingQueue<Car>(maxSlot, fairness);

        initExecutorEntrances();
        initExecutorExit();
    }

    private void fillPoolParking() {
        for(int a = 0; a < maxSlot;a++){
            poolParking.add(0);
        }

    }

    private void initEntrancesAndExitQueues(){

        for(int i = 0;i < nbEntranceExit;i++){
            listEntrances.add(new ArrayBlockingQueue<Car>(queueSize, fairness));
        }
        for(int i = 0;i < nbEntranceExit;i++){
            listExits.add(new ArrayBlockingQueue<Car>(queueSize, fairness));
        }
    }

    private void createEntrances() {
            for(int i = 0;i < nbEntranceExit;i++){
                Entrance entrance = new Entrance(i,5, 2, listEntrances.get(i),this, mUI, poolParking, writeLock, readLock);
                listEntrancesCallable.add(entrance);
            }
    }

    private void createExits() {
        for(int i = 0;i < nbEntranceExit;i++){
            Exit exit = new Exit(i, queueSize, fairness, listExits.get(i), this, mUI, poolParking);
            listExitCallable.add(exit);
        }
    }

    private void initExecutorEntrances() {
        mExecutorEntrances = Executors.newFixedThreadPool(nbEntranceExit);
    }
    private void initExecutorExit() {
        mExecutorExits = Executors.newFixedThreadPool(nbEntranceExit);
    }

    boolean tryAcquireParkingSlot(Car currentCar, boolean isABadCarParkerAKA4x4People) throws  InterruptedException{
        if (isABadCarParkerAKA4x4People){
            boolean firstSpot = mParkingQueue.offer(currentCar, 0, TimeUnit.MILLISECONDS);
            boolean secondSpot = mParkingQueue.offer(currentCar, 0, TimeUnit.MILLISECONDS);
            if (firstSpot && secondSpot){
                System.out.println("Driver " + Integer.toString(currentCar.getId()) + " (" +  currentCar.getDriver() +") is a bad car parker. You should hate him");
                return true;
            } else {
                if (firstSpot){
                    mParkingQueue.remove(currentCar);
                }
                if (secondSpot){
                    mParkingQueue.remove(currentCar);
                }
                return false;
            }
        } else {
            return mParkingQueue.offer(currentCar, 0, TimeUnit.MILLISECONDS);
        }
    }

    public BlockingQueue<Car> getParkingQueue() {
        return mParkingQueue;
    }

    boolean tryLeaveParking(Car currentCar, boolean isABadCarParkerAKA4x4People) throws  InterruptedException{
        if (isABadCarParkerAKA4x4People){
            mParkingQueue.remove(currentCar);
            mParkingQueue.remove(currentCar);
            return true;
        } else {
            return mParkingQueue.remove(currentCar);
        }
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("ParkingManagement launched");
        launchEntrancesAndExits(mExecutorEntrances, mExecutorExits, listEntrancesCallable, listExitCallable);
        return 0;
    }


    private void launchEntrancesAndExits(final ExecutorService executorEntrance, final ExecutorService executorExit, List<Entrance> listEntrances, List<Exit> listExits){

        CompletionService<Integer> completionServiceEntrance = new ExecutorCompletionService<>(executorEntrance);
        CompletionService<Integer> completionServiceExit = new ExecutorCompletionService<>(executorExit);

        List<Future<Integer>> entrancesState = new ArrayList<>();
        List<Future<Integer>> exitsState = new ArrayList<>();

        Integer resEntrance;
        Integer resExit;
        try {
            for(Callable<Integer> t : listEntrances){
                entrancesState.add(completionServiceEntrance.submit(t));
            }
            for(Callable<Integer> f : listExits){
                exitsState.add(completionServiceExit.submit(f));
            }

            for (int i = 0; i < listEntrances.size(); ++i) {

                try {
                    resEntrance = completionServiceEntrance.take().get();
                    resExit = completionServiceEntrance.take().get();
                    if (resEntrance != null) {
                        System.out.println("Entrances closed");
                    }
                    if (resExit != null) {
                        System.out.println("Exits closed" + resEntrance);
                        System.out.println("All threads interrupted, simulation finished");
                        mUI.createParkingDisplay(false);
                    }
                }
                catch(ExecutionException ignore) {}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            executorEntrance.shutdown();
            executorExit.shutdown();
        }
    }

    List<Entrance> getListEntrancesCallable() {
        return listEntrancesCallable;
    }

    List<Exit> getListExitCallable() {
        return listExitCallable;
    }



    void setDayIsOverForEntrance(){
        for(Entrance entrance : listEntrancesCallable){
            entrance.setDayIsOver(true);
        }
    }

    void setDayIsOverForExit(){
        for(Entrance entrance : listEntrancesCallable){
            entrance.setDayIsOver(true);
        }
        for(Exit exit : listExitCallable){
            exit.setDayIsOver(true);
        }
    }

    public int getTotalCarInParking() {
        return totalCarInParking;
    }

    public void setTotalCarInParking(int totalCarInParking) {
        this.totalCarInParking = totalCarInParking;
    }
}
