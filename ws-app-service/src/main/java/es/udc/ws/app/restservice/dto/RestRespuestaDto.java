package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestRespuestaDto {

    private Long respuestaId;
    private Long encuestaId;
    private String emailUsuario;
    private boolean respuestaPositiva;
    private String fechaRespuesta;

    public RestRespuestaDto() {
    }

    public RestRespuestaDto(Long respuestaId, Long encuestaId, String emailUsuario,
                            boolean respuestaPositiva, String fechaRespuesta) {
        this.respuestaId = respuestaId;
        this.encuestaId = encuestaId;
        this.emailUsuario = emailUsuario;
        this.respuestaPositiva = respuestaPositiva;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(Long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public boolean isRespuestaPositiva() {
        return respuestaPositiva;
    }

    public void setRespuestaPositiva(boolean respuestaPositiva) {
        this.respuestaPositiva = respuestaPositiva;
    }

    public String getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(String fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    @Override
    public String toString() {
        return "RestRespuestaDto{" +
                "respuestaId=" + respuestaId +
                ", encuestaId=" + encuestaId +
                ", emailUsuario='" + emailUsuario + '\'' +
                ", respuestaPositiva=" + respuestaPositiva +
                '}';
    }
}
