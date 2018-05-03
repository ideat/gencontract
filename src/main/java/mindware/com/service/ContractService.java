package mindware.com.service;

import mindware.com.mappers.ContractMapper;
import mindware.com.model.Contract;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class ContractService {

    public List<Contract> findAllContract(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ContractMapper contractMapper = sqlSession.getMapper(ContractMapper.class);
            return contractMapper.findAllContract();
        }finally {
            sqlSession.close();
        }
    }

    public List<Contract> findCotractByLoanNumber(Integer loanNumber){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ContractMapper contractMapper = sqlSession.getMapper(ContractMapper.class);
            return contractMapper.findContractByLoanNumber(loanNumber);
        }finally {
            sqlSession.close();
        }
    }

    public List<Contract> findContractByNameDebtor(String debtorName){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ContractMapper contractMapper = sqlSession.getMapper(ContractMapper.class);
            return contractMapper.findContractByNameDebtor(debtorName);
        }finally {
            sqlSession.close();
        }
    }

    public void insertContract(Contract contract){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ContractMapper contractMapper = sqlSession.getMapper(ContractMapper.class);
            contractMapper.insertContract(contract);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public void updateContract(Contract contract){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ContractMapper contractMapper = sqlSession.getMapper(ContractMapper.class);
            contractMapper.updateContract(contract);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }
}
