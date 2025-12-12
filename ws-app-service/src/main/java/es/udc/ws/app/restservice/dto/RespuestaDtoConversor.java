package es.udc.ws.app.restservice.dto;


import es.udc.ws.app.model.respuesta.Respuesta;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RespuestaDtoConversor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static RestRespuestaDto toRestRespuestaDto(Respuesta respuesta) {
        return new RestRespuestaDto(
                respuesta.getRespuestaId(),
                respuesta.getEncuestaId(),
                respuesta.getEmailUsuario(),
                respuesta.isRespuestaPositiva(),
                respuesta.getFechaRespuesta() != null
                        ? respuesta.getFechaRespuesta().format(FORMATTER)
                        : null
        );
    }

    public static Respuesta toRespuesta(RestRespuestaDto dto) {
        return new Respuesta(
                dto.getRespuestaId(),
                dto.getEncuestaId(),
                dto.getEmailEmpleado(),
                dto.isRespuestaPositiva(),
                dto.getFechaRespuesta() != null
                        ? LocalDateTime.parse(dto.getFechaRespuesta(), FORMATTER)
                        : null
        );
    }

    public static List<RestRespuestaDto> toRestRespuestaDtos(List<Respuesta> respuestas) {
        return respuestas.stream()
                .map(RespuestaDtoConversor::toRestRespuestaDto)
                .collect(Collectors.toList());
    }
}