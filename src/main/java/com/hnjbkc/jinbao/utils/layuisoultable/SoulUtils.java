package com.hnjbkc.jinbao.utils.layuisoultable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 */
@Component
public class SoulUtils {

    private static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    public void setMappingJackson2HttpMessageConverter(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        SoulUtils.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }


    @SuppressWarnings({"unchecked", "unused"})
    public static void  addFilterSo (SoulPage soulPage , String filterSos  ) {
        // 把搜索条件 封装进 soulPage
        if ( filterSos != null ) {
            ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
            List<Map<String , Object >> mapList = new ArrayList<>();
            try {
                mapList = objectMapper.readValue(filterSos, List.class);
            } catch (IOException e) {
                new RuntimeException("映射搜索条件错误 , 搜索条件可能不会生效 ").printStackTrace();
            }
            List<FilterSo> filterSos1 = soulPage.getFilterSoses();
            for (Map<String, Object> stringObjectMap : mapList) {
                FilterSo filterSo ;
                try {
                    filterSo = objectMapper.readValue(objectMapper.writeValueAsString(stringObjectMap), FilterSo.class);
                    filterSos1.add(filterSo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
