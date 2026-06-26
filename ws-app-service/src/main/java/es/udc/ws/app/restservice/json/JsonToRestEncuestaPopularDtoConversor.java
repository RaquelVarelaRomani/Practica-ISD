package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestEncuestaPopularDto;

import java.util.List;

public class JsonToRestEncuestaPopularDtoConversor {

    /* DTO -> JSON ObjectNode */
    public static ObjectNode toObjectNode(RestEncuestaPopularDto dto) {

        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("encuestaId", dto.getEncuestaId())
            .put("fechaFin", dto.getFechaFin())
            .put("cancelada", dto.isCancelada())
            .put("porcentajeAfirmativas", dto.getPorcentajeAfirmativas());

        return node;
    }

    /* List<DTO> -> JSON ArrayNode */
    public static ArrayNode toArrayNode(List<RestEncuestaPopularDto> dtos) {

        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        for (RestEncuestaPopularDto dto : dtos) {
            arrayNode.add(toObjectNode(dto));
        }

        return arrayNode;
    }
}
