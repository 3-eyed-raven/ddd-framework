package net.jsrbc.adapter.common.handler.dto;

/**
 * 视图状态DTO
 * @author ZZZ on 2021/6/5 16:03
 * @version 1.0
 */
public class ViewStatusDTO {

    /** 是否已经更新 */
    private final Boolean isUpdated;

    public ViewStatusDTO(Boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public Boolean getUpdated() {
        return isUpdated;
    }
}
