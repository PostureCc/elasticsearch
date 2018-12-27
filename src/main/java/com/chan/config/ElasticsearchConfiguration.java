package com.chan.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Configuration
public class ElasticsearchConfiguration {

    private static TransportClient client;

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    @Bean
    public TransportClient client() throws Exception {
        try {
            if (null == client) {
                synchronized (this) {
                    if (null == client) {
                        client = new PreBuiltTransportClient(Settings.EMPTY)
                                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
                    }
                }
            }
            logger.info("threadID:{}", Thread.currentThread().getId());
            return client;
        } catch (UnknownHostException e) {
            logger.info(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }


    @Bean
    public ElasticsearchOperations getElasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }

    //Embedded Elasticsearch Server
    /*@Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
    }*/
}