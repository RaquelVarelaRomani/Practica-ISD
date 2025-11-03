package es.udc.ws.app.model.encuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        String queryString = "SELECT pregunta, fechaCreacion, fechaFin, cancelada "
                + "FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

            String pregunta = resultSet.getString(1);
            LocalDateTime fechaCreacion = resultSet.getTimestamp(2).toLocalDateTime();
            LocalDateTime fechaFin = resultSet.getTimestamp(3).toLocalDateTime();
            boolean cancelada = resultSet.getBoolean(4);

            return new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean soloActivas)
            throws InstanceNotFoundException {

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

                // Constructor estándar (los contadores se gestionan en la capa de servicio)
                Encuesta encuesta = new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada);
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
}
