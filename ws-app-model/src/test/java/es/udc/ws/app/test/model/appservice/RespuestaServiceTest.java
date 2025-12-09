package es.udc.ws.app.test.model.appservice;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.encuestaservice.EncuestaService;
import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
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

    /*   TODO:
    *       1. RespuestaServiceTest no borra nunca las respuestas creadas como parte de las pruebas.
    *       2. RespuestaServiceTest En casi todos los métodos de pruebas obtienen de forma explícita la conexión.
    *       3. RespuestaServiceTest no comprueban en ningún caso el número de respuestas positivas / negativas de una encuesta.
    *       4. RespuestaServiceTest.testResponderEncuestaValida no comprueba que la respuesta ha sido creada.
    *          - En testModificarRespuestaExistente sí se comprueba algún atributo de la respuesta, pero no todos.
    * */

    private static RespuestaService respuestaService = null;
    private static SqlEncuestaDao encuestaDao = null;
    private static EncuestaService encuestaService = null;
    private static final String VALID_EMAIL = "empleado@empresa.com";
    private static final String INVALID_EMAIL = "correo-invalido";

    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        respuestaService = RespuestaServiceFactory.getService();
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        encuestaService = EncuestaServiceFactory.getService();
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
            encuestaService.eliminarEncuesta(encuesta.getEncuestaId());
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

    @Test
    public void testObtenerRespuestas() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            // Paso 1: Crear una encuesta normal
            Encuesta encuesta = new Encuesta(
                    "¿Te gusta Java?", LocalDateTime.now().plusDays(2)
            );
            encuesta.setFechaCreacion(LocalDateTime.now().withNano(0));
            Encuesta encuestaCreada = encuestaDao.create(connection, encuesta);
            Long encuestaId = encuestaCreada.getEncuestaId();

            connection.commit();

            // Paso 2: Responder la encuesta con varios emails
            respuestaService.responderEncuesta(encuestaId, "a@email.com", true);
            respuestaService.responderEncuesta(encuestaId, "b@email.com", false);
            respuestaService.responderEncuesta(encuestaId, "c@email.com", true);

            // Paso 3: Recuperar todas las respuestas
            List<Respuesta> todas = respuestaService.obtenerRespuestas(encuestaId, false);
            assertEquals(3, todas.size());
            assertTrue(todas.stream().anyMatch(r -> r.getEmailUsuario().equals("a@email.com") && r.isRespuestaPositiva()));
            assertTrue(todas.stream().anyMatch(r -> r.getEmailUsuario().equals("b@email.com") && !r.isRespuestaPositiva()));

            // Paso 4: Recuperar solo las positivas
            List<Respuesta> soloPositivas = respuestaService.obtenerRespuestas(encuestaId, true);
            assertEquals(2, soloPositivas.size());
            assertTrue(soloPositivas.stream().allMatch(Respuesta::isRespuestaPositiva));

            // Limpieza
            encuestaService.eliminarEncuesta(encuestaId);;
        }
    }

    @Test
    public void testModificarRespuestaExistente() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            //Crear una encuesta
            Encuesta encuesta = new Encuesta(
                    "¿Te gusta Java?", LocalDateTime.now().plusDays(2)
            );
            encuesta.setFechaCreacion(LocalDateTime.now().withNano(0));
            Encuesta encuestaCreada = encuestaDao.create(connection, encuesta);
            Long encuestaId = encuestaCreada.getEncuestaId();

            connection.commit();

            //Responder la encuesta
            respuestaService.responderEncuesta(encuestaId, "a@email.com", true);
            respuestaService.responderEncuesta(encuestaId, "b@email.com", false);
            respuestaService.responderEncuesta(encuestaId, "c@email.com", true);

            //Modificar una respuesta existente
            respuestaService.responderEncuesta(encuestaId, "b@email.com", true);

            //Recuperar respuestas
            List<Respuesta> todas = respuestaService.obtenerRespuestas(encuestaId, false);
            assertEquals(3, todas.size());
            assertTrue(todas.stream().anyMatch(r -> r.getEmailUsuario().equals("a@email.com") && r.isRespuestaPositiva()));
            assertTrue(todas.stream().anyMatch(r -> r.getEmailUsuario().equals("b@email.com") && r.isRespuestaPositiva()));
            assertTrue(todas.stream().anyMatch(r -> r.getEmailUsuario().equals("c@email.com") && r.isRespuestaPositiva()));

            //Recuperar las positivas
            List<Respuesta> soloPositivas = respuestaService.obtenerRespuestas(encuestaId, true);
            assertEquals(3, soloPositivas.size());
            assertTrue(soloPositivas.stream().allMatch(Respuesta::isRespuestaPositiva));

            encuestaService.eliminarEncuesta(encuestaId);
        }
    }

    @Test
    public void testResponderEncuestaCancelada() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            Encuesta encuesta = crearEncuestaValida(connection, "¿Te gusta programar en Go?", 3);
            encuesta.setCancelada(true);
            encuestaDao.update(connection, encuesta);
            connection.commit();

            assertThrows(EncuestaCanceladaException.class, () ->
                            respuestaService.responderEncuesta(
                                    encuesta.getEncuestaId(), VALID_EMAIL, true
                            ),
                    "Debe lanzar EncuestaCanceladaException al responder una encuesta cancelada"
            );
            encuestaService.eliminarEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testObtenerRespuestasEncuestaSinRespuestas() throws Exception {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);

            //Crear una encuesta
            Encuesta encuesta = new Encuesta(
                    "¿Te gusta Kotlin?", LocalDateTime.now().plusDays(2)
            );
            encuesta.setFechaCreacion(LocalDateTime.now().withNano(0));
            Encuesta encuestaCreada = encuestaDao.create(connection, encuesta);
            Long encuestaId = encuestaCreada.getEncuestaId();

            connection.commit();

            //Recuperar respuestas
            List<Respuesta> todas = respuestaService.obtenerRespuestas(encuestaId, false);
            assertEquals(0, todas.size(), "Debe devolver una lista vacía si no hay respuestas");

            encuestaService.eliminarEncuesta(encuestaId);
        }
    }

    @Test
    public void testGettersAndSetters() {
        LocalDateTime fecha = LocalDateTime.now().withNano(0);

        Respuesta r = new Respuesta(1L, 10L, "empleado@test.com", true, fecha);

        // Setters
        r.setRespuestaId(2L);
        r.setEncuestaId(20L);
        r.setEmailUsuario("nuevo@test.com");
        r.setRespuestaPositiva(false);
        r.setFechaRespuesta(fecha.plusDays(1));

        // Getters
        assertEquals(2L, r.getRespuestaId());
        assertEquals(20L, r.getEncuestaId());
        assertEquals("nuevo@test.com", r.getEmailUsuario());
        assertFalse(r.isRespuestaPositiva());
        assertEquals(fecha.plusDays(1), r.getFechaRespuesta());
    }

    @Test
    public void testToStringContainsFields() {
        LocalDateTime fecha = LocalDateTime.now();
        Respuesta r = new Respuesta(5L, 15L, "empleado@empresa.com", true, fecha);
        String text = r.toString();

        assertTrue(text.contains("respuestaId=5"));
        assertTrue(text.contains("encuestaId=15"));
        assertTrue(text.contains("emailUsuario='empleado@empresa.com'"));
        assertTrue(text.contains("respuestaPositiva=true"));
    }

    @Test
    public void testEqualsAndHashCode() {
        LocalDateTime fecha = LocalDateTime.now().withNano(0);

        Respuesta r1 = new Respuesta(1L, 10L, "empleado@empresa.com", true, fecha);
        Respuesta r2 = new Respuesta(1L, 10L, "empleado@empresa.com", true, fecha);
        Respuesta r3 = new Respuesta(2L, 11L, "otro@empresa.com", false, fecha);

        // Igualdad
        assertEquals(r1, r2, "Dos respuestas idénticas deben ser iguales");
        assertEquals(r1.hashCode(), r2.hashCode(), "hashCode debe coincidir para objetos iguales");

        // Desigualdad
        assertNotEquals(r1, r3, "Respuestas diferentes no deben ser iguales");
        assertNotEquals(r1, null, "Una respuesta no debe ser igual a null");
        assertNotEquals(r1, "cadena", "Una respuesta no debe ser igual a un tipo distinto");
    }

    @Test
    public void testConstructorSinId() {
        Respuesta r = new Respuesta(100L, "sinid@empresa.com", true);

        assertEquals(100L, r.getEncuestaId());
        assertEquals("sinid@empresa.com", r.getEmailUsuario());
        assertTrue(r.isRespuestaPositiva());
        assertNull(r.getRespuestaId(), "El ID debe ser nulo en el constructor sin ID");
        assertNull(r.getFechaRespuesta(), "La fechaRespuesta debe ser nula por defecto");
    }
}
