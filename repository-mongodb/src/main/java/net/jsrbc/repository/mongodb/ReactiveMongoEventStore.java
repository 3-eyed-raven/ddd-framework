package net.jsrbc.repository.mongodb;

import net.jsrbc.ddd.core.event.EventStore;
import net.jsrbc.ddd.core.event.DomainEvent;
import net.jsrbc.ddd.core.event.EventSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;

import static net.jsrbc.ddd.core.event.EventStatus.*;
import static net.jsrbc.ddd.core.event.EventStatus.SUCCESS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * 响应式Mongo仓库
 * @author ZZZ on 2021/5/26 20:15
 * @version 1.0
 */
@Configuration
public class ReactiveMongoEventStore extends EventStore {

    /** 日志记录 */
    private final static Logger LOGGER = LogManager.getLogger();

    /** 错误处理 */
    private final static Consumer<? super Throwable> ERROR_HANDLER = LOGGER::error;

    /** 数据操作 */
    private final ReactiveMongoOperations reactiveMongoOperations;

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @PreDestroy
    public void close() {
        super.close();
    }

    @Override
    protected Consumer<? super Throwable> errorHandler() {
        return ERROR_HANDLER;
    }

    @Override
    protected DomainEvent getAndMarkSending(Long deliveryTime) {
        return this.reactiveMongoOperations.findAndModify(
                query(where(EVENT_STATUS_FIELD).is(PENDING)),
                update(EVENT_STATUS_FIELD, SENDING).set(DELIVERY_TIME, deliveryTime),
                DomainEvent.class,
                EVENT_STORE).block();
    }

    @Override
    protected void markPending(DomainEvent event) {
        this.reactiveMongoOperations.updateMulti(query(where("id").is(event.getId())),
                update(EVENT_STATUS_FIELD, PENDING),
                DomainEvent.class,
                EVENT_STORE).block();
    }

    @Override
    protected void markSuccess(DomainEvent event) {
        this.reactiveMongoOperations.updateMulti(query(where("id").is(event.getId())),
                update(EVENT_STATUS_FIELD, SUCCESS),
                DomainEvent.class,
                EVENT_STORE).block();
    }

    @Override
    protected void markSendingTimeoutToPending(Long deliveryExpiredTime) {
        this.reactiveMongoOperations.updateMulti(
                query(where(EVENT_STATUS_FIELD).is(SENDING).and(DELIVERY_TIME).lt(deliveryExpiredTime)),
                update(EVENT_STATUS_FIELD, PENDING),
                DomainEvent.class,
                EVENT_STORE).block();
    }

    @Override
    protected void deleteSuccessEvent() {
        this.reactiveMongoOperations.remove(where(EVENT_STATUS_FIELD).is(SUCCESS), EVENT_STORE).block();
    }

    @Override
    protected void insertEvent(DomainEvent event) {
        this.reactiveMongoOperations.insert(event, EVENT_STORE).block();
    }

    public ReactiveMongoEventStore(EventSender eventSender, ReactiveMongoOperations reactiveMongoOperations) {
        super(eventSender);
        this.reactiveMongoOperations = reactiveMongoOperations;
    }
}
