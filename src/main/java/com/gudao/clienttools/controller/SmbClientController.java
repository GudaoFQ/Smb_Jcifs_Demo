package com.gudao.clienttools.controller;

import com.gudao.clienttools.model.DirInfo;
import com.gudao.clienttools.model.ShareParam;
import com.gudao.clienttools.service.SmbClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: Gudao
 * @Date: 2021/5/11
 * @Description:
 */
@RestController
@RequestMapping("/smdClient")
public class SmbClientController {
    @Autowired
    private SmbClientService smbClientService;

    @RequestMapping("/dirList")
    private List<DirInfo> dirList(@RequestParam("pathAndUserName") String pathAndUserName,
                                  @RequestParam("password") String password,
                                  @RequestParam(value = "isReadUserFiles", defaultValue = "false") boolean isReadUserFiles,
                                  @RequestParam(value = "path", defaultValue = "/") String path) {
        List<DirInfo> dirInfos = new ArrayList<>();
        // 解析账号密码
        String[] split = pathAndUserName.split("/");
        String ip = split[0];
        String user = split[1];
        ShareParam smbSetting = new ShareParam("smb", ip, null, user, password, null, "/", isReadUserFiles, new HashMap<>());
        try {
            dirInfos = smbClientService.dirList(dirInfos, path, smbSetting);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dirInfos;
    }
//
    @RequestMapping("/downFile")
    private List<DirInfo> DdownFileir(HttpServletResponse response,
                                      @RequestParam("pathAndUserName") String pathAndUserName,
                                      @RequestParam("password") String password,
                                      @RequestParam(value = "path") String path) {
        List<DirInfo> dirInfos = new ArrayList<>();
        // 解析账号密码
        String[] split = pathAndUserName.split("/");
        String ip = split[0];
        String user = split[1];
        ShareParam smbSetting = new ShareParam("smb", ip, null, user, password, null, "/", false, new HashMap<>());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = smbClientService.downloadFile(path, smbSetting);
            outputStream = response.getOutputStream();
            byte[] bytes = new byte[4096];
            int i = 0;
            while ((i = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != outputStream){
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dirInfos;
    }
}
