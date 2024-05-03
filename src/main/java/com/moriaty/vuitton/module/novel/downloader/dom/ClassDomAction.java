package com.moriaty.vuitton.module.novel.downloader.dom;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * <p>
 * Class Dom Action
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/29 上午1:42
 */
public class ClassDomAction implements DomAction {

    private final String name;

    private final int index;

    public ClassDomAction(String name) {
        this.name = name;
        this.index = 0;
    }

    public ClassDomAction(String name, int index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public Element execute(Element e) {
        Elements elements = e.getElementsByClass(name);
        return elements.isEmpty() || elements.size() <= index ? null : elements.get(index);
    }

}
