package es.udc.ws.app.restservice.json;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestRespuestaDto;

public class JsonToRestRespuestaDtoConversor {

    public static ObjectNode toObjectNode(RestRespuestaDto respuesta) {

        ObjectNode respuestaNode = JsonNodeFactory.instance.objectNode();

        if (respuesta.getRespuestaId() != null) {
            respuestaNode.put("respuestaId", respuesta.getRespuestaId());
        }

        respuestaNode
                .put("encuestaId", respuesta.getEncuestaId())
                .put("emailUsuario", respuesta.getEmailUsuario())
                .put("respuestaPositiva", respuesta.isRespuestaPositiva())
                .put("fechaRespuesta", respuesta.getFechaRespuesta());

        return respuestaNode;
    }

    public static ArrayNode toArrayNode(List<RestRespuestaDto> respuestas) {

        ArrayNode respuestasNode = JsonNodeFactory.instance.arrayNode();

        for (RestRespuestaDto dto : respuestas) {
            respuestasNode.add(toObjectNode(dto));
        }

        return respuestasNode;
    }
}
