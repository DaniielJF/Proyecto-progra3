package reservas.backend;

import reservas.comm.Protocolo;

public class BackendMain {

    public static void main(String[] args) {
        int puerto = Protocolo.PUERTO_POR_DEFECTO;
        if (args.length > 0) {
            try {
                puerto = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto invalido '" + args[0] + "', se usa el puerto por defecto " + puerto);
            }
        }
        Server server = new Server(puerto, ServiceImpl.instance());
        server.run();
    }
}
