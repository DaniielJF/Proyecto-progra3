package reservas.logic;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Utilidad de fechas compartida por Backend y Frontend (no requiere red). */
public final class Fechas {
    private Fechas() { }

    public static LocalDate lunesDe(LocalDate fecha) {
        return fecha.with(DayOfWeek.MONDAY);
    }
}
