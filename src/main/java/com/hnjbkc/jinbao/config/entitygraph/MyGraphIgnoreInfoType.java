package com.hnjbkc.jinbao.config.entitygraph;

/**
 * 实体图field模式
 * @author xudaz
 */

public enum MyGraphIgnoreInfoType {


    /**
     * 仅仅排除自己
     */
    ONLY_SELF,

    /**
     * 白名单模式
     */
    WHITE_LIST,


    /**
     * 黑名单模式
     */
    BLACK_LIST
}
