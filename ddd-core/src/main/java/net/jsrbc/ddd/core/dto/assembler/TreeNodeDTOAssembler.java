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
        // 1、获取根节点
        List<TreeNodeDTO> roots = list
                .stream()
                .filter(t -> treeInfoGetter.apply(t).getParentId() == null)
                .map(t -> new TreeNodeDTO(keyGetter.apply(t), t.toString(), payloadMapper.apply(t)))
                .collect(Collectors.toList());
        // 2、获取最高层级
        int maxLevels = list.stream().mapToInt(t -> Optional.ofNullable(treeInfoGetter.apply(t).getAncestorIds()).map(List::size).orElse(0)).max().orElse(0);
        // 3、按照最大层级去组装
        List<TreeNodeDTO> parentNodes = new ArrayList<>(roots);
        for (int level = 0; level < maxLevels; level++) {
            for (TreeNodeDTO parentNode : parentNodes) {
                List<TreeNodeDTO> children = list
                        .stream()
                        .filter(t -> Objects.equals(treeInfoGetter.apply(t).getParentId(), parentNode.getKey()))
                        .sorted()
                        .map(t -> new TreeNodeDTO(keyGetter.apply(t), t.toString(), payloadMapper.apply(t)))
                        .collect(toList());
                parentNode.setChildren(children.isEmpty() ? null : children);
            }
        }
        // 4、返回根节点
        return roots;
    }

    private TreeNodeDTOAssembler() {}
}
