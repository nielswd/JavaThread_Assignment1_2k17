package com.waughrn2.ca4006;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by iNfecteD on 22/03/2017.
 */
public class CarIdentity implements Callable<Integer> {
    private String driver;
    private int randomProblemFactor;
    private int durationStay;
    private int state;

    private List<ArrayBlockingQueue> listEntrances = new ArrayList<>();
    private List<ArrayBlockingQueue> listExits = new ArrayList<>();

    /**
     *
     * @param driver
     * @param randomProblemFactor
     * @param listEntrances
     * @param listExits
     */
    public CarIdentity(String driver, int randomProblemFactor, int durationStay, List<ArrayBlockingQueue> listEntrances, List<ArrayBlockingQueue> listExits){
        this.driver = driver;
        this.randomProblemFactor = randomProblemFactor;
        this.listEntrances = listEntrances;
        this.listExits = listExits;
        this.durationStay = durationStay;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getrandomProblemFactor() {
        return randomProblemFactor;
    }

    public void setrandomProblemFactor(int randomProblemFactor) {
        this.randomProblemFactor = randomProblemFactor;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<ArrayBlockingQueue> getListEntrances() {
        return listEntrances;
    }

    public void setListEntrances(List<ArrayBlockingQueue> listEntrances) {
        this.listEntrances = listEntrances;
    }

    public List<ArrayBlockingQueue> getListExits() {
        return listExits;
    }

    public void setListExits(List<ArrayBlockingQueue> listExits) {
        this.listExits = listExits;
    }


    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public Integer call() throws Exception {
        int a = 0;
        while (true)
        {
            try
            {
                System.out.println("Entrance "+ Integer.toString(a) + " remaining Capacity: " + Integer.toString(listEntrances.get(a).remainingCapacity()));

                //TODO Find why there is a warning and remove it for more stability. Quick fix : supresswarnings "unchecked"
                @SuppressWarnings("unchecked")
                boolean success = listEntrances.get(a).offer(Thread.currentThread(), 1000*randomProblemFactor, TimeUnit.MILLISECONDS);

                if(success) //Car got a spot in the parking
                {
                    System.out.println(driver + ": " + Thread.currentThread() + " manage to find a parking slot with time : " + Integer.toString(randomProblemFactor * 1000));
                    System.out.println("\n");
                    Thread.sleep(durationStay*1000); //Car owner went to class
                    while (true) {
                        try {
                            Object leaving = listEntrances.get(a).poll(1000 * randomProblemFactor, TimeUnit.MILLISECONDS);
                            if (leaving != null) {
                                System.out.println(driver + ": " + Thread.currentThread() + " manage to leave the parking from entrance : " + Integer.toString(a) + " and released a spot there");
                                return 0;
                            } else {
                                if (a <= 1) {
                                    System.out.println("Come on, I just want to leave... Let's try next exit...");
                                    a++;
                                } else { //Problem : it never come here? Why? Threads are exiting before? ExecutorService shutdown before they could possibly say something?
                                    a = 0;
                                }
                            }
                        } catch (InterruptedException g) {
                            g.printStackTrace();
                        }
                    }
                } else { //Car timed out and is going to next entrance to try to get a spot
                    if (a <= 1) {
                        System.out.println("I'm mad about waiting, i'm moving to next queue");
                        a++;
                    } else { //Problem : it never come here? Why? Threads are exiting before? ExecutorService shutdown before they could possibly say something?
                        a = 0;
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


}
