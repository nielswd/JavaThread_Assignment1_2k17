package com.waughrn2.ca4006.assignement1;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class CarParked extends Thread{
    private Constants.TYPE_OF_CAR carType;
    private int carId;

    public Constants.TYPE_OF_CAR getCarType() {
        return carType;
    }

    public void setCarType(Constants.TYPE_OF_CAR carType) {
        this.carType = carType;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }
}
