package es.udc.ws.app.client.service.rest.json;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientRespuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientRespuestaDtoConversor {

    /* JSON -> ClientRespuestaDto                                   */
    public static ClientRespuestaDto toClientRespuestaDto(InputStream jsonRespuesta) throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRespuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            }
            return toClientRespuestaDto(rootNode);

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    /* JSON Array -> List<ClientRespuestaDto>                       */
    public static List<ClientRespuestaDto> toClientRespuestaDtos(
            InputStream jsonRespuestas) throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRespuestas);

            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            }

            ArrayNode respuestasArray = (ArrayNode) rootNode;
            List<ClientRespuestaDto> respuestas =
                    new ArrayList<>(respuestasArray.size());

            for (JsonNode respuestaNode : respuestasArray) {
                respuestas.add(toClientRespuestaDto(respuestaNode));
            }

            return respuestas;

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    /* JSON Node -> ClientRespuestaDto                              */
    private static ClientRespuestaDto toClientRespuestaDto(JsonNode respuestaNode) throws ParsingException {

        if (respuestaNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        }

        ObjectNode respuestaObject = (ObjectNode) respuestaNode;

        Long respuestaId =
                respuestaObject.hasNonNull("respuestaId")
                        ? respuestaObject.get("respuestaId").longValue()
                        : null;

        Long encuestaId = respuestaObject.get("encuestaId").longValue();
        String emailUsuario = respuestaObject.get("emailUsuario").textValue().trim();
        boolean respuestaPositiva = respuestaObject.get("respuestaPositiva").booleanValue();

        LocalDateTime fechaRespuesta =
                LocalDateTime.parse(respuestaObject.get("fechaRespuesta").textValue());

        return new ClientRespuestaDto(
                respuestaId,
                encuestaId,
                emailUsuario,
                respuestaPositiva,
                fechaRespuesta
        );
    }
}
