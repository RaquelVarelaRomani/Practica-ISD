package es.udc.ws.app.restservice.json;


import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestRespuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToRestRespuestaDtoConversor {

    public static ObjectNode toObjectNode(RestRespuestaDto respuesta) {
        ObjectNode respuestaObject = JsonNodeFactory.instance.objectNode();

        if (respuesta.getRespuestaId() != null) {
            respuestaObject.put("respuestaId", respuesta.getRespuestaId());
        }
        respuestaObject.put("encuestaId", respuesta.getEncuestaId())
                .put("emailUsuario", respuesta.getEmailEmpleado())
                .put("respuestaPositiva", respuesta.isRespuestaPositiva());

        if (respuesta.getFechaRespuesta() != null) {
            respuestaObject.put("fechaRespuesta", respuesta.getFechaRespuesta());
        }

        return respuestaObject;
    }

    public static ArrayNode toArrayNode(List<RestRespuestaDto> respuestas) {
        ArrayNode respuestasNode = JsonNodeFactory.instance.arrayNode();
        for (RestRespuestaDto respuestaDto : respuestas) {
            ObjectNode respuestaObject = toObjectNode(respuestaDto);
            respuestasNode.add(respuestaObject);
        }
        return respuestasNode;
    }

    public static RestRespuestaDto toRestRespuestaDto(InputStream jsonRespuesta) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRespuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode respuestaObject = (ObjectNode) rootNode;

                JsonNode respuestaIdNode = respuestaObject.get("respuestaId");
                Long respuestaId = (respuestaIdNode != null) ? respuestaIdNode.longValue() : null;

                Long encuestaId = respuestaObject.get("encuestaId").longValue();
                String emailUsuario = respuestaObject.get("emailUsuario").textValue().trim();
                boolean respuestaPositiva = respuestaObject.get("respuestaPositiva").booleanValue();

                JsonNode fechaRespuestaNode = respuestaObject.get("fechaRespuesta");
                String fechaRespuesta = (fechaRespuestaNode != null) ? fechaRespuestaNode.textValue() : null;

                return new RestRespuestaDto(respuestaId, encuestaId, emailUsuario,
                        respuestaPositiva, fechaRespuesta);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
