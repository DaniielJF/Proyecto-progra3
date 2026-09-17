package reservas.logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaIntegrationIT {

    @Test
    void flujoCompletoDeReservaDebeFuncionar() throws Exception {
        String sufijo = String.valueOf(System.nanoTime());

        // 1. Crear categoria
        CategoriaRecurso categoria = new CategoriaRecurso();
        categoria.setDescripcion("Sala IT " + sufijo);
        Service.instance().create(categoria);

        // 2. Crear recurso de esa categoria
        Recurso recurso = new Recurso();
        recurso.setId("REC-IT-" + sufijo);
        recurso.setCategoria(categoria);
        recurso.setDescripcion("Recurso de prueba IT");
        Service.instance().create(recurso);

        // 3. Crear funcionario
        Funcionario funcionario = new Funcionario("func-it-" + sufijo, "clave", "Funcionario IT", "8888-0000");
        Service.instance().create(funcionario);

        // 4. Crear reserva: debe tener exito porque el recurso esta libre
        LocalDate fecha = LocalDate.now().plusDays(10);
        ResultadoReserva resultado = Service.instance().crearReserva(
                "Actividad de prueba", fecha, LocalTime.of(9, 0), LocalTime.of(10, 0),
                funcionario, List.of(categoria));

        assertTrue(resultado.isExito());
        assertEquals(1, resultado.getReserva().getRecursosAsignados().size());
        assertEquals(recurso.getId(), resultado.getReserva().getRecursosAsignados().get(0).getId());

        // 5. Intentar reservar el mismo recurso/horario con otro funcionario: debe fallar (no hay mas unidades)
        Funcionario otroFuncionario = new Funcionario("func-it2-" + sufijo, "clave", "Otro Funcionario", "8888-1111");
        Service.instance().create(otroFuncionario);

        ResultadoReserva resultadoFallido = Service.instance().crearReserva(
                "Actividad conflictiva", fecha, LocalTime.of(9, 30), LocalTime.of(10, 30),
                otroFuncionario, List.of(categoria));
        assertFalse(resultadoFallido.isExito());
        assertEquals(1, resultadoFallido.getCategoriasNoDisponibles().size());

        // 6. Cancelar la primera reserva libera el recurso
        Service.instance().cancelarReserva(resultado.getReserva().getId());
        ResultadoReserva resultadoDespuesDeCancelar = Service.instance().crearReserva(
                "Actividad conflictiva", fecha, LocalTime.of(9, 30), LocalTime.of(10, 30),
                otroFuncionario, List.of(categoria));
        assertTrue(resultadoDespuesDeCancelar.isExito());

        // 7. Verificar que la reserva del funcionario aparece en su listado
        List<Reserva> reservasFuncionario = Service.instance().reservasDe(otroFuncionario);
        assertEquals(1, reservasFuncionario.size());

        // 8. Modificar el funcionario y verificar que el cambio se refleja en la MISMA instancia
        //    referenciada por la reserva (@XmlIDREF), sin necesidad de recargar el XML.
        Funcionario cambios = new Funcionario();
        cambios.setId(otroFuncionario.getId());
        cambios.setNombre("Otro Funcionario Actualizado");
        cambios.setTelefono("8888-2222");
        Service.instance().update(cambios);
        assertEquals("Otro Funcionario Actualizado", reservasFuncionario.get(0).getFuncionario().getNombre());
    }
}
