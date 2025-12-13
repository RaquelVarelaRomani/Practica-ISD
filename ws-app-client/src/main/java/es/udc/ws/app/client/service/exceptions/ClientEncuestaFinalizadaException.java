package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class ClientEncuestaFinalizadaException extends Exception {

    private Long encuestaId;
    private LocalDateTime fechaFinalizacion;

    public ClientEncuestaFinalizadaException(Long encuestaId, LocalDateTime fechaFinalizacion) {
        super("Encuesta con id=\"" + encuestaId + "\" ha finalizado (fechaFinalizacion= \"" + fechaFinalizacion + "\")");
        this.encuestaId = encuestaId;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }
}
