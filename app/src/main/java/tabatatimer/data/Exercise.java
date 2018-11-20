package tabatatimer.data;

import java.io.Serializable;

public class Exercise implements Serializable {

    private String name;
    private int workoutTime;
    private int restTime;
    private int numberOfRounds;

    public int getTotalExerciseTime() {
        return workoutTime * numberOfRounds + restTime * (numberOfRounds -1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(int workoutTime) {
        this.workoutTime = workoutTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }
}
