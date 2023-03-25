package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    private Item item;


    /**
     * item 화면 접근
     * */
    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }


    /**
     * attachFile: 파일 단일 저장 / storeImageFiles: 파일 다중 저장 / storeFile,s 를 사용하여서 파일을 실질적 등록한다.
     * view 에서 넘어온 정보들을 db 저장 로직에 넣는다.
     * id 를 기준으로, 상품 조회 컨트롤러로 보낸다.
     * */
    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile()); //파일 하나 저장
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles()); //파일 여러개 저장

        //db 저장 로직 (ItemForm 에 저장되어있는 데이터를 Item 에 set 함)
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId",item.getId());

        return "redirect:/items/{itemId)";
    }


    /**
     * PostMapping (items/new) 컨트롤러에서 넘어온 id 로 저장소 검색.
     * 해당 아이디로 검색된 model (vo) 를 얻어오고 addAttribute 함
     * 해당 데이터를 item - view 로 보냄
     * */
    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model){
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }


    /**
     * 업로드한 파일 이미지를 조회하는 로직
     *  fileStore 의 getFullPath(파일의 전체 경로를 알아오는) 을 사용하고 매개값으로 현재 파일의 이름을 넣음.
     * 특정 URL 로 부터 정보를 읽어오는 UrlResource 를 사용하고, @ResponseBody 로 바디 부분에 데이터를 출력한다.
     *      ㄴ 즉 UrlResource 로 파일을 띄우고, 띄우는 위치를 바디로 선언한것.
     * */
    @ResponseBody
    @GetMapping("/images/{fileName}") //업로드한 파일 이미지 조회
    public Resource downloadImage (@PathVariable String fileName) throws MalformedURLException {
      return new UrlResource("file:" + fileStore.getFullPath(fileName));
    }


    /**
     * 파일 다운로드
     * ResponseEntity 는 HTTP 응답에 관련된 객체이며, Resource 는 이미지와 파일 같은 리소스를 뜻한다.
     *      ㄴ 즉 해당 로직은 -> HTTP 요청에 대한 응답으로 리소스를 전송 한다는 뜻이다.
     * 
     *
     * */
    @GetMapping("/attach/{itemId}") //파일 다운로드
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        
        //파일의 실제 이름과, DB 에 저장될 이름 가져오기
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();//사용자가 다운 받을때 실제 파일 이름이 나와야함 [uuid 가 나오면 안됌] 고로 사용자 이름도 가져옴
        UrlResource resource =  new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        //한글 파일을 위한 인코딩 (없으면 한글 자체 다운 안됌)

        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
        //파일 다운로드를 위한 통신 규약 임

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition).body(resource);
        //헤더를 주지 않으면, 파일이 다운로드 되지 않고 그냥 웹 브라우저에서 열려버린다. -> 응용하면 쓸 곳이 있을지도. . . ?


    }

}
