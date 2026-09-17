package reservas.presentation.reservas;

import reservas.logic.*;
import reservas.presentation.Pdf;
import reservas.presentation.Sesion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final Model model;
    private final IAService iaService = new IAService();

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        model.setCategoriasDisponibles(Service.instance().findAllCategorias());
        view.poblarCategorias(model.getCategoriasDisponibles());
        refrescarListado();
    }

    private Funcionario funcionarioActual() {
        return (Funcionario) Sesion.getUsuario();
    }

    public void refrescarListado() {
        model.setMisReservas(Service.instance().reservasDe(funcionarioActual()));
    }

    public void extraerConIA(String frase) {
        DatosExtraidosIA datos = iaService.extraer(frase, model.getCategoriasDisponibles());
        view.mostrarDatosExtraidos(datos);
    }

    public ResultadoReserva reservar(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                      List<CategoriaRecurso> categoriasSeleccionadas) throws Exception {
        ResultadoReserva resultado = Service.instance().crearReserva(
                actividad, fecha, horaInicio, horaFin, funcionarioActual(), categoriasSeleccionadas);
        if (resultado.isExito()) {
            refrescarListado();
        }
        return resultado;
    }

    public void cancelar(Reserva r) throws Exception {
        Service.instance().cancelarReserva(r.getId());
        refrescarListado();
    }

    public void imprimir() {
        List<String[]> filas = new ArrayList<>();
        for (Reserva r : model.getMisReservas()) {
            String recursos = String.join(", ", r.getRecursosAsignados().stream().map(Recurso::getId).toList());
            filas.add(new String[]{r.getId(), r.getActividad(), r.getFecha().toString(),
                    r.getHoraInicio() + " - " + r.getHoraFin(), recursos, r.getEstado().toString()});
        }
        Pdf.imprimir("Mis Reservas", new String[]{"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"}, filas, "mis_reservas.pdf");
    }
}
