package tabatatimer.data.async;

import android.os.AsyncTask;
import android.widget.AdapterView;

import java.util.List;

import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

/**
 * Création d'une classe asynchrone pour récupérer un entrainement
 */
public class GetTraining extends AsyncTask<Void, Void, Training> {

    protected GetTraining.AsyncResponse delegate = null;
    protected DatabaseClient mDb;
    private AdapterView<?> adapterView;
    private int position;
    protected Training trainingFromDb;

    public interface AsyncResponse {
        void processFinish(Training trainingFromDb);
    }

    public GetTraining(GetTraining.AsyncResponse delegate, DatabaseClient mDb, AdapterView<?> adapterView, int position){
        this.mDb = mDb;
        this.delegate = delegate;
        this.adapterView = adapterView;
        this.position = position;
    }

    @Override
    protected Training doInBackground(Void... Voids) {
        //Récupération dans la base de donné de l'entrainement sur lequel l'utilisateur a cliqué
        trainingFromDb = mDb.getAppDatabase()
                .trainingDao()
                .getByName(adapterView.getItemAtPosition(position).toString());
        return trainingFromDb;
    }

    @Override
    protected void onPostExecute(Training trainingFromDb) {
        delegate.processFinish(trainingFromDb);
    }

}
