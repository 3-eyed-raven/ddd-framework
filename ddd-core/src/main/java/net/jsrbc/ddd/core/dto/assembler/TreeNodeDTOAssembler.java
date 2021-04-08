package net.jsrbc.ddd.core.dto.assembler;

import net.jsrbc.ddd.core.dto.TreeNodeDTO;
import net.jsrbc.ddd.core.model.aggregate.Aggregate;
import net.jsrbc.ddd.core.model.aggregate.TreeAggregate;
import net.jsrbc.ddd.core.model.valueobject.TreeInfo;
import net.jsrbc.ddd.core.view.TreeView;
import net.jsrbc.ddd.core.view.View;

import java.util.*;
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
        return toTreeNodes(aggregates, TreeAggregate::getTreeInfo, Aggregate::getId, o -> null);
    }

    /**
     * 树形聚合转DTO，将原聚合作为负载对象
     * @param aggregates 聚合集合
     * @return DTO
     */
    public static <T extends TreeAggregate<?>> List<TreeNodeDTO> aggregateToDTOs(List<T> aggregates, Function<T, ?> payloadMapper) {
        return toTreeNodes(aggregates, TreeAggregate::getTreeInfo, Aggregate::getId, payloadMapper);
    }

    /**
     * 树形视图转DTO
     * @param treeViews 树形视图集合
     * @return DTO
     */
    public static List<TreeNodeDTO> viewToDTOs(List<? extends TreeView<?>> treeViews) {
        return toTreeNodes(treeViews, TreeView::getTreeInfo, View::getId, o -> null);
    }

    /**
     * 树形视图转DTO，将原聚合作为负载对象
     * @param treeViews 树形视图集合
     * @return DTO
     */
    public static <T extends TreeView<?>> List<TreeNodeDTO> viewToDTOs(List<T> treeViews, Function<T, ?> payloadMapper) {
        return toTreeNodes(treeViews, TreeView::getTreeInfo, View::getId, payloadMapper);
    }

    private static <T> List<TreeNodeDTO> toTreeNodes(List<T> list, Function<T, TreeInfo> treeInfoGetter, Function<T, String> keyGetter, Function<T, ?> payloadMapper) {
        // 1、按层级进行分组
        Map<Integer, List<T>> childrenMap = list
                .stream()
                .collect(Collectors.groupingBy(t -> Optional.ofNullable(treeInfoGetter.apply(t).getAncestorIds()).orElse(Collections.emptyList()).size()));
        // 2、获取最高层级，这里层级从0开始，遍历时忽略根节点
        int maxLevels = childrenMap.size();
        // 3、从根节点开始按照层级去组装
        List<TreeNodeDTO> root = toDTOs(childrenMap.get(0), keyGetter, payloadMapper);
        List<TreeNodeDTO> parentNodes = new ArrayList<>(root);
        for (int level = 1; level < maxLevels; level++) {
            List<TreeNodeDTO> nextParentNodes = new ArrayList<>();
            List<T> candidateChildren = childrenMap.get(level);
            for (TreeNodeDTO parentNode : parentNodes) {
                List<TreeNodeDTO> children = toDTOs(
                        candidateChildren
                                .stream()
                                .filter(t -> Objects.equals(treeInfoGetter.apply(t).getParentId(), parentNode.getKey()))
                                .collect(toList()),
                        keyGetter,
                        payloadMapper
                );
                nextParentNodes.addAll(children);
                parentNode.setChildren(children.isEmpty() ? null : children);
            }
            parentNodes = nextParentNodes;
        }
        // 4、返回根节点
        return root;
    }

    private static <T> List<TreeNodeDTO> toDTOs(List<T> list, Function<T, String> keyGetter, Function<T, ?> payloadMapper) {
        return Optional
                .ofNullable(list)
                .orElse(Collections.emptyList())
                .stream()
                .sorted()
                .map(t -> new TreeNodeDTO(keyGetter.apply(t), t.toString(), payloadMapper.apply(t)))
                .collect(toList());
    }

    private TreeNodeDTOAssembler() {}
}
