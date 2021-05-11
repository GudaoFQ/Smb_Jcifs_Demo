package com.gudao.clienttools.service.impl;

import com.gudao.clienttools.model.DirInfo;
import com.gudao.clienttools.model.ShareParam;
import com.gudao.clienttools.service.SmbClientService;
import com.gudao.clienttools.utils.SmbUtil;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Auther: Gudao
 * @Date: 2021/5/10
 * @Description:
 */
@Service
public class SmbClientServiceImpl implements SmbClientService {
    @Override
    public boolean hasFile(String filePath, ShareParam shareParam) throws IOException {
        return SmbUtil.smbInit(shareParam.getBasePath(),shareParam.getRemotePort(),shareParam.getRemoteUrl(),filePath,shareParam.getUsername(),shareParam.getPassword()).exists();
    }

    @Override
    public InputStream downloadFile(String sourceFilePath, ShareParam shareParam) throws IOException {
        SmbFile smbFile = SmbUtil.smbInit(shareParam.getBasePath(), shareParam.getRemotePort(), shareParam.getRemoteUrl(), sourceFilePath, shareParam.getUsername(), shareParam.getPassword());
        if(!smbFile.isDirectory()){
            return SmbUtil.downloadFile(smbFile);
        }
        return null;
    }

    @Override
    public List<DirInfo> dirList(List<DirInfo> infos, String dirPath, ShareParam shareParam) throws IOException {
        //连接共享初始化
        if (!dirPath.endsWith("/")) {
            dirPath += "/";
        }
        SmbFile smbFile = SmbUtil.smbInit(shareParam.getBasePath(), shareParam.getRemotePort(), shareParam.getRemoteUrl(), dirPath, shareParam.getUsername(), shareParam.getPassword());
        SmbFile[] smbFiles = SmbUtil.dirList(smbFile);
        if(null != smbFiles){
            for (int i = 0; i < smbFiles.length; i++) {
                // 过滤没有用的文件/文件夹信息
                if(smbFiles[i].getShare().endsWith("$")){
                    continue;
                }
                DirInfo info = toDirInfo(smbFiles[i]);
                infos.add(info);
                // 如果是文件夹，继续遍历（如果是用户信息文件夹此处不会遍历）
                if(smbFiles[i].isDirectory() && !"Users".equals(info.getGroup())){
                    dirList(info.getDirList(),info.getPath().substring(info.getPath().indexOf(info.getGroup())),shareParam);
                }
                // 用户如果选择遍历用户文件，下面方法执行
                if(shareParam.getIsReadUserFiles() && "Users".equals(info.getGroup())){
                    dirList(info.getDirList(), info.getPath().substring(info.getPath().indexOf(info.getGroup())),shareParam);
                }
            }
        }
        return infos;
    }

    /**
     * 数据信息装配
     *
     * @param smbFile smb数据
     * @return {@link DirInfo}
     * @throws SmbException smb异常
     */
    private DirInfo toDirInfo(SmbFile smbFile) throws SmbException {
        return new DirInfo(
                // 文件全路径
                smbFile.getUncPath().replaceAll("\\\\","/"),
                // 文件大小
                smbFile.getContentLength(),
                smbFile.isDirectory(),
                smbFile.getLastModified(),
                // 文件名称
                smbFile.getCanonicalPath().substring(smbFile.getCanonicalPath().lastIndexOf("/")+1),
                0,
                null,
                // 最外层共享文件夹名称
                smbFile.getShare(),
                null);
    }
}
