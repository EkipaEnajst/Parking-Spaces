package org.ekipaenajst.parkingspaces.entitete;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="predlog")
@NamedQueries(value = {
        @NamedQuery(name = "Predlog.findAll", query="SELECT p FROM Predlog p")
})
public class Predlog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ime;
    private int stGlasov;
    private String lokacija;

    @ManyToMany
    private List<Uporabnik> volilci;

    @Override
    public String toString() {
        return String.format("ime: %s, stGlasov: %d, lokacija: %s", ime, stGlasov, lokacija);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getStGlasov() {
        return stGlasov;
    }

    public void setStGlasov(int stGlasov) {
        this.stGlasov = stGlasov;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public List<Uporabnik> getVolilci() {
        return volilci;
    }

    public void setVolilci(List<Uporabnik> volilci) {
        this.volilci = volilci;
    }
}
