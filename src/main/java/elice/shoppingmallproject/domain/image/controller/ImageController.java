package elice.shoppingmallproject.domain.image.controller;

import elice.shoppingmallproject.domain.image.dto.ImageDto;
import elice.shoppingmallproject.domain.image.service.S3UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
public class ImageController {

    private final S3UploadService s3UploadService;

    @Autowired
    public ImageController(S3UploadService s3UploadService) {
        this.s3UploadService = s3UploadService;
    }

    @GetMapping("/home")
    public String home(){
        return "hello world";
    }

    //S3에 imageUrl을 저장 후 url을 DTO로 받아와서 사용자에게 보여주기
    @PostMapping("/upload")
    public ResponseEntity<String> saveImage(ImageDto imageDto, Model model) throws IOException {

        ImageDto img = s3UploadService.uploadFile(imageDto.getImage());

        String imageUrl = img.getUrl();

        model.addAttribute("imageUrl",imageUrl);

        return new ResponseEntity<>("File uploaded successfully: ",HttpStatus.OK);
    }

    /*
    //S3로 이미지 여러개 보여주게 하기(메인화면)
    @GetMapping("members/images")
    public List<String> getProfileImages (){
        List<String> profileImageList = s3UploadService.getFileList("profile");
        return profileImageList;
    }*/

}

