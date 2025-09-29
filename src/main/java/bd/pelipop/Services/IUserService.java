package bd.pelipop.Services;
import bd.pelipop.Models.User;
import java.util.List;

public interface IUserService {

    public List<User> getAllUsers();
    public User getUserById(Long id);
    public User createUser(User user);
    public User updateUser(Long id, User user);
    public void deleteUser(Long id);

    public User findUserByUsername(String username);
    public User findUserByEmail(String email);
}
