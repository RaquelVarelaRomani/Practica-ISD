package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Partial implementation of {@code SqlEncuestaDao} that leaves
 * {@code create(Connection, Encuesta)} as abstract.
 */
public abstract class AbstractSqlEncuestaDao implements SqlEncuestaDao {

    protected AbstractSqlEncuestaDao() {
    }

    @Override
    public Encuesta findById(Connection connection, Long encuestaId)
            throws InstanceNotFoundException {

        String queryString =
                "SELECT e.pregunta, e.fechaCreacion, e.fechaFin, e.cancelada, " +
                "COUNT(CASE WHEN r.respuestaPositiva = TRUE THEN 1 END) AS numPositivas, " +
                "COUNT(CASE WHEN r.respuestaPositiva = FALSE THEN 1 END) AS numNegativas " +
                "FROM Encuesta e " +
                "LEFT JOIN Respuesta r ON e.encuestaId = r.encuestaId " +
                "WHERE e.encuestaId = ? " +
                "GROUP BY e.pregunta, e.fechaCreacion, e.fechaFin, e.cancelada";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

            int i = 1;
            String pregunta = resultSet.getString(i++);
            LocalDateTime fechaCreacion = resultSet.getTimestamp(i++).toLocalDateTime();
            LocalDateTime fechaFin = resultSet.getTimestamp(i++).toLocalDateTime();
            boolean cancelada = resultSet.getBoolean(i++);
            int numPositivas = resultSet.getInt(i++);
            int numNegativas = resultSet.getInt(i++);

            return new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada, numPositivas, numNegativas);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean soloActivas) {

        String queryString =
                "SELECT e.encuestaId, e.pregunta, e.fechaCreacion, e.fechaFin, e.cancelada, " +
                        "COUNT(CASE WHEN r.respuestaPositiva = TRUE THEN 1 END) AS numPositivas, " +
                        "COUNT(CASE WHEN r.respuestaPositiva = FALSE THEN 1 END) AS numNegativas " +
                        "FROM Encuesta e " +
                        "LEFT JOIN Respuesta r ON e.encuestaId = r.encuestaId " +
                        "WHERE LOWER(e.pregunta) LIKE LOWER(?) " +
                        "AND (? = FALSE OR e.fechaFin > NOW()) " +
                        "GROUP BY e.encuestaId, e.pregunta, e.fechaCreacion, e.fechaFin, e.cancelada " +
                        "ORDER BY e.fechaCreacion DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            if (keyword == null || keyword.isBlank()) keyword = "";

            preparedStatement.setString(1, "%" + keyword + "%");
            preparedStatement.setBoolean(2, soloActivas);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Encuesta> encuestas = new ArrayList<>();

            while (resultSet.next()) {
                int i = 1;

                Long encuestaId = resultSet.getLong(i++);
                String pregunta = resultSet.getString(i++);
                LocalDateTime fechaCreacion = resultSet.getTimestamp(i++).toLocalDateTime();
                LocalDateTime fechaFin = resultSet.getTimestamp(i++).toLocalDateTime();
                boolean cancelada = resultSet.getBoolean(i++);
                int numPositivas = resultSet.getInt(i++);
                int numNegativas = resultSet.getInt(i++);

                Encuesta encuesta = new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada, numPositivas, numNegativas);
                encuestas.add(encuesta);
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar encuestas por palabra clave", e);
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

    @Override
    public void update(Connection connection, Encuesta encuesta)
            throws InstanceNotFoundException {

        String queryString = "UPDATE Encuesta "
                + "SET pregunta = ?, fechaFin = ?, cancelada = ? "
                + "WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaFin()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());
            preparedStatement.setLong(i++, encuesta.getEncuestaId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(
                        encuesta.getEncuestaId(), Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la encuesta con ID="
                    + encuesta.getEncuestaId(), e);
        }
    }

//  =========================================
//                 EXTENSIÓN
//  =========================================

    @Override
    public List<Encuesta> findPopularSurveys(Connection connection, int minRespuestas,
                                              LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        // Construye la query dinámicamente según los parámetros opcionales
        StringBuilder sb = new StringBuilder(
                "SELECT e.encuestaId, e.pregunta, e.fechaCreacion, e.fechaFin, e.cancelada, " +
                "COUNT(CASE WHEN r.respuestaPositiva = TRUE THEN 1 END) AS numPositivas, " +
                "COUNT(CASE WHEN r.respuestaPositiva = FALSE THEN 1 END) AS numNegativas " +
                "FROM Encuesta e " +
                "LEFT JOIN Respuesta r ON e.encuestaId = r.encuestaId " +
                "WHERE e.fechaFin > NOW() ");

        if (fechaInicio != null) {
            sb.append("AND e.fechaFin >= ? ");
        }
        if (fechaFin != null) {
            sb.append("AND e.fechaFin <= ? ");
        }

        sb.append("GROUP BY e.encuestaId, e.pregunta, e.fechaCreacion, e.fechaFin, e.cancelada ");
        sb.append("HAVING COUNT(r.respuestaId) >= ? ");
        sb.append("ORDER BY e.fechaFin ASC");

        try (PreparedStatement preparedStatement = connection.prepareStatement(sb.toString())) {

            int paramIndex = 1;
            if (fechaInicio != null) {
                preparedStatement.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicio));
            }
            if (fechaFin != null) {
                preparedStatement.setTimestamp(paramIndex++, Timestamp.valueOf(fechaFin));
            }
            preparedStatement.setInt(paramIndex, minRespuestas);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Encuesta> encuestas = new ArrayList<>();

            while (resultSet.next()) {
                int i = 1;
                Long encuestaId     = resultSet.getLong(i++);
                String pregunta     = resultSet.getString(i++);
                LocalDateTime fechaCreacion = resultSet.getTimestamp(i++).toLocalDateTime();
                LocalDateTime fechaFinDB    = resultSet.getTimestamp(i++).toLocalDateTime();
                boolean cancelada   = resultSet.getBoolean(i++);
                int numPositivas    = resultSet.getInt(i++);
                int numNegativas    = resultSet.getInt(i++);

                encuestas.add(new Encuesta(encuestaId, pregunta, fechaCreacion,
                        fechaFinDB, cancelada, numPositivas, numNegativas));
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar encuestas populares", e);
        }
    }

}
