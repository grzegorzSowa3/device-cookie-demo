package pl.recompiled.devicecookiedemo.security.devicecookie;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ListOfLongsConverter implements AttributeConverter<List<Long>, String> {

    @Override
    public String convertToDatabaseColumn(List<Long> longs) {
        return longs.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Long> convertToEntityAttribute(String s) {
        if (s.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(s.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

}
