package tabatatimer.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tabatatimer.data.db.Training;

@Dao
public interface TrainingDao {

    @Query("SELECT * FROM training")
    List<Training> getAll();

    @Query("SELECT * FROM training WHERE name LIKE :name")
    public abstract List<Training> getByName(String name);

    @Insert
    void insert(Training training);

    @Delete
    void delete(Training training);

    @Update
    void update(Training training);

}