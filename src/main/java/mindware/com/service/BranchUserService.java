package mindware.com.service;

import mindware.com.mappers.BranchUserMapper;
import mindware.com.model.BranchUser;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class BranchUserService {
    public void insertBranchUser(BranchUser branchUser){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            BranchUserMapper branchUserMapper = sqlSession.getMapper(BranchUserMapper.class);
            branchUserMapper.insertBranchUser(branchUser);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public void deleteBranchUser(Integer rolViewContractId, String cityName){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            BranchUserMapper branchUserMapper = sqlSession.getMapper(BranchUserMapper.class);
            branchUserMapper.deleteBranchUser(rolViewContractId,cityName);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public List<BranchUser> findBranchUserByRolViewerId(Integer rolViewContractId){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            BranchUserMapper branchUserMapper = sqlSession.getMapper(BranchUserMapper.class);
            return branchUserMapper.findBranchUserByRolViewerId(rolViewContractId);
        }finally {
            sqlSession.close();
        }
    }

    public List<BranchUser> findBranchUserByRolViewerIdCity( Integer rolViewContractId,String city){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            BranchUserMapper branchUserMapper = sqlSession.getMapper(BranchUserMapper.class);
            return branchUserMapper.findBranchUserByRolViewerIdCity(rolViewContractId,city);
        }finally {
            sqlSession.close();
        }
    }
}
