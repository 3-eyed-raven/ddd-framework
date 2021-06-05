package net.jsrbc.adapter.common.handler.dto;

/**
 * 视图状态DTO
 * @author ZZZ on 2021/6/5 16:03
 * @version 1.0
 */
public class ViewStatusRequestDTO {

    /** 主键 */
    private final String id;

    /** 版本号 */
    private final Long version;

    /** 视图名 */
    private final String viewName;

    public ViewStatusRequestDTO(String id, Long version, String viewName) {
        this.id = id;
        this.version = version;
        this.viewName = viewName;
    }

    public String getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getViewName() {
        return viewName;
    }
}
