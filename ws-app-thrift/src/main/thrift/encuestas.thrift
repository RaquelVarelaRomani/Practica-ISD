namespace java es.udc.ws.app.thrift

struct ThriftEncuestaDto {
    1: i64 encuestaId
    2: string pregunta
    3: string fechaCreacion
    4: string fechaFin
    5: bool cancelada
    6: i32 numPositivas
    7: i32 numNegativas
}

struct ThriftRespuestaDto {
    1: i64 respuestaId
    2: i64 encuestaId
    3: string emailUsuario
    4: bool respuestaPositiva
    5: string fechaRespuesta
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftEncuestaFinalizadaException {
    1: i64 encuestaId
    2: string fechaFin
}

exception ThriftEncuestaCanceladaException {
    1: i64 encuestaId
}

service ThriftEncuestaService {

   // Funcionalidad 1: Crear encuesta
   ThriftEncuestaDto crearEncuesta(1: ThriftEncuestaDto encuestaDto) throws (1: ThriftInputValidationException e)

   // Funcionalidad 2: Buscar encuesta por ID
   ThriftEncuestaDto buscarEncuestaPorId(1: i64 encuestaId) throws (1: ThriftInstanceNotFoundException e)

   // Funcionalidad 3: Buscar encuestas por palabra clave
   list<ThriftEncuestaDto> buscarEncuestasPorPalabraClave(1: string keyword, 2: bool soloActivas)

   // Funcionalidad 4: Responder encuesta
   ThriftRespuestaDto responderEncuesta(1: i64 encuestaId, 2: string emailUsuario, 3: bool respuestaPositiva) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee, 3: ThriftEncuestaFinalizadaException eee, 4: ThriftEncuestaCanceladaException eeee)

   // Funcionalidad 5: Cancelar encuesta
   void cancelarEncuesta(1: i64 encuestaId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftEncuestaFinalizadaException ee, 3: ThriftEncuestaCanceladaException eee)

   // Funcionalidad 6: Buscar respuestas por encuesta
   list<ThriftRespuestaDto> buscarRespuestasPorEncuesta(1: i64 encuestaId, 2: bool soloPositivas) throws (1: ThriftInstanceNotFoundException e)
}
