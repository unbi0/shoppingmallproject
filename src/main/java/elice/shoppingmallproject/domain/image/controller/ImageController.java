package elice.shoppingmallproject.domain.image.controller;

import elice.shoppingmallproject.domain.image.dto.ImageDto;
import elice.shoppingmallproject.domain.image.service.S3UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageController {

    private final S3UploadService s3UploadService;

    @Autowired
    public ImageController(S3UploadService s3UploadService) {
        this.s3UploadService = s3UploadService;
    }

    //S3에 imageUrl을 저장 후 url을 DTO로 받아와서 사용자에게 보여주기
    @PostMapping("/upload")
    public String saveImage(MultipartFile image, Model model) throws IOException {
        ImageDto imageDto = s3UploadService.uploadFile(image);

        String imageUrl = imageDto.getUrl();

        model.addAttribute("imageUrl",imageUrl);

        return "image";
    }
/*
    //S3로 이미지 여러개 보여주게 하기(메인화면)
    @GetMapping("members/images")
    public List<String> getProfileImages (){
        List<String> profileImageList = s3UploadService.getFileList("profile");
        return profileImageList;
    }*/

}

