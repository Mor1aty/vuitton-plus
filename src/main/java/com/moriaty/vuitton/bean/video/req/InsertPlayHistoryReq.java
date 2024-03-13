package com.moriaty.vuitton.bean.video.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 视频插入播放历史 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 下午10:31
 */
@Data
public class InsertPlayHistoryReq {

    @NotNull(message = "videoId 不能为空")
    private Integer videoId;

    @NotNull(message = "episodeId 不能为空")
    private Integer episodeId;

}
