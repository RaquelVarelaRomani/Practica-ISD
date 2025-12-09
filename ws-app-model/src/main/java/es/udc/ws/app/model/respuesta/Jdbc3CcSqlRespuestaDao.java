package es.udc.ws.app.model.respuesta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Jdbc3CcSqlRespuestaDao extends AbstractSqlRespuestaDao {


    // TODO:
    //     1. Sólo debería de tener el method create, que es el único que depende de aspectos específicos de una familia de base de datos concreta (con soporte de columnas contador).

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
    public List<Respuesta> findByEncuesta(Connection connection, Long encuestaId, boolean soloPositivas) throws SQLException {
        StringBuilder query = new StringBuilder(
                "SELECT respuestaId, encuestaId, emailUsuario, respuestaPositiva, fechaRespuesta FROM Respuesta WHERE encuestaId = ? "
        );
        if (soloPositivas) {
            query.append("AND respuestaPositiva = TRUE ");
        }
        query.append("ORDER BY fechaRespuesta");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            preparedStatement.setLong(1, encuestaId);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Respuesta> respuestas = new ArrayList<>();

            while (resultSet.next()) {
                int i = 1;
                Long respuestaId = resultSet.getLong(i++);
                Long encId = resultSet.getLong(i++);
                String emailUsuario = resultSet.getString(i++);
                boolean respuestaPositiva = resultSet.getBoolean(i++);
                java.sql.Timestamp fechaRespTimestamp = resultSet.getTimestamp(i++);
                java.time.LocalDateTime fechaRespuesta = fechaRespTimestamp.toLocalDateTime();

                respuestas.add(new Respuesta(respuestaId, encId, emailUsuario, respuestaPositiva, fechaRespuesta));
            }

            return respuestas;
        }
    }
}
