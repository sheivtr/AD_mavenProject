package net.javaguides;

import net.javaguides.db;
import net.javaguides.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LibroDao {
    public List<Libro> findAll() {
        String sql = "SELECT id, titulo, isbn, anio, disponible FROM libro ORDER BY id";
        try (Connection con = db.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Libro> libros = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String isbn = rs.getString("isbn");
                Integer anio = (Integer) rs.getObject("anio"); // admite NULL
                boolean disponible = rs.getBoolean("disponible");
                libros.add(new Libro(id, titulo, isbn, anio, disponible));
            }
            return libros;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo libros", e);
        }
    }
}
