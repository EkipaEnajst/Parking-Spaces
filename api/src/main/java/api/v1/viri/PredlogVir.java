package api.v1.viri;

import org.ekipaenajst.parkingspaces.beans.PredlogiZrno;
import org.ekipaenajst.parkingspaces.entitete.Predlog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("predlogi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PredlogVir {

    @Inject
    PredlogiZrno predlogiZrno;

    @POST
    public Response dodajPredlog(Predlog p) {
        predlogiZrno.createPredlog(p);
        return Response.status(Response.Status.CREATED).entity(p).build();
    }
}
