package tabatatimer.data;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<Exercise> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Exercise>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Exercise> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
