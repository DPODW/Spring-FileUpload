package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
        /**
         * 사용자가 업로드한 파일 이름과(uploadFileName)
         *  실질적으로 db 에 저장되는 파일 이름(storeFileName) 은
         *  구별해야 한다.
         *
         *  그 이유는 파일 명이 같은채로 db에 저장되면 파일이 덮어씌워지게 된다.
         *  그러므로 db 파일 명을 UUID 로 주는 등, 유일한 이름으로 만들어야 한다.
         * */
    private String uploadFileName;
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
