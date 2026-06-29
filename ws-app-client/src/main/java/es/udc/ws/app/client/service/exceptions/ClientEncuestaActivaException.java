package es.udc.ws.app.client.service.exceptions;

@SuppressWarnings("serial")
public class ClientEncuestaActivaException extends Exception {

    private Long encuestaId;

    public ClientEncuestaActivaException(Long encuestaId) {
        super("Encuesta con id=\"" + encuestaId + "\" no se puede eliminar porque sigue activa");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

}
