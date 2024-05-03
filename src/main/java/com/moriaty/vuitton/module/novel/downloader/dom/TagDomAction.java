package com.moriaty.vuitton.module.novel.downloader.dom;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * <p>
 * Tag Dom Action
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/29 上午1:44
 */
public class TagDomAction implements DomAction {

    private final String name;

    private final int index;

    public TagDomAction(String name) {
        this.name = name;
        this.index = 0;
    }

    public TagDomAction(String name, int index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public Element execute(Element e) {
        Elements elements = e.getElementsByTag(name);
        return elements.isEmpty() || elements.size() <= index ? null : elements.get(index);
    }

}
