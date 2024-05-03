package com.moriaty.vuitton.module.novel.downloader.dom;

import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * Dom Action 参数
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/29 上午2:29
 */

public record DomActionParam(Element startElement, List<DomAction> actions, Function<String, String> textFunction) {

}
