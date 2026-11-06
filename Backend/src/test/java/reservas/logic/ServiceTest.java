package reservas.logic;

import org.junit.jupiter.api.Test;
import reservas.backend.ServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de unidad (Surefire) de la logica de negocio del Backend.
 *
 * Se prueba directamente ServiceImpl (sin pasar por sockets ni por el
 * Proxy del Frontend), ya que el Proxy solo reenvia las llamadas por red:
 * la logica real a validar vive en el Backend. Estas pruebas SI requieren
 * una base de datos MySQL alcanzable (la creada con reservas.sql y
 * configurada en database.properties), pero no requieren levantar el
 * proceso Backend (BackendMain) ni ninguna conexion por socket.
 */
class ServiceTest {

    private final IService service = ServiceImpl.instance();

    @Test
    void loginConUsuarioInexistenteDebeFallar() {
        assertThrows(Exception.class, () -> service.login("no-existe", "clave"));
    }

    @Test
    void loginConClaveIncorrectaDebeFallar() {
        // El administrador semilla se crea automaticamente si no hay usuarios: admin/admin
        assertThrows(Exception.class, () -> service.login("admin", "clave-mala"));
    }

    @Test
    void crearYBuscarCategoriaDebeFuncionar() throws Exception {
        CategoriaRecurso categoria = new CategoriaRecurso();
        categoria.setDescripcion("Categoria de prueba " + System.nanoTime());
        service.create(categoria);

        assertNotNull(categoria.getId());
        var filtro = new CategoriaRecurso();
        filtro.setDescripcion(categoria.getDescripcion());
        var encontradas = service.search(filtro);
        assertEquals(1, encontradas.size());
        assertEquals(categoria.getId(), encontradas.get(0).getId());
    }

    @Test
    void crearReservaSinCategoriasDebeFallar() {
        Funcionario f = new Funcionario("func-test-" + System.nanoTime(), "clave", "Funcionario Prueba", "0000-0000");
        assertThrows(Exception.class, () -> service.crearReserva(
                "Actividad", java.time.LocalDate.now().plusDays(1),
                java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 0),
                f, java.util.List.of()));
    }
}
