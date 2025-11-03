package es.udc.ws.app.test.model.appservice;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.sql.Connection;
import java.time.LocalDateTime;
import javax.sql.DataSource;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.respuestaservice.RespuestaService;
import es.udc.ws.app.model.respuestaservice.RespuestaServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RespuestaServiceTest {

    private static RespuestaService respuestaService = null;
    private static SqlEncuestaDao encuestaDao = null;
    private static final String VALID_EMAIL = "empleado@empresa.com";
    private static final String INVALID_EMAIL = "correo-invalido";

    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        respuestaService = RespuestaServiceFactory.getService();
        encuestaDao = SqlEncuestaDaoFactory.getDao();
    }

    // Métodos auxiliares

    private Encuesta crearEncuestaValida(Connection connection, String pregunta, int diasFuturo)
            throws Exception {
        Encuesta encuesta = new Encuesta(pregunta, LocalDateTime.now().plusDays(diasFuturo));
        encuesta.setFechaCreacion(LocalDateTime.now().withNano(0)); // 💡 evita NullPointer
        return encuestaDao.create(connection, encuesta);
    }

    // TESTS

    @Test
    public void testResponderEncuestaValida() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            Encuesta encuesta = crearEncuestaValida(connection, "¿Te gusta programar en Java?", 3);
            connection.commit();

            assertDoesNotThrow(() ->
                            respuestaService.responderEncuesta(
                                    encuesta.getEncuestaId(), VALID_EMAIL, true
                            ),
                    "No debe lanzar excepción al responder una encuesta válida"
            );
        }
    }

    @Test
    public void testResponderEncuestaEmailInvalido() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            Encuesta encuesta = crearEncuestaValida(connection, "¿Te gusta programar en Python?", 3);
            connection.commit();

            assertThrows(InputValidationException.class, () ->
                            respuestaService.responderEncuesta(
                                    encuesta.getEncuestaId(), INVALID_EMAIL, true
                            ),
                    "Debe lanzar InputValidationException por email inválido"
            );
        }
    }

    @Test
    public void testResponderEncuestaFinalizada() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            // Creamos una encuesta con fecha fin en el pasado
            Encuesta temp = new Encuesta("¿Te gusta programar en C?", LocalDateTime.now().minusDays(1));
            temp.setFechaCreacion(LocalDateTime.now().minusDays(2)); // evita null
            Encuesta encuestaCreada = encuestaDao.create(connection, temp);
            connection.commit();

            assertThrows(EncuestaFinalizadaException.class, () ->
                            respuestaService.responderEncuesta(
                                    encuestaCreada.getEncuestaId(), VALID_EMAIL, true
                            ),
                    "Debe lanzar EncuestaFinalizadaException al responder una encuesta ya finalizada"
            );
        }
    }

    @Test
    public void testResponderEncuestaNoExistente() {
        assertThrows(InstanceNotFoundException.class, () ->
                        respuestaService.responderEncuesta(
                                99999L, VALID_EMAIL, true
                        ),
                "Debe lanzar InstanceNotFoundException al intentar responder una encuesta inexistente"
        );
    }
}
