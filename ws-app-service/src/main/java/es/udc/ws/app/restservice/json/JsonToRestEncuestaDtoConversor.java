package es.udc.ws.app.restservice.json;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestEncuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToRestEncuestaDtoConversor {

    /* ------------------------------------------------------------ */
    /* Encuesta -> JSON ObjectNode                                  */
    /* ------------------------------------------------------------ */
    public static ObjectNode toObjectNode(RestEncuestaDto encuesta) {

        ObjectNode encuestaObject = JsonNodeFactory.instance.objectNode();

        encuestaObject
                .put("encuestaId", encuesta.getEncuestaId())
                .put("pregunta", encuesta.getPregunta())
                .put("fechaCreacion", encuesta.getFechaCreacion())
                .put("fechaFin", encuesta.getFechaFin())
                .put("cancelada", encuesta.isCancelada())
                .put("numPositivas", encuesta.getNumPositivas())
                .put("numNegativas", encuesta.getNumNegativas());

        return encuestaObject;
    }

    /* ------------------------------------------------------------ */
    /* List<Encuesta> -> JSON ArrayNode                             */
    /* ------------------------------------------------------------ */
    public static ArrayNode toArrayNode(List<RestEncuestaDto> encuestas) {

        ArrayNode encuestasNode = JsonNodeFactory.instance.arrayNode();

        for (RestEncuestaDto encuestaDto : encuestas) {
            encuestasNode.add(toObjectNode(encuestaDto));
        }

        return encuestasNode;
    }

    /* ------------------------------------------------------------ */
    /* JSON -> RestEncuestaDto                                      */
    /* ------------------------------------------------------------ */
    public static RestEncuestaDto toRestEncuestaDto(InputStream jsonEncuesta)
            throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            }

            ObjectNode encuestaObject = (ObjectNode) rootNode;

            JsonNode encuestaIdNode = encuestaObject.get("encuestaId");
            Long encuestaId =
                    (encuestaIdNode != null) ? encuestaIdNode.longValue() : null;

            JsonNode preguntaNode = encuestaObject.get("pregunta");
            if (preguntaNode == null) {
                throw new ParsingException("Missing field 'pregunta'");
            }
            String pregunta = preguntaNode.textValue().trim();

            String fechaFin =
                    encuestaObject.hasNonNull("fechaFin")
                            ? encuestaObject.get("fechaFin").textValue()
                            : null;

            return new RestEncuestaDto(
                    encuestaId,
                    pregunta,
                    null,
                    fechaFin,
                    false,
                    0,
                    0
            );

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
