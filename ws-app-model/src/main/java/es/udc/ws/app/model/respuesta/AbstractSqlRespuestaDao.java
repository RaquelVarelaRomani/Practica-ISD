package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.*;


public abstract class AbstractSqlRespuestaDao implements SqlRespuestaDao {

    protected AbstractSqlRespuestaDao() {
    }

    @Override
    public void update(Connection connection, Respuesta respuesta)
            throws InstanceNotFoundException {

        String queryString = "UPDATE Respuesta "
                + "SET respuestaPositiva = ?, fechaRespuesta = ? "
                + "WHERE encuestaId = ? AND emailUsuario = ?";

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
    public Respuesta findByEncuestaAndEmail(Connection connection, Long encuestaId, String emailUsuario)
            throws InstanceNotFoundException {

        String queryString = "SELECT respuestaId, encuestaId, emailUsuario, respuestaPositiva, fechaRespuesta "
                + "FROM Respuesta WHERE encuestaId = ? AND emailUsuario = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);
            preparedStatement.setString(2, emailUsuario);

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
                    resultSet.getTimestamp("fechaRespuesta").toLocalDateTime()
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
