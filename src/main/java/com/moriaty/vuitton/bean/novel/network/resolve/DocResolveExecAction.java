package com.moriaty.vuitton.bean.novel.network.resolve;

import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * Doc 解析小说信息 Action
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/10 上午2:50
 */
public record DocResolveExecAction(List<DocResolveAction> actionList,
                                   Function<Element, String> finalGetAction) {

}
