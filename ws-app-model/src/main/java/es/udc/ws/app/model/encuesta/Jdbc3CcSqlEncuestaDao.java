package es.udc.ws.app.model.encuesta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     *  Será posible buscar encuestas por una palabra clave contenida en la pregunta
     *  correspondiente a la encuesta (si se especifica la cadena vacía como palabra clave, se
     *  devolverán todas las encuestas). Además, se indicará si deben recuperarse solamente
     *  encuestas cuya fecha de finalización sea futura o si deben recuperarse también encuestas
     *  cuya fecha de finalización sea pasada. La información devuelta de cada encuesta incluirá la
     *  información proporcionada al crearla y, además, la fecha y hora a la que se ha creado, el
     *  número de empleados que han respondido afirmativamente, el número de empleados que
     *  han respondido negativamente, y si la encuesta está cancelada.
     *  @param connection
     * @param keyword
     * @param soloActivas
     * @return Lista de encuestas que coinciden con el criterio de búsqueda
     * @throws SQLException
     * @throws InstanceNotFoundException
     *
     * **/
    @Override
    public List<Encuesta> findByKeyword(Connection connection, String keyword, boolean soloActivas) throws InstanceNotFoundException{

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

                // Puedes ampliar el constructor para incluir estos contadores si se quieren mostrar
                Encuesta e = new Encuesta(encuestaId, pregunta, fechaCreacion, fechaFin, cancelada);
                // Si planeas visualizarlos, guárdalos en la capa Service o DTO
                encuestas.add(e);
            }

            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar encuestas por palabra clave", e);
        }
    }

    /**
     * todo Funcionalidad 6: Obtener respuestas de una encuesta
     *  Despues modificar SqlEncuestaDao e implementarlo en EncuestaService y EncuestaServiceImpl
     *
    *  Será posible obtener las respuestas que se han recibido para una encuesta, con
    * independencia de si la encuesta aún no ha finalizado o ha sido cancelada. Debe indicarse
    * si se desean obtener todas las respuestas o solamente las respuestas positivas. Deben
    * devolverse todos los datos almacenados para cada respuesta, incluyendo, entre otros, el
    * email del empleado, si el empleado ha respondido afirmativamente o negativamente, y la
    * fecha y hora de la respuesta.
     * @param connection
     * @param encuestaId
     * @param soloPositivas
     * @return Lista de respuestas que coinciden con el criterio de búsqueda
     * @throws SQLException
     * @throws InstanceNotFoundException
    **/






}
