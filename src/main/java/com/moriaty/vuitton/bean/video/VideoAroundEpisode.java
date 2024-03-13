package com.moriaty.vuitton.bean.video;

import com.moriaty.vuitton.dao.model.VideoEpisode;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频环绕分集
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 上午10:20
 */
@Data
@Accessors(chain = true)
public class VideoAroundEpisode {

    private VideoEpisode episode;

    private VideoEpisode preEpisode;

    private VideoEpisode nextEpisode;

}
