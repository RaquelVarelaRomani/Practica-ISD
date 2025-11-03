package es.udc.ws.app.model.respuestaservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class RespuestaServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "RespuestaServiceFactory.className";
    private static RespuestaService service = null;

    private RespuestaServiceFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static RespuestaService getInstance() {
        try {
            String className = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(className);
            return (RespuestaService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error creando instancia de RespuestaService", e);
        }
    }

    public synchronized static RespuestaService getService() {
        if (service == null) {
            service = getInstance();
        }
        return service;
    }
}
