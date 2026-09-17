package reservas.logic;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {

    @XmlID
    private String id;

    private String actividad;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFin;

    @XmlIDREF
    private Funcionario funcionario;

    private EstadoReserva estado;

    @XmlIDREF
    @XmlElementWrapper(name = "categoriasSolicitadas")
    @XmlElement(name = "categoria")
    private List<CategoriaRecurso> categoriasSolicitadas;

    @XmlIDREF
    @XmlElementWrapper(name = "recursosAsignados")
    @XmlElement(name = "recurso")
    private List<Recurso> recursosAsignados;

    public Reserva(String id, String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                    Funcionario funcionario, EstadoReserva estado) {
        this.id = id;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.funcionario = funcionario;
        this.estado = estado;
        this.categoriasSolicitadas = new ArrayList<>();
        this.recursosAsignados = new ArrayList<>();
    }

    public Reserva() {
        this("", "", null, null, null, null, null);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public List<CategoriaRecurso> getCategoriasSolicitadas() { return categoriasSolicitadas; }
    public void setCategoriasSolicitadas(List<CategoriaRecurso> categoriasSolicitadas) { this.categoriasSolicitadas = categoriasSolicitadas; }

    public List<Recurso> getRecursosAsignados() { return recursosAsignados; }
    public void setRecursosAsignados(List<Recurso> recursosAsignados) { this.recursosAsignados = recursosAsignados; }
}
