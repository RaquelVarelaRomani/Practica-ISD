package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlEncuestaDao {

    public Encuesta create(Connection connection, Encuesta encuesta);

    public Encuesta findById(Connection connection, Long encuestaId) throws InstanceNotFoundException;

    public void remove(Connection connection, Long encuestaId) throws InstanceNotFoundException;

    public void  update(Connection connection, Encuesta encuesta) throws InstanceNotFoundException;

    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean soloActivas);

//  =========================================
//                 EXTENSIÓN
//  =========================================

    //Funcionalidad 7: Encuestas populares con fecha de finalización futura
    public List<Encuesta> findPopularSurveys(Connection connection, int minRespuestas,
                                              LocalDateTime fechaInicio, LocalDateTime fechaFin);

}

