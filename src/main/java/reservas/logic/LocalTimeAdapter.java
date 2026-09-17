package reservas.logic;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
    @Override
    public LocalTime unmarshal(String v) throws Exception {
        return (v == null || v.isEmpty()) ? null : LocalTime.parse(v);
    }

    @Override
    public String marshal(LocalTime v) throws Exception {
        return v == null ? null : v.toString();
    }
}
