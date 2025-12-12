package es.udc.ws.app.restservice.dto;

public class RestEncuestaDto {

    private Long encuestaId;
    private String pregunta;
    private String fechaFin;
    private boolean cancelada;
    private int numPositivas;
    private int numNegativas;


    public RestEncuestaDto() {}

    public RestEncuestaDto(Long encuestaId, String pregunta,
                          String fechaFin, boolean cancelada,
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
                ", fechaFin='" + fechaFin + '\'' +
                ", cancelada=" + cancelada +
                ", numPositivas=" + numPositivas +
                ", numNegativas=" + numNegativas +
                '}';
    }
}
