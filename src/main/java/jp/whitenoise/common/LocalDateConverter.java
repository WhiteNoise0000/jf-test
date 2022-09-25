package jp.whitenoise.common;

import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, java.sql.Date> {

    @Override
    public java.sql.Date convertToDatabaseColumn(LocalDate localDateTime) {
        return (localDateTime == null ? null : java.sql.Date.valueOf(localDateTime));
    }

    @Override
    public LocalDate convertToEntityAttribute(java.sql.Date date) {
        return (date == null ? null : date.toLocalDate());
    }
}
