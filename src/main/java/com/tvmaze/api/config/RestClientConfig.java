package com.tvmaze.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * Creates the REST client used to communicate with the TVMaze API.
     *
     * @param baseUrl TVMaze API base URL
     * @return configured TVMaze REST client
     */
    @Bean
    public RestClient tvMazeRestClient(
            @Value("${tvmaze.base-url}") String baseUrl) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}