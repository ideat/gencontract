package mindware.com.service;

import mindware.com.mappers.RolViewContractMapper;
import mindware.com.model.RolViewContract;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class RolViewContractService {
    public void insertRolViewContract(RolViewContract rolViewContract){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            RolViewContractMapper rolViewContractMapper = sqlSession.getMapper(RolViewContractMapper.class);
            rolViewContractMapper.insertRolViewContract(rolViewContract);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public void updateRolViewContract(RolViewContract rolViewContract) {
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            RolViewContractMapper rolViewContractMapper = sqlSession.getMapper(RolViewContractMapper.class);
            rolViewContractMapper.updateRolViewContract(rolViewContract);
            sqlSession.commit();
        } finally {
            sqlSession.close();

        }
    }

    public List<RolViewContract> findAllRolViewContract(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            RolViewContractMapper rolViewContractMapper = sqlSession.getMapper(RolViewContractMapper.class);
            return  rolViewContractMapper.findAllRolViewContract();
        }finally {
            sqlSession.close();
        }
    }

    public RolViewContract findAllRolViewContractByRoId(int rolViewContractId){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            RolViewContractMapper rolViewContractMapper = sqlSession.getMapper(RolViewContractMapper.class);
            return rolViewContractMapper.findAllRolViewContractByRolId(rolViewContractId);
        }finally {
            sqlSession.close();
        }
    }

}
