package elice.shoppingmallproject.domain.image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import elice.shoppingmallproject.domain.image.dao.ImageDaoImpl;
import elice.shoppingmallproject.domain.image.dto.ImageDto;
import elice.shoppingmallproject.domain.image.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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


    public String getThumbnailPath(String path) {
        return amazonS3Client.getUrl(bucket, path).toString();
    }


    public ImageDto uploadFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName =  createFileName(originalFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        //S3에 파일 업로드
        amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        String url = amazonS3Client.getUrl(bucket, fileName).toString();


        Image image = createImage(1L, url, fileName);

        //이 Entity는 DB를 거쳐 왔으므로 image_id가 생성되어있음
        Image savedImage = imageDao.saveImg(image);

        //savedEntity가 필드 값들을 전부 다 제대로 가지고 있는지 확인하는 Log 작성

        return savedImage.toDto();
    }

    public List<String> uploadFiles(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket+"/post/image", fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

                imgUrlList.add(amazonS3Client.getUrl(bucket+"/post/image", fileName).toString());
                String url = amazonS3Client.getUrl(bucket+"/post/image", fileName).toString();

                Image image = createImage(2L,url,fileName);
                imageDao.saveImg(image);
            } catch(IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }
        return imgUrlList;
    }

    private  Image createImage(Long product_id, String url, String fileName){
        Image image = Image.builder()
                .product_id(product_id) //임의의 값 1L로 설정
                .url(url)
                .file_name(fileName)
                .build();

        return image;
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

    public void deleteImage(Long image_id){
        // Retrieve the Image entity from the database using the image_id
        Image image = imageDao.findById(image_id);

        if (image != null) {
            // Get the file_name from the Image entity
            String keyName = image.getFile_name();

            // Delete the image from the S3 bucket
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, keyName));

            // Delete the image record from the database
            imageDao.deleteImg(image_id);
        }else {
            throw new IllegalArgumentException("Image not found with id: " + image_id);
        }
    }
}
