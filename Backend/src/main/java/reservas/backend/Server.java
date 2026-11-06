package reservas.backend;

import reservas.comm.Protocolo;
import reservas.logic.IService;
import reservas.logic.Reserva;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final int puerto;
    private final IService service;
    private final List<Worker> workers = new CopyOnWriteArrayList<>();
    private volatile boolean continuar = true;
    private ServerSocket srv;

    public Server(int puerto, IService service) {
        this.puerto = puerto;
        this.service = service;
    }

    public void run() {
        try {
            srv = new ServerSocket(puerto);
            System.out.println("Backend escuchando en el puerto " + puerto + "...");
            Socket s;
            while (continuar) {
                s = srv.accept();
                System.out.println("Conexion establecida... " + s.getRemoteSocketAddress());

                ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream is = new ObjectInputStream(s.getInputStream());

                int tipo = is.readInt();
                switch (tipo) {
                    case Protocolo.SYNC: {
                        String sid = UUID.randomUUID().toString();
                        System.out.println("SYNC: " + sid);
                        Worker worker = new Worker(this, s, os, is, sid, service);
                        workers.add(worker);
                        System.out.println("Frontends conectados: " + workers.size());
                        worker.start();
                        os.writeObject(sid);
                        os.flush();
                        break;
                    }
                    case Protocolo.ASYNC: {
                        String sid = (String) is.readObject();
                        System.out.println("ASYNC: " + sid);
                        join(s, os, is, sid);
                        break;
                    }
                    default:
                        s.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void join(Socket as, ObjectOutputStream aos, ObjectInputStream ais, String sid) {
        for (Worker w : workers) {
            if (w.getSid().equals(sid)) {
                w.setAs(as, aos, ais);
                break;
            }
        }
    }

    public void deliverMessage(Worker from, Reserva reserva) {
        for (Worker w : workers) {
            if (w != from) {
                w.notificarReserva(reserva);
            }
        }
    }

    public void removerWorker(Worker w) {
        workers.remove(w);
        System.out.println("Frontend desconectado. Frontends conectados: " + workers.size());
    }

    public void detener() {
        continuar = false;
        try { if (srv != null) srv.close(); } catch (Exception ignored) { }
        for (Worker w : workers) {
            w.cerrar();
        }
    }
}
