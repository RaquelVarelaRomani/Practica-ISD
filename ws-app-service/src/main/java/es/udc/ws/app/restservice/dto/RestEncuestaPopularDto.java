package es.udc.ws.app.restservice.dto;

public class RestEncuestaPopularDto {

    private Long encuestaId;
    private String fechaFin;          // ISO-8601: "yyyy-MM-dd'T'HH:mm:ss"
    private boolean cancelada;
    private double porcentajeAfirmativas;  // redondeado a 2 decimales

    public RestEncuestaPopularDto() {}

    public RestEncuestaPopularDto(Long encuestaId, String fechaFin,
                                   boolean cancelada, double porcentajeAfirmativas) {
        this.encuestaId = encuestaId;
        this.fechaFin = fechaFin;
        this.cancelada = cancelada;
        this.porcentajeAfirmativas = porcentajeAfirmativas;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
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
        return "RestEncuestaPopularDto{" +
                "encuestaId=" + encuestaId +
                ", fechaFin='" + fechaFin + '\'' +
                ", cancelada=" + cancelada +
                ", porcentajeAfirmativas=" + porcentajeAfirmativas +
                '}';
    }
}
