package es.udc.ws.app.model.encuesta;

import java.sql.*;
import java.time.LocalDateTime;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class Jdbc3CcSqlEncuestaDao implements SqlEncuestaDao {

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
                    encuesta.isCancelada());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Encuesta findById(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String queryString = "SELECT pregunta, fechaCreacion, fechaFin, cancelada "
                + "FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, encuestaId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

            String pregunta = resultSet.getString(1);
            Timestamp fechaCreacionTimestamp = resultSet.getTimestamp(2);
            Timestamp fechaFinTimestamp = resultSet.getTimestamp(3);
            boolean cancelada = resultSet.getBoolean(4);

            LocalDateTime fechaCreacion = fechaCreacionTimestamp.toLocalDateTime();
            LocalDateTime fechaFin = fechaFinTimestamp.toLocalDateTime();

            return new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String queryString = "DELETE FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, encuestaId);

            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*@Override
    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean soloActivas) {

        String queryString = "SELECT encuestaId, pregunta, fechaCreacion, fechaFin, cancelada "
                + "FROM Encuesta "
                + "WHERE LOWER(pregunta) LIKE LOWER(?)";

        if (soloActivas) {
            queryString += " AND fechaFin > NOW()";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setString(1, "%" + keyword + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Encuesta> encuestas = new ArrayList<>();

            while (resultSet.next()) {
                Long encuestaId = resultSet.getLong(1);
                String pregunta = resultSet.getString(2);
                LocalDateTime fechaCreacion = resultSet.getTimestamp(3).toLocalDateTime();
                LocalDateTime fechaFin = resultSet.getTimestamp(4).toLocalDateTime();
                boolean cancelada = resultSet.getBoolean(5);

                encuestas.add(new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada));
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}
