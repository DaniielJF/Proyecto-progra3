package reservas.presentation.comm;

import reservas.comm.Protocolo;
import reservas.logic.Reserva;

import javax.swing.SwingUtilities;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Frontend-SocketListener: hilo dedicado a "escuchar" las notificaciones
 * asincronicas que el Backend envia a este Frontend (actualmente, cada vez
 * que otro usuario registra una nueva reserva). Al llegar una notificacion se
 * la pasa (deliver) a quien se haya registrado como su ThreadListener, usando
 * invokeLater para respetar el diseño mono-hilo (single threaded) de Swing.
 */
public class SocketListener {

    private final ThreadListener listener;
    private final String sid; // Session Id

    private Socket as;              // Asynchronous Socket
    private ObjectOutputStream aos;
    private ObjectInputStream ais;

    private Thread hilo;
    private volatile boolean continuar = true;

    public SocketListener(ThreadListener listener, String sid) throws Exception {
        this.listener = listener;
        this.sid = sid;

        as = new Socket(ConexionConfig.HOST, ConexionConfig.PUERTO);
        aos = new ObjectOutputStream(as.getOutputStream());
        ais = new ObjectInputStream(as.getInputStream());

        aos.writeInt(Protocolo.ASYNC);
        aos.writeObject(sid);
        aos.flush();
    }

    public void start() {
        hilo = new Thread(this::listen, "SocketListener-" + sid);
        continuar = true;
        hilo.setDaemon(true);
        hilo.start();
    }

    public void stop() {
        continuar = false;
        try { as.close(); } catch (Exception ignored) { }
    }

    private void listen() {
        while (continuar) {
            try {
                int metodo = ais.readInt();
                switch (metodo) {
                    case Protocolo.NOTIFICAR_RESERVA: {
                        Reserva reserva = (Reserva) ais.readObject();
                        deliver(reserva);
                        break;
                    }
                    default:
                        // notificacion desconocida: se ignora
                        break;
                }
            } catch (Exception ex) {
                continuar = false;
            }
        }
        try { as.close(); } catch (Exception ignored) { }
    }

    private void deliver(Reserva reserva) {
        SwingUtilities.invokeLater(() -> listener.recibirReserva(reserva));
    }
}
