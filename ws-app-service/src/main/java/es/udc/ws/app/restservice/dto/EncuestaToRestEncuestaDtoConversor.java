package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.encuesta.Encuesta;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EncuestaToRestEncuestaDtoConversor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static RestEncuestaDto toRestEncuestaDto(Encuesta encuesta) {
        return new RestEncuestaDto(
                encuesta.getEncuestaId(),
                encuesta.getPregunta(),
                encuesta.getFechaFin() != null
                        ? encuesta.getFechaFin().format(FORMATTER)
                        : null,
                encuesta.isCancelada(),
                encuesta.getNumPositivas(),
                encuesta.getNumNegativas()
        );
    }

    public static Encuesta toEncuesta(RestEncuestaDto dto) {
        return new Encuesta(
                dto.getEncuestaId(),
                dto.getPregunta(),
                LocalDateTime.now(),
                dto.getFechaFin() != null
                        ? LocalDateTime.parse(dto.getFechaFin(), FORMATTER)
                        : null,
                dto.isCancelada(),
                dto.getNumPositivas(),
                dto.getNumNegativas()
        );
    }

    public static List<RestEncuestaDto> toRestEncuestaDtos(List<Encuesta> encuestas) {
        return encuestas.stream()
                .map(EncuestaToRestEncuestaDtoConversor::toRestEncuestaDto)
                .collect(Collectors.toList());
    }
}
