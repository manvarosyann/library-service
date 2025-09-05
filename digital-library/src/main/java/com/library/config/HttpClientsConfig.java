package com.library.config;

import com.library.client.AuthorClient;
import com.library.client.SectionClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientsConfig {
    @Bean
    @LoadBalanced
    RestClient.Builder lbRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    RestClient authorsRestClient(@Qualifier("lbRestClientBuilder") RestClient.Builder lb,
                                 ClientsProperties props) {
        return lb.baseUrl(props.getAuthors().getBaseUrl()).build();
    }

    @Bean
    RestClient sectionRestClient(@Qualifier("lbRestClientBuilder") RestClient.Builder lb,
                                 ClientsProperties props) {
        return lb.baseUrl(props.getSections().getBaseUrl()).build();
    }

    @Bean
    AuthorClient authorClient(@Qualifier("authorsRestClient") RestClient authorsRestClient) {
        var f = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(authorsRestClient)).build();
        return f.createClient(AuthorClient.class);
    }

    @Bean
    SectionClient sectionClient(@Qualifier("sectionRestClient") RestClient sectionsRestClient) {
        var f = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(sectionsRestClient)).build();
        return f.createClient(SectionClient.class);
    }
}

