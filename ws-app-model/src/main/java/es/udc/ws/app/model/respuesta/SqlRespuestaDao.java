package es.udc.ws.app.model.respuesta;

import java.sql.Connection;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlRespuestaDao {

    //Crear una nueva respuesta
    Respuesta create(Connection connection, Respuesta respuesta);

    //Actualizar una respuesta existente (si el usuario ya respondió)
    void update(Connection connection, Respuesta respuesta) throws InstanceNotFoundException;

    //Buscar una respuesta por encuesta y email (para saber si el usuario ya respondió)
    Respuesta findByEncuestaAndEmail(Connection connection, Long encuestaId, String emailUsuario) throws InstanceNotFoundException;

    List<Respuesta> findByEncuesta(Connection connection, Long encuestaId, boolean soloPositivas);

    void remove(Connection connection, Long respuestaId) throws InstanceNotFoundException;
}
