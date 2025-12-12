package es.udc.ws.app.client.service.dto;


import java.time.LocalDateTime;

public class ClientRespuestaDto {

    private Long respuestaId;
    private Long encuestaId;
    private String emailEmpleado;
    private boolean respuestaPositiva;
    private LocalDateTime fechaRespuesta;

    public ClientRespuestaDto() {
    }

    public ClientRespuestaDto(Long respuestaId, Long encuestaId, String emailUsuario,
                              boolean respuestaPositiva, LocalDateTime fechaRespuesta) {
        this.respuestaId = respuestaId;
        this.encuestaId = encuestaId;
        this.emailEmpleado = emailUsuario;
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

    public String getEmailEmpleado() {
        return emailEmpleado;
    }

    public void setEmailEmpleado(String emailEmpleado) {
        this.emailEmpleado = emailEmpleado;
    }

    public boolean isRespuestaPositiva() {
        return respuestaPositiva;
    }

    public void setRespuestaPositiva(boolean respuestaPositiva) {
        this.respuestaPositiva = respuestaPositiva;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    @Override
    public String toString() {
        return "ClientRespuestaDto [respuestaId=" + respuestaId +
                ", encuestaId=" + encuestaId +
                ", emailUsuario='" + emailEmpleado + '\'' +
                ", respuestaPositiva=" + respuestaPositiva +
                ", fechaRespuesta=" + fechaRespuesta + "]";
    }
}