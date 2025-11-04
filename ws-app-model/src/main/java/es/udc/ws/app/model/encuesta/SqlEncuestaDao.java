package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.util.List;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlEncuestaDao {

    public Encuesta create(Connection connection, Encuesta encuesta);

    public Encuesta findById(Connection connection, Long encuestaId) throws InstanceNotFoundException;

    public void remove(Connection connection, Long encuestaId) throws InstanceNotFoundException;

    public void  update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException;

    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean soloActivas) throws InstanceNotFoundException;

}

