package com.moriaty.vuitton.bean;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 分页 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/2 下午12:44
 */
@Data
public class PageReq {

    @NotNull(message = "pageNum 不能为空")
    @Min(value = 1, message = "pageNum 最小为 1")
    private Integer pageNum;

    @NotNull(message = "pageSize 不能为空")
    @Min(value = 1, message = "pageSize 最小为 1")
    private Integer pageSize;

}
