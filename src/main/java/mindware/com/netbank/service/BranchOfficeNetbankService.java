package mindware.com.netbank.service;

import mindware.com.netbank.mappers.BranchOfficeNetbankMapper;
import mindware.com.netbank.model.BranchOfficeNetbank;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class BranchOfficeNetbankService {
    public List<BranchOfficeNetbank> findAllBranchOfficeNetbank(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("netbank");
        try{
            BranchOfficeNetbankMapper branchOfficeNetbankMapper = sqlSession.getMapper(BranchOfficeNetbankMapper.class);
            return branchOfficeNetbankMapper.findAllBranchOfficeNetbank();
        }finally {
            sqlSession.close();
        }

    }

}
