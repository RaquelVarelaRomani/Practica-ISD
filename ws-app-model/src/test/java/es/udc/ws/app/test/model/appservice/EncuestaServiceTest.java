package es.udc.ws.app.test.model.encuestaservice;

import static org.junit.jupiter.api.Assertions.*;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuestaservice.EncuestaService;
//import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

public class EncuestaServiceTest {

    private final long NON_EXISTENT_ENCUESTA_ID = -1;

    private static EncuestaService encuestaService = null;

    @BeforeAll
    public static void init() {

        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        //encuestaService = EncuestaServiceFactory.getService();
        encuestaService = new es.udc.ws.app.model.encuestaservice.EncuestaServiceImpl();
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

    /*private void removeEncuesta(Long encuestaId) {

        try {
            //Este método solo funcionará cuando se implemente la funcionalidad de borrado
            System.out.println("Simulando borrado de encuesta con id: " + encuestaId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    } */

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

    
}
