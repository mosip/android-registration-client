package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import org.simpleframework.xml.transform.Transform;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTransformer implements Transform<LocalDateTime> {
    @Override
    public LocalDateTime read(String value) throws Exception {
        ZonedDateTime parse = ZonedDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)
                .withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime locale = parse.toLocalDateTime();
        return locale;
    }

    @Override
    public String write(LocalDateTime value) throws Exception {
        if(value == null) { return ""; }
        return value.toInstant(ZoneOffset.UTC).toString();
    }
}
