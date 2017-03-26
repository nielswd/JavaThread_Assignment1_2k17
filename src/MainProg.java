import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by iNfecteD on 22/03/2017.
 */
public class MainProg implements  Runnable{

    private int maxCar = 2000;
    private  int maxSlot = 1000;
    private int queueSize = 1;
    private boolean isFair = true;
    private int nbEntranceExit = 3;


    /**
     * ThreadPool, it simulates all the cars.
     */
    private ExecutorService mExecutor;

    /**
     * Cars elements as callable, to get a future and to be able to keep track of every car state
     */
    private List<Car> listCars = new ArrayList<Car>();


    private ParkingManagement mParkingManagement;


    private GuiRunnable mUI;

    private ExecutorService parkingExecutor;


    public MainProg(GuiRunnable ui, int maxCar, int maxSlot, int queueSize, boolean isFair, int nbEntranceExit){
        this.mUI = ui;
        this.maxCar = maxCar;
        this.maxSlot = maxSlot;
        this.queueSize = queueSize;
        this.isFair = isFair;
        this.nbEntranceExit = nbEntranceExit;
    }

    /**
     * Initialize all arrays and datas then launch the simulation
     */
    private void setup(){
        initParking();
        initExecutor();
        initCars();

        startSimulation();
    }

    public void startSimulation(){
        try {
            Thread.sleep(5000);
            launchSimulation(mExecutor, listCars, mParkingManagement, maxCar);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void initParking() {
        this.mParkingManagement = new ParkingManagement(maxSlot, maxCar, nbEntranceExit, nbEntranceExit, isFair, mUI, queueSize);
        parkingExecutor = Executors.newFixedThreadPool(1);
        parkingExecutor.submit(mParkingManagement);
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
        for(int i = 0;i < (maxCar/10) ;i++){
            Random rn = new Random();
           int randomFactorProblem =  rn.nextInt(10) + 1;
           int durationStay = rn.nextInt(100) + 1;
           int randomlyAssignedEntrance = rn.nextInt(nbEntranceExit);
            Car teacherCar = new Car(i,"teacher", randomFactorProblem, durationStay, mParkingManagement, mUI, randomlyAssignedEntrance);
            listCars.add(teacherCar);
        }
    }

    private void initStudents() {
        for(int i = 0;i < (maxCar - (maxCar/10));i++){
            Random rn = new Random();
            int randomFactorProblem =  rn.nextInt(10 - 1 + 1) + 1;
            int durationStay = rn.nextInt(100) + 1;
            int randomlyAssignedEntrance = rn.nextInt(nbEntranceExit);
            Car studentCar = new Car(i,"student", randomFactorProblem, durationStay, mParkingManagement, mUI, randomlyAssignedEntrance);
            listCars.add(studentCar);
        }
    }


    /**
     * Init the ExecutorService with the constant max_car, which can be modified if needed
     */
    private void initExecutor() {
        mExecutor = Executors.newFixedThreadPool(maxCar);
    }


    /**
     * Launch the simulation by feeding data to the executorService, then feed a completionService with
     * this ExecutorService to keep track of our cars
     * Shutdown the ExecutorService at the end
     * @param executor ExecutorService empty
     * @param listCars List of callable containing the caridentity objects, shuffled.
     */
    private void launchSimulation(final ExecutorService executor, List<Car> listCars, ParkingManagement parkingManagement, int maxCar){
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
                       if (done == maxCar){
                           parkingManagement.setDayIsOver();
                           Thread.currentThread().interrupt();
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
    @Override
    public void run() {
        setup();
    }

}
