package tabatatimer.data.db;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseClient {

    private static DatabaseClient instance;

    //our app database object
    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {

        //creating the app database with Room database builder
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "TabathaTimer").fallbackToDestructiveMigration().build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
