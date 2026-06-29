package es.udc.ws.app.model.encuestaservice.exceptions;

@SuppressWarnings("serial")
public class EncuestaActivaException extends Exception {

    private Long encuestaId;

    public EncuestaActivaException(Long encuestaId) {
        super("La encuesta con ID=" + encuestaId +
                " no se puede eliminar porque sigue activa.");
        this.encuestaId = encuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    @Override
    public String toString() {
        return "EncuestaActivaException{" +
                "encuestaId=" + encuestaId +
                '}';
    }
}
