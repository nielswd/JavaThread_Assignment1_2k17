package com.waughrn2.ca4006;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by iNfecteD on 22/03/2017.
 */
public class MainProg {


    /**
     * Custom constant variables, possibility to ajust values of the assignement if needed.
     */
    private static final int MAX_CAR = 2000;
    private static final int MAX_SLOT = 1000;
    private static final boolean IS_FAIR = true;
    private static final int NUMBER_STUDENT = 1800;
    private static final int NUMBER_TEACHER = 200;
    private static final int QUEUE_MAX_SIZE = 1000;
    private static final int NB_ENTRANCE = 3;
    private static final int NB_EXIT = 3;

    private final int NUM_THREADS = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * ThreadPool, it simulates all the cars.
     */
    private ExecutorService mExecutor;

    /**
     * Cars elements as callable, to get a future and to be able to keep track of every car state
     */
    private List<Car> listCars = new ArrayList<Car>();


    private ParkingManagement mParkingManagement;

    /**
     * Start the program and launch the setup
     * @param args
     */
    public static void main(String[] args){
        MainProg main = new MainProg();
        main.setup();
    }

    /**
     * Initialize all arrays and datas then launch the simulation
     */
    private void setup(){
        initParking();
        initExecutor();
        initCars();


        launchSimulation(mExecutor, listCars, mParkingManagement);
    }

    private void initParking() {
        this.mParkingManagement = new ParkingManagement(MAX_SLOT, MAX_CAR, NB_ENTRANCE, NB_EXIT, IS_FAIR);
        ExecutorService parkingService = Executors.newFixedThreadPool(1);
        parkingService.submit(mParkingManagement);
    }

    /**
     * Initialize cars then shuffle the list for more realism (It is supposed to be a real FIFO with fair access)
     */
    private void initCars() {

        initStudents();
        initTeachers();
        shuffleCarListForRealism();
    }

    private void shuffleCarListForRealism() {
        Collections.shuffle(listCars);
    }

    private void initTeachers() {
        for(int i = 0;i < NUMBER_TEACHER;i++){
            Random rn = new Random();
           int randomFactorProblem =  rn.nextInt(10) + 1;
           int durationStay = rn.nextInt(100) + 1;
            Car teacherCar = new Car(i,"teacher", randomFactorProblem, durationStay, mParkingManagement);
            listCars.add(teacherCar);
        }
    }

    private void initStudents() {
        for(int i = 0;i < NUMBER_STUDENT;i++){
            Random rn = new Random();
            int randomFactorProblem =  rn.nextInt(10 - 1 + 1) + 1;
            int durationStay = rn.nextInt(100) + 1;
            Car studentCar = new Car(i,"student", randomFactorProblem, durationStay, mParkingManagement);
            listCars.add(studentCar);
        }
    }


    /**
     * Init the ExecutorService with the constant max_car, which can be modified if needed
     */
    private void initExecutor() {
        mExecutor = Executors.newFixedThreadPool(MAX_CAR);
    }


    /**
     * Launch the simulation by feeding data to the executorService, then feed a completionService with
     * this ExecutorService to keep track of our cars
     * Shutdown the ExecutorService at the end
     * @param executor ExecutorService empty
     * @param listCars List of callable containing the caridentity objects, shuffled.
     */
    private static void launchSimulation(final ExecutorService executor, List<Car> listCars, ParkingManagement parkingManagement){
        int done = 0;
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

        List<Future<Integer>> listCarState = new ArrayList<>();

        Integer res = null;
        try {
            for(Callable<Integer> t : listCars){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                listCarState.add(completionService.submit(t));
            }

            for (int i = 0; i < listCars.size(); ++i) {

                try {
                    res = completionService.take().get();
                    if (res != null) {
                       System.out.println(res);
                       done += 1;
                       if (done == MAX_CAR){
                           parkingManagement.setDayIsOver();
                       }

                    }
                }
                catch(ExecutionException ignore) {}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            executor.shutdown();
        }
    }

}
