package com.waughrn2.ca4006.assignement1;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class People extends Thread {
    private Constants.TYPE_OF_CAR typeOfCar;
    private int carId;
    private  Constants.CAR_STATE carState;

    public Constants.TYPE_OF_CAR getTypeOfCar() {
        return typeOfCar;
    }

    public void setTypeOfCar(Constants.TYPE_OF_CAR typeOfCar) {
        this.typeOfCar = typeOfCar;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public Constants.CAR_STATE getCarState() {
        return carState;
    }

    public void setCarState(Constants.CAR_STATE carState) {
        this.carState = carState;
    }

    public void run(){
        if (carState == Constants.CAR_STATE.NOT_PARKED){
            //try_to_park();
        } else {
            //try_to_leave();
        }
    }
}
