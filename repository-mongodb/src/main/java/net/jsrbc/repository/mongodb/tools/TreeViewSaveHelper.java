package net.jsrbc.repository.mongodb.tools;

import net.jsrbc.ddd.core.view.TreeView;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

/**
 * 树节点视图保存助手
 * @author ZZZ on 2021/5/27 10:04
 * @version 1.0
 */
public class TreeViewSaveHelper {

    private final ReactiveViewMongoOperations reactiveViewMongoOperations;

    /**
     * 保存树节点视图
     * @param treeView 树节点视图
     */
    public Mono<Void> saveAndUpdateAncestor(TreeView<?> treeView) {
        return updateAncestorIds(treeView)
                .doOnNext(t -> this.reactiveViewMongoOperations
                        .findById(t.getId(), t.getClass())
                        .filter(o -> !Objects.equals(o.getParentId(), t.getParentId())) // 先判断父节点有无修改，有修改先更新子节点的祖先元素
                        .flatMapMany(o -> this.changeSubNodeAncestorIds(t))
                        .blockLast())
                .flatMap(this.reactiveViewMongoOperations::save);
    }

    public TreeViewSaveHelper(ReactiveViewMongoOperations reactiveViewMongoOperations) {
        this.reactiveViewMongoOperations = reactiveViewMongoOperations;
    }

    /** 更新祖先节点 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Mono<TreeView<?>> updateAncestorIds(TreeView<?> treeView) {
        return Mono.defer(() -> { // 然后进行保存
            if (treeView.getParentId() != null) {
                return this.reactiveViewMongoOperations
                        .findById(treeView.getParentId(), treeView.getClass())
                        .map(m -> {
                            List<String> ancestorIds = new ArrayList<>(m.getAncestorIds());
                            ancestorIds.add(m.getId());
                            treeView.setAncestorIds(ancestorIds);
                            return treeView;
                        });
            } else {
                treeView.setAncestorIds(Collections.emptyList());
                return Mono.just(treeView);
            }
        });
    }

    /** 修改子节点的祖先节点 */
    @SuppressWarnings({"unchecked"})
    private Flux<Void> changeSubNodeAncestorIds(TreeView<?> treeView) {
        return this.reactiveViewMongoOperations
                .find(query(where(TreeView.ANCESTOR_KEY).is(treeView.getId())), treeView.getClass())
                .flatMap(m -> {
                    List<String> ancestorIds = new ArrayList<>(treeView.getAncestorIds());
                    int index = m.getAncestorIds().indexOf(treeView.getId());
                    ancestorIds.addAll(m.getAncestorIds().subList(index, m.getAncestorIds().size()));
                    return this.reactiveViewMongoOperations
                            .update(query(where("id").is(m.getId())), Update.update(TreeView.ANCESTOR_KEY, ancestorIds), treeView.getClass())
                            .then();
                });
    }
}
