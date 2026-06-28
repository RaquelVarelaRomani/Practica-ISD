package es.udc.ws.app.client.service;

import java.util.List;
import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.*;

public interface ClientEncuestaService {

    Long crearEncuesta(ClientEncuestaDto encuesta) throws InputValidationException;

    List<ClientEncuestaDto> buscarEncuestas(String keyword, boolean soloActivas);

    ClientEncuestaDto buscarEncuestaPorId(Long encuestaId) throws InstanceNotFoundException;

    void cancelarEncuesta(Long encuestaId) throws InstanceNotFoundException, ClientEncuestaFinalizadaException, ClientEncuestaCanceladaException;

    Long responderEncuesta(Long encuestaId, String emailUsuario, boolean respuestaPositiva) throws InstanceNotFoundException, ClientEncuestaFinalizadaException, ClientEncuestaCanceladaException;

    List<ClientRespuestaDto> obtenerRespuestas(Long encuestaId, boolean soloPositivas) throws InstanceNotFoundException;

    // Funcionalidad B (Extensión): Borrar encuesta finalizada o cancelada
    void borrarEncuesta(Long encuestaId) throws InstanceNotFoundException, ClientEncuestaActivaException;
}
