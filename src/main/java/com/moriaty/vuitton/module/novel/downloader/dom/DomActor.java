package com.moriaty.vuitton.module.novel.downloader.dom;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import org.jsoup.nodes.Element;

/**
 * <p>
 * Dom 工具
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/29 上午1:48
 */
public class DomActor {

    private DomActor() {

    }

    public static String findText(DomActionParam param) {
        Element e = param.startElement();
        for (DomAction action : param.actions()) {
            e = action.execute(e);
            if (e == null) {
                return null;
            }
        }
        return param.textFunction().apply(e.text());
    }

    public static Element findElement(DomActionParam param) {
        Element e = param.startElement();
        for (DomAction action : param.actions()) {
            e = action.execute(e);
            if (e == null) {
                return null;
            }
        }
        return e;
    }

    public static NovelNetworkInfo findInfo(DomActionParam nameParam, DomActionParam authorParam,
                                            DomActionParam introParam, DomActionParam imgParam) {
        NovelNetworkInfo info = new NovelNetworkInfo();
        String name = findText(nameParam);
        if (name == null) {
            return null;
        }
        info.setName(name);
        String author = findText(authorParam);
        if (author == null) {
            return null;
        }
        info.setAuthor(author);
        String intro = findText(introParam);
        if (intro == null) {
            return null;
        }
        info.setIntro(intro);
        if (imgParam != null) {
            Element img = findElement(imgParam);
            if (img == null) {
                return null;
            }
            info.setImgUrl(img.attr("src"));
        } else {
            info.setImgUrl("");
        }
        return info;
    }
}
