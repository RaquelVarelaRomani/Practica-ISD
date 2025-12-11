package es.udc.ws.app.model.respuestaservice;

import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;

import java.util.List;


public interface RespuestaService {

    Respuesta responderEncuesta(Long encuestaId, String emailEmpleado, boolean respuestaPositiva)
            throws InstanceNotFoundException, InputValidationException,
            EncuestaCanceladaException, EncuestaFinalizadaException;

    List<Respuesta> obtenerRespuestas(Long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException;
}
