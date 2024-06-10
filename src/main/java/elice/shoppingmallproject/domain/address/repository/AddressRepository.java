package elice.shoppingmallproject.domain.address.repository;


import elice.shoppingmallproject.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
