package es.udc.ws.app.client.service.exceptions;

@SuppressWarnings("serial")
public class ClientEncuestaCanceladaException extends Exception {

    private Long encuestaId;

    public ClientEncuestaCanceladaException(Long encuestaId) {
        super("Encuesta con id=\"" + encuestaId + "\" ya está cancelada");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }
}
