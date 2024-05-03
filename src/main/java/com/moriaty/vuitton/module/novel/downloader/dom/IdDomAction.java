package com.moriaty.vuitton.module.novel.downloader.dom;

import org.jsoup.nodes.Element;

/**
 * <p>
 * Id Dom Action
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/29 上午1:40
 */
public class IdDomAction implements DomAction {

    private final String name;

    public IdDomAction(String name) {
        this.name = name;
    }

    @Override
    public Element execute(Element e) {
        return e.getElementById(name);
    }
}
