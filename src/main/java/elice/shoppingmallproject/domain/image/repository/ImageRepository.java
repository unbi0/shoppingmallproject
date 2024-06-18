package elice.shoppingmallproject.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import elice.shoppingmallproject.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}

