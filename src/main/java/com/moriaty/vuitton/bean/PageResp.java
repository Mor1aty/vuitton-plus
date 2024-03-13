package com.moriaty.vuitton.bean;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 分页 Resp
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/2 下午12:46
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PageResp<T> {

    private List<T> list;

    private long total;

    private Integer pageNum;

    private Integer pageSize;

    public PageResp(PageReq req, Page<T> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
        this.pageNum = req.getPageNum();
        this.pageSize = req.getPageSize();
    }
}
