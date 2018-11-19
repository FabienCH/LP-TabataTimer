package tabatatimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tabatatimer.data.Exercice;
import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

public class TrainingSetupActivity extends AppCompatActivity {

    private EditText input;
    private Training training = new Training();
    private DatabaseClient mDb;
    protected List<Training> trainingFromDb;
    protected ArrayList<AlertDialog.Builder> alertDialogList  = new ArrayList<AlertDialog.Builder>();

    public static final String TRAINING = "Training";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);

        String trainingName = getIntent().getStringExtra(MainActivity.TRAINING_NAME);
        TextView textView = findViewById(R.id.training_name);
        textView.setText(trainingName);

        //Initialise un entrainement par défaut
        training.setName(trainingName);

        mDb = DatabaseClient.getInstance(getApplicationContext());

    }

    private void saveAndStartTraining() {

        /**
         * Création d'une classe asynchrone pour sauvegarder l'entrainement donné par l'utilisateur
         */
        class SaveAndStartTraining extends AsyncTask<Void, Void, Training> {

            @Override
            protected Training doInBackground(Void... Voids) {
                //Insertion dans la base de donnée
                mDb.getAppDatabase()
                        .trainingDao()
                        .insert(training);
                return training;
            }

            //Après l'enregistrement en base de donnée, lance l'activité TimerActivity
            @Override
            protected void onPostExecute(Training training) {
                super.onPostExecute(training);
                //Création de l'activty Timer
                Intent intent = new Intent(TrainingSetupActivity.this, TimerActivity.class);
                intent.putExtra(TRAINING, training);
                startActivity(intent);
            }
        }

        SaveAndStartTraining st = new SaveAndStartTraining();
        st.execute();
    }

    public void onClickAdd(View v) {
        Button button = findViewById(R.id.button);
        String textButton = button.getText().toString();
        if(textButton == "Sauvegarder et lancer") {
            if(validTrainingData()) {
                input = (EditText) findViewById(R.id.temps_prep);
                Integer trainingTempsPrep = Integer.parseInt(input.getText().toString());
                input = (EditText) findViewById(R.id.temps_reposL);
                Integer trainingTempsReposL = Integer.parseInt(input.getText().toString());
                training.setTempsPrep(trainingTempsPrep);
                training.setTempsReposL(trainingTempsReposL);
                saveAndStartTraining();
            }
        }
        else {
            EditText input = findViewById(R.id.nb_exercice);
            String strNbExercice = input.getText().toString();
            if(validTrainingData()) {
                for(int i=training.getExercices().size(); i < Integer.parseInt(strNbExercice); i++) {
                    alertDialogList.add(new AlertDialog.Builder(this));
                    // Add the buttons
                    final int finalI = i;
                    final int nbExercice = Integer.parseInt(strNbExercice);
                    alertDialogList.get(i - training.getExercices().size()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface currentDialog, int id) {

                            // User clicked OK button
                            AlertDialog alertDialog = (AlertDialog) currentDialog;
                            if(validExerciceData(alertDialog)) {
                                Exercice exercice = new Exercice();
                                exercice = setExerciceAttributes(exercice, alertDialog);
                                training.addExercice(exercice);

                                LayoutInflater inflater = getLayoutInflater();
                                View alertLayout = inflater.inflate(R.layout.layout_add_exercice, null);

                                ListView listView = findViewById(R.id.text_liste_exercices);
                                ArrayList exercicesList = getExercicesList(training.getExercices());
                                ArrayAdapter<String> adaptor = new ArrayAdapter<String>(TrainingSetupActivity.this, android.R.layout.simple_list_item_1, exercicesList);

                                listView.setAdapter(adaptor);
                                currentDialog.dismiss();

                                if(finalI < nbExercice -1) {
                                    int numExercice = training.getExercices().size() +1;
                                    AlertDialog nextDialog = alertDialogList.get(finalI +1).setTitle("Exercice " + numExercice).create();
                                    nextDialog.setView(alertLayout);
                                    nextDialog.show();
                                }
                                else {
                                    EditText input = findViewById(R.id.nb_exercice);
                                    input.setEnabled(false);
                                    TextView button = TrainingSetupActivity.this.findViewById(R.id.button);
                                    button.setText("Sauvegarder et lancer");
                                }
                            }
                            else {
                                displayFirstDialog(finalI);
                            }
                        }
                    });

                    // Create the AlertDialog
                    if(i == training.getExercices().size()) {
                        displayFirstDialog(i);
                    }
                }
            }

        }

    }

    public boolean validExerciceData(AlertDialog dialog) {
        TextView input = dialog.findViewById(R.id.input_name);
        String textName = input.getText().toString();
        if(textName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Veillez entrer un nom d'exercice", Toast.LENGTH_LONG).show();
            return false;
        }

        input = dialog.findViewById(R.id.tps_travail);
        String tpsTravail = input.getText().toString();
        if(tpsTravail.isEmpty() || Integer.parseInt(tpsTravail) <= 0){
            Toast.makeText(getApplicationContext(), "Le temps de travail doit être supérieur à 0", Toast.LENGTH_LONG).show();
            return false;
        }

        input = dialog.findViewById(R.id.temps_repos);
        String tpsRepos = input.getText().toString();
        if(tpsRepos.isEmpty() || Integer.parseInt(tpsRepos) <= 0){
            Toast.makeText(getApplicationContext(), "Le temps de repos doit être supérieur à 0", Toast.LENGTH_LONG).show();
            return false;
        }

        input = dialog.findViewById(R.id.nb_serie);
        String nbSerie = input.getText().toString();
        if(nbSerie.isEmpty() || Integer.parseInt(nbSerie) <= 0){
            Toast.makeText(getApplicationContext(), "Le nombre de série doit être supérieur à 0", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean validTrainingData() {
        EditText input = findViewById(R.id.temps_prep);
        String tempsPrep = input.getText().toString();
        if(tempsPrep.isEmpty() || Integer.parseInt(tempsPrep) <= 0){
            Toast.makeText(getApplicationContext(), "Le temps de préparation doit être supérieur à 0", Toast.LENGTH_LONG).show();
            return false;
        }

        input = findViewById(R.id.nb_exercice);
        String nbExercice = input.getText().toString();
        if(nbExercice.isEmpty() || Integer.parseInt(nbExercice) <= 0){
            Toast.makeText(getApplicationContext(), "Veillez entrer un nombre d'exercice supérieur à 0", Toast.LENGTH_LONG).show();
            return false;
        }

        input = findViewById(R.id.temps_reposL);
        String tempsReposL = input.getText().toString();
        if(tempsReposL.isEmpty() || Integer.parseInt(tempsReposL) <= 0){
            Toast.makeText(getApplicationContext(), "Le temps de repos doit être supérieur à 0", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public Exercice setExerciceAttributes(Exercice exercice, AlertDialog dialog) {
        TextView input = dialog.findViewById(R.id.input_name);
        String textName = input.getText().toString();
        exercice.setName(textName);

        input = dialog.findViewById(R.id.tps_travail);
        Integer tpsTravail = Integer.parseInt(input.getText().toString());
        exercice.setTempsTravail(tpsTravail);

        input = dialog.findViewById(R.id.temps_repos);
        Integer tpsRepos = Integer.parseInt(input.getText().toString());
        exercice.setTempsRepos(tpsRepos);

        input = dialog.findViewById(R.id.nb_serie);
        Integer nbSerie = Integer.parseInt(input.getText().toString());
        exercice.setNbSerie(nbSerie);

        return exercice;
    }

    public ArrayList<String> getExercicesList(List<Exercice> exercices) {
        ArrayList<String> exercicesList = new ArrayList<String>();
        for(int i=0; i<exercices.size(); i++) {
            String exerciceDetails = exercices.get(i).getName()
                    + " : ["
                    + exercices.get(i).getTempsTravail()
                    + "s - "
                    + exercices.get(i).getTempsRepos()
                    + "s] x "
                    + exercices.get(i).getNbSerie();
            exercicesList.add(exerciceDetails);
        }
        return exercicesList;
    }

    public void displayFirstDialog(int i){
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_add_exercice, null);
            alertDialogList.get(i).setView(alertLayout); // Again this is a set method, not add
            int numExercice = i +1;
            AlertDialog dialog =  alertDialogList.get(i).setTitle("Exercice " + numExercice).create();
            dialog.show();
    }
}
