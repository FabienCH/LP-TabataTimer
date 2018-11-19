package tabatatimer;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import tabatatimer.data.Compteur;
import tabatatimer.data.Exercice;
import tabatatimer.data.OnUpdateListener;
import tabatatimer.data.db.Training;

public class TimerActivity extends AppCompatActivity implements OnUpdateListener {
   
    // VIEW
    private TextView globalTimerText;
    private TextView timerText;
    private TextView stepText;

    // DATA
    private Training training;
    private Integer nbSerieRestante;
    private Integer nbExercicesRestant;
    private int tempsTotalExercices;

    private Compteur compteur;
    private Compteur globalTimer;
    private LinkedHashMap<Integer, Exercice> exercices;
    private int currentExerciceId;
    private ArrayList<String> stepsList;
    private ArrayAdapter<String> adaptor;
    LinkedHashMap<String, String> allText;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        if(savedInstanceState == null) {
            // Initialise les données pour les compteurs
            training = (Training) getIntent().getSerializableExtra(MainActivity.TRAINING);
            initActivity();
        }
        else {
            this.restoreMe(savedInstanceState);
            setAllText();
            ListView listView = findViewById(R.id.text_liste_etapes);
            adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepsList);
            listView.setAdapter(adaptor);
            miseAJour();

        }

    }

    public void initActivity() {
        currentExerciceId = 0;
        tempsTotalExercices = 0;
        exercices = new LinkedHashMap<>();
        stepsList =  new ArrayList<>();
        for(int i=0; i<training.getExercices().size(); i++) {
            Exercice exercice = training.getExercices().get(i);
            exercices.put(i, exercice);
            tempsTotalExercices += exercice.getTempsTravail() * exercice.getNbSerie() + exercice.getTempsRepos() * (exercice.getNbSerie() -1);
            for(int j=0; j<exercice.getNbSerie(); j++) {
                stepsList.add(exercice.getName() + " : " + exercice.getTempsTravail() + "s");
                if(j != exercice.getNbSerie() -1) {
                    stepsList.add("Repos : " + exercice.getTempsRepos() + "s");
                }
            }
            if(i != training.getExercices().size() -1) {
                stepsList.add("Repos Long : " + training.getTempsReposL() + "s");
            }
        }
        stepsList.add("Terminé");
        nbExercicesRestant = exercices.size();
        nbSerieRestante = exercices.get(currentExerciceId).getNbSerie();

        int globalTimerValue = training.getTempsPrep() + tempsTotalExercices
                + training.getTempsReposL() * (exercices.size() -1);

        // Initialise les champs textes
        initText();

        // Affiche la liste des étapes
        ListView listView = findViewById(R.id.text_liste_etapes);
        adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepsList);
        listView.setAdapter(adaptor);

        //Récupère l'étape actuelle et met à jour l'affichage
        stepText = findViewById(R.id.text_etape);
        stepText.setText("Préparation");

        // Initialise les compteurs
        compteur = new Compteur(training.getTempsPrep()*1000);
        globalTimer = new Compteur(globalTimerValue*1000);

        // Abonne l'activité aux compteurs pour "suivre" les événements
        compteur.addOnUpdateListener(this);
        globalTimer.addOnUpdateListener(this);

        miseAJour();
    }

    // Initialise les champs texte et bouton de la vue
    public void initText() {
        Button input = findViewById(R.id.stop_button);
        input.setText("Retour");
        input = findViewById(R.id.pause_button);
        input.setText("Start");

        TextView textView = findViewById(R.id.text_cycle);
        textView.setText("Série restante : " + nbSerieRestante + "/" + exercices.get(0).getNbSerie());
        textView = findViewById(R.id.text_serie);
        textView.setText("Exercice restant : " + nbExercicesRestant + "/" + training.getExercices().size());
    }

    // Lance le compteur
    public void onStart(View view) {
        compteur.start();
        globalTimer.start();
    }

    // Met en pause le compteur
    public void onPause(View view) {
        compteur.pause();
        globalTimer.pause();
    }

    // Remet à zéro le compteur
    public void onReset(View view) {
        compteur.reset();
        globalTimer.reset();
    }

    // Mise à jour graphique
    private void miseAJour() {
        // Affichage des informations du compteur
        timerText = (TextView) findViewById(R.id.text_timer);
        timerText.setText(compteur.getMinutes() + ":"
                + String.format("%02d", compteur.getSecondes()) + ":"
                + String.format("%03d", compteur.getMillisecondes()));

        globalTimerText = (TextView) findViewById(R.id.text_global_timer);
        globalTimerText.setText(training.getName()+ " - " + globalTimer.getMinutes() + ":"
                + String.format("%02d", globalTimer.getSecondes()) + ":"
                + String.format("%03d", globalTimer.getMillisecondes()));

    }

    // Mise à jour graphique à la fin d'une étape
    public void majFinEtape() {

        String textStep = stepsList.get(0);
        if(textStep.equals("Terminé")) {
            stepText.setText(textStep);
        }
        else {
            stepText.setText(textStep.substring(0, textStep.length() - 5));
        }


        TextView textView = findViewById(R.id.text_cycle);
        textView.setText("Série restante : " + nbSerieRestante + "/" +  exercices.get(currentExerciceId).getNbSerie());
        textView = findViewById(R.id.text_serie);
        textView.setText("Exercice restant : " + nbExercicesRestant + "/" + training.getExercices().size());

        // Mise à jour de la liste des étapes (on supprime celle qui vient de se terminer)
        stepsList.remove(0);

        // Affiche la liste des étapes
        ListView listView = findViewById(R.id.text_liste_etapes);
        adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepsList);
        listView.setAdapter(adaptor);
    }

    /**
     * Méthode appelée à chaque update du compteur (l'activité est abonnée au compteur)
     */
    @Override
    public void onUpdate() {
        // Si le compteur se termine et qu'il reste au moins une étape
        if(compteur.getUpdatedTime() == 0) {
            System.out.println("compteur");
            System.out.println(compteur);
            System.out.println("global compteur");
            System.out.println(globalTimer);
            if(globalTimer.getUpdatedTime() != 0) {

                String testStep = stepsList.get(0).substring(0, stepsList.get(0).length() - 5);
                stepText = (TextView) findViewById(R.id.text_etape);
                // Décrémentation du nombres de cycle/série restants
                if(stepText.getText().toString().equals("Préparation")) {
                    nbSerieRestante--;
                    nbExercicesRestant--;
                    compteur = new Compteur((int) exercices.get(currentExerciceId).getTempsTravail() * 1000);
                }
                else if(testStep.equals("Repos")) {
                    compteur = new Compteur((int) exercices.get(currentExerciceId).getTempsRepos() * 1000);
                }
                else if(testStep.equals("Repos Long")) {
                    nbExercicesRestant--;
                    compteur = new Compteur((int) training.getTempsReposL() * 1000);
                    currentExerciceId++;
                    nbSerieRestante = exercices.get(currentExerciceId).getNbSerie();
                }
                else {
                    nbSerieRestante--;
                    compteur = new Compteur((int) exercices.get(currentExerciceId).getTempsTravail() * 1000);
                }

                // Mise à jour graphique
                majFinEtape();

                compteur.addOnUpdateListener(this);
                compteur.start();
            }
            // Si l'entrainement se termine
            else if(globalTimer.getUpdatedTime() == 0){
                Button input = findViewById(R.id.stop_button);
                input.setText("Retour");
                input = findViewById(R.id.pause_button);
                input.setText("Restart");

                // Mise à jour graphique
                majFinEtape();

            }
        }
        // Mise à jour graphique
        miseAJour();
    }

    // Retour à l'activité principale
    public void onClickStop(View v) {
        Button input = findViewById(R.id.stop_button);
        String textButton = input.getText().toString();
        if(textButton == "Reset") {
            initActivity();
            input = findViewById(R.id.pause_button);
            input.setText("Start");
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if(compteur.hasStarted()) {
                compteur.stop();
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
            String textButton = input.getText().toString();
            if(textButton == "Pause") {
                onPause(v);
                input.setText("Start");
                input = findViewById(R.id.stop_button);
                input.setText("Reset");
            }
            if(textButton == "Start") {
                onStart(v);
                input.setText("Pause");
                input = findViewById(R.id.stop_button);
                input.setText("Stop");
            }
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("compteur", compteur.getUpdatedTime());
        savedInstanceState.putLong("globalTimer", globalTimer.getUpdatedTime());
        if(compteur.isStarted() && globalTimer.isStarted()) {
            savedInstanceState.putBoolean("timerWasStarted", true);
            compteur.stop();
            globalTimer.stop();
        }
        else {
            savedInstanceState.putBoolean("timerWasStarted", false);
        }
        compteur.addOnUpdateListener(null);
        globalTimer.addOnUpdateListener(null);
        savedInstanceState.putSerializable("training", training);
        savedInstanceState.putSerializable("exercices", exercices);
        savedInstanceState.putInt("currentExerciceId", currentExerciceId);
        savedInstanceState.putInt("nbExercicesRestant", nbExercicesRestant);
        savedInstanceState.putInt("nbSerieRestante", nbSerieRestante);
        savedInstanceState.putSerializable("stepsList", stepsList);
        getAllText();
        savedInstanceState.putSerializable("allText", allText);
    }

    private void restoreMe(Bundle state) {
        if (state!=null) { // Le bundle existe donc:
            // Restauration des données
            Long compteurTime = state.getLong("compteur");
            Long globalTimerTime = state.getLong("globalTimer");
            compteur = new Compteur(compteurTime);
            globalTimer = new Compteur(globalTimerTime);

            if(globalTimer.getUpdatedTime() != 0) {
                compteur.addOnUpdateListener(this);
                globalTimer.addOnUpdateListener(this);
                boolean timerWasStarted = state.getBoolean("timerWasStarted");
                if(timerWasStarted) {
                    compteur.start();
                    globalTimer.start();
                }
            }

            training = (Training) state.getSerializable("training");
            exercices = (LinkedHashMap<Integer, Exercice>) state.getSerializable("exercices");
            currentExerciceId = state.getInt("currentExerciceId");
            nbExercicesRestant = state.getInt("nbExercicesRestant");
            nbSerieRestante = state.getInt("nbSerieRestante");
            stepsList = (ArrayList<String>) state.getSerializable("stepsList");
            allText = (LinkedHashMap<String, String>) state.getSerializable("allText");
        }

}

    public void getAllText(){
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

    public void setAllText() {
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

}
