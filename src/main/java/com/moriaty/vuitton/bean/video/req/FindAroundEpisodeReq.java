package com.moriaty.vuitton.bean.video.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 视频获取环绕分集 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 上午10:19
 */
@Data
public class FindAroundEpisodeReq {

    @NotNull(message = "videoId 不能为空")
    private Integer videoId;

    @NotNull(message = "episodeIndex 不能为空")
    private Integer episodeIndex;

}
