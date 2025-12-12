package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.model.respuesta.Respuesta;
import es.udc.ws.app.model.respuestaservice.RespuestaServiceFactory;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.encuestaservice.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.restservice.dto.RestRespuestaDto;
import es.udc.ws.app.restservice.dto.RespuestaToRestRespuestaDtoConversor;
import es.udc.ws.app.restservice.json.EncuestasExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestRespuestaDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class RespuestaServlet extends RestHttpServletTemplate {

    /* POST /respuestas                                             */
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException {

        ServletUtils.checkEmptyPath(req);

        Long encuestaId =
                ServletUtils.getMandatoryParameterAsLong(req, "encuestaId");
        String emailUsuario =
                ServletUtils.getMandatoryParameter(req, "emailUsuario");
        boolean respuestaPositiva =
                Boolean.parseBoolean(
                        ServletUtils.getMandatoryParameter(req, "respuestaPositiva"));

        Respuesta respuesta;
        try {
            respuesta =
                    RespuestaServiceFactory.getService()
                            .responderEncuesta(encuestaId, emailUsuario, respuestaPositiva);
        } catch (EncuestaFinalizadaException ex) {
            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_FORBIDDEN,
                    EncuestasExceptionToJsonConversor.toEncuestaFinalizadaException(ex),
                    null
            );
            return;
        } catch (EncuestaCanceladaException ex) {
            ServletUtils.writeServiceResponse(
                    resp,
                    HttpServletResponse.SC_CONFLICT,
                    EncuestasExceptionToJsonConversor.toEncuestaCanceladaException(ex),
                    null
            );
            return;
        }

        RestRespuestaDto dto =
                RespuestaToRestRespuestaDtoConversor.toRestRespuestaDto(respuesta);

        ServletUtils.writeServiceResponse(
                resp,
                HttpServletResponse.SC_CREATED,
                JsonToRestRespuestaDtoConversor.toObjectNode(dto),
                null
        );
    }

    /* GET /respuestas                                              */
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException, InstanceNotFoundException {

        Long encuestaId = ServletUtils.getIdFromPath(req, "encuesta");

        boolean soloPositivas =
                Boolean.parseBoolean(req.getParameter("soloPositivas"));

        List<Respuesta> respuestas =
                RespuestaServiceFactory.getService()
                        .obtenerRespuestas(encuestaId, soloPositivas);

        List<RestRespuestaDto> dtos =
                RespuestaToRestRespuestaDtoConversor.toRestRespuestaDtos(respuestas);

        ServletUtils.writeServiceResponse(
                resp,
                HttpServletResponse.SC_OK,
                JsonToRestRespuestaDtoConversor.toArrayNode(dtos),
                null
        );
    }
}
