package es.udc.ws.app.model.encuesta;

import java.sql.*;

public class Jdbc3CcSqlEncuestaDao extends AbstractSqlEncuestaDao {

    @Override
    public Encuesta create(Connection connection, Encuesta encuesta) {

        String queryString = "INSERT INTO Encuesta (pregunta, fechaCreacion, fechaFin, cancelada) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaCreacion()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException("El driver JDBC no devolvió el ID generado.");
            }
            Long encuestaId = resultSet.getLong(1);

            return new Encuesta(
                    encuestaId,
                    encuesta.getPregunta(),
                    encuesta.getFechaCreacion(),
                    encuesta.getFechaFin(),
                    encuesta.isCancelada(),
                    0,
                    0);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
