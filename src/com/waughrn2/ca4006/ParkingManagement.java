package com.waughrn2.ca4006;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by iNfecteD on 23/03/2017.
 */
public class ParkingManagement implements Callable<Integer> {
    private int maxSlot;
    private int maxCar;
    private boolean fairness;
    private BlockingQueue<Car> mParkingQueue;
    private int nbEntrance;
    private int nbExit;



    private List<Entrance> listEntrancesCallable = new ArrayList<Entrance>();


    private List<Exit> listExitCallable = new ArrayList<Exit>();


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


    /**
     * ThreadPool exits.
     */
    private ExecutorService mExecutorExits;

    ParkingManagement(int maxSlot, int maxCar, int nbEntrance, int nbExit, boolean fairness){
        this.maxSlot = maxSlot;
        this.maxCar = maxCar;
        this.fairness = fairness;
        this.nbEntrance = nbEntrance;
        this.nbExit = nbExit;

        setupParking();
    }

    private void setupParking(){
        initEntrancesAndExitQueues();
        createEntrances();
        createExits();

        mParkingQueue = new ArrayBlockingQueue<Car>(maxSlot, fairness);

        initExecutorEntrances();
        initExecutorExit();
    }

    private void initEntrancesAndExitQueues(){

        for(int i = 0;i < nbEntrance;i++){
            listEntrances.add(new ArrayBlockingQueue<Car>(1000, fairness));
        }
        for(int i = 0;i < nbExit;i++){
            listExits.add(new ArrayBlockingQueue<Car>(1000, fairness));
        }
    }

    private void createEntrances() {
            for(int i = 0;i < nbEntrance;i++){
                Entrance entrance = new Entrance(i, 1000, 5, 2, fairness, listEntrances.get(i), this);
                listEntrancesCallable.add(entrance);
            }
    }

    private void createExits() {
        for(int i = 0;i < nbExit;i++){
            Exit exit = new Exit(i, 1000, fairness, listExits.get(i), this);
            listExitCallable.add(exit);
        }
    }

    private void initExecutorEntrances() {
        mExecutorEntrances = Executors.newFixedThreadPool(nbEntrance);
    }
    private void initExecutorExit() {
        mExecutorExits = Executors.newFixedThreadPool(nbExit);
    }

    boolean tryAcquireParkingSlot(Car currentCar) throws  InterruptedException{
        return mParkingQueue.offer(currentCar, 0, TimeUnit.MILLISECONDS);
    }

    boolean tryLeaveParking(Car currentCar, int id) throws  InterruptedException{
        System.out.println("Driver " + currentCar.getDriver() + " which is id: " + currentCar.getId() + "left the parking from exit " + id);
        return mParkingQueue.remove(currentCar);
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
    }
}
