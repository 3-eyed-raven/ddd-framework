package net.jsrbc.adapter.common.handler;

import net.jsrbc.adapter.common.handler.dto.ViewStatusDTO;
import net.jsrbc.adapter.common.handler.dto.ViewStatusRequestDTO;
import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import net.jsrbc.ddd.core.utils.JsonUtils;
import net.jsrbc.ddd.core.view.View;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

/**
 * 视图状态处理
 * @author ZZZ on 2021/6/5 16:19
 * @version 1.0
 */
public class ViewStatusHandler implements WebSocketHandler {

    private final ReactiveMongoOperations reactiveMongoOperations;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> output = session
                .receive()
                .map(m -> JsonUtils.toObject(m.getPayloadAsText(), ViewStatusRequestDTO.class))
                .flatMap(d -> this.reactiveMongoOperations
                        .exists(query(where("id").is(d.getId())
                                        .and(Aggregate.VERSION_KEY).gte(d.getVersion())),
                                View.class,
                                d.getViewName())
                        .map(ViewStatusDTO::new))
                .map(d -> session.textMessage(JsonUtils.toJson(d)));
        return session.send(output);
    }

    public ViewStatusHandler(ReactiveMongoOperations reactiveMongoOperations) {
        this.reactiveMongoOperations = reactiveMongoOperations;
    }
}
