package hello.upload.domain;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;

    /**
     * attachFile -> 단일 파일
     * imageFiles -> 다중 파일
     *
     * 사진은 한장만 올리는 경우가 잘 없음
     * */


}
