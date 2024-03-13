package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 重新解析文件 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午12:08
 */
@Data
public class ReparseFileReq {

    @NotNull(message = "id 不能为空")
    private Integer id;

}
