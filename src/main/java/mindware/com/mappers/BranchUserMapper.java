package mindware.com.mappers;

import mindware.com.model.BranchUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BranchUserMapper {
    void insertBranchUser(BranchUser branchUser);
    void deleteBranchUser(@Param("rolViewContractId") Integer rolViewContractId, @Param("city") String city);
    List<BranchUser> findBranchUserByRolViewerId(@Param("rolViewContractId") Integer rolViewContractId);
    List<BranchUser> findBranchUserByRolViewerIdCity(@Param("rolViewContractId") Integer rolViewContractId,
                                                     @Param("city") String city);
}
