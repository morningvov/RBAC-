package com.momo.warehouse.sys.common.TreeNode;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author momo
 * @create 2019-12-06 下午 21:44
 */
public class TreeNodeBuilder {

    /**
     * 把没有层级关系的集合变成有层级关系的【两重for循环——>还能进行优化(用map)】
     * @param treeNodes
     * @param topPid
     * @return
     */
    public static List<TreeNode> build(List<TreeNode> treeNodes, Integer topPid) {
        List<TreeNode> nodes = new ArrayList<>();
        for (TreeNode n1 : treeNodes){
            if(n1.getPid()==topPid){
                nodes.add(n1);
            }
            for (TreeNode n2 : treeNodes){
                if (n1.getId()==n2.getPid()){
                    n1.getChildren().add(n2);
                }
            }
        }
        return nodes;
    }
}
