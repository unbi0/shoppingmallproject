package elice.shoppingmallproject.domain.image.dao;

import elice.shoppingmallproject.domain.image.entity.Image;

import java.util.Optional;

public interface ImageDao {
    Image saveImg(Image image);
    void deleteImg(Long image_id);

    Image findById(Long imageId);
}
