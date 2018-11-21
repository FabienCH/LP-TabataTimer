package tabatatimer.data.async;

import android.os.AsyncTask;
import android.widget.AdapterView;

import java.util.List;

import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

public class DeleteTraining  extends AsyncTask<Void, Void, Integer> {

    protected DeleteTraining.AsyncResponse delegate = null;
    protected DatabaseClient mDb;
    private AdapterView<?> adapterView;
    private int position;
    protected Integer rowDeleted;

    public interface AsyncResponse {
        void processFinish(int rowDeleted);
    }

    public DeleteTraining(DeleteTraining.AsyncResponse delegate, DatabaseClient mDb, AdapterView<?> adapterView, int position){
        this.mDb = mDb;
        this.delegate = delegate;
        this.adapterView = adapterView;
        this.position = position;
    }

    @Override
    protected Integer doInBackground(Void... Voids) {
        //Récupération dans la base de données de l'entrainement sur lequel l'utilisateur a cliqué
        System.out.println(adapterView.getItemAtPosition(position).toString());
        rowDeleted = mDb.getAppDatabase()
                .trainingDao()
                .deleteByName(adapterView.getItemAtPosition(position).toString());
        return rowDeleted;
    }

    @Override
    protected void onPostExecute(Integer rowDeleted) {
        delegate.processFinish(rowDeleted);
    }

}
