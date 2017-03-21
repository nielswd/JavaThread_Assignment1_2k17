package com.waughrn2.ca4006.assignement1;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class Teacher{
    private Constants.CAR_STATE carState;
    private int teacherId;


    public Teacher(int teacherId, Constants.CAR_STATE carState){
        this.teacherId = teacherId;
        this.carState = carState;
    }

    public Constants.CAR_STATE getCarState() {
        return carState;
    }

    public void setCarState(Constants.CAR_STATE carState) {
        this.carState = carState;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void enterParking(){
        setCarState(Constants.CAR_STATE.PARKED);
    }

    public void leaveParking(){
        setCarState(Constants.CAR_STATE.NOT_PARKED);
    }

    public void run(){
        if (carState == Constants.CAR_STATE.NOT_PARKED){
            setCarState(Constants.CAR_STATE.PARKED);
        } else {
            setCarState(Constants.CAR_STATE.NOT_PARKED);
        }
    }
}
