package es.udc.ws.app.test.model.appservice;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.encuestaservice.EncuestaService;
import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.respuesta.Respuesta;
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
    private static EncuestaService encuestaService = null;

    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        respuestaService = RespuestaServiceFactory.getService();
        encuestaService = EncuestaServiceFactory.getService();
    }

    // ------------------------
    // MÉTODO AUXILIAR
    // ------------------------
    private Encuesta crearEncuestaValida() throws Exception {
        Encuesta e = new Encuesta("Pregunta test", LocalDateTime.now().plusDays(2));
        return encuestaService.crearEncuesta(e);
    }

    // ------------------------
    // TESTS
    // ------------------------

    @Test
    public void testResponderEncuestaValida() throws Exception {
        Encuesta encuesta = crearEncuestaValida();

        Respuesta r = respuestaService.responderEncuesta(
                encuesta.getEncuestaId(), "empleado@test.com", true
        );

        assertNotNull(r.getRespuestaId());
        assertEquals("empleado@test.com", r.getEmailUsuario());
        assertTrue(r.isRespuestaPositiva());

        encuestaService.cancelarEncuesta(encuesta.getEncuestaId()); // limpieza
    }

    @Test
    public void testResponderEncuestaEmailInvalido() throws Exception {
        Encuesta encuesta = crearEncuestaValida();

        assertThrows(InputValidationException.class,
                () -> respuestaService.responderEncuesta(
                        encuesta.getEncuestaId(), "xxxxx", true));

        encuestaService.cancelarEncuesta(encuesta.getEncuestaId());
    }

    @Test
    public void testResponderEncuestaFinalizada() throws Exception {

        Encuesta encuesta = new Encuesta(
                "Finalizada",
                LocalDateTime.now().minusDays(1)
        );
        encuesta.setFechaCreacion(LocalDateTime.now().minusDays(2));

        // Crear directamente en la BD usando el DAO
        try (var con = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            con.setAutoCommit(false);
            encuesta = SqlEncuestaDaoFactory.getDao().create(con, encuesta);
            con.commit();
        }

        final Long encuestaId = encuesta.getEncuestaId();

        assertThrows(EncuestaFinalizadaException.class,
                () -> respuestaService.responderEncuesta(
                        encuestaId,
                        "a@test.com",
                        true
                ),
                "Debe lanzar EncuestaFinalizadaException al responder una encuesta finalizada"
        );
    }



    @Test
    public void testResponderEncuestaNoExistente() {
        assertThrows(InstanceNotFoundException.class,
                () -> respuestaService.responderEncuesta(
                        -999L, "a@test.com", true));
    }

    @Test
    public void testModificarRespuestaExistente() throws Exception {
        Encuesta encuesta = crearEncuestaValida();
        Long id = encuesta.getEncuestaId();

        respuestaService.responderEncuesta(id, "a@test.com", true);
        respuestaService.responderEncuesta(id, "b@test.com", false);

        Respuesta modificada =
                respuestaService.responderEncuesta(id, "b@test.com", true);

        assertEquals("b@test.com", modificada.getEmailUsuario());
        assertTrue(modificada.isRespuestaPositiva());

        encuestaService.cancelarEncuesta(id);
    }

    @Test
    public void testObtenerRespuestas() throws Exception {
        Encuesta encuesta = crearEncuestaValida();
        Long id = encuesta.getEncuestaId();

        respuestaService.responderEncuesta(id, "a@test.com", true);
        respuestaService.responderEncuesta(id, "b@test.com", false);

        List<Respuesta> todas = respuestaService.obtenerRespuestas(id, false);
        assertEquals(2, todas.size());

        List<Respuesta> positivas = respuestaService.obtenerRespuestas(id, true);
        assertEquals(1, positivas.size());
        assertEquals("a@test.com", positivas.get(0).getEmailUsuario());

        encuestaService.cancelarEncuesta(id);
    }

    @Test
    public void testContadoresPositivasNegativas() throws Exception {
        Encuesta encuesta = crearEncuestaValida();

        respuestaService.responderEncuesta(encuesta.getEncuestaId(), "a@test.com", true);
        respuestaService.responderEncuesta(encuesta.getEncuestaId(), "b@test.com", false);
        respuestaService.responderEncuesta(encuesta.getEncuestaId(), "c@test.com", true);

        Encuesta reloaded =
                encuestaService.buscarEncuestaPorId(encuesta.getEncuestaId());

        assertEquals(2, reloaded.getNumPositivas());
        assertEquals(1, reloaded.getNumNegativas());

        encuestaService.cancelarEncuesta(encuesta.getEncuestaId());
    }
}
