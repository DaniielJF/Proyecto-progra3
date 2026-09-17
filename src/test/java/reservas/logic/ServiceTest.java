package reservas.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void loginConUsuarioInexistenteDebeFallar() {
        assertThrows(Exception.class, () -> Service.instance().login("no-existe", "clave"));
    }

    @Test
    void loginConClaveIncorrectaDebeFallar() {
        // El administrador semilla se crea automaticamente si no hay usuarios: admin/admin
        assertThrows(Exception.class, () -> Service.instance().login("admin", "clave-mala"));
    }

    @Test
    void crearYBuscarCategoriaDebeFuncionar() throws Exception {
        CategoriaRecurso categoria = new CategoriaRecurso();
        categoria.setDescripcion("Categoria de prueba " + System.nanoTime());
        Service.instance().create(categoria);

        assertNotNull(categoria.getId());
        var filtro = new CategoriaRecurso();
        filtro.setDescripcion(categoria.getDescripcion());
        var encontradas = Service.instance().search(filtro);
        assertEquals(1, encontradas.size());
        assertEquals(categoria.getId(), encontradas.get(0).getId());
    }

    @Test
    void crearReservaSinCategoriasDebeFallar() {
        Funcionario f = new Funcionario("func-test-" + System.nanoTime(), "clave", "Funcionario Prueba", "0000-0000");
        assertThrows(Exception.class, () -> Service.instance().crearReserva(
                "Actividad", java.time.LocalDate.now().plusDays(1),
                java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 0),
                f, java.util.List.of()));
    }
}
