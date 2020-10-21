package com.hnjbkc.jinbao.quote;

import com.hnjbkc.jinbao.base.BaseService;
import com.hnjbkc.jinbao.contract.ContractServiceImpl;
import com.hnjbkc.jinbao.contract.material.MaterialBean;
import com.hnjbkc.jinbao.hqldao.SqlUtilsDao;
import com.hnjbkc.jinbao.organizationalstructure.company.CompanyBean;
import com.hnjbkc.jinbao.organizationalstructure.user.UserBean;
import com.hnjbkc.jinbao.quote.quotefile.QuoteFileBean;
import com.hnjbkc.jinbao.quote.quotefile.QuoteFileDao;
import com.hnjbkc.jinbao.utils.AttrExchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author 12
 * @Date 2019-07-31
 */
@Service
public class QuoteServiceImpl implements BaseService<QuoteBean> {

    private QuoteDao quoteDao;

    private SqlUtilsDao sqlUtilsDao;

    private QuoteFileDao quoteFileDao;

    @Autowired
    public void setQuoteFileDao(QuoteFileDao quoteFileDao) {
        this.quoteFileDao = quoteFileDao;
    }

    @Autowired
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    @Autowired
    public void setSqlUtilsDao(SqlUtilsDao sqlUtilsDao) {
        this.sqlUtilsDao = sqlUtilsDao;
    }

    @Override
    public QuoteBean add(QuoteBean quoteBean) {
        return quoteDao.save(quoteBean);
    }

    public QuoteBean add(QuoteBean quoteBean, HttpServletRequest request, Collection<Part> parts) {

        List<QuoteFileBean> upload = uploadQuote(parts, request);
        for (QuoteFileBean quoteFileBean : upload) {
            quoteFileBean.setQuoteBean(quoteBean);
        }
        quoteBean.setQuoteFileBeanList(upload);
        return quoteDao.save(quoteBean);
    }

    @Override
    public Boolean delete(Integer id) {
        quoteDao.deleteById(id);
        return true;
    }

    @Override
    public QuoteBean update(QuoteBean quoteBean) {
        return null;
    }

    public QuoteBean update(QuoteBean quoteBean, HttpServletRequest request, Collection<Part> parts) {
        List<QuoteFileBean> upload = uploadQuote(parts, request);
        quoteBean.setQuoteFileBeanList(upload);
        Optional<QuoteBean> byId = quoteDao.findById(quoteBean.getQuoteId());
        if(byId.isPresent()){
            QuoteBean quoteDnBean = byId.get();
            AttrExchange.onAttrExchange(quoteDnBean,quoteBean);
            return quoteDao.save(quoteDnBean);
        }
        return null;
    }

    public Page getQuotes(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.getAllByCustomProps(QuoteBean.class, propMap, pageRequest);
    }

    public QuoteBean getQuoteById(Integer quoteId) {
        Optional<QuoteBean> byId = quoteDao.findById(quoteId);
        return byId.orElse(null);
    }

    /**
     * 获取数据库现存的该字段的数据 (去重)
     *
     * @param property 属性名(以javaBean为准)
     * @return json Str
     */
    List getSingleProperty(String property) {
        return sqlUtilsDao.getSingleProperties(QuoteBean.class, property);
    }

    /**
     * 查询并且分页的方法(模糊查询)
     *
     * @param propMap 分页的参数
     * @return 需要展示的条数
     */
    public Page<QuoteBean> search(Map<String, Object> propMap, Pageable pageRequest) {
        return sqlUtilsDao.searchAllByCustomProps(QuoteBean.class, propMap, pageRequest);
    }

    public List<Map<String, Object>> getSelectBox() {
        return quoteDao.getSelectBox();
    }

    public String findMaxNumber() {
        String maxNumber = quoteDao.findMaxNumber();
        if (maxNumber != null) {
            //如果项目编号 不是空 则 转成 整型 +1  并且把 由字符串转成整型去掉的 0 添加回去
            StringBuilder s = new StringBuilder(Integer.parseInt(maxNumber) + 1 + "");
            int a = 4;
            for (int i = s.length(); i < a; i++) {
                s.insert(0, "0");
            }
            return s.toString();
        }
        return "0001";
    }



    private List<QuoteFileBean> uploadQuote(Collection<Part> parts, HttpServletRequest request) {
        ArrayList<QuoteFileBean> quoteFileBeans = new ArrayList<>();
        for (Part part : parts) {
            if (part.getSubmittedFileName() != null && !"".equals(part.getSubmittedFileName())) {
                QuoteFileBean quoteFileBean = new QuoteFileBean();
                //获取上传的文件名称
                String oldFileName = part.getSubmittedFileName();
                //截取文件名称的后缀
                assert oldFileName != null;
                String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
                /*上传后的路径*/
                String filePath = request.getSession().getServletContext().getRealPath("quote");
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    if (mkdirs) {
                        System.out.println("首次创建文件夹成功");
                    }
                }
                /*新文件名*/
                String newFileName = oldFileName + "" + UUID.randomUUID() + suffixName;
                File dest = new File(filePath + "/" + newFileName);
                String s = dest.toString();
                try {
                    part.write(s);
                    quoteFileBean.setQuoteFileNewName(newFileName);
                    quoteFileBean.setQuoteFileOldName(oldFileName);
                    quoteFileBeans.add(quoteFileBean);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return quoteFileBeans;
    }
}
