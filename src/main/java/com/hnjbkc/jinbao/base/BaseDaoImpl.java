package com.hnjbkc.jinbao.base;

import com.hnjbkc.jinbao.config.entitygraph.EntityGraphImplements;
import com.hnjbkc.jinbao.hqldao.ManyAndOneToOneAndOne;
import com.hnjbkc.jinbao.utils.MyBeanUtils;
import com.hnjbkc.jinbao.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author xudaz
 */
@SuppressWarnings({"NullableProblems", "unchecked"})
public class BaseDaoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

    private ManyAndOneToOneAndOne manyAndOneToOneAndOne;

    @Autowired
    public void setManyAndOneToOneAndOne(ManyAndOneToOneAndOne manyAndOneToOneAndOne) {
        this.manyAndOneToOneAndOne = manyAndOneToOneAndOne;
    }

    @SuppressWarnings("DuplicatedCode")
    private <P> Object addCascades(P object) {

        if ( manyAndOneToOneAndOne == null ) {
            try {
                manyAndOneToOneAndOne = (ManyAndOneToOneAndOne) SpringUtils.getBean("manyAndOneToOneAndOne");
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        if ( object == null ) {
            return null;
        }
        try {
            //如果是page
            if (object instanceof Page) {
                Page<T> page = (Page<T>) object;
                List<T> listTemp = new ArrayList<>(page.getContent());
                List<T> cascades = manyAndOneToOneAndOne.getCascades(listTemp);
                return new PageImpl<>(cascades, page.getPageable(), page.getTotalElements());
            } else
            //如果是list
            if (object instanceof List) {
                List<T> listTemp = (List<T>) object;
                return manyAndOneToOneAndOne.getCascades(listTemp);
            } else
            //如果是findById
            if (object instanceof Optional) {
                Optional proceed = (Optional) object;
                if (proceed.isPresent()) {
                    manyAndOneToOneAndOne.getCascades(new ArrayList<>(Collections.singletonList(proceed.get())));
                }
            } else
            //如果是findOne
            if (  MyBeanUtils.getPrimaryKey( object ) != null ) {
                manyAndOneToOneAndOne.getCascades(new ArrayList<>(Collections.singletonList(object)));
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return object;
        }

    }


    private EntityManager entityManager;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public BaseDaoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        EntityGraphImplements.createGraphOnLoad(entityManager);
    }


    private <F extends Query> F add4Query(F query) {
        List<EntityGraph<? super T>> entityGraphs = entityManager.getEntityGraphs(this.getDomainClass());
        if (entityGraphs != null && entityGraphs.size() > 0) {
            query.setHint("javax.persistence.loadgraph", entityGraphs.get(0));
            System.out.println(" setHint 'javax.persistence.loadgraph' ==> " + entityGraphs.get(0));
        }
        return query;
    }

    @Override
    public Optional<T> findById(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        List<EntityGraph<? super T>> entityGraphs = entityManager.getEntityGraphs(this.getDomainClass());
        assert entityGraphs != null && entityGraphs.size() > 0;
        Map<String, Object> map = new HashMap<>(1);
        map.put("javax.persistence.loadgraph", entityGraphs.get(0));
        Optional<T> t = Optional.ofNullable(this.entityManager.find(this.getDomainClass(), id, map));
        return (Optional<T>) addCascades( t );
    }


    @Override
    public T getOne(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        Optional<T> byId = this.findById(id);
        return (T) addCascades( byId.orElse(null) );
    }

    @Override
    public List<T> findAll() {
        TypedQuery query = add4Query(this.getQuery((Specification) null, Sort.unsorted()));
        return (List<T>) addCascades( query.getResultList() );
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        Assert.notNull(ids, "Ids must not be null!");
        String hqlByIn = "from " + this.getDomainClass().getSimpleName() + " where " + MyBeanUtils.getIdAttrNameByBean(this.getDomainClass()) + " in ( :ids )";
        TypedQuery<T> query = add4Query(this.entityManager.createQuery(hqlByIn, this.getDomainClass()));
        query.setParameter("ids", ids);
        return (List<T>) addCascades( query.getResultList() );
    }

    @Override
    public List<T> findAll(Sort sort) {
        TypedQuery query = add4Query(this.getQuery((Specification) null, sort));
        return (List<T>) addCascades( query.getResultList() );
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        Page page = pageable.isUnpaged() ? new PageImpl(this.findAll()) : this.findAll((Specification) null, pageable);
        return (Page<T>) addCascades( page );
    }

    @Override
    public Optional<T> findOne(Specification<T> spec) {
        try {
            return Optional.of(add4Query(this.getQuery(spec, Sort.unsorted())).getSingleResult());
        } catch (NoResultException var3) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        return add4Query(this.getQuery(spec, Sort.unsorted())).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        TypedQuery<T> query = add4Query(this.getQuery(spec, pageable));
        return (Page) (pageable.isUnpaged() ? new PageImpl(query.getResultList()) : this.readPage(query, this.getDomainClass(), pageable, spec));
    }


    @Override
    protected TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {
        return add4Query(super.getQuery(spec, pageable));
    }

    @Override
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass, Pageable pageable) {
        return add4Query(super.getQuery(spec, domainClass, pageable));
    }

    @Override
    protected TypedQuery<T> getQuery(Specification<T> spec, Sort sort) {
        return add4Query(super.getQuery(spec, sort));
    }

    @Override
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass, Sort sort) {
        return add4Query(super.getQuery(spec, domainClass, sort));
    }

    @Override
    protected <S extends T> TypedQuery<Long> getCountQuery(Specification<S> spec, Class<S> domainClass) {
        return add4Query(super.getCountQuery(spec, domainClass));
    }


    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return (List<S>) addCascades( super.findAll(example) );
    }


    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        return (List<T>) addCascades( super.findAll(spec, sort) );
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return (Optional<S>) addCascades( super.findOne(example) );
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return (List<S>) addCascades( super.findAll(example, sort) );
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return (Page<S>) addCascades( super.findAll(example, pageable) );

    }
}
