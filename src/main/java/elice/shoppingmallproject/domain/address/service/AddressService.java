package elice.shoppingmallproject.domain.address.service;


import elice.shoppingmallproject.domain.address.entity.Address;
import elice.shoppingmallproject.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public void saveAddress(Address address) {

        addressRepository.save(address);
    }
    public void deleteAddress(Long categoryId) {
        addressRepository.deleteById(categoryId);
    }


}