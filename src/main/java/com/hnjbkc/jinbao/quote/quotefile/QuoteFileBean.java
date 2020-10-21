package com.hnjbkc.jinbao.quote.quotefile;

import com.hnjbkc.jinbao.quote.QuoteBean;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 报价文件
 * @author 12
 * @Date 2019-10-06
 */
@Getter
@Setter
@Entity
@Table(name = "quote_file")
public class QuoteFileBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer	quoteFileId;

    /**
     * 报价老的名称
     */
    String	quoteFileOldName;

    /**
     * 报价新的名称
     */
    String	quoteFileNewName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_file_quote_id",referencedColumnName = "quoteId")
    private QuoteBean quoteBean;


}
