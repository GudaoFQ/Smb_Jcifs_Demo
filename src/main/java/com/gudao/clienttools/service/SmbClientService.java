package com.gudao.clienttools.service;

import com.gudao.clienttools.model.DirInfo;
import com.gudao.clienttools.model.ShareParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Auther: Gudao
 * @Date: 2021/5/10
 * @Description:
 */
public interface SmbClientService {
    /**
     * 判断文件是否存在
     *
     * @param filePath   文件路径
     * @param shareParam 共享设置
     * @return 是否存在
     * @throws IOException IO异常
     */
    public boolean hasFile(String filePath, ShareParam shareParam) throws IOException;

    /**
     * 下载文件
     *
     * @param sourceFilePath 源文件路径
     * @param shareParam     共享设置
     * @throws IOException IO异常
     */
    public InputStream downloadFile(String sourceFilePath, ShareParam shareParam) throws IOException;

    /**
     * 获取路径目录
     *
     * @param shareParam 共享参数
     * @param dirPath    文件夹路径
     * @return 文件或目录的状态, 如果目录下没有文件或文件不存在返回空数组
     * @throws IOException ioexception
     */
    public List<DirInfo> dirList(List<DirInfo> infos, String dirPath, ShareParam shareParam) throws IOException;
}
