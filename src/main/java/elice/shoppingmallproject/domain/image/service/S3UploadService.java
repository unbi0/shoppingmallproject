package elice.shoppingmallproject.domain.image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import elice.shoppingmallproject.domain.image.dao.ImageDaoImpl;
import elice.shoppingmallproject.domain.image.dto.ImageDto;
import elice.shoppingmallproject.domain.image.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        String fileName =  originalFilename +"/"+ UUID.randomUUID();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        //S3에 파일 업로드
        amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        String url = amazonS3Client.getUrl(bucket, fileName).toString();


        Image image = Image.builder()
                .product_id(1L) //임의의 값 1L로 설정
                .url(url)
                .file_name(fileName)
                .build();

        //이 Entity는 DB를 거쳐 왔으므로 image_id가 생성되어있음
        Image savedImage = imageDao.saveImg(image);

        //savedEntity가 필드 값들을 전부 다 제대로 가지고 있는지 확인하는 Log 작성

        return savedImage.toDto();
    }

/*
    //S3폴더 내 파일 리스트 전달
    public List<String> getFileList(String directory){
        List<String> fileList = new ArrayList<>();

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(directory+"/");  //폴더 경로 지정

        ListObjectsV2Result result = amazonS3.listObjectsV2(listObjectsV2Request);
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();
            if (!key.equals(directory + "/")) {  // "board/", 안나오게하기위해
                fileList.add("https://"+bucket+".s3."+region+".amazonaws.com/" + key);
            } //URL 바로 내보내주려고
        }

        return fileList;
    }*/

}
