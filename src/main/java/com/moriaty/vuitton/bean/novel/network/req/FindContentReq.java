package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 网络小说查询内容 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午2:50
 */
@Data
public class FindContentReq {

    @NotBlank(message = "downloaderMark 不能为空")
    private String downloaderMark;

    @NotBlank(message = "title 不能为空")
    private String title;

    @NotBlank(message = "contentUrl 不能为空")
    private String contentUrl;
}
