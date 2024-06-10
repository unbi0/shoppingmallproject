package elice.shoppingmallproject.domain.image.dao;

import elice.shoppingmallproject.domain.image.entity.Image;

public interface ImageDao {
    Image saveImg(Image image);
    void deleteImg(Long image_id);
}
