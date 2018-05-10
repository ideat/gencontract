package mindware.com.mappers;

import mindware.com.model.Parameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParameterMapper {
    void insertParameter(Parameter parameter);
    void updateParameter(Parameter parameter);
    List<Parameter> findParameterByType(@Param("typeParameter") String typeParameter);
    int findParameterByNameAndType(@Param("typeParameter") String typeParameter, @Param("valueParameter") String valueParameter);
}
