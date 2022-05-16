package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Getter;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.jfantasy.framework.search.exception.ElasticsearchConnectionException;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class ElasticsearchConnection {

  private final String hostname;
  private final int port;

  @Getter private ElasticsearchClient client = null;
  @Getter private ElasticsearchAsyncClient asyncClient = null;
  private RestClient restClient;
  private ElasticsearchTransport transport;

  private String sslCertificatePath;

  public ElasticsearchConnection(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  private JsonpMapper buildJsonpMapper() {
    JacksonJsonpMapper mapper = new JacksonJsonpMapper();
    mapper.objectMapper().setPropertyNamingStrategy(new ElasticPropertyNamingStrategy());
    return mapper;
  }

  public void connect(String username, String password) {
    try {
      SSLContext sslContext = this.sslContext();

      boolean ssl = sslContext != null;

      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(
          AuthScope.ANY, new UsernamePasswordCredentials(username, password));

      RestClientBuilder builder =
          RestClient.builder(new HttpHost(this.hostname, this.port, ssl ? "https" : "http"))
              .setHttpClientConfigCallback(
                  httpClientBuilder ->
                      httpClientBuilder
                          .setSSLContext(sslContext)
                          .setDefaultCredentialsProvider(credentialsProvider));

      this.restClient = builder.build();

      this.transport = new RestClientTransport(restClient, buildJsonpMapper());

      this.client = new ElasticsearchClient(transport);
      this.asyncClient = new ElasticsearchAsyncClient(transport);
    } catch (Exception e) {
      throw new ElasticsearchConnectionException("Elasticsearch 连接失败", e);
    }
  }

  public void connect(String apiKey) {
    try {
      SSLContext sslContext = this.sslContext();

      boolean ssl = sslContext != null;

      RestClientBuilder builder =
          RestClient.builder(new HttpHost(this.hostname, this.port, ssl ? "https" : "http"))
              .setHttpClientConfigCallback(
                  httpClientBuilder -> httpClientBuilder.setSSLContext(sslContext));

      Header[] defaultHeaders = new Header[] {new BasicHeader("Authorization", "ApiKey " + apiKey)};
      builder.setDefaultHeaders(defaultHeaders);

      this.restClient = builder.build();
      this.transport = new RestClientTransport(restClient, buildJsonpMapper());

      this.client = new ElasticsearchClient(transport);
      this.asyncClient = new ElasticsearchAsyncClient(transport);
    } catch (Exception e) {
      throw new ElasticsearchConnectionException("Elasticsearch 连接失败", e);
    }
  }

  private SSLContext sslContext()
      throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
          KeyManagementException {
    if (this.sslCertificatePath == null) {
      return null;
    }
    Path caCertificatePath = Paths.get(this.sslCertificatePath);
    CertificateFactory factory = CertificateFactory.getInstance("X.509");
    Certificate trustedCa;
    try (InputStream is = Files.newInputStream(caCertificatePath)) {
      trustedCa = factory.generateCertificate(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    KeyStore trustStore = KeyStore.getInstance("pkcs12");
    trustStore.load(null, null);
    trustStore.setCertificateEntry("ca", trustedCa);
    SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
    return sslContextBuilder.build();
  }

  public void setSslCertificatePath(String path) {
    this.sslCertificatePath = path;
  }

  public void close() throws IOException {
    if (this.restClient != null) {
      this.transport.close();
      this.restClient.close();
    }
  }
}
