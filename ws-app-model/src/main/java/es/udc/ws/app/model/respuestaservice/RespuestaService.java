package es.udc.ws.app.model.respuestaservice;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;


public interface RespuestaService {

    void responderEncuesta(Long encuestaId, String emailEmpleado, boolean respuestaPositiva)
            throws InstanceNotFoundException, InputValidationException,
            EncuestaCanceladaException, EncuestaFinalizadaException;
}
