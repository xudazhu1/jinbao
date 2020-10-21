package com.hnjbkc.jinbao.utils.downloader;

import com.hnjbkc.jinbao.utils.Downloader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @author siliiqang
 * @date 2019/12/3
 */
@Controller

public class DounloaderController {
    @RequestMapping("/download_file")
    public void down(String fileName, HttpServletResponse response) throws Exception {
        Downloader.Downloader(fileName, response);
    }
}
