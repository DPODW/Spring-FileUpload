package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}")
    private  String fileDir;

    @GetMapping("/upload")
    public String newFileV2() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV2(HttpServletRequest request) throws ServletException, IOException {
        log.info("request",request);

        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);

        Collection<Part> parts = request.getParts(); //전송 온 데이터를 파트별로 나누어 받음
        log.info("parts={}",parts);
        for (Part part : parts) {
            log.info("====part====");
            log.info("name={}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for(String headerName : headerNames){
                log.info("header {} : {}",headerName,part.getHeader(headerName));
            }
                /*
                * http 헤더 안에 part 의 헤더 또한 있음 (헤더 안에 헤더가 있는격)
                * 편의 메소드가 존재함
                *   ㄴ content-disposition; file name
                *   ㄴ 파트 별로 묶여서 통채로 들어온 데이터들을 각각으로 받을수 있는 편의 기능을 제공한다.
                * */
                log.info("submittedFilename={}", part.getSubmittedFileName()); //전송된 파일 이름
                log.info("size={}", part.getSize()); //파일의 크기

                InputStream inputStream = part.getInputStream(); //input(들어오는) 데이터 읽기
                String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); //들어온 데이터를 우리가 읽을수 있게 변환 
                //(StreamUtils) 는 변환 기능을 제공하는 편의 기능
                log.info("body={}", body);


                //파일에 저장하기
            if(StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath); //part 는 write 라는 기능을 제공하는데, 해당 기능이 내 pc 에서 파일을 저장하게 해주는 기능이다. (내 폴에 write 한다고 생각하면 될듯)
            }
            inputStream.close();
        }

        return "upload-form";
    }
}
