package reservas.logic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reservas.backend.ServiceImpl;
import reservas.backend.Server;
import reservas.comm.Protocolo;
import reservas.presentation.comm.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integracion (Failsafe): a diferencia de ServiceTest/ServiceCrudTest
 * (que prueban la logica del Backend directamente), esta prueba ejercita TODA
 * la arquitectura distribuida: levanta un Backend real (Server + Worker,
 * atendiendo por sockets, usando ServiceImpl contra MySQL) y, como Frontend,
 * usa el Proxy real (reservas.presentation.comm.Service) que le habla por
 * sockets. Requiere entonces tanto MySQL (ver reservas.sql) como que el
 * puerto por defecto (Protocolo.PUERTO_POR_DEFECTO) este libre.
 */
class ReservaIntegrationIT {

    private static Server backend;

    @BeforeAll
    static void levantarBackend() throws Exception {
        backend = new Server(Protocolo.PUERTO_POR_DEFECTO, ServiceImpl.instance());
        Thread hiloBackend = new Thread(backend::run);
        hiloBackend.setDaemon(true);
        hiloBackend.start();
        // Pequena espera para asegurar que el ServerSocket ya este escuchando
        // antes de que el Proxy (Service.instance()) intente conectarse.
        Thread.sleep(500);
    }

    @AfterAll
    static void detenerBackend() {
        try { Service.instance().cerrar(); } catch (Exception ignored) { }
        if (backend != null) backend.detener();
    }

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

        // 8. Modificar el funcionario y verificar que el cambio se refleja al
        //    consultar de nuevo. A diferencia de la version XML/en memoria del
        //    Proyecto #1 (donde la reserva guardaba una referencia directa al
        //    MISMO objeto Funcionario, @XmlIDREF), en la arquitectura distribuida
        //    cada respuesta del Backend es una copia nueva, serializada y enviada
        //    por el socket: no hay identidad de objetos entre Frontend y Backend,
        //    ni entre dos respuestas distintas. Por eso se vuelve a pedir el
        //    listado despues de actualizar, en vez de asumir que la referencia
        //    ya obtenida en el paso 7 se actualizo "sola".
        Funcionario cambios = new Funcionario();
        cambios.setId(otroFuncionario.getId());
        cambios.setNombre("Otro Funcionario Actualizado");
        cambios.setTelefono("8888-2222");
        Service.instance().update(cambios);

        List<Reserva> reservasFuncionarioActualizado = Service.instance().reservasDe(otroFuncionario);
        assertEquals(1, reservasFuncionarioActualizado.size());
        assertEquals("Otro Funcionario Actualizado", reservasFuncionarioActualizado.get(0).getFuncionario().getNombre());
    }
}
