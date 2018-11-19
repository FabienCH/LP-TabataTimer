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

    private EditText input;
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

        new GetAllTraining(new GetAllTraining.AsyncResponse(){
            @Override
            public void processFinish(List<Training> trainingFromDb){
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.
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

    public void setTrainingList() {
        // Affiche la liste des étapes
        ListView listView = findViewById(R.id.liste_entrainements);
        adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainingsNameList);
        listView.setAdapter(adaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                new GetTraining(new GetTraining.AsyncResponse(){
                    @Override
                    public void processFinish(List<Training> trainingFromDb){
                        //Here you will receive the result fired from async class
                        //of onPostExecute(result) method.
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

    public void onClickCreate(View v) {
        input = (EditText)findViewById(R.id.nom_entrainement);
        String nomEntrainement = input.getText().toString();
        if(nomEntrainement.isEmpty()){
            Toast.makeText(getApplicationContext(), "Veillez entrer un nom d'entrainement", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = new Intent(this, TrainingSetupActivity.class);
            intent.putExtra(TRAINING_NAME, nomEntrainement);
            startActivity(intent);
        }
    }
}
