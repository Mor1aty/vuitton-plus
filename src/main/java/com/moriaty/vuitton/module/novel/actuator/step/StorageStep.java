package com.moriaty.vuitton.module.novel.actuator.step;

import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.ServerInfo;
import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.step.Step;
import com.moriaty.vuitton.library.actuator.step.StepMeta;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * <p>
 * 存储步骤
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午9:11
 */
@Slf4j
public class StorageStep extends Step {

    private final BiPredicate<Novel, List<NovelChapter>> storage;

    public StorageStep(BiPredicate<Novel, List<NovelChapter>> storage) {
        this.storage = storage;
    }

    @Override
    protected StepMeta initMeta() {
        return new StepMeta().setName("存储小说");
    }

    @Override
    public String getProgress() {
        return "正在存储";
    }

    @Override
    public boolean runContent() {
        Novel novel = super.getStepData("novel", new TypeReference<>() {
        });
        List<NovelChapter> chapterList = super.getStepData("chapterList", new TypeReference<>() {
        });
        File file = NovelUtil.writeToFile(novel.getName(), novel.getAuthor(), novel.getIntro(), chapterList);
        if (file == null) {
            log.error("写入小说文件失败");
            return false;
        }
        String imgFileUrl = super.getStepData("defaultNovelImg", new TypeReference<>() {
        });
        if (StringUtils.hasText(novel.getImgUrl())) {
            String uploadImgUrl = NovelUtil.uploadImg(ServerInfo.INFO.getFileServerUploadUrl(),
                    novel.getImgUrl(), novel.getName());
            if (uploadImgUrl == null) {
                log.error("上传小说图片失败, 将使用默认图片");
            } else {
                imgFileUrl = uploadImgUrl;
            }
        }

        String fileUrl = NovelUtil.upload(ServerInfo.INFO.getFileServerUploadUrl(),
                file, novel.getName() + ".txt");
        if (fileUrl == null) {
            log.error("上传小说失败");
            return false;
        }
        novel.setImgUrl(imgFileUrl);
        novel.setFileUrl(fileUrl);
        Boolean success = storage.test(novel, chapterList);
        if (Boolean.TRUE.equals(success)) {
            log.info("小说 {}[{}] 存储成功", novel.getName(), novel.getId());
        } else {
            log.info("小说 {} 存储失败", novel.getName());
        }

        return true;
    }
}
