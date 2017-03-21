package com.waughrn2.ca4006.assignement1; /**
 * Created by iNfecteD on 21/03/2017.
 */


/**
 *   Specify the values of the assignement. If those values were changing, we only need to modify them here.
 */

public class Constants {

    public static final int MAX_VEHICULES_HOLDING = 1000;
    public static final int TOTAL_VEHICULES = 2000;

    public static final int NUMBER_OF_STUDENT = 1500;
    public static final int NUMBER_OF_TEACHER = 500;

    public static final int MAX_ENTRANCE_EXIT = 3;
    public enum ENTRANCE_EXIT {
        ENTRANCE_EXIT_1,
        ENTRANCE_EXIT_2,
        ENTRANCE_EXIT_3
    }

    public static final double VEHICULE_MAX_SPACE_TAKEN = 1.5;

    public static final boolean CAN_VEHICULES_ENTER_LEAVE_SIMULTANEOUSLY = true;

    public enum CAR_STATE {
        PARKED,
        NOT_PARKED
    }

    public enum TYPE_OF_CAR {
        STUDENT,
        TEACHER
    }

    public static final double PRIORITY = 0.25; // Near 0, priorities Students, near 1, priorities Teachers.
}
