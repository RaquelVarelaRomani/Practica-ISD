package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEncuestaService;
import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientEncuestaPopularDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaDto;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaActivaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaCanceladaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaFinalizadaException;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientEncuestaDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientEncuestaPopularDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientRespuestaDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

public class RestClientEncuestaService implements ClientEncuestaService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEncuestaService.endpointAddress";
    private String endpointAddress;

    @Override
    public Long crearEncuesta(ClientEncuestaDto encuesta) throws InputValidationException {
        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "encuestas").
                    bodyStream(toInputStream(encuesta), ContentType.create("application/json")).
                    execute().returnResponse();
            validateStatusCode(HttpStatus.SC_CREATED, response);
            return JsonToClientEncuestaDtoConversor.toClientEncuestaDto(response.getEntity().getContent()).getEncuestaId();
        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEncuestaDto> buscarEncuestas(String keyword, boolean soloActivas) {

        try {
            String url = getEndpointAddress() + "encuestas?keyword="
                    + URLEncoder.encode(keyword == null ? "" : keyword, "UTF-8")
                    + "&soloActivas=" + soloActivas;

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(url).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEncuestaDtoConversor.toClientEncuestaDtos(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEncuestaDto buscarEncuestaPorId(Long encuestaId) throws InstanceNotFoundException {

        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "encuestas/" + encuestaId).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEncuestaDtoConversor.toClientEncuestaDto(response.getEntity().getContent());

        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelarEncuesta(Long encuestaId) throws InstanceNotFoundException, ClientEncuestaFinalizadaException, ClientEncuestaCanceladaException {

        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.put(getEndpointAddress() + "encuestas/" + encuestaId + "/cancelar").execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InstanceNotFoundException | ClientEncuestaFinalizadaException | ClientEncuestaCanceladaException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long responderEncuesta(Long encuestaId, String emailUsuario, boolean respuestaPositiva) throws InstanceNotFoundException, ClientEncuestaFinalizadaException, ClientEncuestaCanceladaException {

        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "respuestas").bodyForm(Form.form()
                            .add("encuestaId", encuestaId.toString())
                            .add("emailUsuario", emailUsuario)
                            .add("respuestaPositiva", Boolean.toString(respuestaPositiva))
                            .build()).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientRespuestaDtoConversor.toClientRespuestaDto(response.getEntity().getContent()).getRespuestaId();

        } catch (InstanceNotFoundException | ClientEncuestaFinalizadaException | ClientEncuestaCanceladaException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientRespuestaDto> obtenerRespuestas(Long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException {

        try {
            String url = getEndpointAddress() + "respuestas/" + encuestaId + "?soloPositivas=" + soloPositivas;

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(url).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientRespuestaDtoConversor.toClientRespuestaDtos(response.getEntity().getContent());

        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Funcionalidad 8 (Extensión): Borrar encuesta finalizada o cancelada
    @Override
    public void borrarEncuesta(Long encuestaId)
            throws InstanceNotFoundException, ClientEncuestaActivaException {

        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request
                    .delete(getEndpointAddress() + "encuestas/" + encuestaId)
                    .execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InstanceNotFoundException | ClientEncuestaActivaException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Funcionalidad 7 (Extensión): Encuestas populares con fecha de finalización futura
    @Override
    public List<ClientEncuestaPopularDto> buscarEncuestasPopulares(int minRespuestas, LocalDate fechaInicio)
            throws InputValidationException {

        try {
            String url = getEndpointAddress() + "encuestas-populares?minRespuestas=" + minRespuestas
                    + "&fechaInicio=" + URLEncoder.encode(fechaInicio.toString(), "UTF-8");

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(url).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEncuestaPopularDtoConversor.toClientEncuestaPopularDtos(
                    response.getEntity().getContent());

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientEncuestaDto encuesta) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, JsonToClientEncuestaDtoConversor.toObjectNode(encuesta));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(response.getEntity().getContent());
                case HttpStatus.SC_CONFLICT -> throw JsonToClientExceptionConversor.fromConflictErrorCode(response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = " + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

