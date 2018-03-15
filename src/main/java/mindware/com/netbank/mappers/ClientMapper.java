package mindware.com.netbank.mappers;

import mindware.com.netbank.model.Client;
import org.apache.ibatis.annotations.Param;


public interface ClientMapper {
    Client findClientNetbankById(@Param("clientId") int id);

}
