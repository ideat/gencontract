package mindware.com.mappers;

import mindware.com.model.Rol;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolMapper {
    void insertRol(Rol rol);
    void updateRol(Rol rol);
    List<Rol> findAllRol();
    Rol findAllRolMenuOptionByRolId(@Param("rolId") int rolId);
}
