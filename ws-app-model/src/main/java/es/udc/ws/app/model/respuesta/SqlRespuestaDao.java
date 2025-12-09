package es.udc.ws.app.model.respuesta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlRespuestaDao {

    // TODO: SqlRespuestaDao no incluye operación para eliminar una respuesta, y es necesaria para implementar las pruebas conforme se ha explicado en clase.

    //Crear una nueva respuesta
    Respuesta create(Connection connection, Respuesta respuesta);

    //Actualizar una respuesta existente (si el usuario ya respondió)
    void update(Connection connection, Respuesta respuesta) throws InstanceNotFoundException;

    //Buscar una respuesta por encuesta y email (para saber si el usuario ya respondió)
    Respuesta findByEncuestaAndEmail(Connection connection, Long encuestaId, String emailUsuario) throws InstanceNotFoundException;

    // TODO: puede devolver SQLException, cuando no debería para independizar lo más posible de la tecnología de implementación (principio de capas).
    List<Respuesta> findByEncuesta(Connection connection, Long encuestaId, boolean soloPositivas)
            throws SQLException;
}
