package com.moriaty.vuitton.bean;

import com.moriaty.vuitton.library.wrap.WrapConstant;
import com.moriaty.vuitton.library.wrap.Wrapper;

/**
 * <p>
 * 返回封装映射拓展
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/10 下午6:45
 */
public class WrapMapperExt {

    private WrapMapperExt() {

    }

    public static <E> Wrapper<E> novelDownloaderNotExisted() {
        return new Wrapper<>(WrapConstant.CODE_FAILURE, "小说下载器不存在", null);
    }
}
