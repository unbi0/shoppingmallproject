package elice.shoppingmallproject.domain.image.dao;

import elice.shoppingmallproject.domain.image.entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageDao {
    Image saveImg(Image image);
    Image findByImageId(Long image_id);

}
