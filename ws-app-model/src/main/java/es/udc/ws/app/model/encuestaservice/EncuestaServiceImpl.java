package es.udc.ws.app.model.encuestaservice;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.Jdbc3CcSqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EncuestaServiceImpl implements EncuestaService {

    private final DataSource dataSource;
    private SqlEncuestaDao encuestaDao = SqlEncuestaDaoFactory.getDao();
    //private SqlRespuestaDao respuestaDao = SqlRespuestaDaoFactory.getDao();

    public EncuestaServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        //respuestaDao = SqlRespuestaDaoFactory.getDao();
    }

    private void validateEncuesta(Encuesta encuesta) throws InputValidationException {

        PropertyValidator.validateMandatoryString("pregunta", encuesta.getPregunta());
        
        if (encuesta.getFechaFin() == null) {
            throw new InputValidationException("La fecha de finalización no puede ser nula.");
        }

        if (!encuesta.getFechaFin().isAfter(LocalDateTime.now())) {
            throw new InputValidationException("La fecha de finalización debe ser una fecha futura.");
        }
    }

    @Override
    public Encuesta crearEncuesta(Encuesta encuesta) throws InputValidationException {
        
        validateEncuesta(encuesta);
        encuesta.setFechaCreacion(LocalDateTime.now().withNano(0));

        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            try {
                Encuesta encuestaCreada = encuestaDao.create(connection, encuesta);
                connection.commit();
                return encuestaCreada;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error SQL al crear encuesta",e);
            } catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}

        } catch (SQLException e) {
            throw new RuntimeException("Error al acceder a la base de datos",e);
        }

    }

    @Override
    public Encuesta buscarEncuestaPorId(Long encuestaId) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            return encuestaDao.findById(connection, encuestaId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> buscarEncuestasPorPalabraClave(String keyword, boolean soloActivas) {

        try (Connection connection = dataSource.getConnection()) {
            return encuestaDao.findByKeyword(connection, keyword, soloActivas);

        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void eliminarEncuesta(Long encuestaId) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {

            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            try {
                encuestaDao.remove(connection, encuestaId);
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error SQL al eliminar encuesta", e);

            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }
}
