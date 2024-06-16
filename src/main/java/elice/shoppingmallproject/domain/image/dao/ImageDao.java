package elice.shoppingmallproject.domain.image.dao;

import elice.shoppingmallproject.domain.image.entity.Image;

import java.util.Optional;

public interface ImageDao {
    Image saveImg(Image image);
    void deleteImg(Long image_id);
    Image findByImageId(Long image_id);
    Image findByProductId(Long product_id);
    Long getProductCount(Long product_id);
}
