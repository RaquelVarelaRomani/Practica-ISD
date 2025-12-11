package es.udc.ws.app.model.encuestaservice;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import java.util.List;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.exceptions.InputValidationException;

public interface EncuestaService {
    
    //Funcionalidad 1: Crear encuesta
    public Encuesta crearEncuesta(Encuesta encuesta) throws InputValidationException;

    //Funcionalidad 2: Buscar encuesta por ID
    public Encuesta buscarEncuestaPorId(Long encuestaId) throws InstanceNotFoundException;

    //Funcionalidad 3: Buscar encuestas por palabra clave
    public List<Encuesta> buscarEncuestasPorPalabraClave(String keyword, boolean soloActivas);

    //Funcionalidad 5: Cancelar encuesta
    public void cancelarEncuesta(Long encuestaId) throws InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException;

}
