package es.udc.ws.app.model.encuesta;

import java.time.LocalDateTime;
import java.util.List;
import es.udc.ws.app.model.respuesta.Respuesta;

public class Encuesta {

    // TODO:
    //      1. Atributos de contadores para respuestas positivas y negativas
    //      2. El atributo List<Respuesta> "No se persiste (ok) perpo no es una aproximación adecuada"


    private Long encuestaId;
    private String pregunta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFin;
    private boolean cancelada;
    private List<Respuesta> respuestas;

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

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }

    @Override
    public String toString() {
        return "Encuesta{" +
                "encuestaId=" + encuestaId +
                ", pregunta='" + pregunta + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaFin=" + fechaFin +
                ", cancelada=" + cancelada +
                ", respuestas=" + (respuestas != null ? respuestas.size() : "null") +
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
        return cancelada == other.cancelada;
    }


}
