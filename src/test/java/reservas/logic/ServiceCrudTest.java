package reservas.logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServiceCrudTest {

    @Test
    void crudDeFuncionarioDebeFuncionar() throws Exception {
        String id = "func-crud-" + System.nanoTime();
        Funcionario f = new Funcionario(id, "clave-temporal", "Nombre Original", "1111-1111");
        Service.instance().create(f);
        assertEquals(id, f.getClave()); // la clave inicial queda igual al id

        Funcionario cambios = new Funcionario();
        cambios.setId(id);
        cambios.setNombre("Nombre Modificado");
        cambios.setTelefono("2222-2222");
        Service.instance().update(cambios);

        Funcionario filtro = new Funcionario();
        filtro.setId(id);
        filtro.setNombre("");
        List<Funcionario> encontrados = Service.instance().search(filtro);
        assertEquals(1, encontrados.size());
        assertEquals("Nombre Modificado", encontrados.get(0).getNombre());

        Service.instance().delete(f);
        assertTrue(Service.instance().search(filtro).isEmpty());
    }

    @Test
    void noSePuedeBorrarCategoriaConRecursosAsociados() throws Exception {
        CategoriaRecurso categoria = new CategoriaRecurso();
        categoria.setDescripcion("Categoria con recurso " + System.nanoTime());
        Service.instance().create(categoria);

        Recurso recurso = new Recurso();
        recurso.setId("REC-CRUD-" + System.nanoTime());
        recurso.setCategoria(categoria);
        recurso.setDescripcion("Recurso de prueba");
        Service.instance().create(recurso);

        assertThrows(Exception.class, () -> Service.instance().delete(categoria));

        Service.instance().delete(recurso);
        assertDoesNotThrow(() -> Service.instance().delete(categoria));
    }

    @Test
    void estadisticasActividadesDebeIncluirTodasLasSemanasDelPeriodo() {
        LocalDate desde = LocalDate.now().plusMonths(6); // periodo sin ninguna reserva
        LocalDate hasta = desde.plusWeeks(3);

        Map<String, Integer> resultado = Service.instance().estadisticasActividades(desde, hasta);

        // debe haber al menos 4 semanas listadas, todas en 0 (no solo las que tuvieron actividad)
        assertTrue(resultado.size() >= 4);
        assertTrue(resultado.values().stream().allMatch(v -> v == 0));
    }

    @Test
    void cambiarClaveDebeFuncionarYRechazarClaveActualIncorrecta() throws Exception {
        String id = "func-clave-" + System.nanoTime();
        Funcionario f = new Funcionario(id, "clave-temporal", "Usuario Clave", "3333-3333");
        Service.instance().create(f); // la clave inicial queda igual al id

        assertThrows(Exception.class, () -> Service.instance().cambiarClave(id, "clave-incorrecta", "nueva123"));

        Service.instance().cambiarClave(id, id, "nueva123");
        // login con la clave nueva debe funcionar
        assertDoesNotThrow(() -> Service.instance().login(id, "nueva123"));
        // login con la clave vieja ya no debe funcionar
        assertThrows(Exception.class, () -> Service.instance().login(id, id));
    }

    @Test
    void crearFuncionarioSinTelefonoDebeFallar() {
        Funcionario f = new Funcionario("func-sin-tel-" + System.nanoTime(), "clave", "Sin Telefono", "");
        assertThrows(Exception.class, () -> Service.instance().create(f));
    }

    @Test
    void crearRecursoSinDescripcionDebeFallar() throws Exception {
        CategoriaRecurso categoria = new CategoriaRecurso();
        categoria.setDescripcion("Categoria para recurso sin desc " + System.nanoTime());
        Service.instance().create(categoria);

        Recurso r = new Recurso();
        r.setId("REC-SIN-DESC-" + System.nanoTime());
        r.setCategoria(categoria);
        r.setDescripcion("");
        assertThrows(Exception.class, () -> Service.instance().create(r));
    }
}
