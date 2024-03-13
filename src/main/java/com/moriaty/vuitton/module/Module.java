package com.moriaty.vuitton.module;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 模块
 * </p>
 *
 * @author Moriaty
 * @since 2024-01-28 12:02:37
 */
@Data
@Accessors(chain = true)
public class Module {

    private Integer id;

    private String name;

}
