package tabatatimer.data;

import java.io.Serializable;

public class Exercice implements Serializable {

    private String name;
    private int tempsTravail;
    private int tempsRepos;
    private int nbSerie;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTempsTravail() {
        return tempsTravail;
    }

    public void setTempsTravail(int tempsTravail) {
        this.tempsTravail = tempsTravail;
    }

    public int getTempsRepos() {
        return tempsRepos;
    }

    public void setTempsRepos(int tempsRepos) {
        this.tempsRepos = tempsRepos;
    }

    public int getNbSerie() {
        return nbSerie;
    }

    public void setNbSerie(int nbSerie) {
        this.nbSerie = nbSerie;
    }
}
