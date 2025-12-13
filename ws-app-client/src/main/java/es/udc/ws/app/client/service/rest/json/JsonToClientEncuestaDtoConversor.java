package es.udc.ws.app.client.service.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientEncuestaDtoConversor {

    /* Encuesta -> JSON ObjectNode (para POST)                       */
    public static ObjectNode toObjectNode(ClientEncuestaDto encuesta)
            throws IOException {

        ObjectNode encuestaObject = JsonNodeFactory.instance.objectNode();

        if (encuesta.getEncuestaId() != null) {
            encuestaObject.put("encuestaId", encuesta.getEncuestaId());
        }

        encuestaObject
                .put("pregunta", encuesta.getPregunta())
                .put("fechaFin",
                        encuesta.getFechaFin() != null
                                ? encuesta.getFechaFin().toString()
                                : null);

        return encuestaObject;
    }

    /* ------------------------------------------------------------ */
    /* JSON -> ClientEncuestaDto                                    */
    /* ------------------------------------------------------------ */
    public static ClientEncuestaDto toClientEncuestaDto(InputStream jsonEncuesta)
            throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            }

            return toClientEncuestaDto(rootNode);

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    /* ------------------------------------------------------------ */
    /* JSON Array -> List<ClientEncuestaDto>                        */
    /* ------------------------------------------------------------ */
    public static List<ClientEncuestaDto> toClientEncuestaDtos(
            InputStream jsonEncuestas) throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuestas);

            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            }

            ArrayNode encuestasArray = (ArrayNode) rootNode;
            List<ClientEncuestaDto> encuestas = new ArrayList<>(encuestasArray.size());

            for (JsonNode encuestaNode : encuestasArray) {
                encuestas.add(toClientEncuestaDto(encuestaNode));
            }

            return encuestas;

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    /* ------------------------------------------------------------ */
    /* JSON Node -> ClientEncuestaDto                               */
    /* ------------------------------------------------------------ */
    private static ClientEncuestaDto toClientEncuestaDto(JsonNode encuestaNode)
            throws ParsingException {

        if (encuestaNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        }

        ObjectNode encuestaObject = (ObjectNode) encuestaNode;

        Long encuestaId =
                encuestaObject.hasNonNull("encuestaId")
                        ? encuestaObject.get("encuestaId").longValue()
                        : null;

        String pregunta = encuestaObject.get("pregunta").textValue().trim();

        LocalDateTime fechaFin =
                encuestaObject.hasNonNull("fechaFin")
                        ? LocalDateTime.parse(encuestaObject.get("fechaFin").textValue())
                        : null;

        boolean cancelada = encuestaObject.get("cancelada").booleanValue();
        int numPositivas = encuestaObject.get("numPositivas").intValue();
        int numNegativas = encuestaObject.get("numNegativas").intValue();

        return new ClientEncuestaDto(
                encuestaId,
                pregunta,
                fechaFin,
                cancelada,
                numPositivas,
                numNegativas
        );
    }
}
