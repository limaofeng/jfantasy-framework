package net.asany.jfantasy.framework.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Date;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

public class DateSerializer extends JsonSerializer<Date> {

  private String dateFormat;

  public DateSerializer(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  @Override
  public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeString(
        StringUtil.isNotBlank(dateFormat)
            ? DateUtil.format(value, dateFormat)
            : provider.getConfig().getDateFormat().format(value));
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }
}
