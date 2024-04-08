package com.moriaty.vuitton.bean.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设置信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/8 下午6:22
 */
@Data
@Accessors(chain = true)
public class SettingInfo {

    private VideoPlayerSetting videoPlayer;

}
