package elice.shoppingmallproject.domain.image.repository;

import elice.shoppingmallproject.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

}

