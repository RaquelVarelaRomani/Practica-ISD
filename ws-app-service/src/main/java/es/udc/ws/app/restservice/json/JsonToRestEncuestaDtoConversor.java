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

    public static ObjectNode toObjectNode(RestEncuestaDto encuesta) {
        ObjectNode encuestaObject = JsonNodeFactory.instance.objectNode();

        if (encuesta.getEncuestaId() != null) {
            encuestaObject.put("encuestaId", encuesta.getEncuestaId());
        }
        encuestaObject.put("pregunta", encuesta.getPregunta());

        if (encuesta.getFechaCreacion() != null) {
            encuestaObject.put("fechaCreacion", encuesta.getFechaCreacion());
        }
        if (encuesta.getFechaFin() != null) {
            encuestaObject.put("fechaFin", encuesta.getFechaFin());
        }

        encuestaObject.put("cancelada", encuesta.isCancelada())
                .put("numPositivas", encuesta.getNumPositivas())
                .put("numNegativas", encuesta.getNumNegativas());

        return encuestaObject;
    }

    public static ArrayNode toArrayNode(List<RestEncuestaDto> encuestas) {
        ArrayNode encuestasNode = JsonNodeFactory.instance.arrayNode();
        for (RestEncuestaDto encuestaDto : encuestas) {
            ObjectNode encuestaObject = toObjectNode(encuestaDto);
            encuestasNode.add(encuestaObject);
        }
        return encuestasNode;
    }

    public static RestEncuestaDto toRestEncuestaDto(InputStream jsonEncuesta) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode encuestaObject = (ObjectNode) rootNode;

                JsonNode encuestaIdNode = encuestaObject.get("encuestaId");
                Long encuestaId = (encuestaIdNode != null) ? encuestaIdNode.longValue() : null;

                String pregunta = encuestaObject.get("pregunta").textValue().trim();

                JsonNode fechaCreacionNode = encuestaObject.get("fechaCreacion");
                String fechaCreacion = (fechaCreacionNode != null) ? fechaCreacionNode.textValue() : null;

                JsonNode fechaFinNode = encuestaObject.get("fechaFin");
                String fechaFin = (fechaFinNode != null) ? fechaFinNode.textValue() : null;

                boolean cancelada = encuestaObject.get("cancelada").booleanValue();
                int numPositivas = encuestaObject.get("numPositivas").intValue();
                int numNegativas = encuestaObject.get("numNegativas").intValue();

                return new RestEncuestaDto(encuestaId, pregunta, fechaCreacion,
                        fechaFin, cancelada, numPositivas, numNegativas);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}