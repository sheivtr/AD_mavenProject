package net.javaguides;

import net.javaguides.LibroDao;

public class Main {
    public static void main(String[] args) {
        LibroDao dao = new LibroDao();
        System.out.println("=== LIBROS ===");
        dao.findAll().forEach(System.out::println);
    }
}
