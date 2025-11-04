package es.udc.ws.app.test.model.appservice;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.time.LocalDateTime;

import javax.sql.DataSource;

import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.encuestaservice.EncuestaService;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.app.model.encuestaservice.exceptions.*;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

public class EncuestaServiceTest {

    private static final long NON_EXISTENT_ENCUESTA_ID = -1;
    private static EncuestaService encuestaService = null;
    private static SqlEncuestaDao encuestaDao = null;

    //private static EncuestaService encuestaService = null;

    @BeforeAll
    public static void init() {

        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        encuestaService = EncuestaServiceFactory.getService();
        encuestaDao = SqlEncuestaDaoFactory.getDao();
    }

    private Encuesta getValidEncuesta(String pregunta) {
        return new Encuesta(pregunta, LocalDateTime.now().plusDays(3));
    }

    private Encuesta getValidEncuesta() {
        return getValidEncuesta("Pregunta válida");
    }

    private Encuesta createEncuesta(Encuesta encuesta) throws InputValidationException {
        return encuestaService.crearEncuesta(encuesta);
    }

    private void removeEncuesta(Long encuestaId) {

        try {
            System.out.println("Simulando borrado de encuesta con id: " + encuestaId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCrearEncuestaInvalida() {

        // Caso 1: pregunta vacía
        assertThrows(InputValidationException.class, () -> {
            Encuesta encuesta = getValidEncuesta("");
            createEncuesta(encuesta);
        }, "Debe lanzar InputValidationException por pregunta vacía");

        // Caso 2: pregunta nula
        assertThrows(InputValidationException.class, () -> {
            Encuesta encuesta = getValidEncuesta(null);
            createEncuesta(encuesta);
        }, "Debe lanzar InputValidationException por pregunta nula");

        // Caso 3: fecha fin en el pasado
        assertThrows(InputValidationException.class, () -> {
            Encuesta encuesta = new Encuesta("¿Cuál es tu nombre?", LocalDateTime.now().minusDays(1));
            createEncuesta(encuesta);
        }, "Debe lanzar InputValidationException por fecha pasada");

        // Caso 4: fecha fin igual a ahora
        assertThrows(InputValidationException.class, () -> {
            Encuesta encuesta = new Encuesta("¿Cuál es tu nombre?", LocalDateTime.now());
            createEncuesta(encuesta);
        }, "Debe lanzar InputValidationException por fecha actual");
    }

    @Test
    public void testCrearBuscarEncuesta() throws InputValidationException, InstanceNotFoundException {
        Encuesta encuesta = getValidEncuesta();
        Encuesta encuestaCreada = null;

        try {
            encuestaCreada = createEncuesta(encuesta);

            assertNotNull(encuestaCreada.getEncuestaId(),
                    "El ID de la encuesta creada no debe ser nulo");
            assertTrue(encuestaCreada.getEncuestaId() > 0,
                    "El ID debe ser mayor que 0");

            assertNotNull(encuestaCreada.getFechaCreacion(),
                    "La fecha de creación debe ser establecida");

            Encuesta encuestaEncontrada = encuestaService.buscarEncuestaPorId(
                    encuestaCreada.getEncuestaId()
            );

            assertNotNull(encuestaEncontrada,
                    "La encuesta encontrada no debe ser nula");
            assertEquals(encuestaCreada.getEncuestaId(),
                    encuestaEncontrada.getEncuestaId(),
                    "Los IDs deben coincidir");
            assertEquals(encuesta.getPregunta(), encuestaEncontrada.getPregunta(),
                    "Las preguntas deben coincidir");

            assertEquals(encuestaCreada.getFechaFin().withNano(0).withSecond(0),
                    encuestaEncontrada.getFechaFin().withNano(0).withSecond(0),
                    "Las fechas de fin deben coincidir");
            assertEquals(encuestaCreada.getFechaCreacion().withNano(0).withSecond(0),
                    encuestaEncontrada.getFechaCreacion().withNano(0).withSecond(0),
                    "Las fechas de creación deben coincidir");



        } finally {

             if (encuestaCreada != null) {
                 encuestaService.eliminarEncuesta(encuestaCreada.getEncuestaId());
             }
        }
    }

    // Buscar encuesta por palabra clave
    @Test
    public void testBuscarEncuestasPorPalabraClave() throws InputValidationException, InstanceNotFoundException {
        Encuesta encuesta1 = getValidEncuesta("¿Cuál es tu color favorito?");
        Encuesta encuesta2 = getValidEncuesta("¿Cuál es tu comida favorita?");
        Encuesta encuesta3 = getValidEncuesta("¿Cuál es tu deporte favorito?");

        Encuesta creada1 = null;
        Encuesta creada2 = null;
        Encuesta creada3 = null;

        try {
            creada1 = createEncuesta(encuesta1);
            creada2 = createEncuesta(encuesta2);
            creada3 = createEncuesta(encuesta3);

            // Buscar por palabra clave "comida"
            var resultados = encuestaService.buscarEncuestasPorPalabraClave("comida", false);
            assertEquals(1, resultados.size(), "Debe encontrar una encuesta con la palabra 'comida'");
            assertEquals(creada2.getEncuestaId(), resultados.getFirst().getEncuestaId(),
                    "El ID de la encuesta encontrada debe coincidir con la creada2");

            // Buscar por palabra clave "favorito"
            resultados = encuestaService.buscarEncuestasPorPalabraClave("favorito", false);
            assertEquals(2, resultados.size(), "Debe encontrar tres encuestas con la palabra 'favorito'");

            // Buscar por palabra clave que no existe
            resultados = encuestaService.buscarEncuestasPorPalabraClave("inexistente", false);
            assertEquals(0, resultados.size(), "No debe encontrar encuestas con la palabra 'inexistente'");

        } finally {
            // Limpieza
            if (creada1 != null) {
                encuestaService.eliminarEncuesta(creada1.getEncuestaId());
            }
            if (creada2 != null) {
                encuestaService.eliminarEncuesta(creada2.getEncuestaId());
            }
            if (creada3 != null) {
                encuestaService.eliminarEncuesta(creada3.getEncuestaId());
            }
        }
    }
    @Test
    public void testCancelarEncuestaValida() throws Exception {
        Encuesta encuesta = getValidEncuesta();
        Encuesta creada = encuestaService.crearEncuesta(encuesta);

        encuestaService.cancelarEncuesta(creada.getEncuestaId());

        Encuesta cancelada = encuestaService.buscarEncuestaPorId(creada.getEncuestaId());
        assertTrue(cancelada.isCancelada(), "La encuesta debe estar marcada como cancelada");

        assertThrows(EncuestaCanceladaException.class,
                () -> encuestaService.cancelarEncuesta(creada.getEncuestaId()));
    }

    @Test
    public void testCancelarEncuestaFinalizada() throws Exception {
        Encuesta encuesta = new Encuesta("¿Te gusta programar en Java?", LocalDateTime.now().minusDays(1));
        Encuesta creada = encuestaService.crearEncuesta(encuesta);

        assertThrows(es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException.class,
                () -> encuestaService.cancelarEncuesta(creada.getEncuestaId()),
                "Debe lanzar EncuestaFinalizadaException al intentar cancelar una encuesta ya finalizada");
    }

}
