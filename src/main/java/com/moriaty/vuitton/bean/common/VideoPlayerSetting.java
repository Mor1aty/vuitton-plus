package com.moriaty.vuitton.bean.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频播放器设置
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/8 下午6:23
 */
@Data
@Accessors(chain = true)
public class VideoPlayerSetting {

    private int group;

    private boolean skipOpEd;

    private String skipOpEdName;

    private boolean autoPlayNext;

    private String autoPlayNextName;

}
