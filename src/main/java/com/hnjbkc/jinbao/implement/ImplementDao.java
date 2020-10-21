package com.hnjbkc.jinbao.implement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 12
 * @Date 2019-08-08
 */
@Repository
public interface ImplementDao extends JpaRepository<ImplementBean,Integer> {

    /**
     *
     * @param projectNumbers 项目编号list
     * @return
     */
    @EntityGraph
    List<ImplementBean> findAllByProjectBeanProjectNumIn(Collection<String> projectNumbers);

    /**
     *
     * @return
     */
    @Query("select i.implementId,im.incomeImplMoney  from ImplementBean i " +
            "left join IncomeImplMoneyBean im on im.implementBean.implementId = i.implementId where i in(?1)" +
            " ")
    List<Object[]> findImplementBeanByIncomeImplMoneyBean(List<ImplementBean> list);

    @Modifying
    @Transactional
    @Query("delete from ImplementBean as i  where i.implementId = ?1")
    void deleteImplementById(Integer id);
}
