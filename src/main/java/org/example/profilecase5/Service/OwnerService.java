package org.example.profilecase5.Service;
import jakarta.transaction.Transactional;
import org.example.profilecase5.Model.Owner;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;
    @Autowired
    private UserService userService;
//    private final PasswordEncoder passwordEncoder;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public void registerOwner(Owner owner) {
        // Lấy user hiện tại
        User currentUser = userService.getCurrentUser();

        // Kiểm tra user đã có owner chưa
        if (ownerRepository.existsByUser(currentUser)) {
            throw new RuntimeException("Người dùng đã là chủ nhà");
        }

        owner.setUser(currentUser);
        ownerRepository.save(owner);
    }

    public Owner getOwnerById(int id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chủ nhà"));
    }

    public boolean checkPassword(String password) {
        User currentUser = userService.getCurrentUser();
        return password.equals(currentUser.getPassword());
//        String encodedPassword = currentUser.getPassword();
//        return passwordEncoder.matches(Password, encodedPassword);
    }
}
