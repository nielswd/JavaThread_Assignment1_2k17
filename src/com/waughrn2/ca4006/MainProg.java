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

    private static final int MAX_CAR = 2000;
    private static final int MAX_SLOT = 1000;
    private static final boolean IS_FAIR = true;
    private static final int NUMBER_STUDENT = 1800;
    private static final int NUMBER_TEACHER = 200;


    private ArrayBlockingQueue entrance1 = new ArrayBlockingQueue(400, true);
    private ArrayBlockingQueue entrance2 = new ArrayBlockingQueue(300, true);
    private ArrayBlockingQueue entrance3 = new ArrayBlockingQueue(300, true);

    private ArrayBlockingQueue exit1 = new ArrayBlockingQueue(400, true);
    private ArrayBlockingQueue exit2 = new ArrayBlockingQueue(300, true);
    private ArrayBlockingQueue exit3 = new ArrayBlockingQueue(300, true);

    private List<ArrayBlockingQueue> listEntrances = new ArrayList<>();
    private List<ArrayBlockingQueue> listExits = new ArrayList<>();

    private ExecutorService mExecutor;

    private List<Callable<Integer>> listCars = new ArrayList<Callable<Integer>>();

    public static void main(String[] args){
        MainProg main = new MainProg();
        main.setup();
    }

    public void setup(){
        initEntrancesAndExitQueues();
        initExecutor();
        initCars();

        launchSimulation(mExecutor, listCars);
    }

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
           int randomFactorProblem =  rn.nextInt(10 - 1 + 1) + 1;
            Callable<Integer> teacherCar = new CarIdentity("teacher", randomFactorProblem, listEntrances, listExits);
            listCars.add(teacherCar);
        }
    }

    private void initStudents() {
        for(int i = 0;i < NUMBER_STUDENT;i++){
            Random rn = new Random();
            int randomFactorProblem =  rn.nextInt(10 - 1 + 1) + 1;
            Callable<Integer> studentCar = new CarIdentity("student", randomFactorProblem, listEntrances, listExits);
            listCars.add(studentCar);
        }
    }

    private void initExecutor() {
        mExecutor = Executors.newFixedThreadPool(MAX_CAR);
    }


    public void initEntrancesAndExitQueues(){
        listEntrances.add(entrance1);
        listEntrances.add(entrance2);
        listEntrances.add(entrance3);

        listExits.add(exit1);
        listExits.add(exit2);
        listExits.add(exit3);
    }

    public static void launchSimulation(final ExecutorService executor, List<Callable<Integer>> listCars){

        //Le service de terminaison
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executor);

        //une liste de Future pour récupérer les résultats
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        Integer res = null;
        try {
            //On soumet toutes les tâches à l'executor
            for(Callable<Integer> t : listCars){
                futures.add(completionService.submit(t));
            }

            for (int i = 0; i < listCars.size(); ++i) {

                try {

                    //On récupère le premier résultat disponible
                    //sous la forme d'un Future avec take(). Puis l'appel
                    //à get() nous donne le résultat du Callable.
                    res = completionService.take().get();
                    if (res != null) {

                        //On affiche le resultat de la tâche
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
