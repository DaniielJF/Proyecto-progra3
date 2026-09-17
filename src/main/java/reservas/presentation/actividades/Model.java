package reservas.presentation.actividades;

import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    private LocalDate fechaReferencia = LocalDate.now();
    private List<Reserva> reservasSemana;

    public static final String RESERVAS_SEMANA = "reservasSemana";

    public static final int HORA_INICIO = 6;
    public static final int HORA_FIN = 21;

    public Model() {
        reservasSemana = new ArrayList<>();
    }

    public LocalDate getFechaReferencia() { return fechaReferencia; }
    public void setFechaReferencia(LocalDate fechaReferencia) { this.fechaReferencia = fechaReferencia; }

    public List<Reserva> getReservasSemana() { return reservasSemana; }
    public void setReservasSemana(List<Reserva> reservasSemana) {
        this.reservasSemana = reservasSemana;
        firePropertyChange(RESERVAS_SEMANA);
    }
}
