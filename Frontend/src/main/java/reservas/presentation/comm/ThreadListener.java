package reservas.presentation.comm;

import reservas.logic.Reserva;

/**
 * Quien quiera recibir notificaciones asincronicas del Backend (en este caso
 * el Controller de Alertas) implementa esta interface y se la registra a un
 * SocketListener.
 */
public interface ThreadListener {
    void recibirReserva(Reserva reserva);
}
