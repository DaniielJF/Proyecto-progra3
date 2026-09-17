package reservas.presentation.calendarizacion;

import reservas.logic.CategoriaRecurso;
import reservas.logic.Recurso;
import reservas.presentation.AbstractModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    private LocalDate fecha = LocalDate.now();
    private CategoriaRecurso categoria;
    private List<CategoriaRecurso> categoriasDisponibles;
    private List<Recurso> recursosDeCategoria;

    public static final String CATEGORIAS = "categoriasDisponibles";
    public static final String RECURSOS = "recursosDeCategoria";

    public static final int HORA_INICIO = 6;
    public static final int HORA_FIN = 21; // inclusive

    public Model() {
        categoriasDisponibles = new ArrayList<>();
        recursosDeCategoria = new ArrayList<>();
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public CategoriaRecurso getCategoria() { return categoria; }
    public void setCategoria(CategoriaRecurso categoria) { this.categoria = categoria; }

    public List<CategoriaRecurso> getCategoriasDisponibles() { return categoriasDisponibles; }
    public void setCategoriasDisponibles(List<CategoriaRecurso> categoriasDisponibles) {
        this.categoriasDisponibles = categoriasDisponibles;
        firePropertyChange(CATEGORIAS);
    }

    public List<Recurso> getRecursosDeCategoria() { return recursosDeCategoria; }
    public void setRecursosDeCategoria(List<Recurso> recursosDeCategoria) {
        this.recursosDeCategoria = recursosDeCategoria;
        firePropertyChange(RECURSOS);
    }
}
