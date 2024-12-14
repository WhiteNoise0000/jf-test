package jp.whitenoise.jfnotify;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 小数点切り捨て.<BR>
 */
public class DoubleToInteger extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // CosmosDBがSUMの結果を小数点付きで返却するため、小数を切り捨て
        return (int) p.getDoubleValue();
    }
}