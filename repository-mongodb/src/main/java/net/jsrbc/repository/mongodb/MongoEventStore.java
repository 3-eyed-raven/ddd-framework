package net.jsrbc.repository.mongodb;

import net.jsrbc.ddd.core.model.event.AbstractEventStore;
import net.jsrbc.ddd.core.model.event.DomainEvent;
import net.jsrbc.ddd.core.model.event.EventSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;

import static net.jsrbc.ddd.core.model.event.EventStatus.*;
import static net.jsrbc.ddd.core.model.event.EventStatus.SUCCESS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * mongodb版事件存储
 * @author ZZZ on 2021-03-24 16:38
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(EventSender.class)
public class MongoEventStore extends AbstractEventStore {

    /** 日志记录 */
    private final static Logger LOGGER = LogManager.getLogger();

    /** 错误处理 */
    private final static Consumer<? super Throwable> ERROR_HANDLER = LOGGER::error;

    /** 数据操作 */
    private final MongoOperations mongoOperations;

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
        return this.mongoOperations.findAndModify(
                query(where(EVENT_STATUS_FIELD).is(PENDING)),
                update(EVENT_STATUS_FIELD, SENDING).set(DELIVERY_TIME, deliveryTime),
                DomainEvent.class,
                EVENT_STORE);
    }

    @Override
    protected void markPending(DomainEvent event) {
        this.mongoOperations.updateMulti(query(where("id").is(event.getId())),
                update(EVENT_STATUS_FIELD, PENDING),
                DomainEvent.class,
                EVENT_STORE);
    }

    @Override
    protected void markSuccess(DomainEvent event) {
        this.mongoOperations.updateMulti(query(where("id").is(event.getId())),
                update(EVENT_STATUS_FIELD, SUCCESS),
                DomainEvent.class,
                EVENT_STORE);
    }

    @Override
    protected void markSendingTimeoutToPending(Long deliveryExpiredTime) {
        this.mongoOperations.updateMulti(
                query(where(EVENT_STATUS_FIELD).is(SENDING).and(DELIVERY_TIME).lt(deliveryExpiredTime)),
                update(EVENT_STATUS_FIELD, PENDING),
                DomainEvent.class,
                EVENT_STORE);
    }

    @Override
    protected void deleteSuccessEvent() {
        this.mongoOperations.remove(where(EVENT_STATUS_FIELD).is(SUCCESS), EVENT_STORE);
    }

    @Override
    protected void insertEvent(DomainEvent event) {
        mongoOperations.insert(event, EVENT_STORE);
    }

    public MongoEventStore(EventSender eventSender, MongoOperations mongoOperations) {
        super(eventSender);
        this.mongoOperations = mongoOperations;
    }
}
