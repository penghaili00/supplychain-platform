package com.supplychain.common.web.filter;

import com.supplychain.common.core.constant.SupplyChainConstants;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TraceIdFilterTest {

    @Test
    void shouldGenerateTraceIdWhenMissing() throws Exception {
        TraceIdFilter filter = new TraceIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/demo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> traceIdSeenInChain = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> traceIdSeenInChain.set(MDC.get(SupplyChainConstants.MDC_TRACE_ID)));

        assertThat(traceIdSeenInChain.get()).hasSize(32);
        assertThat(response.getHeader(SupplyChainConstants.HEADER_TRACE_ID)).isEqualTo(traceIdSeenInChain.get());
        assertThat(MDC.get(SupplyChainConstants.MDC_TRACE_ID)).isNull();
    }

    @Test
    void shouldReuseIncomingTraceId() throws Exception {
        TraceIdFilter filter = new TraceIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/demo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(SupplyChainConstants.HEADER_TRACE_ID, "trace-from-client");
        AtomicReference<String> traceIdSeenInChain = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> traceIdSeenInChain.set(MDC.get(SupplyChainConstants.MDC_TRACE_ID)));

        assertThat(traceIdSeenInChain.get()).isEqualTo("trace-from-client");
        assertThat(response.getHeader(SupplyChainConstants.HEADER_TRACE_ID)).isEqualTo("trace-from-client");
        assertThat(MDC.get(SupplyChainConstants.MDC_TRACE_ID)).isNull();
    }
}
