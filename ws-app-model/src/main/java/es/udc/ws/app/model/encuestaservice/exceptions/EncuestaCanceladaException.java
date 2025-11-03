package es.udc.ws.app.model.encuestaservice.exceptions;

@SuppressWarnings("serial")
public class EncuestaCanceladaException extends Exception {

    private Long encuestaId;

    public EncuestaCanceladaException(Long encuestaId) {
        super("La encuesta con ID=" + encuestaId + " está cancelada y no puede modificarse ni responderse.");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    @Override
    public String toString() {
        return "EncuestaCanceladaException{" +
                "encuestaId=" + encuestaId +
                '}';
    }
}
