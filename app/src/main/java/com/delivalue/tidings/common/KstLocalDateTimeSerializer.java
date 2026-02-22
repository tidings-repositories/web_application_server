package com.delivalue.tidings.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class KstLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

	private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		String formatted = value.atOffset(ZoneOffset.UTC)
				.withOffsetSameInstant(KST_OFFSET)
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		gen.writeString(formatted);
	}
}
