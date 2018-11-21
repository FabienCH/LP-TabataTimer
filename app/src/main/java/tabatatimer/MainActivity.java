package tabatatimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tabatatimer.data.async.DeleteTraining;
import tabatatimer.data.db.Training;
import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.async.GetAllTraining;
import tabatatimer.data.async.GetTraining;

public class MainActivity extends AppCompatActivity {

    private static Training training = new Training();
    private DatabaseClient mDb;
    protected ArrayList<String> trainingsNameList;
    private ArrayAdapter<String> adaptor;

    public static final String TRAINING = "Training";
    public static final String TRAINING_NAME = "TrainingName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = DatabaseClient.getInstance(getApplicationContext());

        // Récupération de la liste des entrainements depuis la base de données
        getAllTraining();

    }

    // Affiche la liste des entrainements
    public void setTrainingList(List<Training> trainingFromDb) {
        ListView listView = findViewById(R.id.liste_entrainements);
        // Si il n'y a pas d'entrainement dans la base de données, on affiche une liste simple
        if(trainingFromDb.size() == 0) {
            adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainingsNameList);
            listView.setAdapter(adaptor);
        }
        // Sinon, on affiche une liste contenant le nom des entrainements et un bouton pour les supprimer
        else {

            String[] trainingsNameArray = new String[trainingsNameList.size()];
            trainingsNameArray = trainingsNameList.toArray(trainingsNameArray);
            adaptor = new TrainingListAdaptor(MainActivity.this, trainingsNameArray);
            listView.setAdapter(adaptor);
            setClickOnTrainingList(listView);
        }
    }

    public void getAllTraining() {
        new GetAllTraining(new GetAllTraining.AsyncResponse(){
            @Override
            // Récupère le résultat renvoyé par la méthode onPostExecute() de la classe GetAllTraining
            public void processFinish(List<Training> trainingFromDb){
                trainingsNameList = new ArrayList<>();
                if(trainingFromDb != null && !trainingFromDb.isEmpty()) {
                    for(int i=0; i < trainingFromDb.size(); i++) {
                        trainingsNameList.add(trainingFromDb.get(i).getName());
                    }
                }
                else {
                    trainingsNameList.add("Pas d'entrainement");
                }
                setTrainingList(trainingFromDb);
            }
        }, mDb).execute();
    }

    public void setClickOnTrainingList(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long id) {
                // Récupération l'entrainements sur lequel on a cliqué depuis la base de données
                long viewId = view.getId();
                if (viewId == R.id.label) {
                    new GetTraining(new GetTraining.AsyncResponse(){
                        @Override
                        // Récupère le résultat renvoyé par la méthode onPostExecute() de la classe GetTraining
                        public void processFinish(Training trainingFromDb){
                            if (trainingFromDb != null) {
                                //Initialise l'entrainement avec les données récupérées dans la base de données
                                training = trainingFromDb;
                            }
                            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                            intent.putExtra(TRAINING, training);
                            startActivity(intent);
                        }
                    }, mDb, adapterView, position).execute();
                }
                else if (viewId == R.id.icon) {
                    String trainingName = adapterView.getItemAtPosition(position).toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Suppresion d'entrainement");
                    builder.setMessage("Etes-vous sûr de vouloir supprimer l'entrainement " + trainingName + " ?");
                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            deleteTraining(adapterView, position);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public void deleteTraining(AdapterView<?> adapterView, final int position) {
        new DeleteTraining(new DeleteTraining.AsyncResponse(){
            @Override
            // Récupère le résultat renvoyé par la méthode onPostExecute() de la classe DeleteTraining
            public void processFinish(int trainingFromDb){
                if (trainingFromDb != 0) {
                    Toast.makeText(getApplicationContext(), "Entrainement supprimé", Toast.LENGTH_SHORT).show();
                    getAllTraining();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Une erreur est survenue, veuillez réessayer", Toast.LENGTH_SHORT).show();
                }
            }
        }, mDb, adapterView, position).execute();
    }

    // Lorsque l'utilisateur souhaite créé un nouvel entraienement
    public void onClickCreate(View v) {
        EditText input = findViewById(R.id.nom_entrainement);
        String trainingName = input.getText().toString();
        if(trainingName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Veuillez entrer un nom d'entrainement", Toast.LENGTH_LONG).show();
        }
        else if(trainingsNameList.contains(trainingName)) {
            Toast.makeText(getApplicationContext(), "Le nom de l'entrainement doit être unique", Toast.LENGTH_LONG).show();
        }
        // Si l'utilisateur a bien entré un nom d'entrainement
        else{
            Intent intent = new Intent(this, TrainingSetupActivity.class);
            intent.putExtra(TRAINING_NAME, trainingName);
            startActivity(intent);
        }
    }
}
