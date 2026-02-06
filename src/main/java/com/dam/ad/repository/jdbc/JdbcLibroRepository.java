package com.dam.ad.repository.jdbc;

import com.dam.ad.repository.LibroRepository;
import net.javaguides.db;
import net.javaguides.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLibroRepository implements LibroRepository { 

private Libro mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String isbn = rs.getString("isbn");
        Integer anio = (Integer) rs.getObject("anio"); // admite NULL
        boolean disponible = rs.getBoolean("disponible");
        return new Libro(id, titulo, isbn, anio, disponible);
    }

    public List<Libro> findAll() {
        String sql = "SELECT id, titulo, isbn, anio, disponible FROM libro ORDER BY id";

        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            List<Libro> libros = new ArrayList<>();
            while (rs.next()) {
                libros.add(mapRow(rs));
            }
            return libros;

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo libros", e);
        }
    }

    public Optional<Libro> findById(int id) {
        String sql = "SELECT id, titulo, isbn, anio, disponible FROM  libro WHERE id = ?";

        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando libro por id=" +
                    id, e);
        }
    }

    public int insert(Libro libro) {
        String sql = "INSERT INTO libro (titulo, isbn, anio, disponible) VALUES (?,?,?,?)";

        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getIsbn());
            ps.setObject(3, libro.getAnio()); // admite NULL
            ps.setBoolean(4, libro.isDisponible());

            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new SQLException("INSERT inesperado. Filas afectadas: " + rows);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    libro.setId(id);
                    return id;
                }
                throw new SQLException("No se devolvió id generado (getGeneratedKeys vacío)");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando libro", e);
        }
    }

    public boolean update(Libro libro) {
        if (libro.getId() == null) {
            throw new IllegalArgumentException("No se puede UPDATE sin id. ¿Hiciste insert primero?");
        }

        String sql = "UPDATE libro SET titulo=?, isbn=?, anio=?, disponible=? WHERE id=?";

        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getIsbn());
            ps.setObject(3, libro.getAnio());
            ps.setBoolean(4, libro.isDisponible());
            ps.setInt(5, libro.getId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando libro id=" +
                    libro.getId(), e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM libro WHERE id=?";

        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error borrando libro id=" + id,
                    e);
        }
    }

    /* BÚSQUEDA DE LIBROS */
    public List<Libro> searchByTitulo(String texto) {

        // Consulta SQL usando LIKE y LOWER para búsqueda case-insensitive
        String sql = "SELECT id, titulo, isbn, anio, disponible " +
                "FROM libro " +
                "WHERE LOWER(titulo) LIKE ? " +
                "ORDER BY id";

        // Lista donde se almacenarán los resultados
        List<Libro> resultados = new ArrayList<>();

        // try-with-resources para asegurar cierre de recursos JDBC
        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            
            // %texto% permite buscar el texto en cualquier parte del título
            ps.setString(1, "%" + texto.toLowerCase() + "%");

            // Se ejecuta la consulta
            try (ResultSet rs = ps.executeQuery()) {

                // Se recorren los resultados y se mapean usando mapRow
                while (rs.next()) {
                    resultados.add(mapRow(rs));
                }
            }

            // Se devuelve la lista (puede estar vacía si no hay coincidencias)
            return resultados;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando libros por título: " + texto, e);
        }
    }

}
