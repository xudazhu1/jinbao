package com.hnjbkc.jinbao.utils.tableutils;

import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.PageableUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xudaz
 */
@Service
public class TableUtilsService {


    private TableUtilsDao tableUtilsDao;


    @Autowired
    public void setTableUtilsDao(TableUtilsDao tableUtilsDao) {
        this.tableUtilsDao = tableUtilsDao;
    }

     Page getByTableUtils(Map<String, Object> map , HttpServletRequest request) {
         //获取主体bean
         Class tableNameBean = MyBeanUtils.getBean4TableName((String) map.remove("table_utils.tableName"));
         assert  tableNameBean != null;
         Pageable pageable = PageableUtils.producePageable4Map(map, MyBeanUtils.getToLowerNameByBean(tableNameBean) + "." + MyBeanUtils.getIdAttrNameByBean(tableNameBean), tableNameBean);
         String[] customWhere = request.getParameterMap().get("table_utils.custom_where");
         if ( customWhere == null  ) {
             customWhere = new String[0];
         }
         map.remove("table_utils.custom_where");
         return tableUtilsDao.search4Table(tableNameBean , map , pageable , customWhere );
    }

    /**
     * 注入实体管理器,执行持久化操作
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 添加或升级方法 ( 带数据交换 )
     * @see  AttrExchange
     * @param tableName 表名
     * @return r
     */
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @SuppressWarnings("unchecked")
    public Object update( String tableName ) throws Exception {
        return tableUtilsDao.update( tableName );
    }

    @Transactional(rollbackOn = Exception.class)
    @SuppressWarnings("unchecked")
    public Boolean delete(String tableName, Integer id) {
        return tableUtilsDao.delete(tableName , id);
    }
    @Transactional(rollbackOn = Exception.class)
    @SuppressWarnings("unchecked")
    public Boolean deleteByIds(String tableName, String ids) {
        return tableUtilsDao.deleteByIds(tableName , ids);
    }


    private SqlUtilsDao sqlUtilsDao;

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

     Page getInfo(Map<String, Object> map) {
        Class tableNameBean = MyBeanUtils.getBean4TableName((String) map.remove("table_utils.tableName"));
        //noinspection unchecked
        return sqlUtilsDao.getAllByCustomProps(tableNameBean , map , PageableUtils.producePageable4Map(map , MyBeanUtils.getIdAttrNameByBean(tableNameBean) )  );
    }

    public Object getMultipleProperties(Map<String, Object> map , HttpServletRequest request ) {
        Class tableNameBean = MyBeanUtils.getBean4TableName((String) map.remove("table_utils.tableName"));
        assert  tableNameBean != null;
        String[] customWhere = request.getParameterMap().get("table_utils.custom_where");
        if ( customWhere == null  ) {
            customWhere = new String[0];
        }
        map.remove("table_utils.custom_where");
        //获取主体bean
        return tableUtilsDao.getMultipleProperties(tableNameBean , map  );
    }

    public HSSFWorkbook export(Map<String, Object> map) {
        //获取主体bean
        Class tableNameBean = MyBeanUtils.getBean4TableName((String) map.remove("table_utils.tableName"));
        assert  tableNameBean != null;
        Pageable pageable = PageableUtils.producePageable4Map(map, MyBeanUtils.getToLowerNameByBean(tableNameBean) + "." + MyBeanUtils.getIdAttrNameByBean(tableNameBean), tableNameBean);

        //获取field中文名
        String fieldsCn = (String) map.remove("table_utils.fields_cn");

        @SuppressWarnings("unchecked")
        List<Object[]> objects = tableUtilsDao.searchNoCount(tableNameBean, map, pageable);

        //创建excel工作簿对象
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        //创建excel页
        HSSFSheet sheet = hssfWorkbook.createSheet("POI导出测试");

        //遍历创建表头
        HSSFRow row1 = sheet.createRow(0);
        String[] split = fieldsCn.split("[$]");
        for (int i = 0; i < split.length; i++) {
            //创建表头的单元格-------------------------------
            HSSFCell cell = row1.createCell(i);
            cell.setCellValue( split[i] );
        }
        //遍历写入行内容：
        for (int i = 0; i < objects.size(); i++) {
           Object[] objects1 =  objects.get( i );
            HSSFRow row2 = sheet.createRow(i + 1);
            for (int j = 0; j < split.length; j++) {
                HSSFCell hssfCell = row2.createCell( j );
                setCellValue( hssfCell , objects1[j]  );
            }

        }
        return  hssfWorkbook;
    }

    private void setCellValue( HSSFCell hssfCell , Object value ) {
        if ( value instanceof Date ) {
            hssfCell.setCellValue( (Date) value );
        } else
        if ( value instanceof String ) {
            hssfCell.setCellValue( (String) value );
        } else
        if ( value instanceof Double ) {
            hssfCell.setCellValue( (Double ) value );
        } else
        if ( value instanceof Boolean ) {
            hssfCell.setCellValue( (Boolean ) value );
        } else
        if ( value instanceof Integer ) {
            hssfCell.setCellValue( (Integer ) value );
        } else if ( value != null ) {
            hssfCell.setCellValue(  value.toString() );
        } else {
            hssfCell.setCellValue(  "" );
        }

    }

}
