package tabatatimer.data.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

import tabatatimer.data.Exercise;

@Entity
public class Training implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "tempsPrep")
    private int tempsPrep;

    @ColumnInfo(name = "exercises")
    private ArrayList<Exercise> exercises = new ArrayList<Exercise>();

    @ColumnInfo(name = "tempsReposL")
    private int tempsReposL;

    /*
     * Getters and Setters
     * */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTempsPrep() {
        return tempsPrep;
    }

    public void setTempsPrep(int tempsPrep) {
        this.tempsPrep = tempsPrep;
    }

    public ArrayList<Exercise> getExercises() { return exercises; }

    public void setExercises(ArrayList<Exercise> exercises) { this.exercises = exercises; }

    public void addExercice(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public int getTempsReposL(){ return tempsReposL; }

    public void setTempsReposL(int tempsReposL) {
        this.tempsReposL = tempsReposL;
    }

}

