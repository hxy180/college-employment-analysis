package com.shixun.controller.vo;

import lombok.Data;

import java.util.List;

/**
 * 桑基图数据 VO（SankeyDataVO）
 * 用途：存储专业与行业流向关系数据（对应前端sankeyData），包含节点和链接两个内部类
 */
@Data
public class SankeyDataVO {
    /** 节点列表（专业和行业） */
    private List<Node> nodes;

    /** 链接列表（专业到行业的流向关系） */
    private List<Link> links;

    // 节点内部类（代表专业或行业）
    @Data
    public static class Node {
        /** 节点名称（如"人工智能"或"信息技术"） */
        private String name;

        /** 节点类型（可选，区分"major"和"industry"，用于前端样式区分） */
        private String type;
    }

    // 链接内部类（代表流向关系）
    @Data
    public static class Link {
        /** 源节点名称（专业名称） */
        private String source;

        /** 目标节点名称（行业名称） */
        private String target;

        /** 流量值（如就业人数、占比） */
        private Integer value;
    }
}