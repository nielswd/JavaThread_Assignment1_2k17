import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by iNfecteD on 23/03/2017.
 */
public class ParkingManagement implements Callable<Integer> {
    private int maxSlot;
    private int maxCar;
    private boolean fairness;
    private BlockingQueue<Car> mParkingQueue;

    private int nbEntranceExit;

    private GuiRunnable mUI;

    private List<Entrance> listEntrancesCallable = new ArrayList<Entrance>();


    private List<Exit> listExitCallable = new ArrayList<Exit>();

    private int queueSize;


    /**
     * List of entrances, to let cars keep a track of their position
     */
    private List<ArrayBlockingQueue<Car>> listEntrances = new ArrayList<>();



    /**
     * List of exits, to let cars keep a track of their position
     */
    private List<ArrayBlockingQueue<Car>> listExits = new ArrayList<>();

    //private Semaphore mParkingSlot;

    /**
     * ThreadPool entrances.
     */
    private ExecutorService mExecutorEntrances;


    private List<Integer> poolParking = new ArrayList<>();

    /**
     * ThreadPool exits.
     */
    private ExecutorService mExecutorExits;

    ParkingManagement(int maxSlot, int maxCar, int nbEntrance, int nbExit, boolean fairness, GuiRunnable mUI, int queueSize){
        this.maxSlot            = maxSlot;
        this.maxCar             = maxCar;
        this.fairness           = fairness;
        this.nbEntranceExit     = nbEntrance;
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
                Entrance entrance = new Entrance(i, queueSize, 5, 2, fairness, listEntrances.get(i), this, mUI, poolParking);
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

    boolean tryLeaveParking(Car currentCar, int id, boolean isABadCarParkerAKA4x4People) throws  InterruptedException{
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


    private static void launchEntrancesAndExits(final ExecutorService executorEntrance, final ExecutorService executorExit, List<Entrance> listEntrances, List<Exit> listExits){

        CompletionService<Integer> completionServiceEntrance = new ExecutorCompletionService<>(executorEntrance);
        CompletionService<Integer> completionServiceExit = new ExecutorCompletionService<>(executorExit);

        List<Future<Integer>> entrancesState = new ArrayList<>();
        List<Future<Integer>> exitsState = new ArrayList<>();

        Integer resEntrance = null;
        Integer resExit = null;
        try {
            for(Callable<Integer> t : listEntrances){
                entrancesState.add(completionServiceEntrance.submit(t));
            }
            for(Callable<Integer> f : listExits){
                exitsState.add(completionServiceExit.submit(f));
            }

//            if (listEntrances.size() != listExits.size()){
//                System.out.println("Different number of entrances and exits, interrupting here");
//                return;
//            }
            for (int i = 0; i < listEntrances.size(); ++i) {

                try {
                    resEntrance = completionServiceEntrance.take().get();
                    resExit = completionServiceEntrance.take().get();
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

    public void setListEntrancesCallable(List<Entrance> listEntrancesCallable) {
        this.listEntrancesCallable = listEntrancesCallable;
    }

    public List<Exit> getListExitCallable() {
        return listExitCallable;
    }

    public void setListExitCallable(List<Exit> listExitCallable) {
        this.listExitCallable = listExitCallable;
    }


    void setDayIsOver(){
        for(Entrance entrance : listEntrancesCallable){
            entrance.setDayIsOver(true);
        }
        for(Exit exit : listExitCallable){
            exit.setDayIsOver(true);
        }
        Thread.currentThread().interrupt();
    }
}
