package es.udc.ws.app.restservice.dto;

public class RestEncuestaDto {

    private Long encuestaId;
    private String pregunta;
    private String fechaCreacion;
    private String fechaFin;
    private boolean cancelada;
    private int numPositivas;
    private int numNegativas;


    public RestEncuestaDto() {}

    public RestEncuestaDto(Long encuestaId, String pregunta, String fechaCreacion,
                          String fechaFin, boolean cancelada,
                          int numPositivas, int numNegativas) {
        this.encuestaId = encuestaId;
        this.pregunta = pregunta;
        this.fechaCreacion = fechaCreacion;
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

    public String getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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
        return "RestEncuestaDto{" +
                "encuestaId=" + encuestaId +
                ", pregunta='" + pregunta + '\'' +
                ", fechaCreacion='" + fechaCreacion + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", cancelada=" + cancelada +
                ", numPositivas=" + numPositivas +
                ", numNegativas=" + numNegativas +
                '}';
    }
}
