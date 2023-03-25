package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {
    @Value("${file.dir}")
    private  String fileDir;

    @GetMapping("/upload")
    public String newFileV2() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file,
                           HttpServletRequest request) throws IOException {
        /**
         * spring 은 @RequestParam 으로 파일을 받을수 있다.
         * MultipartFile 을 타입으로 주어서 파일을 받는다.
         * */
        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);

        if(!file.isEmpty()){
            String fullPath = fileDir + file.getOriginalFilename(); //fullpath 에 파일의 경로와, 파일의 이름을 넣음
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath)); // transferTo: 파일을 저장해주는 메소드. 경로와 이름을 매개값으로 던지고 파일을 저장한다.
        }
        return "upload-form";
    }
}
