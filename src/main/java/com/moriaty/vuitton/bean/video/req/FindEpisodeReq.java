package com.moriaty.vuitton.bean.video.req;

import com.moriaty.vuitton.bean.PageReq;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 视频获取分集 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 上午10:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FindEpisodeReq extends PageReq {

    @NotNull(message = "videoId 不能为空")
    private Integer videoId;

}
