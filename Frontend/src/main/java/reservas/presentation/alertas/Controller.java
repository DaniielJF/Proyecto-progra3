package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.comm.ThreadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller del area de Alertas. Implementa ThreadListener: el SocketListener
 * del Frontend le entrega (ya en el EDT) cada reserva que otros usuarios van
 * registrando, y este Controller la agrega al tope de la lista de alertas.
 *
 * "Descartar" es una operacion puramente local: solo quita la fila de esta
 * lista, nunca se envia nada al Backend.
 */
public class Controller implements ThreadListener {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    @Override
    public void recibirReserva(Reserva reserva) {
        List<Reserva> nuevas = new ArrayList<>();
        nuevas.add(reserva);
        nuevas.addAll(model.getAlertas());
        model.setAlertas(nuevas);
    }

    public void descartar(Reserva reserva) {
        List<Reserva> restantes = new ArrayList<>(model.getAlertas());
        restantes.remove(reserva);
        model.setAlertas(restantes);
    }
}
