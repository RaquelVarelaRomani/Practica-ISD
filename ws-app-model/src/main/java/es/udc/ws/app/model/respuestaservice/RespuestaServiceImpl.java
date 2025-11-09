package es.udc.ws.app.model.respuestaservice;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaDao;
import es.udc.ws.app.model.respuesta.SqlRespuestaDaoFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RespuestaServiceImpl implements RespuestaService {

    private final DataSource dataSource;
    private SqlEncuestaDao encuestaDao = SqlEncuestaDaoFactory.getDao();
    private SqlRespuestaDao respuestaDao = SqlRespuestaDaoFactory.getDao();

    public RespuestaServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        respuestaDao = SqlRespuestaDaoFactory.getDao();
    }

    // --------------------------------------------------------------
    // VALIDACIONES
    // --------------------------------------------------------------

    private void validateEmail(String emailEmpleado) throws InputValidationException {
        PropertyValidator.validateMandatoryString("emailEmpleado", emailEmpleado);

        if (!emailEmpleado.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InputValidationException("Formato de email inválido: " + emailEmpleado);
        }
    }

    // --------------------------------------------------------------
    // FUNCIONALIDAD 4: Responder Encuesta
    // --------------------------------------------------------------

    @Override
    public void responderEncuesta(Long encuestaId, String emailEmpleado, boolean respuestaPositiva)
            throws InstanceNotFoundException, InputValidationException,
            EncuestaCanceladaException, EncuestaFinalizadaException {

        // Validación del email
        validateEmail(emailEmpleado);

        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            try {
                // Buscar encuesta
                Encuesta encuesta = encuestaDao.findById(connection, encuestaId);

                // Validar estado de la encuesta
                if (encuesta.isCancelada()) {
                    throw new EncuestaCanceladaException(encuestaId);
                }

                if (!encuesta.getFechaFin().isAfter(LocalDateTime.now())) {
                    throw new EncuestaFinalizadaException(encuestaId);
                }

                // Intentar recuperar respuesta existente
                try {
                    Respuesta existente = respuestaDao.findByEncuestaAndEmail(connection, encuestaId, emailEmpleado);
                    existente.setRespuestaPositiva(respuestaPositiva);
                    existente.setFechaRespuesta(LocalDateTime.now().withNano(0));
                    respuestaDao.update(connection, existente);

                } catch (InstanceNotFoundException e) {
                    // Si no existe, crear nueva
                    Respuesta nueva = new Respuesta(encuestaId, emailEmpleado, respuestaPositiva);
                    nueva.setFechaRespuesta(LocalDateTime.now().withNano(0));
                    respuestaDao.create(connection, nueva);
                }

                connection.commit();

            } catch (InstanceNotFoundException | EncuestaCanceladaException | EncuestaFinalizadaException e) {
                connection.commit();
                throw e;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error SQL al responder encuesta", e);

            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public List<Respuesta> obtenerRespuestas(Long encuestaId, boolean soloPositivas) throws InstanceNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            encuestaDao.findById(connection, encuestaId);

            return respuestaDao.findByEncuesta(connection, encuestaId, soloPositivas);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener respuestas", e);
        }
    }
}
