package tabatatimer.data;

import android.os.AsyncTask;
import android.widget.AdapterView;

import java.util.List;

import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

/**
 * Création d'une classe asynchrone pour récupérer un entrainement
 */
public class GetTraining extends AsyncTask<Void, Void, List<Training>> {

    protected GetTraining.AsyncResponse delegate = null;
    protected DatabaseClient mDb;
    private AdapterView<?> adapterView;
    private int position;
    protected List<Training> trainingFromDb;

    public interface AsyncResponse {
        void processFinish(List<Training> trainingFromDb);
    }

    public GetTraining(GetTraining.AsyncResponse delegate, DatabaseClient mDb, AdapterView<?> adapterView, int position){
        this.mDb = mDb;
        this.delegate = delegate;
        this.adapterView = adapterView;
        this.position = position;
    }

    @Override
    protected List<Training> doInBackground(Void... Voids) {
        //Récupération dans la base de donné du dernier entrainement effectué
        trainingFromDb = mDb.getAppDatabase()
                .trainingDao()
                .getByName(adapterView.getItemAtPosition(position).toString());
        return trainingFromDb;
    }

    @Override
    protected void onPostExecute(List<Training> trainingFromDb) {
        delegate.processFinish(trainingFromDb);
    }

}
