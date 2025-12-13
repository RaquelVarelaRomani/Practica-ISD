package es.udc.ws.app.client.service.exceptions;

@SuppressWarnings("serial")
public class ClientEncuestaFinalizadaException extends Exception {

    private Long encuestaId;

    public ClientEncuestaFinalizadaException(Long encuestaId) {
        super("Encuesta con id=\"" + encuestaId + "\" ha finalizado");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

}
