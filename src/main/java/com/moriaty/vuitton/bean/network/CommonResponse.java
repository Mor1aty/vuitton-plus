package com.moriaty.vuitton.bean.network;

import lombok.Data;

/**
 * <p>
 * 通用 Response
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/27 下午11:39
 */
@Data
public class CommonResponse<T> {

    private int code;

    private String msg;

    private T data;
}
