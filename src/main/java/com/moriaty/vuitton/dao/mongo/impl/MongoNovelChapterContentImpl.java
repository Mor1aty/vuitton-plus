package com.moriaty.vuitton.dao.mongo.impl;

import com.anwen.mongo.service.IService;
import com.anwen.mongo.service.impl.ServiceImpl;
import com.moriaty.vuitton.dao.mongo.model.MongoNovelChapterContent;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 小说章节内容 Mapper
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/28 上午9:11
 */
@Component
public class MongoNovelChapterContentImpl extends ServiceImpl<MongoNovelChapterContent>
        implements IService<MongoNovelChapterContent> {
}
