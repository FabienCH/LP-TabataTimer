package tabatatimer.data;

import android.os.AsyncTask;
import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

/**
 * Création d'une classe asynchrone pour sauvegarder un entrainement
 */
public class SaveTraining extends AsyncTask<Void, Void, Training> {

    protected SaveTraining.AsyncResponse delegate = null;
    protected DatabaseClient mDb;
    protected Training training;

    public interface AsyncResponse {
        void processFinish(Training training);
    }

    public SaveTraining(SaveTraining.AsyncResponse delegate, DatabaseClient mDb, Training training){
        this.mDb = mDb;
        this.delegate = delegate;
        this.training = training;
    }

    @Override
    protected Training doInBackground(Void... Voids) {
        //Insertion dans la base de donnée
        mDb.getAppDatabase()
                .trainingDao()
                .insert(training);
        return training;
    }

    @Override
    protected void onPostExecute(Training training) {
        delegate.processFinish(training);
    }

}
