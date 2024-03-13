package com.moriaty.vuitton.bean.novel.network;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网络小说信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午1:52
 */
@Data
@Accessors(chain = true)
public class NovelNetworkInfo {

    private String name;

    private String author;

    private String intro;

    private String imgUrl;

    private String downloaderMark;

    private String downloaderCatalogueUrl;

}
