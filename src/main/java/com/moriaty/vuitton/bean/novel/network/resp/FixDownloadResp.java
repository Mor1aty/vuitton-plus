package com.moriaty.vuitton.bean.novel.network.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 网络小说修补下载 Resp
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午9:04
 */
@Data
@Accessors(chain = true)
public class FixDownloadResp {

    private List<String> fixContentList;

    private List<String> failureContentList;

}
