package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.Respuesta;
import java.util.List;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.exceptions.InputValidationException;

//TODO: Importar tambien las excepciones creadas por nosotros

public interface EncuestaService {
    
    //Funcionalidad 1: Crear encuesta
    public Encuesta crearEncuesta(Encuesta encuesta) throws InputValidationException;

}
