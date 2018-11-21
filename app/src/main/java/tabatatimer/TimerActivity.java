package tabatatimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import tabatatimer.data.Compteur;
import tabatatimer.data.Exercise;
import tabatatimer.data.OnUpdateListener;
import tabatatimer.data.db.Training;

public class TimerActivity extends AppCompatActivity implements OnUpdateListener {
   
    // VIEW
    private TextView globalTimerText;
    private TextView timerText;
    private TextView stepText;

    // DATA
    private Training training;
    private Integer remainingRounds;
    private Integer remainingExercises;
    private int exercicesTotalTime;

    private Compteur currentExerciseTimer;
    private Compteur globalTimer;
    private LinkedHashMap<Integer, Exercise> exercises;
    private int currentExerciseId;
    private ArrayList<String> stepsList;
    private ArrayAdapter<String> adaptor;
    private LinkedHashMap<String, String> allText;

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if(currentExerciseTimer.isStarted()) {
            currentExerciseTimer.pause();
            globalTimer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentExerciseTimer.hasStarted()) {
            Button input = findViewById(R.id.pause_button);
            input.setText("Start");
            input = findViewById(R.id.stop_button);
            input.setText("Reset");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        if(savedInstanceState == null) {
            // Initialise les données pour les compteurs
            training = (Training) getIntent().getSerializableExtra(MainActivity.TRAINING);
            initActivity();
        }
        else {
            this.restoreMe(savedInstanceState);
            restoreAllText();
            ListView listView = findViewById(R.id.text_liste_etapes);
            adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepsList);
            listView.setAdapter(adaptor);
            UIupdate();

        }
    }

    // Initialise les données et textes de l'activité
    public void initActivity() {
        currentExerciseId = 0;
        exercicesTotalTime = 0;
        exercises = new LinkedHashMap<>();
        stepsList =  new ArrayList<>();
        for(int i = 0; i<training.getExercises().size(); i++) {
            Exercise exercise = training.getExercises().get(i);
            exercises.put(i, exercise);
            exercicesTotalTime += exercise.getTotalExerciseTime();
            addStepToList (i, exercise);
        }
        stepsList.add("Terminé");
        remainingExercises = exercises.size();
        remainingRounds = exercises.get(currentExerciseId).getNumberOfRounds();

        int globalTimerValue = training.getTempsPrep() + exercicesTotalTime
                + training.getTempsReposL() * (exercises.size() -1);

        // Initialise les champs textes
        initText();

        // Initialise les compteurs
        currentExerciseTimer = new Compteur(training.getTempsPrep()*1000);
        globalTimer = new Compteur(globalTimerValue*1000);

        // Abonne l'activité aux compteurs pour "suivre" les événements
        currentExerciseTimer.addOnUpdateListener(this);
        globalTimer.addOnUpdateListener(this);

        UIupdate();
    }

    // Initialise les champs texte et bouton de la vue
    public void initText() {
        Button input = findViewById(R.id.stop_button);
        input.setText("Retour");
        input = findViewById(R.id.pause_button);
        input.setText("Start");

        // Affiche le nombre de séries et exercices restants
        TextView textView = findViewById(R.id.text_cycle);
        textView.setText("Série restante : " + remainingRounds + "/" + exercises.get(0).getNumberOfRounds());
        textView = findViewById(R.id.text_serie);
        textView.setText("Exercice restant : " + remainingExercises + "/" + training.getExercises().size());

        // Affiche la liste des étapes
        ListView listView = findViewById(R.id.text_liste_etapes);
        adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepsList);
        listView.setAdapter(adaptor);

        // Affiche la première étape comme étant préparation
        stepText = findViewById(R.id.text_etape);
        stepText.setText("Préparation");
    }

    // Mise à jour graphique
    private void UIupdate() {
        // Affichage les informations du compteur
        timerText = (TextView) findViewById(R.id.text_timer);
        timerText.setText(currentExerciseTimer.getMinutes() + ":"
                + String.format("%02d", currentExerciseTimer.getSecondes()) + ":"
                + String.format("%03d", currentExerciseTimer.getMillisecondes()));

        globalTimerText = (TextView) findViewById(R.id.text_global_timer);
        globalTimerText.setText(training.getName()+ " - " + globalTimer.getMinutes() + ":"
                + String.format("%02d", globalTimer.getSecondes()) + ":"
                + String.format("%03d", globalTimer.getMillisecondes()));

    }

    // Mise à jour graphique à la fin d'une étape
    public void UIupdateEndedStep() {
        String textStep = stepsList.get(0);
        if(globalTimer.getUpdatedTime() == 0) {
            stepText.setText(textStep);
        }
        else {
            stepText.setText(textStep.substring(0, textStep.length() - 5));
        }
        TextView textView = findViewById(R.id.text_cycle);
        textView.setText("Série restante : " + remainingRounds + "/" +  exercises.get(currentExerciseId).getNumberOfRounds());
        textView = findViewById(R.id.text_serie);
        textView.setText("Exercice restant : " + remainingExercises + "/" + training.getExercises().size());

        // Mise à jour de la liste des étapes (on supprime celle qui vient de se terminer)
        stepsList.remove(0);

        // Affiche la liste des étapes
        ListView listView = findViewById(R.id.text_liste_etapes);
        adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepsList);
        listView.setAdapter(adaptor);
    }

    // Ajoute une étape à la liste des étapes
    public void addStepToList(int i, Exercise exercise) {

        for(int j = 0; j< exercise.getNumberOfRounds(); j++) {
            stepsList.add(exercise.getName() + " : " + exercise.getWorkoutTime() + "s");
            if(j != exercise.getNumberOfRounds() -1) {
                stepsList.add("Repos : " + exercise.getRestTime() + "s");
            }
        }
        if(i != training.getExercises().size() -1) {
            stepsList.add("Repos Long : " + training.getTempsReposL() + "s");
        }
    }

    // Créé compteur de la prochaine étape
    public void createNextTimer (String testStep) {
        stepText = (TextView) findViewById(R.id.text_etape);
        // Décrémentation du nombres de cycle/série restants et création du compteur
        if(stepText.getText().toString().equals("Préparation")) {
            remainingRounds--;
            remainingExercises--;
            currentExerciseTimer = new Compteur((int) exercises.get(currentExerciseId).getWorkoutTime() * 1000);
        }
        else if(testStep.equals("Repos")) {
            currentExerciseTimer = new Compteur((int) exercises.get(currentExerciseId).getRestTime() * 1000);
        }
        else if(testStep.equals("Repos Long")) {
            remainingExercises--;
            currentExerciseTimer = new Compteur((int) training.getTempsReposL() * 1000);
            currentExerciseId++;
            remainingRounds = exercises.get(currentExerciseId).getNumberOfRounds();
        }
        else {
            remainingRounds--;
            currentExerciseTimer = new Compteur((int) exercises.get(currentExerciseId).getWorkoutTime() * 1000);
        }
    }

    // Lance les compteurs
    public void onStart(View view) {
        currentExerciseTimer.start();
        globalTimer.start();
    }

    // Met en pause les compteurs
    public void onPause(View view) {
        currentExerciseTimer.pause();
        globalTimer.pause();
    }

    /**
     * Méthode appelée à chaque update du compteur (l'activité est abonnée au compteur)
     */
    @Override
    public void onUpdate() {
        // Si le currentExerciseTimer se termine et qu'il reste au moins une étape
        if(currentExerciseTimer.getUpdatedTime() == 0) {
            if(globalTimer.getUpdatedTime() != 0) {
                String testStep = stepsList.get(0).substring(0, stepsList.get(0).length() - 5);
                createNextTimer(testStep);
                currentExerciseTimer.addOnUpdateListener(this);
                currentExerciseTimer.start();

                // Mise à jour graphique
                UIupdateEndedStep();
            }
            // Si l'entrainement se termine
            else if(globalTimer.getUpdatedTime() == 0){
                Button input = findViewById(R.id.stop_button);
                input.setText("Retour");
                input = findViewById(R.id.pause_button);
                input.setText("Restart");

                // Mise à jour graphique
                UIupdateEndedStep();
            }
        }
        // Mise à jour graphique
        UIupdate();
    }

    // Retour à l'activité principale
    public void onClickStop(View v) {
        Button input = findViewById(R.id.stop_button);
        String textButton = input.getText().toString();
        // Si le compteur est en pause, qu'il a déjà été démarré et que l'entrainement n'est pas terminé, on reset l'entrainement
        if(!currentExerciseTimer.isStarted() && currentExerciseTimer.hasStarted() && globalTimer.getUpdatedTime() != 0) {
            initActivity();
            input = findViewById(R.id.pause_button);
            input.setText("Start");
        }
        // Sinon on le stop et on retourne à l'activité principale
        else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if(currentExerciseTimer.hasStarted()) {
                currentExerciseTimer.stop();
                globalTimer.stop();
            }

        }
    }

    public void onClickPause(View v) {
        // Si l'entrainement est fini, le bouton pause devient le bouton restart
        if(globalTimer.getUpdatedTime() == 0) {
            initActivity();
        }
        else {
            Button input = findViewById(R.id.pause_button);
            if(currentExerciseTimer.isStarted() && globalTimer.isStarted()) {
                onPause(v);
                input.setText("Start");
                input = findViewById(R.id.stop_button);
                input.setText("Reset");
            }
            else {
                onStart(v);
                input.setText("Pause");
                input = findViewById(R.id.stop_button);
                input.setText("Stop");
            }
        }
    }

    // Sauvegarde les champs textes de l'activité lors d'un changement d'orientation
    public void saveAllText(){
        allText = new LinkedHashMap<>();
        TextView text = findViewById(R.id.text_etape);
        allText.put("textEtape", text.getText().toString());
        Button button = findViewById(R.id.pause_button);
        allText.put("pauseButton", button.getText().toString());
        button = findViewById(R.id.stop_button);
        allText.put("stopButton", button.getText().toString());
        text = findViewById(R.id.text_cycle);
        allText.put("textCycle", text.getText().toString());
        text = findViewById(R.id.text_serie);
        allText.put("textSerie", text.getText().toString());
    }

    // Restore les champs textes de l'activité lors d'un changement d'orientation
    public void restoreAllText() {
        TextView text = findViewById(R.id.text_etape);
        System.out.println(allText.get("textEtape"));
        text.setText(allText.get("textEtape"));
        Button button = findViewById(R.id.pause_button);
        button.setText(allText.get("pauseButton"));
        button = findViewById(R.id.stop_button);
        button.setText(allText.get("stopButton"));
        text = findViewById(R.id.text_cycle);
        text.setText(allText.get("textCycle"));
        text = findViewById(R.id.text_serie);
        text.setText(allText.get("textSerie"));
    }

    // Sauvegarde les données de l'activité lors d'un changement d'orientation
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("currentExerciseTimer", currentExerciseTimer.getUpdatedTime());
        savedInstanceState.putLong("globalTimer", globalTimer.getUpdatedTime());
        if(currentExerciseTimer.isStarted() && globalTimer.isStarted()) {
            savedInstanceState.putBoolean("timerWasStarted", true);
            currentExerciseTimer.stop();
            globalTimer.stop();
        }
        else {
            savedInstanceState.putBoolean("timerWasStarted", false);
        }
        currentExerciseTimer.addOnUpdateListener(null);
        globalTimer.addOnUpdateListener(null);
        savedInstanceState.putSerializable("training", training);
        savedInstanceState.putSerializable("exercises", exercises);
        savedInstanceState.putInt("currentExerciseId", currentExerciseId);
        savedInstanceState.putInt("remainingExercises", remainingExercises);
        savedInstanceState.putInt("remainingRounds", remainingRounds);
        savedInstanceState.putSerializable("stepsList", stepsList);
        saveAllText();
        savedInstanceState.putSerializable("allText", allText);
    }

    // Restaure les données de l'activité lors d'un changement d'orientation
    private void restoreMe(Bundle state) {
        if (state!=null) { // Le bundle existe donc:
            // Restauration des données
            Long compteurTime = state.getLong("currentExerciseTimer");
            Long globalTimerTime = state.getLong("globalTimer");
            currentExerciseTimer = new Compteur(compteurTime);
            globalTimer = new Compteur(globalTimerTime);
            // Si le compteur n'était pas terminé
            if(globalTimer.getUpdatedTime() != 0) {
                currentExerciseTimer.addOnUpdateListener(this);
                globalTimer.addOnUpdateListener(this);
                boolean timerWasStarted = state.getBoolean("timerWasStarted");
                if(timerWasStarted) {
                    currentExerciseTimer.start();
                    globalTimer.start();
                }
            }
            training = (Training) state.getSerializable("training");
            exercises = (LinkedHashMap<Integer, Exercise>) state.getSerializable("exercises");
            currentExerciseId = state.getInt("currentExerciseId");
            remainingExercises = state.getInt("remainingExercises");
            remainingRounds = state.getInt("remainingRounds");
            stepsList = (ArrayList<String>) state.getSerializable("stepsList");
            allText = (LinkedHashMap<String, String>) state.getSerializable("allText");
        }
    }
}
