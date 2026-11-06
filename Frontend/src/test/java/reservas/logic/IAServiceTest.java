package reservas.logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IAServiceTest {

    @Test
    void extraerSinConexionDebeUsarHeuristicoYNoFallar() {
        CategoriaRecurso sala = new CategoriaRecurso("CAT-000001", "Sala de Juntas");
        CategoriaRecurso laptop = new CategoriaRecurso("CAT-000002", "Laptop windows 11");

        String frase = "necesito hacer una reunion de trabajo de 10 personas el proximo 14 de agosto de 8am a 10am en una sala y usando una laptop";

        DatosExtraidosIA datos = new IAService().extraer(frase, List.of(sala, laptop));

        assertNotNull(datos);
        assertNotNull(datos.getHoraInicio());
        assertNotNull(datos.getHoraFin());
        assertTrue(datos.getDescripcionesCategorias().contains("Sala de Juntas"));
        assertTrue(datos.getDescripcionesCategorias().contains("Laptop windows 11"));
    }

    @Test
    void extraerConFraseVaciaNoDebeFallar() {
        DatosExtraidosIA datos = new IAService().extraer("", List.of());
        assertNotNull(datos);
        assertNull(datos.getFecha());
    }
}
