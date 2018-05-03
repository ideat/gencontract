package mindware.com.mappers;

import mindware.com.model.MenuOption;
import mindware.com.model.Option;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuOptionMapper {
    void insertMenuOption(MenuOption menuOption);
    void deleteMenuOption(@Param("rolId") Integer rolId);
    List<MenuOption> findMenuOptionByRolId(@Param("rolId") Integer rolId);
}
