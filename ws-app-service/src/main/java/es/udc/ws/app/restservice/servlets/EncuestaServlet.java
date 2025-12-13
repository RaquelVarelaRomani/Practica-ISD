package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuestaservice.EncuestaServiceFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.restservice.dto.RestEncuestaDto;
import es.udc.ws.app.restservice.dto.EncuestaToRestEncuestaDtoConversor;
import es.udc.ws.app.restservice.json.EncuestasExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestEncuestaDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class EncuestaServlet extends RestHttpServletTemplate {

    /* POST /encuestas  -> crear encuesta                   */
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException {

        ServletUtils.checkEmptyPath(req);

        RestEncuestaDto encuestaDto =
                JsonToRestEncuestaDtoConversor.toRestEncuestaDto(req.getInputStream());

        Encuesta encuesta =
                EncuestaToRestEncuestaDtoConversor.toEncuesta(encuestaDto);

        encuesta = EncuestaServiceFactory.getService().crearEncuesta(encuesta);

        RestEncuestaDto createdDto =
                EncuestaToRestEncuestaDtoConversor.toRestEncuestaDto(encuesta);

        String encuestaURL =
                ServletUtils.normalizePath(req.getRequestURL().toString())
                        + "/" + encuesta.getEncuestaId();

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", encuestaURL);

        ServletUtils.writeServiceResponse(
                resp,
                HttpServletResponse.SC_CREATED,
                JsonToRestEncuestaDtoConversor.toObjectNode(createdDto),
                headers
        );
    }

    /* GET /encuestas | GET /encuestas/{id}                 */
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException {

        if (req.getPathInfo() == null || "/".equals(req.getPathInfo())) {

            String keyword = req.getParameter("keyword");
            boolean soloActivas =
                    Boolean.parseBoolean(req.getParameter("soloActivas"));

            List<Encuesta> encuestas =
                    EncuestaServiceFactory.getService()
                            .buscarEncuestasPorPalabraClave(keyword, soloActivas);

            List<RestEncuestaDto> dtos =
                    EncuestaToRestEncuestaDtoConversor.toRestEncuestaDtos(encuestas);

            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_OK,
                    JsonToRestEncuestaDtoConversor.toArrayNode(dtos),
                    null
            );

        } else {

            Long encuestaId = ServletUtils.getIdFromPath(req, "encuesta");

            Encuesta encuesta =
                    EncuestaServiceFactory.getService()
                            .buscarEncuestaPorId(encuestaId);

            RestEncuestaDto dto =
                    EncuestaToRestEncuestaDtoConversor.toRestEncuestaDto(encuesta);

            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_OK,
                    JsonToRestEncuestaDtoConversor.toObjectNode(dto),
                    null
            );
        }
    }

    /* PUT /encuestas/{id}/cancelar                         */
    @Override
    protected void processPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException {

        String path = req.getPathInfo();
        if (path == null || !path.endsWith("/cancelar")) {
            throw new InputValidationException("Invalid URL path for cancellation. Expected: /ID/cancelar");
        }
        String pathWithoutAction = path.substring(1, path.length() - "/cancelar".length());

        Long encuestaId;

        try{
            encuestaId = Long.parseLong(pathWithoutAction);
        } catch (NumberFormatException e){
            throw new InputValidationException("Invalid encuesta ID in path: " +  pathWithoutAction);
        }

        try {
            EncuestaServiceFactory.getService().cancelarEncuesta(encuestaId);

            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_NO_CONTENT,
                    null,
                    null
            );

        } catch (EncuestaFinalizadaException ex) {
            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_FORBIDDEN,
                    EncuestasExceptionToJsonConversor
                            .toEncuestaFinalizadaException(ex),
                    null
            );
        } catch (EncuestaCanceladaException ex) {
            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_CONFLICT,
                    EncuestasExceptionToJsonConversor
                            .toEncuestaCanceladaException(ex),
                    null
            );
        }
    }
}
