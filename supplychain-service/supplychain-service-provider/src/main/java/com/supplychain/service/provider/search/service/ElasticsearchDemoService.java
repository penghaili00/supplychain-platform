package com.supplychain.service.provider.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.service.api.dto.EsClusterInfoView;
import com.supplychain.service.api.dto.EsProductDocumentView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ElasticsearchDemoService {

    private static final String DEMO_INDEX = "demo_products";

    private final RestClient elasticsearchRestClient;
    private final ObjectMapper objectMapper;

    public EsClusterInfoView getClusterInfo() {
        Map<String, Object> info = getForMap("/");
        Map<String, Object> health = getForMap("/_cluster/health");
        return EsClusterInfoView.builder()
                .name(asString(info.get("name")))
                .clusterName(asString(info.get("cluster_name")))
                .clusterUuid(asString(info.get("cluster_uuid")))
                .version(asString(getNested(info, "version", "number")))
                .status(asString(health.get("status")))
                .build();
    }

    public void initializeDemoProducts() {
        if (!indexExists(DEMO_INDEX)) {
            createDemoIndex();
        }
        putDocument("1", EsProductDocumentView.builder()
                .id(1)
                .name("iPhone 16")
                .brand("Apple")
                .price(6999D)
                .createdAt("2026-04-17T10:00:00")
                .build());
        putDocument("2", EsProductDocumentView.builder()
                .id(2)
                .name("Mate 70")
                .brand("Huawei")
                .price(5999D)
                .createdAt("2026-04-17T10:05:00")
                .build());
        postForMap("/{index}/_refresh", Map.of(), DEMO_INDEX);
    }

    public List<EsProductDocumentView> listDemoProducts() {
        Map<String, Object> response = postForMap("/{index}/_search", Map.of(
                "query", Map.of("match_all", Map.of()),
                "sort", List.of(Map.of("price", Map.of("order", "desc")))
        ), DEMO_INDEX);
        Map<String, Object> hits = asMap(response.get("hits"));
        List<Map<String, Object>> documents = asListOfMap(hits.get("hits"));
        return documents.stream()
                .map(hit -> objectMapper.convertValue(hit.get("_source"), EsProductDocumentView.class))
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean indexExists(String indexName) {
        try {
            return Boolean.TRUE.equals(elasticsearchRestClient.head()
                    .uri("/{index}", indexName)
                    .exchange((request, response) -> response.getStatusCode().is2xxSuccessful()));
        } catch (RestClientException exception) {
            throw new BizException("检查 Elasticsearch 索引失败");
        }
    }

    private void createDemoIndex() {
        Map<String, Object> properties = Map.of(
                "id", Map.of("type", "integer"),
                "name", Map.of("type", "text"),
                "brand", Map.of("type", "keyword"),
                "price", Map.of("type", "double"),
                "createdAt", Map.of("type", "date")
        );
        putWithoutResponse("/{index}", Map.of(
                "mappings", Map.of("properties", properties)
        ), DEMO_INDEX);
    }

    private void putDocument(String id, EsProductDocumentView document) {
        putWithoutResponse("/{index}/_doc/{id}", document, DEMO_INDEX, id);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getForMap(String uri, Object... uriVariables) {
        try {
            Map<String, Object> response = elasticsearchRestClient.get()
                    .uri(uri, uriVariables)
                    .retrieve()
                    .body(Map.class);
            return response == null ? Map.of() : response;
        } catch (RestClientResponseException exception) {
            throw new BizException("调用 Elasticsearch 失败: " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BizException("调用 Elasticsearch 失败");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postForMap(String uri, Object body, Object... uriVariables) {
        try {
            Map<String, Object> response = elasticsearchRestClient.post()
                    .uri(uri, uriVariables)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return response == null ? Map.of() : response;
        } catch (RestClientResponseException exception) {
            throw new BizException("调用 Elasticsearch 失败: " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BizException("调用 Elasticsearch 失败");
        }
    }

    private void putWithoutResponse(String uri, Object body, Object... uriVariables) {
        try {
            elasticsearchRestClient.put()
                    .uri(uri, uriVariables)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new BizException("写入 Elasticsearch 失败: " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BizException("写入 Elasticsearch 失败");
        }
    }

    @SuppressWarnings("unchecked")
    private Object getNested(Map<String, Object> source, String key, String nestedKey) {
        Object nested = source.get(key);
        if (!(nested instanceof Map<?, ?> nestedMap)) {
            return null;
        }
        return ((Map<String, Object>) nestedMap).get(nestedKey);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asListOfMap(Object value) {
        return value instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
