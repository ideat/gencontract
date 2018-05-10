package mindware.com.service;

import mindware.com.mappers.ParameterMapper;
import mindware.com.model.Parameter;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class ParameterService {
    public void insertParameter(Parameter parameter){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ParameterMapper parameterMapper = sqlSession.getMapper(ParameterMapper.class);
            parameterMapper.insertParameter(parameter);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public List<Parameter> findParameterByType(String typeParameter){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ParameterMapper parameterMapper = sqlSession.getMapper(ParameterMapper.class);
            return parameterMapper.findParameterByType(typeParameter);
        }finally {
            sqlSession.close();
        }
    }

    public void updateParameter(Parameter parameter){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            ParameterMapper parameterMapper = sqlSession.getMapper(ParameterMapper.class);
            parameterMapper.updateParameter(parameter);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public int findParameterByNameAndType(String typeParameter, String valueParameter) {
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try {
            ParameterMapper parameterMapper = sqlSession.getMapper(ParameterMapper.class);
            return parameterMapper.findParameterByNameAndType(typeParameter,valueParameter);
        }finally {
            sqlSession.close();
        }
    }

}
