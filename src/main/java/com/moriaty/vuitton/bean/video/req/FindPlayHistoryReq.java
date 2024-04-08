package com.moriaty.vuitton.bean.video.req;

import lombok.Data;

/**
 * <p>
 * 视频获取播放历史 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 下午10:30
 */
@Data
public class FindPlayHistoryReq {

    private Integer videoId;

    private Integer limitNum;

}
