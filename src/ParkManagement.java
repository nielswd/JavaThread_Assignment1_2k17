import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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




    public void startDay(){
        createStudents();
        createTeachers();
        while (nbCarParked < Constants.MAX_VEHICULES_HOLDING){
            manageEntrance();
        }
    }

    private void createTeachers() {
        int nbTeacher = 0;
        while (nbTeacher < Constants.NUMBER_OF_TEACHER){
            queueTeachers.add(new Teacher(nbTeacher, Constants.CAR_STATE.NOT_PARKED));
            nbTeacher++;
        }
    }

    private void createStudents() {
        int nbStudents = 0;
        while (nbStudents < Constants.NUMBER_OF_STUDENT){
            queueStudents.add(new Student(nbStudents, Constants.CAR_STATE.NOT_PARKED));
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
                newCar.setCarType(Constants.TYPE_OF_CAR.TEACHER);
                parkedCars.add(newCar);
                queueTeachers.remove(0);
                nbCarParked += 1;
                teacherCarParked += 1;
                System.out.println("Car Parked : Teacher, idTeacher = " + Integer.toString(newCar.getCarId()));
            }

        } else if (randomNumber == 1) {
            if (queueStudents.size() > 0) {
                newCar.setCarId(queueStudents.get(0).getStudentId());
                newCar.setCarType(Constants.TYPE_OF_CAR.STUDENT);
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

        return (Math.random() <= 0.25) ? 0 : 1;
    }
}
