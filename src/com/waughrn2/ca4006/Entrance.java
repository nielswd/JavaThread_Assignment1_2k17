package com.waughrn2.ca4006;

/**
 * Created by iNfecteD on 22/03/2017.
 */
public interface Entrance {

    /**
     * Specifies what the Gate should do when a car arrives to it.
     * @param arrival, The ArrivalMessage which represents information about the car
     */
    public void onCarArrived(String arrival);

    /**
     * Specifies what to do when a car leaves the gate
     */
    public void onCarLeave();
}
