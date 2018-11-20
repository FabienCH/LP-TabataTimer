package tabatatimer;

import android.content.Intent;
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

import tabatatimer.data.db.Training;
import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.GetAllTraining;
import tabatatimer.data.GetTraining;

public class MainActivity extends AppCompatActivity {

    private static Training training = new Training();
    private DatabaseClient mDb;
    protected ArrayList<String> trainingsNameList  = new ArrayList<String>();
    private ArrayAdapter<String> adaptor;

    public static final String TRAINING = "Training";
    public static final String TRAINING_NAME = "TrainingName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = DatabaseClient.getInstance(getApplicationContext());

        // Récupération de la liste des entrainements depuis la base de données
        new GetAllTraining(new GetAllTraining.AsyncResponse(){
            @Override
            // Récupère le résultat renvoyé par la méthode onPostExecute() de la classe GetAllTraining
            public void processFinish(List<Training> trainingFromDb){
                if(trainingFromDb != null && !trainingFromDb.isEmpty()) {
                    for(int i=0; i < trainingFromDb.size(); i++) {
                        trainingsNameList.add(trainingFromDb.get(i).getName());
                    }
                }
                else {
                    trainingsNameList.add("Pas d'entrainement");
                }
                setTrainingList();
            }
        }, mDb).execute();

    }

    // Affiche la liste des entrainements
    public void setTrainingList() {
        ListView listView = findViewById(R.id.liste_entrainements);
        adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainingsNameList);
        listView.setAdapter(adaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Récupération l'entrainements sur lequel on a cliqué depuis la base de données
                new GetTraining(new GetTraining.AsyncResponse(){
                    @Override
                    // Récupère le résultat renvoyé par la méthode onPostExecute() de la classe GetTraining
                    public void processFinish(List<Training> trainingFromDb){
                        if (trainingFromDb != null && !trainingFromDb.isEmpty()) {
                            //Initialise l'entrainement avec les données récupérées dans la base de données
                            training = trainingFromDb.get(0);
                        }
                        Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                        intent.putExtra(TRAINING, training);
                        startActivity(intent);
                    }
                }, mDb, adapterView, position).execute();
            }
        });
    }

    // Lorsque l'utilisateur souhaite créé un nouvel entraienement
    public void onClickCreate(View v) {
        EditText input = findViewById(R.id.nom_entrainement);
        String trainingName = input.getText().toString();
        if(trainingName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Veillez entrer un nom d'entrainement", Toast.LENGTH_LONG).show();
        }
        // Si l'utilisateur a bien entré un nom d'entrainement
        else{
            Intent intent = new Intent(this, TrainingSetupActivity.class);
            intent.putExtra(TRAINING_NAME, trainingName);
            startActivity(intent);
        }
    }
}
