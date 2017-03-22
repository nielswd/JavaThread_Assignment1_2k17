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




    /**
     * Queues for entrances. Fairness set to true, we want a real FIFO
     */
    private ArrayBlockingQueue entrance1 = new ArrayBlockingQueue(400, IS_FAIR);
    private ArrayBlockingQueue entrance2 = new ArrayBlockingQueue(300, IS_FAIR);
    private ArrayBlockingQueue entrance3 = new ArrayBlockingQueue(300, IS_FAIR);

    /**
     * Queues for exits. Fairness set to true, we want a real FIFO
     */
    private ArrayBlockingQueue exit1 = new ArrayBlockingQueue(400, true);
    private ArrayBlockingQueue exit2 = new ArrayBlockingQueue(300, true);
    private ArrayBlockingQueue exit3 = new ArrayBlockingQueue(300, true);

    /**
     * List of entrances, to let cars keep a track of their position
     */
    private List<ArrayBlockingQueue> listEntrances = new ArrayList<>();

    /**
     * List of exits, to let cars keep a track of their position
     */
    private List<ArrayBlockingQueue> listExits = new ArrayList<>();

    /**
     * ThreadPool, it simulates all the cars.
     */
    private ExecutorService mExecutor;

    /**
     * Cars elements as callable, to get a future and to be able to keep track of every car state
     */
    private List<Callable<Integer>> listCars = new ArrayList<Callable<Integer>>();


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
        initEntrancesAndExitQueues();
        initExecutor();
        initCars();

        launchSimulation(mExecutor, listCars);
    }

    /**
     * Initialize cars then shuffle the list for more realism (It is supposed to be a real FIFO with fair access)
     */
    private void initCars() {
        initTeachers();
        initStudents();
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
            Callable<Integer> teacherCar = new CarIdentity("teacher", randomFactorProblem, durationStay, listEntrances, listExits);
            listCars.add(teacherCar);
        }
    }

    private void initStudents() {
        for(int i = 0;i < NUMBER_STUDENT;i++){
            Random rn = new Random();
            int randomFactorProblem =  rn.nextInt(10 - 1 + 1) + 1;
            int durationStay = rn.nextInt(100) + 1;
            Callable<Integer> studentCar = new CarIdentity("student", randomFactorProblem, durationStay, listEntrances, listExits);
            listCars.add(studentCar);
        }
    }


    /**
     * Init the ExecutorService with the constant max_car, which can be modified if needed
     */
    private void initExecutor() {
        mExecutor = Executors.newFixedThreadPool(MAX_CAR);
    }


    private void initEntrancesAndExitQueues(){
        listEntrances.add(entrance1);
        listEntrances.add(entrance2);
        listEntrances.add(entrance3);

        listExits.add(exit1);
        listExits.add(exit2);
        listExits.add(exit3);
    }

    /**
     * Launch the simulation by feeding data to the executorService, then feed a completionService with
     * this ExecutorService to keep track of our cars
     * Shutdown the ExecutorService at the end
     * @param executor ExecutorService empty
     * @param listCars List of callable containing the caridentity objects, shuffled.
     */
    private static void launchSimulation(final ExecutorService executor, List<Callable<Integer>> listCars){

        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

        List<Future<Integer>> listCarState = new ArrayList<>();

        Integer res = null;
        try {
            for(Callable<Integer> t : listCars){
                listCarState.add(completionService.submit(t));
            }

            for (int i = 0; i < listCars.size(); ++i) {

                try {
                    res = completionService.take().get();
                    if (res != null) {
                        System.out.println(res);
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
