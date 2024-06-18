package elice.shoppingmallproject.domain.image.dao;

import elice.shoppingmallproject.domain.image.entity.Image;
import elice.shoppingmallproject.domain.image.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageDaoImpl implements ImageDao{

    ImageRepository imageRepository;

    @Autowired
    public ImageDaoImpl(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    @Override
    public Image saveImg(Image image){
        return imageRepository.save(image);
    }

    @Override
    public Image findByImageId(Long image_id) {
        return imageRepository.getReferenceById(image_id);
    }

}
