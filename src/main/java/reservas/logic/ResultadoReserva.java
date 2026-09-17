package reservas.logic;

import java.util.ArrayList;
import java.util.List;

public class ResultadoReserva {

    private boolean exito;
    private List<CategoriaRecurso> categoriasNoDisponibles = new ArrayList<>();
    private Reserva reserva;

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public List<CategoriaRecurso> getCategoriasNoDisponibles() { return categoriasNoDisponibles; }
    public void setCategoriasNoDisponibles(List<CategoriaRecurso> categoriasNoDisponibles) { this.categoriasNoDisponibles = categoriasNoDisponibles; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
}
