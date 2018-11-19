package tabatatimer.data.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import tabatatimer.data.Exercice;

@Entity
public class Training implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "tempsPrep")
    private int tempsPrep;

    @ColumnInfo(name = "exercices")
    private ArrayList<Exercice> exercices = new ArrayList<Exercice>();

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

    public ArrayList<Exercice> getExercices() { return exercices; }

    public void setExercices(ArrayList<Exercice> exercices) { this.exercices = exercices; }

    public void addExercice(Exercice exercice) {
        this.exercices.add(exercice);
    }

    public int getTempsReposL(){ return tempsReposL; }

    public void setTempsReposL(int tempsReposL) {
        this.tempsReposL = tempsReposL;
    }

}

