package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientEncuestaPopularDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientEncuestaPopularDtoConversor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /* JSON Array -> List<ClientEncuestaPopularDto> */
    public static List<ClientEncuestaPopularDto> toClientEncuestaPopularDtos(
            InputStream jsonEncuestas) throws ParsingException {

        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuestas);

            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            }

            ArrayNode encuestasArray = (ArrayNode) rootNode;
            List<ClientEncuestaPopularDto> result = new ArrayList<>(encuestasArray.size());

            for (JsonNode node : encuestasArray) {
                result.add(toClientEncuestaPopularDto(node));
            }

            return result;

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    /* JSON Node -> ClientEncuestaPopularDto */
    private static ClientEncuestaPopularDto toClientEncuestaPopularDto(JsonNode node)
            throws ParsingException {

        if (node.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        }

        ObjectNode obj = (ObjectNode) node;

        Long encuestaId = obj.get("encuestaId").longValue();

        // Calcular diasHastaFinalizacion a partir de fechaFin
        String fechaFinStr = obj.get("fechaFin").textValue();
        LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr, FORMATTER);
        long diasHastaFinalizacion = ChronoUnit.DAYS.between(LocalDate.now(), fechaFin.toLocalDate());

        boolean cancelada = obj.get("cancelada").booleanValue();
        double porcentajeAfirmativas = obj.get("porcentajeAfirmativas").doubleValue();

        return new ClientEncuestaPopularDto(encuestaId, diasHastaFinalizacion,
                cancelada, porcentajeAfirmativas);
    }
}
