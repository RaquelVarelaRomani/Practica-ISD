package es.udc.ws.app.model.respuesta;

import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;

public class Jdbc3CcSqlRespuestaDao implements SqlRespuestaDao {

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

    @Override
    public void update(Connection connection, Respuesta respuesta) throws InstanceNotFoundException {

        String queryString = "UPDATE Respuesta" + " SET respuestaPositiva = ?, fechaRespuesta = ?"
                + " WHERE encuestaId = ? AND emailUsuario = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setBoolean(i++, respuesta.isRespuestaPositiva());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(respuesta.getFechaRespuesta()));
            preparedStatement.setLong(i++, respuesta.getEncuestaId());
            preparedStatement.setString(i++, respuesta.getEmailUsuario());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(
                        respuesta.getEncuestaId() + " - " + respuesta.getEmailUsuario(),
                        Respuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Respuesta findByEncuestaAndEmail(Connection connection, Long encuestaId, String emailUsuario) throws InstanceNotFoundException {

        String queryString = "SELECT respuestaId, encuestaId, emailUsuario, respuestaPositiva, fechaRespuesta"
                + " FROM Respuesta WHERE encuestaId = ? AND emailUsuario = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setLong(i++, encuestaId);
            preparedStatement.setString(i++, emailUsuario);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(
                        encuestaId + " - " + emailUsuario, Respuesta.class.getName());
            }

            return new Respuesta(
                    resultSet.getLong("respuestaId"),
                    resultSet.getLong("encuestaId"),
                    resultSet.getString("emailUsuario"),
                    resultSet.getBoolean("respuestaPositiva"),
                    resultSet.getTimestamp("fechaRespuesta").toLocalDateTime());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
