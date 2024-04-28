package net.asany.jfantasy.graphql.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

/**
 * 请求工厂
 *
 * <p>
 *
 * @author limaofeng
 */
public class RequestFactory {

  private RequestFactory() {}

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static HttpEntity<Object> forJson(String json, HttpHeaders headers) {
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(json, headers);
  }

  @SneakyThrows
  static HttpEntity<Object> forMultipart(
      String query, Map<String, Part> parts, HttpHeaders headers) {
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    LinkedMultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
    values.add("operations", query);

    Map<String, List<String>> map = new HashMap<>();
    Map<String, PartResource> partResources = new HashMap<>();

    for (Map.Entry<String, Part> entry : parts.entrySet()) {
      String key = String.valueOf(map.size() + 1);
      map.put(key, List.of(entry.getKey()));
      partResources.put(key, new PartResource(entry.getValue()));
    }

    values.add("map", objectMapper.writeValueAsString(map));

    for (Map.Entry<String, PartResource> entry : partResources.entrySet()) {
      values.add(entry.getKey(), entry.getValue());
    }

    return new HttpEntity<>(values, headers);
  }

  private static class PartResource extends InputStreamResource {

    private final Part part;

    public PartResource(Part part) throws IOException {
      super(part.getInputStream());
      this.part = part;
    }

    @Override
    public String getFilename() {
      return this.part.getSubmittedFileName();
    }

    @Override
    public long contentLength() {
      return this.part.getSize();
    }
  }
}
