package org.ekipaenajst.parkingspaces.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ekipaenajst.parkingspaces.entitete.Parkirisce;
import org.ekipaenajst.parkingspaces.entitete.Predlog;
import org.ekipaenajst.parkingspaces.entitete.Uporabnik;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@ApplicationScoped
public class PredlogiZrno {

    @PersistenceContext(unitName = "external-jpa")
    private EntityManager em;

    private Logger log = Logger.getLogger(PredlogiZrno.class.getName());

    private HttpClient httpClient;

    private String externaldataURL;

    private ObjectMapper objectMapper;

    private final int EARTH_RADIUS = 6371;


    @PostConstruct
    private void init() {

        Map<String,String> env = System.getenv();

        objectMapper = new ObjectMapper();

        httpClient = HttpClient.newBuilder().build();

        //externaldataURL = env.get("EXTERNALDATA_URL");//"http://172.17.0.3:8080/v1/parkirisca/";
        String edHost = env.get("EXTERNALDATA_DEPLOYMENT_SERVICE_SERVICE_HOST");
        //String edPort = env.get("EXTERNALDATA_DEPLOYMENT_SERVICE_SERVICE_PORT");
        externaldataURL = String.format("http://%s:%s/v1/parkirisca", edHost, 8080);

    }

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

    @Transactional
    public void createPredlog(Predlog predlog) {

        Predlog obstojecPredlog = this.findByLokacija(predlog.getLokacija());

        if (obstojecPredlog == null) {
            obstojecPredlog = new Predlog();
            obstojecPredlog.setLokacija(predlog.getLokacija());
            obstojecPredlog.setIme(predlog.getIme());
            obstojecPredlog.setStGlasov(1);
            obstojecPredlog.setVolilci(predlog.getVolilci());
        }
        else {
            List<Uporabnik> volilci = obstojecPredlog.getVolilci();

            for (Uporabnik novVolilec: predlog.getVolilci()) {
                if (!volilci.contains(novVolilec)) {
                    volilci.add(novVolilec);
                }
            }

            obstojecPredlog.setVolilci(volilci);
            obstojecPredlog.setStGlasov(volilci.size());



//            List<Uporabnik> volilci = obstojecPredlog.getVolilci();
//            volilci.addAll(predlog.getVolilci());
//            obstojecPredlog.setStGlasov(obstojecPredlog.getStGlasov() + predlog.getVolilci().size());
        }


        em.persist(obstojecPredlog);

        if (obstojecPredlog.getStGlasov() >= 1) {
            Parkirisce parkirisce = new Parkirisce();

            parkirisce.setIme(obstojecPredlog.getIme());
            parkirisce.setLokacija(obstojecPredlog.getLokacija());

            parkirisce.setCenaDefault(0);

            // ustvarimo parkirisce v storitvi externaldata
            this.createParkirisce(parkirisce);

            this.deletePredlog(obstojecPredlog);
        }


    }

    public void createParkirisce(Parkirisce p) {
        // TODO implementiraj komunikacijo z externaldata, da se doda parkirisce


        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(externaldataURL))
                    .headers("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(p)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
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
