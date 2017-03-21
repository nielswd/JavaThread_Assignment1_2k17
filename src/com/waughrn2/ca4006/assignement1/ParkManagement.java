package com.waughrn2.ca4006.assignement1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.waughrn2.ca4006.assignement1.Constants.*;
import static com.waughrn2.ca4006.assignement1.Constants.CAR_STATE.NOT_PARKED;
import static com.waughrn2.ca4006.assignement1.Constants.TYPE_OF_CAR.STUDENT;
import static com.waughrn2.ca4006.assignement1.Constants.TYPE_OF_CAR.TEACHER;

/**
 * Created by iNfecteD on 21/03/2017.
 */

public class ParkManagement {
    public int nbCarParked = 0;
    public int studentCarParked = 0;
    public int teacherCarParked = 0;
    public List<Student> queueStudents =  new ArrayList<>();
    public List<Teacher> queueTeachers =  new ArrayList<>();
    public List<CarParked> parkedCars =  new ArrayList<>();
    public Thread[] totalSpots = new Thread[MAX_VEHICULES_HOLDING];





    public void startDay(){
        createStudents();
        createTeachers();
        createParkingSpot();
        while (nbCarParked < MAX_VEHICULES_HOLDING){
            manageEntrance();
        }
    }

    private void createParkingSpot(){
        for (int i =  0 ; i < MAX_VEHICULES_HOLDING ; i++) {
            People people = new People();
            totalSpots[i] =  new Thread(people) ;
        }
    }

    private void createTeachers() {
        int nbTeacher = 0;
        while (nbTeacher < NUMBER_OF_TEACHER){
            queueTeachers.add(new Teacher(nbTeacher, NOT_PARKED));
            nbTeacher++;
        }
    }

    private void createStudents() {
        int nbStudents = 0;
        while (nbStudents < NUMBER_OF_STUDENT){
            queueStudents.add(new Student(nbStudents, NOT_PARKED));
            nbStudents++;
        }
    }

    private void manageEntrance() {
        CarParked newCar = new CarParked();
        int randomNumber = getRandom();
        System.out.println(Integer.toString(randomNumber));
        if (randomNumber == 0){
            if (queueTeachers.size() > 0) {
                newCar.setCarId(queueTeachers.get(0).getTeacherId());
                newCar.setCarType(TEACHER);
                parkedCars.add(newCar);
                queueTeachers.remove(0);
                nbCarParked += 1;
                teacherCarParked += 1;
                System.out.println("Car Parked : Teacher, idTeacher = " + Integer.toString(newCar.getCarId()));
            }

        } else if (randomNumber == 1) {
            if (queueStudents.size() > 0) {
                newCar.setCarId(queueStudents.get(0).getStudentId());
                newCar.setCarType(STUDENT);
                parkedCars.add(newCar);
                queueStudents.remove(0);
                nbCarParked += 1;
                studentCarParked += 1;
                System.out.println("Car Parked : Student, idStudent = " + Integer.toString(newCar.getCarId()));
            }
        }
        System.out.println("Number Car Parked : " + Integer.toString(nbCarParked));
        System.out.println("Number Student Car Parked : " + Integer.toString(studentCarParked));
        System.out.println("Number Teacher Car Parked : " + Integer.toString(teacherCarParked));
    }

    private int getRandom(){
        Random rand = new Random();

        return (Math.random() <= PRIORITY) ? 0 : 1;
    }
}
