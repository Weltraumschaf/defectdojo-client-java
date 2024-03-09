// SPDX-FileCopyrightText: the secureCodeBox authors
//
// SPDX-License-Identifier: Apache-2.0
package io.securecodebox.persistence.defectdojo.service;

import io.securecodebox.persistence.defectdojo.model.Endpoint;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Tests for {@link EndpointService}
 */
final class EndpointServiceTest extends WireMockBaseTestCase {
  private final EndpointService sut = new EndpointService(conf());
  private final Endpoint[] expectedFromSearch = new Endpoint[]{
    Endpoint.builder()
      .id(956)
      .protocol("tcp")
      .host("10.0.0.1")
      .port(80)
      .product(320)
      .build(),
    Endpoint.builder()
      .id(957)
      .protocol("tcp")
      .host("10.0.0.1")
      .port(443)
      .product(320)
      .build(),
    Endpoint.builder()
      .id(961)
      .protocol("tcp")
      .host("10.0.0.2")
      .port(80)
      .product(323)
      .build(),
    Endpoint.builder()
      .id(962)
      .protocol("tcp")
      .host("10.0.0.2")
      .port(443)
      .product(323)
      .build(),
    Endpoint.builder()
      .id(893)
      .protocol("tcp")
      .host("10.0.0.3")
      .port(443)
      .product(296)
      .build()};

  @Test
  void search() throws URISyntaxException, IOException {
    final var response = readFixtureFile("EndpointService_response_fixture.json");
    stubFor(
      get("/api/v2/endpoints/?offset=0&limit=100")
        .willReturn(
          ok()
            .withHeaders(responseHeaders(response.length()))
            .withBody(response)
        )
    );

    final var result = sut.search();

    assertAll(
      () -> assertThat(result, hasSize(5)),
      () -> assertThat(result, containsInAnyOrder(expectedFromSearch))
    );
  }

  @Test
  void search_withQueryParams() throws URISyntaxException, IOException {
    final var response = readFixtureFile("EndpointService_response_fixture.json");
    stubFor(
      get("/api/v2/endpoints/?limit=100&bar=42&offset=0&foo=23")
        .willReturn(
          ok()
            .withHeaders(responseHeaders(response.length()))
            .withBody(response)
        )
    );
    final var params = new HashMap<String, Object>();
    params.put("foo", 23);
    params.put("bar", 42);

    final var result = sut.search(params);

    assertAll(
      () -> assertThat(result, hasSize(5)),
      () -> assertThat(result, containsInAnyOrder(expectedFromSearch))
    );
  }

  @Test
  void get_byId() {
    final var response = """
      {
        "id": 42,
        "tags": [],
        "protocol": "tcp",
        "userinfo": null,
        "host": "www.owasp.org",
        "port": 443,
        "path": null,
        "query": null,
        "fragment": null,
        "product": 285,
        "endpoint_params": [],
        "findings": [
          34706,
          34684,
          34679,
          34677
        ],
        "prefetch": {}
      }
      """;
    stubFor(
      get("/api/v2/endpoints/42")
        .willReturn(
          ok()
            .withHeaders(responseHeaders(response.length()))
            .withBody(response)
        )
    );
    final var expected = Endpoint.builder()
      .id(42)
      .protocol("tcp")
      .host("www.owasp.org")
      .port(443)
      .product(285)
      .build();

    final var result = sut.get(42L);

    assertThat(result, is(expected));
  }


  @Test
  @Disabled("TODO: Implement test.")
  void searchUnique_withSearchObject() {
  }

  @Test
  @Disabled("TODO: Implement test.")
  void searchUnique_withQueryParams() {
  }

  @Test
  @Disabled("TODO: Implement test.")
  void create() {
  }

  @Test
  @Disabled("TODO: Implement test.")
  void delete() {
  }

  @Test
  @Disabled("TODO: Implement test.")
  void update() {
  }
}
