package org.ekipaenajst.parkingspaces.beans;

import org.ekipaenajst.parkingspaces.dtos.PredlogDTO;
import org.ekipaenajst.parkingspaces.entitete.Predlog;
import org.ekipaenajst.parkingspaces.entitete.Uporabnik;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class PredlogiZrno {

    @PersistenceContext(unitName = "external-jpa")
    private EntityManager em;

    private Logger log = Logger.getLogger(PredlogiZrno.class.getName());

    private final int EARTH_RADIUS = 6371;

    public List<Predlog> getPredlogi() {
        Query q = em.createNamedQuery("Predlog.findAll", Predlog.class);

        List<Predlog> resultList = (List<Predlog>)q.getResultList();

        return resultList;
    }


    public Predlog findByLokacija(String lokacija) {
        String[] stringFloats = lokacija.split(",");

        Double latitude = Double.parseDouble(stringFloats[0]);
        Double longitude = Double.parseDouble(stringFloats[1]);

        List<Predlog> predlogi = this.getPredlogi();

        for (Predlog predlog : predlogi) {

            String[] predlogFloats = predlog.getLokacija().split(",");

            Double latitude2 = Double.parseDouble(predlogFloats[0]);
            Double longitude2 = Double.parseDouble(predlogFloats[1]);

            if (calculateDistance(latitude,longitude,latitude2,longitude2) <= 0.5) {
                return predlog;
            }
        }
        return null;
    }

    public void deletePredlog(Predlog p) {
        em.remove(p);
    }

    public void createPredlog(Predlog p) {

        Predlog obstojecPredlog = this.findByLokacija(p.getLokacija());

        if (obstojecPredlog == null) {
            obstojecPredlog = new Predlog();
            obstojecPredlog.setLokacija(p.getLokacija());
            obstojecPredlog.setIme(p.getIme());
            obstojecPredlog.setStGlasov(1);
            obstojecPredlog.setVolilci(p.getVolilci());
        }
        else {
            List<Uporabnik> volilci = obstojecPredlog.getVolilci();
            volilci.addAll(p.getVolilci());
            obstojecPredlog.setStGlasov(obstojecPredlog.getStGlasov() + p.getVolilci().size());
        }


        em.persist(obstojecPredlog);

        if (obstojecPredlog.getStGlasov() >= 10) {
            // TODO implementiraj komunikacijo z externaldata, da se doda parkirisce
            this.deletePredlog(obstojecPredlog);
        }


    }

    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    // vrne KM
    private double calculateDistance(double startLat, double startLong, double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }


}