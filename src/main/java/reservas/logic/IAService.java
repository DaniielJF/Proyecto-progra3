package reservas.logic;

import reservas.ai.ReservaExtraccion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IAService {

    private static final String[] MESES = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "setiembre", "septiembre", "octubre", "noviembre", "diciembre"
    };

    public DatosExtraidosIA extraer(String frase, List<CategoriaRecurso> categoriasDisponibles) {
        if (frase == null || frase.isBlank()) return new DatosExtraidosIA();

        try {
            return extraerConLLM(frase);
        } catch (Exception e) {
            // Nunca dejamos caer la funcionalidad: si el LLM falla (sin internet,
            // el proxy demo no responde, etc.) se usa el metodo heuristico.
            System.err.println("No se pudo usar el LLM, se usa extraccion heuristica. Motivo: " + e.getMessage());
            return extraerHeuristico(frase, categoriasDisponibles);
        }
    }

    // =================================================================
    // Modo 1: LLM real (LangChain4j, via Service.extraerReserva)
    // =================================================================

    private DatosExtraidosIA extraerConLLM(String frase) throws Exception {
        ReservaExtraccion r = Service.instance().extraerReserva(frase);

        DatosExtraidosIA datos = new DatosExtraidosIA();
        datos.setActividad(r.getActividad());

        if (r.getFecha() != null && !r.getFecha().isBlank()) {
            datos.setFecha(LocalDate.parse(r.getFecha()));
        }
        if (r.getHoraInicio() != null && !r.getHoraInicio().isBlank()) {
            datos.setHoraInicio(LocalTime.parse(r.getHoraInicio()));
        }
        if (r.getHoraFinal() != null && !r.getHoraFinal().isBlank()) {
            datos.setHoraFin(LocalTime.parse(r.getHoraFinal()));
        }
        datos.setDescripcionesCategorias(r.getCategoriasRecurso() != null ? r.getCategoriasRecurso() : new ArrayList<>());

        return datos;
    }

    // =================================================================
    // Modo 2: Heuristico con expresiones regulares (respaldo sin internet)
    // =================================================================

    private DatosExtraidosIA extraerHeuristico(String frase, List<CategoriaRecurso> categoriasDisponibles) {
        DatosExtraidosIA resultado = new DatosExtraidosIA();
        String textoLower = frase.toLowerCase(Locale.ROOT);

        resultado.setFecha(extraerFecha(textoLower));
        LocalTime[] horas = extraerHoras(textoLower);
        resultado.setHoraInicio(horas[0]);
        resultado.setHoraFin(horas[1]);
        resultado.setDescripcionesCategorias(extraerCategorias(textoLower, categoriasDisponibles));
        resultado.setActividad(extraerActividad(frase));

        return resultado;
    }

    private LocalDate extraerFecha(String texto) {
        // Formato "14 de agosto de 2026" o "14 de agosto"
        Pattern p = Pattern.compile("(\\d{1,2})\\s+de\\s+([a-zA-Z]+)(?:\\s+de\\s+(\\d{4}))?");
        Matcher m = p.matcher(texto);
        if (m.find()) {
            int dia = Integer.parseInt(m.group(1));
            String nombreMes = m.group(2);
            int mes = indiceMes(nombreMes);
            int anio = m.group(3) != null ? Integer.parseInt(m.group(3)) : LocalDate.now().getYear();
            if (mes > 0) {
                try {
                    LocalDate fecha = LocalDate.of(anio, mes, dia);
                    // Si el mes/dia ya paso este anio y no se indico anio explicito, asumir el siguiente
                    if (m.group(3) == null && fecha.isBefore(LocalDate.now())) {
                        fecha = fecha.plusYears(1);
                    }
                    return fecha;
                } catch (Exception ignored) { }
            }
        }
        // Formato dd/mm/yyyy
        Pattern p2 = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})");
        Matcher m2 = p2.matcher(texto);
        if (m2.find()) {
            try {
                return LocalDate.of(Integer.parseInt(m2.group(3)), Integer.parseInt(m2.group(2)), Integer.parseInt(m2.group(1)));
            } catch (Exception ignored) { }
        }
        return null;
    }

    private int indiceMes(String nombreMes) {
        String[] nombres = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto",
                "setiembre", "septiembre", "octubre", "noviembre", "diciembre"};
        int[] numeros = {1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 10, 11, 12};
        for (int i = 0; i < nombres.length; i++) {
            if (nombreMes.startsWith(nombres[i].substring(0, 4))) return numeros[i];
        }
        return -1;
    }

    private LocalTime[] extraerHoras(String texto) {
        List<LocalTime> encontradas = new ArrayList<>();
        // Formatos: "8am", "8 am", "8:00 am", "14:00", "10a.m."
        Pattern p = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?\\s?(am|pm|a\\.?\\s?m\\.?|p\\.?\\s?m\\.?)?");
        Matcher m = p.matcher(texto);
        while (m.find()) {
            String horaTxt = m.group(1);
            String minTxt = m.group(2);
            String ampm = m.group(3);
            if (horaTxt == null) continue;
            int hora = Integer.parseInt(horaTxt);
            int min = minTxt != null ? Integer.parseInt(minTxt) : 0;
            if (hora > 23 || min > 59) continue;
            if (ampm != null) {
                ampm = ampm.replace(".", "").replace(" ", "");
                if (ampm.startsWith("p") && hora < 12) hora += 12;
                if (ampm.startsWith("a") && hora == 12) hora = 0;
            }
            if (ampm != null || (minTxt != null)) {
                encontradas.add(LocalTime.of(hora, min));
            }
        }
        LocalTime inicio = encontradas.size() > 0 ? encontradas.get(0) : null;
        LocalTime fin = encontradas.size() > 1 ? encontradas.get(1) : null;
        return new LocalTime[]{inicio, fin};
    }

    private List<String> extraerCategorias(String texto, List<CategoriaRecurso> categoriasDisponibles) {
        List<String> encontradas = new ArrayList<>();
        if (categoriasDisponibles == null) return encontradas;
        for (CategoriaRecurso c : categoriasDisponibles) {
            String desc = c.getDescripcion().toLowerCase(Locale.ROOT);
            for (String palabra : desc.split("\\s+")) {
                if (palabra.length() >= 4 && texto.contains(palabra)) {
                    encontradas.add(c.getDescripcion());
                    break;
                }
            }
        }
        if (texto.contains("sala") && !contieneAlguna(encontradas, "sala")) {
            buscarPorPalabra(categoriasDisponibles, "sala", encontradas);
        }
        if ((texto.contains("laptop") || texto.contains("computadora")) && !contieneAlguna(encontradas, "laptop")) {
            buscarPorPalabra(categoriasDisponibles, "laptop", encontradas);
        }
        if (texto.contains("proyector") && !contieneAlguna(encontradas, "proyector")) {
            buscarPorPalabra(categoriasDisponibles, "proyector", encontradas);
        }
        return encontradas;
    }

    private boolean contieneAlguna(List<String> lista, String palabra) {
        return lista.stream().anyMatch(s -> s.toLowerCase().contains(palabra));
    }

    private void buscarPorPalabra(List<CategoriaRecurso> categorias, String palabra, List<String> destino) {
        for (CategoriaRecurso c : categorias) {
            if (c.getDescripcion().toLowerCase().contains(palabra) && !destino.contains(c.getDescripcion())) {
                destino.add(c.getDescripcion());
            }
        }
    }

    private String extraerActividad(String fraseOriginal) {
        String actividad = fraseOriginal;
        actividad = actividad.replaceAll("(?i)necesito hacer (una|un)?", "");
        actividad = actividad.replaceAll("(?i)el proximo .*", "");
        actividad = actividad.replaceAll("(?i)de \\d{1,2}(am|pm|a\\.?m\\.?|p\\.?m\\.?)?\\s?a\\s?\\d{1,2}(am|pm|a\\.?m\\.?|p\\.?m\\.?)?.*", "");
        actividad = actividad.trim();
        if (actividad.isEmpty()) actividad = fraseOriginal.trim();
        if (actividad.length() > 60) actividad = actividad.substring(0, 60);
        return actividad;
    }
}
