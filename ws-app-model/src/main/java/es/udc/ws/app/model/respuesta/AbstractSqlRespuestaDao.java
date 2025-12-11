package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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

    @Override
    public List<Respuesta> findByEncuesta(Connection connection, Long encuestaId, boolean soloPositivas) {
        String base =
                "SELECT respuestaId, encuestaId, emailUsuario, respuestaPositiva, fechaRespuesta " +
                        "FROM Respuesta WHERE encuestaId = ? ";

        if (soloPositivas) base += "AND respuestaPositiva = TRUE ";

        base += "ORDER BY fechaRespuesta";

        try (PreparedStatement ps = connection.prepareStatement(base)) {
            ps.setLong(1, encuestaId);
            ResultSet resultSet = ps.executeQuery();
            List<Respuesta> respuestas = new ArrayList<>();

            while (resultSet.next()) {
                respuestas.add(new Respuesta(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getString(3),
                        resultSet.getBoolean(4),
                        resultSet.getTimestamp(5).toLocalDateTime()
                ));
            }
            return respuestas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long respuestaId)
            throws InstanceNotFoundException {

        String query = "DELETE FROM Respuesta WHERE respuestaId = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setLong(1, respuestaId);

            int removed = ps.executeUpdate();

            if (removed == 0) throw new InstanceNotFoundException(respuestaId, Respuesta.class.getName());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
