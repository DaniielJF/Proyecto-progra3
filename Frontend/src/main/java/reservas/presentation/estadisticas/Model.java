package reservas.presentation.estadisticas;

import reservas.presentation.AbstractModel;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model extends AbstractModel {
    private LocalDate desdeRecursos = LocalDate.now().minusWeeks(2);
    private LocalDate hastaRecursos = LocalDate.now().plusWeeks(2);
    private Map<String, Integer> estadisticasRecursos = new LinkedHashMap<>();

    private LocalDate desdeActividades = LocalDate.now().minusWeeks(2);
    private LocalDate hastaActividades = LocalDate.now().plusWeeks(2);
    private Map<String, Integer> estadisticasActividades = new LinkedHashMap<>();

    public static final String ESTADISTICAS_RECURSOS = "estadisticasRecursos";
    public static final String ESTADISTICAS_ACTIVIDADES = "estadisticasActividades";

    public LocalDate getDesdeRecursos() { return desdeRecursos; }
    public void setDesdeRecursos(LocalDate d) { this.desdeRecursos = d; }

    public LocalDate getHastaRecursos() { return hastaRecursos; }
    public void setHastaRecursos(LocalDate d) { this.hastaRecursos = d; }

    public Map<String, Integer> getEstadisticasRecursos() { return estadisticasRecursos; }
    public void setEstadisticasRecursos(Map<String, Integer> m) {
        this.estadisticasRecursos = m;
        firePropertyChange(ESTADISTICAS_RECURSOS);
    }

    public LocalDate getDesdeActividades() { return desdeActividades; }
    public void setDesdeActividades(LocalDate d) { this.desdeActividades = d; }

    public LocalDate getHastaActividades() { return hastaActividades; }
    public void setHastaActividades(LocalDate d) { this.hastaActividades = d; }

    public Map<String, Integer> getEstadisticasActividades() { return estadisticasActividades; }
    public void setEstadisticasActividades(Map<String, Integer> m) {
        this.estadisticasActividades = m;
        firePropertyChange(ESTADISTICAS_ACTIVIDADES);
    }
}
