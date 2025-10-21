-- ----------------------------------------------------------------------------
-- Model
-- -----------------------------------------------------------------------------

DROP TABLE IF EXISTS Respuesta;
DROP TABLE IF EXISTS Encuesta;

-- --------------------------------- Encuesta ------------------------------------
CREATE TABLE Encuesta (
    encuestaId    BIGINT                           NOT NULL AUTO_INCREMENT,
    pregunta      VARCHAR(255) COLLATE utf8mb4_bin NOT NULL,
    fechaCreacion DATETIME                         NOT NULL,
    fechaFin      DATETIME                         NOT NULL,
    cancelada     BOOLEAN                          NOT NULL DEFAULT FALSE,
    PRIMARY KEY (encuestaId)
) ENGINE=InnoDB;

-- --------------------------------- Respuesta ------------------------------------
CREATE TABLE Respuesta (
    respuestaId       BIGINT                           NOT NULL AUTO_INCREMENT,
    encuestaId        BIGINT                           NOT NULL,
    emailUsuario      VARCHAR(255) COLLATE utf8mb4_bin NOT NULL,
    respuestaPositiva BOOLEAN                          NOT NULL,
    fechaRespuesta    DATETIME                         NOT NULL,
    PRIMARY KEY (respuestaId),

    FOREIGN KEY (encuestaId) REFERENCES Encuesta(encuestaId) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;