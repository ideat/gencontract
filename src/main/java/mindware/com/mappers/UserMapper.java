package mindware.com.mappers;

import mindware.com.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    void insertUser(User user);
    void updateUser(User user);
    void updateUserPassword(User user);
    List<User> findAllUser();
    User findUserByUserId(@Param("userId") Integer userId);
    User findUserByLogin(@Param("login") String login);
    User findUserByLoginPassword(@Param("login") String login, @Param("password") String password);
}
