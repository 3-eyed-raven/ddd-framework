package net.jsrbc.adapter.common;

import net.jsrbc.adapter.common.handler.ViewStatusHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 *  视图状态适配器配置
 * @author ZZZ on 2021/6/5 16:28
 * @version 1.0
 */
@Configuration
public class ViewStatusAdapterConfigurer {

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public ViewStatusHandler viewStatusHandler(ReactiveMongoOperations reactiveMongoOperations) {
        return new ViewStatusHandler(reactiveMongoOperations);
    }

    @Bean
    public HandlerMapping viewStatusHandlerMapping(ViewStatusHandler viewStatusHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/view/status", viewStatusHandler);
        return new SimpleUrlHandlerMapping(map, -1);
    }
}
