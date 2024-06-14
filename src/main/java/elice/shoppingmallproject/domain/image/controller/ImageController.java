package elice.shoppingmallproject.domain.image.controller;

import elice.shoppingmallproject.domain.image.dto.ImageDto;
import elice.shoppingmallproject.domain.image.service.S3UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class ImageController {

    private final S3UploadService s3UploadService;

    @Autowired
    public ImageController(S3UploadService s3UploadService) {
        this.s3UploadService = s3UploadService;
    }

    @GetMapping("/product/store")
    public String inputProduct() {
        // 필요한 경우 모델에 데이터 추가
        return "inputProduct";
    }


    //S3에 imageUrl을 저장 후 url을 DTO로 받아와서 사용자에게 보여주기
    @PostMapping("/upload/image")
    public ResponseEntity<String> saveImage(ImageDto imageDto, Model model) throws IOException {

        ImageDto img = s3UploadService.uploadFile(imageDto.getImage());

        String imageUrl = img.getUrl();

        model.addAttribute("imageUrl",imageUrl);

        return new ResponseEntity<>("File uploaded successfully: ",HttpStatus.OK);
    }

    @PostMapping("/upload/images")
    public ResponseEntity<String> saveImages(@RequestPart("imgUrl") List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles == null) {
            throw new IllegalArgumentException("multipartFiles cannot be null");
        }

        List<String> imgPaths = s3UploadService.uploadFiles(multipartFiles);

        return new ResponseEntity<>("File uploaded successfully: " + String.join(", ", imgPaths), HttpStatus.OK);
    }

    @DeleteMapping("/{image_id}/delete")
    public void deleteMovie(@PathVariable Long image_id) {
        log.debug("debug log={}",image_id);
        s3UploadService.deleteImage(image_id);
    }


}

