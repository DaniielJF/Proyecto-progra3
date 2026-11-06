package reservas.logic;

import java.io.Serializable;

public class Recurso implements Serializable {

    private String id;
    private CategoriaRecurso categoria;
    private String descripcion;

    public Recurso(String id, CategoriaRecurso categoria, String descripcion) {
        this.id = id;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public Recurso() {
        this("", null, "");
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CategoriaRecurso getCategoria() { return categoria; }
    public void setCategoria(CategoriaRecurso categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return descripcion + " (" + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recurso)) return false;
        Recurso that = (Recurso) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
