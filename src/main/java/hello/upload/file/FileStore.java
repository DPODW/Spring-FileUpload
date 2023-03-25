package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * 파일 저장과 관련된 업무 처리
 * */
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;
    //해당 경로에 FileStore 가 파일을 저장함

    public String getFullPath(String filename){
        return fileDir + filename;
    }

    /* 이미지 같은 사진은 하나가 아닌, 여러개가 등록된다.
    * 고로 여러개의 파일을 받는 기능 또한 구현해야 한다.
    * */
    public List<UploadFile> storeFiles(List<MultipartFile>multipartFiles) throws IOException { //파일 다중 등록(단일 등록이 여러번 일어나는것)
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()){
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }



    public UploadFile storeFile(MultipartFile multipartFile) throws IOException { //파일 단일 등록
        if(multipartFile.isEmpty()){
            return null;
        }

        //파일의 실제 이름 (image.png 같은)
        String originalFilename = multipartFile.getOriginalFilename();

        //db 에 저장될 파일 이름
        String storeFileName = createStoreFileName(originalFilename);


        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(originalFilename,storeFileName);
    }


    private String createStoreFileName(String originalFilename) { //db 에 저장할 파일 이름 생성 해주는 메소드
        // 확장자명 가지고 오기.
        String ext = extractExt(originalFilename);

        //서버에 저장될 파일 명
        String uuid = UUID.randomUUID().toString();

        // uuid 에 확장자 명 붙히기.
        return uuid + "." + ext;
    }



    /**
     * UUID 로 DB에 저장하되, 확장자 명은  originalFilename 에서 가지고 오고싶다.
     * 그러기 위해선 originalFilename 에서 확장자 명만 따로 추출을 해내야 한다,
     * 해당 역할을 해주는 메소드가 extractExt 메소드이다.
     *
     * originalFilename 에서 "." 다음 문자들을 추출하고, 위치를 가져온다.
     * 그리고 해당 위치에서 +1 한 값으로 substring(문자열 자르기) 를 해서 확장자 명을 떼온다.
     * */
    private String extractExt(String originalFilename) { //확장자 명 가져오기
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos+1);
    }


}
