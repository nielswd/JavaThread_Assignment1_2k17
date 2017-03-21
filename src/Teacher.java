/**
 * Created by iNfecteD on 21/03/2017.
 */
public class Teacher {
    private Constants.CAR_STATE carState = Constants.CAR_STATE.NOT_PARKED;
    private int studentId;

    public Constants.CAR_STATE getCarState() {
        return carState;
    }

    public void setCarState(Constants.CAR_STATE carState) {
        this.carState = carState;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
