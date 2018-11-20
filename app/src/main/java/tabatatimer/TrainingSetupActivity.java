package tabatatimer;

import android.content.DialogInterface;
import android.content.Intent;
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

import tabatatimer.data.Exercise;
import tabatatimer.data.SaveTraining;
import tabatatimer.data.db.DatabaseClient;
import tabatatimer.data.db.Training;

public class TrainingSetupActivity extends AppCompatActivity {

    private EditText input;
    private Training training = new Training();
    private DatabaseClient mDb;
    protected ArrayList<AlertDialog.Builder> alertDialogList  = new ArrayList<AlertDialog.Builder>();

    public static final String TRAINING = "Training";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);

        // Récupère le nom de l'entrainement, l'affiche et l'ajoute à l'objet training
        String trainingName = getIntent().getStringExtra(MainActivity.TRAINING_NAME);
        TextView textView = findViewById(R.id.training_name);
        textView.setText(trainingName);
        training.setName(trainingName);

        mDb = DatabaseClient.getInstance(getApplicationContext());
    }

    // Sauvegarde l'entrainement créé dans la base de données et lance l'Activity timer
    private void saveAndStartTraining() {
        new SaveTraining(new SaveTraining.AsyncResponse(){
            @Override
            // Récupère le résultat renvoyé par la méthode onPostExecute() de la classe SaveTraining
            public void processFinish(Training training){
                Intent intent = new Intent(TrainingSetupActivity.this, TimerActivity.class);
                intent.putExtra(TRAINING, training);
                startActivity(intent);
            }
        }, mDb, training).execute();
    }

    public void onClickAdd(View v) {
        Button button = findViewById(R.id.button);
        String textButton = button.getText().toString();
        // Si l'entrainement a été configuré
        if(textButton == "Sauvegarder et lancer") {
            // On vérifie que les données de l'entrainement sont valides et on le sauvegarde
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
            // On vérifie que les données de l'entrainement sont valides
            if(validTrainingData()) {
                // Affichage des alert dialog pour configurer les exercices
                // i = training.getExercises().size() pour que si l'on clique ok avec des données d'exercice non valide,
                // on afficher la boite de dialogue pour l'exercice n+1 si n exercices ont déja été configuré conrrectement
                for(int i = training.getExercises().size(); i < Integer.parseInt(strNbExercice); i++) {
                    alertDialogList.add(new AlertDialog.Builder(this));
                    // Add the buttons
                    final int finalI = i;
                    final int nbExercice = Integer.parseInt(strNbExercice);
                    alertDialogList.get(i - training.getExercises().size()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface currentDialog, int id) {
                            // User clicked OK button
                            onClickOkOnDialog(currentDialog, finalI, nbExercice);
                        }
                    });
                    // Create the AlertDialog
                    if(i == training.getExercises().size()) {
                        displayFirstDialog(i);
                    }
                }
            }
        }
    }

    // Valide les données d'un exercice, affiche la prochaine allert dialog ou permet de lancer l'entrainement
    public void onClickOkOnDialog(DialogInterface currentDialog, int i, int nbExercice) {
        AlertDialog alertDialog = (AlertDialog) currentDialog;
        // Si les données sont valide, on ajoute l'exercice à la liste
        if(validExerciceData(alertDialog)) {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_add_exercice, null);
            addExerciseToList(alertDialog);
            currentDialog.dismiss();
            // Si il reste des exercices, on affiche la prochaine alert dialog
            if(i < nbExercice -1) {
                int numExercice = training.getExercises().size() +1;
                AlertDialog nextDialog = alertDialogList.get(i +1).setTitle("Exercise " + numExercice).create();
                nextDialog.setView(alertLayout);
                nextDialog.show();
            }
            // Sinon, on permet de lancer l'entrainement
            else {
                EditText input = findViewById(R.id.nb_exercice);
                input.setEnabled(false);
                TextView button = TrainingSetupActivity.this.findViewById(R.id.button);
                button.setText("Sauvegarder et lancer");
            }
        }
        else {
            displayFirstDialog(i);
        }
    }

    // Affiche la permière alert dialog
    public void displayFirstDialog(int i){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_add_exercice, null);
        alertDialogList.get(i).setView(alertLayout);
        int numExercice = i +1;
        AlertDialog dialog =  alertDialogList.get(i).setTitle("Exercise " + numExercice).create();
        dialog.show();
    }

    // Valide les données d'un exercice (nom pas vide et temps supérieur à 0)
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

    // Valide les données d'un entrainement (nom pas vide et temps supérieur à 0)
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

    // Ajoute les données entrées par l'utilisateur à un exercice
    public Exercise setExerciceAttributes(Exercise exercise, AlertDialog dialog) {
        TextView input = dialog.findViewById(R.id.input_name);
        String textName = input.getText().toString();
        exercise.setName(textName);

        input = dialog.findViewById(R.id.tps_travail);
        Integer tpsTravail = Integer.parseInt(input.getText().toString());
        exercise.setWorkoutTime(tpsTravail);

        input = dialog.findViewById(R.id.temps_repos);
        Integer tpsRepos = Integer.parseInt(input.getText().toString());
        exercise.setRestTime(tpsRepos);

        input = dialog.findViewById(R.id.nb_serie);
        Integer nbSerie = Integer.parseInt(input.getText().toString());
        exercise.setNumberOfRounds(nbSerie);

        return exercise;
    }

    // Ajoute un exercice à la liste
    public void addExerciseToList(AlertDialog alertDialog) {
        Exercise exercise = new Exercise();
        exercise = setExerciceAttributes(exercise, alertDialog);
        training.addExercice(exercise);

        ListView listView = findViewById(R.id.text_liste_exercices);
        ArrayList exercicesList = getExercicesList(training.getExercises());
        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(TrainingSetupActivity.this, android.R.layout.simple_list_item_1, exercicesList);
        listView.setAdapter(adaptor);
    }

    // Ajoute un exercice à la liste et la retourne
    public ArrayList<String> getExercicesList(List<Exercise> exercises) {
        ArrayList<String> exercicesList = new ArrayList<String>();
        for(int i = 0; i< exercises.size(); i++) {
            String exerciceDetails = exercises.get(i).getName()
                + " : ["
                + exercises.get(i).getWorkoutTime()
                + "s - "
                + exercises.get(i).getRestTime()
                + "s] x "
                + exercises.get(i).getNumberOfRounds();
            exercicesList.add(exerciceDetails);
        }
        return exercicesList;
    }
}
