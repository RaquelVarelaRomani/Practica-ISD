package es.udc.ws.app.client.service.dto;


import java.time.LocalDateTime;

public class ClientEncuestaDto {

    private Long encuestaId;
    private String pregunta;
    private LocalDateTime fechaFin;
    private boolean cancelada;
    private int numPositivas;
    private int numNegativas;

    public ClientEncuestaDto(Long encuestaId, String pregunta, LocalDateTime fechaFin, boolean cancelada,
                             int numPositivas, int numNegativas) {
        this.encuestaId = encuestaId;
        this.pregunta = pregunta;
        this.fechaFin = fechaFin;
        this.cancelada = cancelada;
        this.numPositivas = numPositivas;
        this.numNegativas = numNegativas;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public int getNumPositivas() {
        return numPositivas;
    }

    public void setNumPositivas(int numPositivas) {
        this.numPositivas = numPositivas;
    }

    public int getNumNegativas() {
        return numNegativas;
    }

    public void setNumNegativas(int numNegativas) {
        this.numNegativas = numNegativas;
    }

    @Override
    public String toString() {
        return "ClientEncuestaDto [encuestaId=" + encuestaId +
                ", pregunta='" + pregunta + '\'' +
                ", fechaFin=" + fechaFin +
                ", cancelada=" + cancelada +
                ", numPositivas=" + numPositivas +
                ", numNegativas=" + numNegativas + "]";
    }
}