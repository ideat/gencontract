package mindware.com.mappers;

import mindware.com.model.Parameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParameterMapper {
    void insertParameter(Parameter parameter);
    List<Parameter> findParameterByType(@Param("typeParameter") String typeParameter);
    void updateParameter(Parameter parameter);
}
