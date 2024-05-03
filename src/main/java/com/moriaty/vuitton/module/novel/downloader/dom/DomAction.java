package com.moriaty.vuitton.module.novel.downloader.dom;

import org.jsoup.nodes.Element;

/**
 * <p>
 * Dom Action
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/29 上午1:37
 */
public interface DomAction {

    /**
     * 执行
     *
     * @param e Element
     * @return Element
     */
    Element execute(Element e);

}
