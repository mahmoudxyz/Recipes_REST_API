package recipes.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import recipes.model.UserAccount;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {

    public UserAccount findUserByEmail(String email);


    boolean existsByEmail(String email);
}
