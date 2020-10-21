package com.hnjbkc.jinbao.datum.datumfile;

import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnore;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfo;
import com.hnjbkc.jinbao.config.entitygraph.MyGraphIgnoreInfoType;
import com.hnjbkc.jinbao.datum.DatumBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author SILIQIANG
 * @date 2019.8.15
 */
@Entity
@Table(name = "datum_file")
@Getter
@Setter
@MyGraphIgnore(ignoreFields = {
        @MyGraphIgnoreInfo(fieldPath = "datumBean", fetchType = MyGraphIgnoreInfoType.WHITE_LIST),
})
public class DatumFileBean  implements Serializable {
    /**
     * 主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer    datumFileId;

    /**
     * 文件的类型
     */
    String datumFileType;

    /**
     * 上传的文件路径
     */
    String datumFilePath;
    /**
     * 多对一
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "datum_file_datum_id", referencedColumnName = "datumId")
    DatumBean datumBean;
}
