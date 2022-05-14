package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
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
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

public class ElasticsearchJava {

  private static ElasticsearchClient client = null;
  private static ElasticsearchAsyncClient asyncClient = null;

  private static synchronized void makeConnection()
      throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,
          KeyManagementException {
    Path caCertificatePath = Paths.get("/Users/limaofeng/Downloads/ca/ca.crt");
    CertificateFactory factory = CertificateFactory.getInstance("X.509");
    Certificate trustedCa;
    try (InputStream is = Files.newInputStream(caCertificatePath)) {
      trustedCa = factory.generateCertificate(is);
    }
    KeyStore trustStore = KeyStore.getInstance("pkcs12");
    trustStore.load(null, null);
    trustStore.setCertificateEntry("ca", trustedCa);
    SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
    final SSLContext sslContext = sslContextBuilder.build();

    RestClientBuilder builder =
        RestClient.builder(new HttpHost("es.thuni-h.com", 9200, "https"))
            .setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setSSLContext(sslContext));

    Header[] defaultHeaders =
        new Header[] {
          new BasicHeader(
              "Authorization",
              "ApiKey RWpsR3dvQUJINEtJY2FvWGJGMXg6QkNXZzJ0X01UY2VocFNBQUVXNGExQQ==")
        };
    builder.setDefaultHeaders(defaultHeaders);

    RestClient restClient = builder.build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport =
        new RestClientTransport(restClient, new JacksonJsonpMapper());

    client = new ElasticsearchClient(transport);
    asyncClient = new ElasticsearchAsyncClient(transport);
  }

  public static void main(String[] args)
      throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException,
          KeyManagementException {
    makeConnection();

    // Index data to an index products
    Product product = new Product("abc", "Bag", 42);

    IndexRequest<Object> indexRequest =
        new IndexRequest.Builder<>().index("products").id("abc").document(product).build();

    client.index(indexRequest);

    Product product1 = new Product("efg", "Bag", 42);

    client.index(builder -> builder.index("products").id(product1.getId()).document(product1));

    // Search for a data
    TermQuery query = QueryBuilders.term().field("name").value("bag").build();

    SearchRequest request =
        new SearchRequest.Builder().index("products").query(query._toQuery()).build();

    SearchResponse<Product> search = client.search(request, Product.class);

    for (Hit<Product> hit : search.hits().hits()) {
      Product pd = hit.source();
      System.out.println(pd);
    }

    // Match search
    String searchText = "bag";
    SearchResponse<Product> response1 =
        client.search(
            s -> s.index("products").query(q -> q.match(t -> t.field("name").query(searchText))),
            Product.class);

    TotalHits total1 = response1.hits().total();
    assert total1 != null;
    boolean isExactResult = total1.relation() == TotalHitsRelation.Eq;

    if (isExactResult) {
      System.out.println("There are " + total1.value() + " results");
    } else {
      System.out.println("There are more than " + total1.value() + " results");
    }

    List<Hit<Product>> hits1 = response1.hits().hits();
    for (Hit<Product> hit : hits1) {
      Product pd2 = hit.source();
      assert pd2 != null;
      System.out.println("Found product " + pd2.getId() + ", score " + hit.score());
    }

    // Term search
    SearchResponse<Product> search1 =
        client.search(
            s ->
                s.index("products")
                    .query(q -> q.term(t -> t.field("name").value(v -> v.stringValue("bag")))),
            Product.class);

    for (Hit<Product> hit : search1.hits().hits()) {
      Product pd = hit.source();
      System.out.println(pd);
    }

    // Splitting complex DSL
    TermQuery termQuery = TermQuery.of(t -> t.field("name").value("bag"));

    SearchResponse<Product> search2 =
        client.search(s -> s.index("products").query(termQuery._toQuery()), Product.class);

    for (Hit<Product> hit : search2.hits().hits()) {
      Product pd = hit.source();
      System.out.println(pd);
    }

    // Search by product name
    Query byName = MatchQuery.of(m -> m.field("name").query("bag"))._toQuery();

    // Search by max price
    Query byMaxPrice = RangeQuery.of(r -> r.field("price").gte(JsonData.of(10)))._toQuery();

    // Combine name and price queries to search the product index
    SearchResponse<Product> response =
        client.search(
            s -> s.index("products").query(q -> q.bool(b -> b.must(byName).should(byMaxPrice))),
            Product.class);

    List<Hit<Product>> hits = response.hits().hits();
    for (Hit<Product> hit : hits) {
      Product product2 = hit.source();
      assert product2 != null;
      System.out.println("Found product " + product2.getId() + ", score " + hit.score());
    }

    // Creating aggregations
    SearchResponse<Void> search3 =
        client.search(
            b ->
                b.index("products")
                    .size(0)
                    .aggregations(
                        "price-histo", a -> a.histogram(h -> h.field("price").interval(20.0))),
            Void.class);

    long firstBucketCount =
        search3.aggregations().get("price-histo").histogram().buckets().array().get(0).docCount();

    System.out.println("doc count: " + firstBucketCount);

    ClosePointInTimeResponse closeResponse =
        client.closePointInTime(ClosePointInTimeRequest.of((builder) -> builder.id("")));
  }
}
