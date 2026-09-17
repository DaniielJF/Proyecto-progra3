package reservas.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ReservaExtractorService {

    @SystemMessage("""
            Eres un asistente especializado en extraer información de reservas de espacios y recursos.
            Reglas estrictas:
            1. La fecha siempre debe estar en formato ISO yyyy-MM-dd. Si el usuario no indica el año,
               usa el año actual o el siguiente si la fecha ya pasó.
            2. Las horas deben estar en formato HH:mm de 24 horas.
            3. Para categoriasRecurso solo puedes usar nombres que aparezcan EXACTAMENTE en la lista
               de categorías disponibles proporcionada. No inventes categorías nuevas.
            4. Si algún campo no está en la frase, devuelve null para ese campo.
            """)
    @UserMessage("""
            Fecha de referencia (hoy): {{hoy}}
            Categorías de recurso disponibles en el sistema:
            {{categorias}}
            Frase del usuario:
            "{{frase}}"
            Extrae los datos de la reserva seleccionando las categorías que mejor correspondan
            a lo que el usuario describe.
            """)
    ReservaExtraccion extraer(@V("frase") String frase, @V("categorias") String categoriasList,
                               @V("hoy") String fechaHoy);
}
