import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class Iosp {
    public static void main(String args[]) throws JsonProcessingException {
        Map<String,String> m = new HashMap<>();
        m.put("61","java开发,软件园实验楼203");
        m.put("23","高等数学（2）,软件园1区207");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(m);
        System.out.println(jsonString);
    }
}
