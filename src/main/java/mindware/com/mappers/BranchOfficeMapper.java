package mindware.com.mappers;

import mindware.com.model.BranchOffice;
import mindware.com.model.Signatories;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BranchOfficeMapper {
    List<BranchOffice> findAllBranchOffice();
    List<BranchOffice> findBranchOfficeByCity(@Param("cityName") String cityName);
    List<BranchOffice> findAllCity();
    BranchOffice findBranchOfficeById(@Param("branchOfficeId") Integer branchId);
    void updateSignatoriesBranchOffice(BranchOffice branchOffice);
    void updateAddressBranchOffice(BranchOffice branchOffice);
    void insertBranchOffice(BranchOffice branchOffice);
    void deleteBranchOffice(int branchOfficeId);
    BranchOffice findSignatorieByBranchOffice(int branchOfficeId);
}
