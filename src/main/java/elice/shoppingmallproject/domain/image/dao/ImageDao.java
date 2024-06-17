package elice.shoppingmallproject.domain.image.dao;

import elice.shoppingmallproject.domain.image.entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageDao {
    Image saveImg(Image image);
    void deleteImg(Long image_id);
    Image findByImageId(Long image_id);
    List<Image> findAllByProductId(Long product_id);
}
