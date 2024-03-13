package com.moriaty.vuitton.service;

import com.moriaty.vuitton.ServerInfo;
import com.moriaty.vuitton.bean.common.CommonInfo;
import com.moriaty.vuitton.library.wrap.WrapMapper;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通用 Service
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:20
 */
@Service
@AllArgsConstructor
@Slf4j
public class CommonService {

    public Wrapper<CommonInfo> info() {
        return WrapMapper.ok(new CommonInfo()
                .setFileServerUrl(ServerInfo.INFO.getFileServerUrl())
                .setFileServerUploadUrl(ServerInfo.INFO.getFileServerUploadUrl())
                .setNovelDownloaderList(NovelDownloaderFactory.getAllDownloaderInfo()));
    }
}
