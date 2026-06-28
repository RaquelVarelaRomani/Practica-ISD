package es.udc.ws.app.client.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ClientEncuestaPopularDto {

    private Long encuestaId;
    private long diasHastaFinalizacion;   // calculado a partir de fechaFin
    private boolean cancelada;
    private double porcentajeAfirmativas;

    public ClientEncuestaPopularDto(Long encuestaId, long diasHastaFinalizacion,
                                     boolean cancelada, double porcentajeAfirmativas) {
        this.encuestaId = encuestaId;
        this.diasHastaFinalizacion = diasHastaFinalizacion;
        this.cancelada = cancelada;
        this.porcentajeAfirmativas = porcentajeAfirmativas;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public long getDiasHastaFinalizacion() {
        return diasHastaFinalizacion;
    }

    public void setDiasHastaFinalizacion(long diasHastaFinalizacion) {
        this.diasHastaFinalizacion = diasHastaFinalizacion;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public double getPorcentajeAfirmativas() {
        return porcentajeAfirmativas;
    }

    public void setPorcentajeAfirmativas(double porcentajeAfirmativas) {
        this.porcentajeAfirmativas = porcentajeAfirmativas;
    }

    @Override
    public String toString() {
        return "ClientEncuestaPopularDto [encuestaId=" + encuestaId +
                ", diasHastaFinalizacion=" + diasHastaFinalizacion +
                ", cancelada=" + cancelada +
                ", porcentajeAfirmativas=" + String.format("%.2f%%", porcentajeAfirmativas) + "]";
    }
}
