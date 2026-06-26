package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import es.udc.ws.app.restservice.dto.EncuestaToRestEncuestaPopularDtoConversor;
import es.udc.ws.app.restservice.dto.RestEncuestaPopularDto;
import es.udc.ws.app.restservice.json.JsonToRestEncuestaPopularDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

/**
 * GET /encuestas-populares?minRespuestas=<n>&fechaInicio=<YYYY-MM-DD>
 *
 * Returns popular surveys (with fechaFin in the future) that have at least
 * minRespuestas answers and whose fechaFin falls within
 * [fechaInicio, fechaInicio + 90 days].
 */
@SuppressWarnings("serial")
public class EncuestaPopularServlet extends RestHttpServletTemplate {

    private static final int WINDOW_DAYS = 90;

    /* GET /encuestas-populares?minRespuestas=<n>&fechaInicio=<YYYY-MM-DD> */
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException {

        // --- minRespuestas (obligatorio, >= 0) ---
        String minRespuestasParam = req.getParameter("minRespuestas");
        if (minRespuestasParam == null || minRespuestasParam.isBlank()) {
            throw new InputValidationException("El parámetro 'minRespuestas' es obligatorio.");
        }

        int minRespuestas;
        try {
            minRespuestas = Integer.parseInt(minRespuestasParam);
        } catch (NumberFormatException e) {
            throw new InputValidationException(
                    "El parámetro 'minRespuestas' debe ser un número entero.");
        }

        // --- fechaInicio (obligatoria, futura) ---
        String fechaInicioParam = req.getParameter("fechaInicio");
        if (fechaInicioParam == null || fechaInicioParam.isBlank()) {
            throw new InputValidationException("El parámetro 'fechaInicio' es obligatorio.");
        }

        LocalDateTime fechaInicio;
        try {
            fechaInicio = LocalDate.parse(fechaInicioParam).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new InputValidationException(
                    "El parámetro 'fechaInicio' debe tener el formato YYYY-MM-DD.");
        }

        if (!fechaInicio.isAfter(LocalDateTime.now())) {
            throw new InputValidationException(
                    "El parámetro 'fechaInicio' debe ser una fecha futura.");
        }

        // fechaFin = fechaInicio + 90 días (fijado en la capa REST)
        LocalDateTime fechaFin = fechaInicio.plusDays(WINDOW_DAYS);

        // --- llamada al servicio ---
        List<Encuesta> encuestas = EncuestaServiceFactory.getService()
                .buscarEncuestasPopulares(minRespuestas, fechaInicio, fechaFin);

        List<RestEncuestaPopularDto> dtos =
                EncuestaToRestEncuestaPopularDtoConversor.toRestEncuestaPopularDtos(encuestas);

        ServletUtils.writeServiceResponse(
                resp,
                HttpServletResponse.SC_OK,
                JsonToRestEncuestaPopularDtoConversor.toArrayNode(dtos),
                null
        );
    }
}
