package net.jsrbc.ddd.core.dto.assembler;

import net.jsrbc.ddd.core.dto.TreeNodeDTO;
import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import net.jsrbc.ddd.core.model.aggregate.TreeAggregate;
import net.jsrbc.ddd.core.model.valueobject.TreeInfo;
import net.jsrbc.ddd.core.view.TreeView;
import net.jsrbc.ddd.core.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 树结构装配器
 * @author ZZZ on 2021/3/19 15:17
 * @version 1.0
 */
public final class TreeNodeDTOAssembler {

    /**
     * 树形聚合转DTO
     * @param aggregates 聚合集合
     * @return DTO
     */
    public static List<TreeNodeDTO> aggregateToDTOs(List<? extends TreeAggregate<?>> aggregates) {
        return aggregateToDTOs(aggregates, m -> {});
    }

    /**
     * 树形聚合转DTO
     * @param aggregates 聚合集合
     * @param payloadConsumer 负载消费, 可以通过这个消费增加需要传输的项
     * @return DTO
     */
    public static List<TreeNodeDTO> aggregateToDTOs(List<? extends TreeAggregate<?>> aggregates, Consumer<Map<String, Object>> payloadConsumer) {
        return toTreeNodes(aggregates, TreeAggregate::getTreeInfo, Aggregate::getId, payloadConsumer);
    }

    /**
     * 树形视图转DTO
     * @param treeViews 树形视图集合
     * @return DTO
     */
    public static List<TreeNodeDTO> viewToDTOs(List<? extends TreeView<?>> treeViews) {
        return viewToDTOs(treeViews, m -> {});
    }

    /**
     * 树形视图转DTO
     * @param treeViews 树形视图集合
     * @param payloadConsumer 负载消费, 可以通过这个消费增加需要传输的项
     * @return DTO
     */
    public static List<TreeNodeDTO> viewToDTOs(List<? extends TreeView<?>> treeViews, Consumer<Map<String, Object>> payloadConsumer) {
        return toTreeNodes(treeViews, TreeView::getTreeInfo, View::getId, payloadConsumer);
    }

    private static <T> List<TreeNodeDTO> toTreeNodes(List<T> list, Function<T, TreeInfo> treeInfoGetter, Function<T, String> keyGetter, Consumer<Map<String, Object>> payloadConsumer) {
        // 1、获取根节点
        List<TreeNodeDTO> roots = list
                .stream()
                .filter(t -> treeInfoGetter.apply(t).getParentId() == null)
                .map(t -> new TreeNodeDTO(keyGetter.apply(t), t.toString()))
                .peek(t -> payloadConsumer.accept(t.getPayload()))
                .collect(Collectors.toList());
        // 2、获取最高层级
        int maxLevels = list.stream().mapToInt(t -> treeInfoGetter.apply(t).getAncestorIds().size()).max().orElse(0);
        // 3、按照最大层级去组装
        List<TreeNodeDTO> parentNodes = new ArrayList<>(roots);
        for (int level = 0; level < maxLevels; level++) {
            for (TreeNodeDTO parentNode : parentNodes) {
                List<TreeNodeDTO> children = list
                        .stream()
                        .filter(t -> Objects.equals(treeInfoGetter.apply(t).getParentId(), parentNode.getKey()))
                        .sorted()
                        .map(t -> new TreeNodeDTO(keyGetter.apply(t), t.toString()))
                        .peek(t -> payloadConsumer.accept(t.getPayload()))
                        .collect(toList());
                parentNode.setChildren(children.isEmpty() ? null : children);
            }
        }
        // 4、返回根节点
        return roots;
    }

    private TreeNodeDTOAssembler() {}
}
