package com.moriaty.vuitton.bean.video.req;

import com.moriaty.vuitton.bean.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 视频获取 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 上午10:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FindVideoReq extends PageReq {

    private String name;

}
