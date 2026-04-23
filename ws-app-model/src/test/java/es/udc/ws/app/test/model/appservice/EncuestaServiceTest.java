package es.udc.ws.app.test.model.appservice;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.time.LocalDateTime;

import javax.sql.DataSource;

import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void limpiarBaseDeDatos() {
        try (var connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(true);

            // Primero eliminar las respuestas (porque dependen de encuesta)
            connection.createStatement().executeUpdate("DELETE FROM Respuesta");

            // Luego eliminar las encuestas
            connection.createStatement().executeUpdate("DELETE FROM Encuesta");

        } catch (Exception e) {
            throw new RuntimeException("Error limpiando BD para tests", e);
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

        // Caso 5: fecha fin nula
        assertThrows(InputValidationException.class, () -> {
            Encuesta encuesta = new Encuesta("¿Te gusta programar?", null);
            createEncuesta(encuesta);
        }, "Debe lanzar InputValidationException cuando la fecha de fin es nula");
    }


    @Test
    public void testCrearBuscarEncuesta() throws InputValidationException, InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException {
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
            if(encuestaCreada != null) encuestaService.cancelarEncuesta(encuestaCreada.getEncuestaId());
        }
    }

    // Buscar encuesta por palabra clave
    @Test
    public void testBuscarEncuestasPorPalabraClave() throws InputValidationException, InstanceNotFoundException, EncuestaFinalizadaException, EncuestaCanceladaException {
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
            assertEquals(2, resultados.size(), "Debe encontrar dos encuestas con la palabra 'favorito'");

            // Buscar por palabra clave que no existe
            resultados = encuestaService.buscarEncuestasPorPalabraClave("inexistente", false);
            assertEquals(0, resultados.size(), "No debe encontrar encuestas con la palabra 'inexistente'");

        } finally {
            if (creada1 != null) encuestaService.cancelarEncuesta(creada1.getEncuestaId());
            if (creada2 != null) encuestaService.cancelarEncuesta(creada2.getEncuestaId());
            if (creada3 != null) encuestaService.cancelarEncuesta(creada3.getEncuestaId());
        }
    }

    @Test
    public void testFindByIdThrowsInstanceNotFoundException() throws Exception {
        try (var connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            assertThrows(InstanceNotFoundException.class,
                    () -> encuestaDao.findById(connection, -999L),
                    "Debe lanzar InstanceNotFoundException al no encontrar encuesta");
        }
    }

    @Test
    public void testBuscarEncuestaPorIdInexistente() {
        assertThrows(InstanceNotFoundException.class,
                () -> encuestaService.buscarEncuestaPorId(-12345L),
                "Debe lanzar InstanceNotFoundException al buscar una encuesta inexistente");
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
        Encuesta encuestaFinalizada = new Encuesta("¿Encuesta ya finalizada?", LocalDateTime.now().minusDays(1));
        encuestaFinalizada.setFechaCreacion(LocalDateTime.now().minusDays(2)); // 💡 Añadimos fechaCreacion manualmente

        try (var connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);
            Encuesta creada = encuestaDao.create(connection, encuestaFinalizada);
            connection.commit();

            assertThrows(EncuestaFinalizadaException.class,
                    () -> encuestaService.cancelarEncuesta(creada.getEncuestaId()),
                    "Debe lanzar EncuestaFinalizadaException al intentar cancelar una encuesta finalizada");
        }
    }


    @Test
    public void testCancelarEncuestaInexistenteDebeLanzarExcepcion() {
        Long idInexistente = -999L;

        assertThrows(InstanceNotFoundException.class,
                () -> encuestaService.cancelarEncuesta(idInexistente),
                "Debe lanzar InstanceNotFoundException al intentar cancelar una encuesta inexistente");
    }


    @Test
    public void testEncuestaSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime fin = now.plusDays(5);

        Encuesta e = new Encuesta("Pregunta inicial", fin);
        e.setEncuestaId(10L);
        e.setPregunta("¿Te gusta Java?");
        e.setFechaCreacion(now);
        e.setFechaFin(fin.plusDays(1));
        e.setCancelada(true);

        assertEquals(10L, e.getEncuestaId());
        assertEquals("¿Te gusta Java?", e.getPregunta());
        assertEquals(now, e.getFechaCreacion());
        assertEquals(fin.plusDays(1), e.getFechaFin());
        assertTrue(e.isCancelada());
    }

    @Test
    public void testEncuestaNumPositivasNegativas() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Encuesta e = new Encuesta(1L, "P", now, now.plusDays(1), false, 2, 3);

        assertEquals(2, e.getNumPositivas());
        assertEquals(3, e.getNumNegativas());
    }

    @Test
    public void testEncuestaToStringContainsFields() {
        Encuesta e = new Encuesta(5L, "¿Te gusta programar?", LocalDateTime.now(), LocalDateTime.now().plusDays(1), false);
        String text = e.toString();
        assertTrue(text.contains("encuestaId=5"));
        assertTrue(text.contains("pregunta='¿Te gusta programar?'"));
    }

    @Test
    public void testEncuestaEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime fin = now.plusDays(3);

        Encuesta e1 = new Encuesta(1L, "P1", now, fin, false);
        Encuesta e2 = new Encuesta(1L, "P1", now, fin, false);
        Encuesta e3 = new Encuesta(2L, "P2", now, fin, true);

        assertEquals(e1, e2, "Dos encuestas con mismos valores deben ser iguales");
        assertEquals(e1.hashCode(), e2.hashCode(), "hashCode debe coincidir para objetos iguales");
        assertNotEquals(e1, e3, "Encuestas diferentes no deben ser iguales");
        assertNotEquals(e1, null);
        assertNotEquals(e1, "string");
    }

    @Test
    public void testRemoveThrowsInstanceNotFoundException() throws Exception {
        try (var connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            assertThrows(InstanceNotFoundException.class,
                    () -> encuestaDao.remove(connection, -999L),
                    "Debe lanzar InstanceNotFoundException al eliminar una encuesta inexistente");
        }
    }

    @Test
    public void testUpdateThrowsInstanceNotFoundException() throws Exception {
        try (var connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            Encuesta encuesta = new Encuesta(-999L, "No existe", LocalDateTime.now(), LocalDateTime.now().plusDays(1), false);
            assertThrows(InstanceNotFoundException.class,
                    () -> encuestaDao.update(connection, encuesta),
                    "Debe lanzar InstanceNotFoundException al actualizar una encuesta inexistente");
        }
    }

    // -------------------------------------------------------------------------
    // Tests Funcionalidad B (Extensión): borrarEncuesta
    // -------------------------------------------------------------------------

    @Test
    public void testBorrarEncuestaFinalizada() throws Exception {
        // Insertar directamente en BD una encuesta con fecha pasada
        Encuesta encuestaFinalizada = new Encuesta("Encuesta ya finalizada", LocalDateTime.now().minusDays(1));
        encuestaFinalizada.setFechaCreacion(LocalDateTime.now().minusDays(2));

        Encuesta creada;
        try (var connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);
            creada = encuestaDao.create(connection, encuestaFinalizada);
            connection.commit();
        }

        // No debe lanzar excepción
        encuestaService.borrarEncuesta(creada.getEncuestaId());

        // Verificar que ya no existe
        final Long id = creada.getEncuestaId();
        assertThrows(InstanceNotFoundException.class,
                () -> encuestaService.buscarEncuestaPorId(id),
                "La encuesta borrada no debe encontrarse");
    }

    @Test
    public void testBorrarEncuestaCancelada() throws Exception {
        Encuesta creada = encuestaService.crearEncuesta(getValidEncuesta());
        encuestaService.cancelarEncuesta(creada.getEncuestaId());

        // No debe lanzar excepción
        encuestaService.borrarEncuesta(creada.getEncuestaId());

        // Verificar que ya no existe
        final Long id = creada.getEncuestaId();
        assertThrows(InstanceNotFoundException.class,
                () -> encuestaService.buscarEncuestaPorId(id),
                "La encuesta cancelada y borrada no debe encontrarse");
    }

    @Test
    public void testBorrarEncuestaActivaLanzaExcepcion() throws Exception {
        Encuesta creada = encuestaService.crearEncuesta(getValidEncuesta());

        try {
            assertThrows(
                    es.udc.ws.app.model.encuestaservice.exceptions.EncuestaActivaException.class,
                    () -> encuestaService.borrarEncuesta(creada.getEncuestaId()),
                    "Debe lanzar EncuestaActivaException al borrar una encuesta activa"
            );
        } finally {
            // Limpiar: cancelar para que el @BeforeEach no tenga problemas de FK
            encuestaService.cancelarEncuesta(creada.getEncuestaId());
        }
    }

    @Test
    public void testBorrarEncuestaInexistenteLanzaExcepcion() {
        assertThrows(InstanceNotFoundException.class,
                () -> encuestaService.borrarEncuesta(-999L),
                "Debe lanzar InstanceNotFoundException al borrar una encuesta inexistente");
    }

}
