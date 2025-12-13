package es.udc.ws.app.client.service.exceptions;

@SuppressWarnings("serial")
public class ClientEncuestaCanceladaException extends Exception {

    private Long encuestaId;

    public ClientEncuestaCanceladaException(Long encuestaId) {
        super("Encuesta with id=\"" + encuestaId + "\" is canceled");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }
}
