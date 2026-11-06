package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String ALERTAS = "alertas";

    private List<Reserva> alertas = new ArrayList<>();

    public List<Reserva> getAlertas() { return alertas; }

    public void setAlertas(List<Reserva> alertas) {
        this.alertas = alertas;
        firePropertyChange(ALERTAS);
    }
}
