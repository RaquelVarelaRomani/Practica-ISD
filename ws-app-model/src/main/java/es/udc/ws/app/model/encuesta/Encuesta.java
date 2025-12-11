package es.udc.ws.app.model.encuesta;

import java.time.LocalDateTime;

public class Encuesta {

    private Long encuestaId;
    private String pregunta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFin;
    private boolean cancelada;
    private int numPositivas;
    private int numNegativas;

    //Constructor para crear nuevas encuestas sin ID ni fecha de creación
    public Encuesta(String pregunta, LocalDateTime fechaFin) {
        this.pregunta = pregunta;
        this.fechaFin = fechaFin;
        this.cancelada = false;
    }

    //Constructor intermedio con ID
    public Encuesta(Long encuestaId, String pregunta, LocalDateTime fechaFin, boolean cancelada) {
        this(pregunta, fechaFin);
        this.encuestaId = encuestaId;
        this.cancelada = cancelada;
    }

    //Constructor completo
    public Encuesta(Long encuestaId, String pregunta, LocalDateTime fechaCreacion, LocalDateTime fechaFin, boolean cancelada) {
        this(encuestaId, pregunta, fechaFin, cancelada);
        this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion.withNano(0) : null;
    }

    // Constructor extendido con contadores
    public Encuesta(Long encuestaId, String pregunta, LocalDateTime fechaCreacion,
                    LocalDateTime fechaFin, boolean cancelada,
                    int numPositivas, int numNegativas) {
        this(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada);
        this.numPositivas = numPositivas;
        this.numNegativas = numNegativas;
    }

    //Getters y setters
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion.withNano(0) : null;
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

    public int getNumPositivas(){
        return  numPositivas;
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
        return "Encuesta{" +
                "encuestaId=" + encuestaId +
                ", pregunta='" + pregunta + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaFin=" + fechaFin +
                ", cancelada=" + cancelada +
                ", numPositivas=" + numPositivas +
                ", numNegativas=" + numNegativas +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((encuestaId == null) ? 0 : encuestaId.hashCode());
        result = prime * result + ((pregunta == null) ? 0 : pregunta.hashCode());
        result = prime * result + ((fechaCreacion == null) ? 0 : fechaCreacion.hashCode());
        result = prime * result + ((fechaFin == null) ? 0 : fechaFin.hashCode());
        result = prime * result + (cancelada ? 1231 : 1237);
        result = prime * result + numPositivas;
        result = prime * result + numNegativas;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Encuesta other = (Encuesta) obj;
        if (encuestaId == null) {
            if (other.encuestaId != null)
                return false;
        } else if (!encuestaId.equals(other.encuestaId))
            return false;
        if (pregunta == null) {
            if (other.pregunta != null)
                return false;
        } else if (!pregunta.equals(other.pregunta))
            return false;
        if (fechaCreacion == null) {
            if (other.fechaCreacion != null)
                return false;
        } else if (!fechaCreacion.equals(other.fechaCreacion))
            return false;
        if (fechaFin == null) {
            if (other.fechaFin != null)
                return false;
        } else if (!fechaFin.equals(other.fechaFin))
            return false;
        if (cancelada != other.cancelada)
            return false;
        if (numPositivas != other.numPositivas)
            return false;
        if (numNegativas != other.numNegativas)
            return false;
        return true;
    }


}
