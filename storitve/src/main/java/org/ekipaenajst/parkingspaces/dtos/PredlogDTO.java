package org.ekipaenajst.parkingspaces.dtos;

import org.ekipaenajst.parkingspaces.entitete.Uporabnik;

import javax.persistence.ManyToMany;
import java.util.List;

public class PredlogDTO {

    private String ime;
    private String lokacija;

    private int stGlasov;
    private List<Uporabnik> volilci;

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public int getStGlasov() {
        return stGlasov;
    }

    public void setStGlasov(int stGlasov) {
        this.stGlasov = stGlasov;
    }

    public List<Uporabnik> getVolilci() {
        return volilci;
    }

    public void setVolilci(List<Uporabnik> volilci) {
        this.volilci = volilci;
    }
}
