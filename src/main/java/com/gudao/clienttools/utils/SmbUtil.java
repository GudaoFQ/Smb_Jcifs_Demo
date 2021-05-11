package com.gudao.clienttools.utils;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Auther: Gudao
 * @Date: 2021/5/11
 * @Description:
 */
public class SmbUtil {

    static {
        //设置连接共享密码
        jcifs.Config.setProperty("jcifs.smb.client.disablePlainTextPasswords", "false");
    }

    /**
     * 用户认证
     *
     * @param userName 用户名
     * @param password 密码
     * @return 认证数据
     */
    public static NtlmPasswordAuthentication authentication(String userName, String password) {
        // 用户认证
        return new NtlmPasswordAuthentication(null, userName, password);
    }

    /**
     * smb init
     * smb init
     * 创建连接
     *
     * @param basePath 基本路径  非必填
     * @param port     端口     非必填
     * @param url      ip地址   必填
     * @param filePath 文件路径 非必填
     * @param userName 用户名  必填
     * @param password 密码   必填
     * @return smb连接
     * @throws IOException ioexception
     */
    public static SmbFile smbInit(String basePath, Integer port, String url, String filePath, String userName, String password) throws IOException {
        // 远程端口
        int remotePort = port != null ? port : 445;
        // 拼接路径
        String path = (basePath != null ? basePath : "") + "/" + filePath;
        // 格式化路径
        path = path.replaceAll("\\\\", "/").replaceAll("//+", "/");
        // smb地址
        String smbUrl = String.format("smb://%s:%d/%s", url, remotePort, path);
        // smb地址
        SmbFile smbFile = new SmbFile(smbUrl, authentication(userName, password));
        smbFile.connect();
        return smbFile;
    }

    /**
     * 下载文件
     *
     * @param smbFile smb数据
     * @return {@link OutputStream}
     * @throws IOException IO异常
     */
    public static InputStream downloadFile(SmbFile smbFile) throws IOException {
        //连接共享初始化
        if (!smbFile.exists()) {
            throw new FileNotFoundException("源文件不存在");
        }
        return smbFile.getInputStream();

    }

    public static SmbFile[] dirList(SmbFile smbFile) throws IOException {
        SmbFile[] smbFiles = null;
        // 文件与文件夹信息读取
        if (smbFile.isDirectory()) {
            smbFiles = smbFile.listFiles();
        } else {
            smbFiles = new SmbFile[]{smbFile};
        }
        return smbFiles;
    }
}
