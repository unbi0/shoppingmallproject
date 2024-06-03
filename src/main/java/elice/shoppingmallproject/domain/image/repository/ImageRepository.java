package elice.shoppingmallproject.domain.image.repository;

import elice.shoppingmallproject.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}

