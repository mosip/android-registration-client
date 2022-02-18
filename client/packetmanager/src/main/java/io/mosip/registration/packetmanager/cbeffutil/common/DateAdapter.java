package io.mosip.registration.packetmanager.cbeffutil.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, LocalDateTime> {

	@Override
	public LocalDateTime unmarshal(String v) throws Exception {
		ZonedDateTime parse = ZonedDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME)
				.withZoneSameInstant(ZoneId.of("UTC"));
		LocalDateTime locale = parse.toLocalDateTime();
		return locale;
	}

	@Override
	public String marshal(LocalDateTime v) throws Exception {
		return v.toInstant(ZoneOffset.UTC).toString();
	}

}