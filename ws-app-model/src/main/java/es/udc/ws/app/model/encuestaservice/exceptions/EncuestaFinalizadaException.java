package es.udc.ws.app.model.encuestaservice.exceptions;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class EncuestaFinalizadaException extends Exception {

    private Long encuestaId;
    private LocalDateTime fechaFin;

    public EncuestaFinalizadaException(Long encuestaId) {
        super("La encuesta con ID=" + encuestaId + " ya ha finalizado.");
        this.encuestaId = encuestaId;
    }

    public EncuestaFinalizadaException(Long encuestaId, LocalDateTime fechaFin) {
        super("La encuesta con ID=" + encuestaId + " finalizó en " + fechaFin + ".");
        this.encuestaId = encuestaId;
        this.fechaFin = fechaFin;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    @Override
    public String toString() {
        return "EncuestaFinalizadaException{" +
                "encuestaId=" + encuestaId +
                ", fechaFin=" + fechaFin +
                '}';
    }
}
