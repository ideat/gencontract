package mindware.com.mappers;

import mindware.com.model.RolViewContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolViewContractMapper {
    void insertRolViewContract(RolViewContract rolViewContract);
    void updateRolViewContract(RolViewContract rolViewContract);
    List<RolViewContract> findAllRolViewContract();
    RolViewContract findAllRolViewContractByRolId(@Param("rolViewContractId") int rolViewContractId);
}
