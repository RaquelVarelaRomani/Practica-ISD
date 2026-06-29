package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.encuesta.Encuesta;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EncuestaToRestEncuestaPopularDtoConversor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static RestEncuestaPopularDto toRestEncuestaPopularDto(Encuesta encuesta) {

        int totalRespuestas = encuesta.getNumPositivas() + encuesta.getNumNegativas();
        double porcentaje = totalRespuestas == 0
                ? 0.0
                : Math.round((encuesta.getNumPositivas() * 100.0 / totalRespuestas) * 100.0) / 100.0;

        return new RestEncuestaPopularDto(
                encuesta.getEncuestaId(),
                encuesta.getFechaFin() != null
                        ? encuesta.getFechaFin().format(FORMATTER)
                        : null,
                encuesta.isCancelada(),
                porcentaje
        );
    }

    public static List<RestEncuestaPopularDto> toRestEncuestaPopularDtos(List<Encuesta> encuestas) {
        return encuestas.stream()
                .map(EncuestaToRestEncuestaPopularDtoConversor::toRestEncuestaPopularDto)
                .collect(Collectors.toList());
    }
}
