package com.moriaty.vuitton.bean.novel.local.req;

import com.moriaty.vuitton.bean.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 本地小说获取 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/31 上午11:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FindNovelReq extends PageReq {

    private String name;

}
