package com.hnjbkc.jinbao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnjbkc.jinbao.utils.MyBeanUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 去除request对象请求map里value为空的键值对
 * @author xudaz
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String , String[]> params = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

     ParameterRequestWrapper(HttpServletRequest request)  {
        // 将request交给父类，以便于调用对应方法的时候，将其输出，其实父亲类的实现方式和第一种new的方式类似
        super(request);
        //将参数表，赋予给当前的Map以便于持有request中的参数
        Map<String, String[]> requestMap=request.getParameterMap();
        this.params.putAll(requestMap);
        this.modifyParameterValues();
    }

//    /**
//     * 去除请求map里value为空的键值对
//     *
//     * 修改此方法主要是因为当RequestMapper中的参数为pojo类型时，
//     * 会通过此方法获取所有的请求参数并进行遍历，对pojo属性赋值
//     * @return r
//     */
//    @Override
//    public Enumeration<String> getParameterNames() {
//        Enumeration<String> enumeration = super.getParameterNames();
//        ArrayList<String> list = Collections.list(enumeration);
//        //value为空时 干掉key 和 value
//        list.removeIf(key -> "".equals(super.getParameter(key) ) );
//        return Collections.enumeration(list);
//    }

//    /**
//     * 去除请求map里value为空的键值对
//     *
//     * 重写getInputStream方法  post类型的请求参数必须通过流才能获取到值
//     */
//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//        //非json类型，直接返回
//        if(!super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)){
//            return super.getInputStream();
//        }
//        //为空，直接返回
//        String json = IOUtils.toString(super.getInputStream(), "utf-8");
//        if (StringUtils.isEmpty(json)) {
//            return super.getInputStream();
//        }
//        @SuppressWarnings("unchecked")
//        Map<String , String[]> map = objectMapper.readValue(json , Map.class);
//        map.keySet().removeIf(key -> "".equals(map.get(key)[0]));
//        ByteArrayInputStream bis = new ByteArrayInputStream(objectMapper.writeValueAsString(map).getBytes(StandardCharsets.UTF_8));
//        return new MyServletInputStream(bis);
//    }
    /**
     * 去除请求map里value为空的键值对
     */
    private void modifyParameterValues(){
        String getMethod =  "GET";
        if ( getMethod.equals(this.getMethod() ) ) {
            return;
        }
        Set<String> set =params.keySet();
        String tableName = this.getParameter("table_utils.tableName") ;
        String requestURI = this.getRequestURI();
        if ( tableName == null ) {
            tableName = this.getRequestURI().split("/")[0];
        }
        Class bean  = MyBeanUtils.getBean4TableName(  tableName  );
        Set<String> strings = params.keySet();
        if ( bean != null ) {
            strings.removeIf(key -> "".equals(params.get(key)[0] ) && MyBeanUtils.isPrimaryKey( bean , key ) );
        }
//        params.forEach( (key , value ) -> System.out.println( key + " = " + value ));
//        if ( params.get("page") != null ) {
//            params.put("pageNum" , params.remove("page"));
//        }
//        if ( params.get("limit") != null ) {
//            params.put("pageSize" , params.remove("limit"));
//        }
    }
//    /**
//     * 去除请求map里value为空的键值对
//     */
//    @Override
//    public String getParameter(String name) {
//        String[]values = params.get(name);
////        if(values == null || values.length == 0 || "".equals(values[0])) {
////            return null;
////        }
//        return values[0];
//    }
    /**
     * 去除请求map里value为空的键值对
     */
    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    /**
     * 去除请求map里value为空的键值对
     * @return r
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    class MyServletInputStream extends ServletInputStream {
        private ByteArrayInputStream bis;
        MyServletInputStream(ByteArrayInputStream bis){
            this.bis=bis;
        }
        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
        @Override
        public int read()   {
            return bis.read();
        }
    }

}