package reservas.presentation.reservas;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    private List<Reserva> misReservas;
    private Reserva seleccionada;
    private List<CategoriaRecurso> categoriasDisponibles;

    public static final String MIS_RESERVAS = "misReservas";
    public static final String SELECCIONADA = "seleccionada";
    public static final String CATEGORIAS = "categoriasDisponibles";

    public Model() {
        misReservas = new ArrayList<>();
        categoriasDisponibles = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(MIS_RESERVAS);
        firePropertyChange(CATEGORIAS);
    }

    public List<Reserva> getMisReservas() { return misReservas; }
    public void setMisReservas(List<Reserva> misReservas) {
        this.misReservas = misReservas;
        firePropertyChange(MIS_RESERVAS);
    }

    public Reserva getSeleccionada() { return seleccionada; }
    public void setSeleccionada(Reserva seleccionada) {
        this.seleccionada = seleccionada;
        firePropertyChange(SELECCIONADA);
    }

    public List<CategoriaRecurso> getCategoriasDisponibles() { return categoriasDisponibles; }
    public void setCategoriasDisponibles(List<CategoriaRecurso> categoriasDisponibles) {
        this.categoriasDisponibles = categoriasDisponibles;
        firePropertyChange(CATEGORIAS);
    }
}
