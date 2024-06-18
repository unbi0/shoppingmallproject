package elice.shoppingmallproject.domain.image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import elice.shoppingmallproject.domain.image.dao.ImageDaoImpl;
import elice.shoppingmallproject.domain.image.entity.Image;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class S3UploadService {

    private final AmazonS3Client amazonS3Client;
    private final ImageDaoImpl imageDao;

    @Autowired
    public S3UploadService(AmazonS3Client amazonS3Client, ImageDaoImpl imageDao){
        this.amazonS3Client = amazonS3Client;
        this.imageDao = imageDao;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<Image> uploadFiles(List<MultipartFile> multipartFile) {

        List<Image> savedImage = new ArrayList<>();

        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket + "/post/images", fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

                String url = amazonS3Client.getUrl(bucket + "/post/images", fileName).toString();

                Image image = createImage(url, fileName);

                savedImage.add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }
        return savedImage;
    }

    private  Image createImage(String url, String fileName){

        return Image.builder()
                .url(url)
                .fileName(fileName)
                .build();
    }

    // 이미지파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new IllegalArgumentException("Invalid file extension: " + idxFileName);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    //S3에서 이미지 삭제
    @Transactional
    public void deleteImage(Long image_id){
        // Retrieve the Image entity from the database using the image_id
        Image image = imageDao.findByImageId(image_id);

        if (image != null) {
            // Get the file_name from the Image entity
            String keyName = image.getFileName();

            // Delete the image from the S3 bucket
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, keyName));

        }else {
            throw new IllegalArgumentException("Image not found with id: " + image_id);
        }
    }
}