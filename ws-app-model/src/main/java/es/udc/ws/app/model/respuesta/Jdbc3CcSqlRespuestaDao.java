package es.udc.ws.app.model.respuesta;
import java.sql.*;
import java.time.LocalDateTime;

public class Jdbc3CcSqlRespuestaDao extends AbstractSqlRespuestaDao {

    @Override
    public Respuesta create(Connection connection, Respuesta respuesta) {
        String queryString = "INSERT INTO Respuesta (encuestaId, emailUsuario, respuestaPositiva, fechaRespuesta) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setLong(i++, respuesta.getEncuestaId());
            preparedStatement.setString(i++, respuesta.getEmailUsuario());
            preparedStatement.setBoolean(i++, respuesta.isRespuestaPositiva());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(
                    respuesta.getFechaRespuesta() != null
                            ? respuesta.getFechaRespuesta()
                            : LocalDateTime.now()));

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException("El driver JDBC no devolvió el ID generado.");
            }

            Long respuestaId = resultSet.getLong(1);

            return new Respuesta(
                    respuestaId,
                    respuesta.getEncuestaId(),
                    respuesta.getEmailUsuario(),
                    respuesta.isRespuestaPositiva(),
                    respuesta.getFechaRespuesta() != null
                            ? respuesta.getFechaRespuesta()
                            : LocalDateTime.now());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
