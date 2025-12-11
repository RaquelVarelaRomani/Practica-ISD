package es.udc.ws.app.model.respuesta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Jdbc3CcSqlRespuestaDao extends AbstractSqlRespuestaDao {

    @Override
    public Respuesta create(Connection con, Respuesta respuesta) {

        String query =
                "INSERT INTO Respuesta (encuestaId, emailUsuario, respuestaPositiva, fechaRespuesta) " +
                        "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            ps.setLong(i++, respuesta.getEncuestaId());
            ps.setString(i++, respuesta.getEmailUsuario());
            ps.setBoolean(i++, respuesta.isRespuestaPositiva());

            LocalDateTime fecha =
                    (respuesta.getFechaRespuesta() != null)
                            ? respuesta.getFechaRespuesta()
                            : LocalDateTime.now();

            ps.setTimestamp(i++, Timestamp.valueOf(fecha));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (!rs.next()) throw new SQLException("No se devolvió ID generado");

            return new Respuesta(
                    rs.getLong(1),
                    respuesta.getEncuestaId(),
                    respuesta.getEmailUsuario(),
                    respuesta.isRespuestaPositiva(),
                    fecha
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}