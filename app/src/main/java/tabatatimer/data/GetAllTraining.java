package tabatatimer.data;

import android.os.AsyncTask;

import java.util.List;

import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

/**
 * Création d'une classe asynchrone pour récupérer la liste des entrainements
 */
public class GetAllTraining extends AsyncTask<Void, Void, List<Training>> {

    protected List<Training> trainingFromDb;
    protected AsyncResponse delegate = null;
    protected DatabaseClient mDb;

    public interface AsyncResponse {
        void processFinish(List<Training> trainingFromDb);
    }

    public GetAllTraining(AsyncResponse delegate, DatabaseClient mDb){
        this.mDb = mDb;
        this.delegate = delegate;
    }

    @Override
    protected List<Training> doInBackground(Void... Voids) {
        //Récupération dans la base de donnée de tous les entrainements
        trainingFromDb = mDb.getAppDatabase()
                .trainingDao()
                .getAll();
        return trainingFromDb;
    }

    @Override
    protected void onPostExecute(List<Training> trainingFromDb) {
        delegate.processFinish(trainingFromDb);
    }
}