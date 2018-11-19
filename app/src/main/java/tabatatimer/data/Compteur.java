package tabatatimer.data;

import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by fbm on 24/10/2017.
 */
public class Compteur extends UpdateSource implements Serializable {

    private long initialTime;

    // DATA
    private long updatedTime;
    private CountDownTimer timer;   // https://developer.android.com/reference/android/os/CountDownTimer.html
    private boolean isStarted = false;


    public Compteur(long initialTime) {
        this.initialTime = initialTime;
        updatedTime = initialTime;
    }

    // Lancer le compteur
    public void start() {

        if (timer == null) {

            // Créer le CountDownTimer
            timer = new CountDownTimer(updatedTime, 10) {

                // Callback fired on regular interval
                public void onTick(long millisUntilFinished) {
                    updatedTime = millisUntilFinished;
                    // Mise à jour
                    update();
                }

                // Callback fired when the time is up
                public void onFinish() {
                    updatedTime = 0;
                    // Mise à jour
                    update();
                }
            }.start();   // Start the countdown
            isStarted = true;
        }

    }

    public boolean hasStarted() {
        return initialTime != updatedTime;
    }

    public boolean isStarted() {
        return isStarted;
    }

    // Mettre en pause le compteur
    public void pause() {

        if (timer != null) {

            // Arreter le timer
            stop();

            // Mise à jour
            update();
        }
    }


    // Remettre à le compteur à la valeur initiale
    public void reset() {

        if (timer != null) {

            // Arreter le timer
            stop();
        }

        // Réinitialiser
        updatedTime = initialTime;

        // Mise à jour
        update();

    }

    // Arrete l'objet CountDownTimer et l'efface
    public void stop() {
        timer.cancel();
        timer = null;
        isStarted = false;
    }

    public int getMinutes() {
        int min = (int) updatedTime/1000/60;
        return min;
    }

    public int getSecondes() {
        int secs = (int) (updatedTime / 1000);
        return secs % 60;
    }

    public int getMillisecondes() {
        return (int) (updatedTime % 1000);
    }

    public long getUpdatedTime() { return updatedTime; }

}
