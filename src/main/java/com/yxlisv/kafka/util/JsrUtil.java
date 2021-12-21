package com.yxlisv.kafka.util;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;

/**
 * Jsr工具类
 *
 * @author yangxueling
 * @date 2021/12/17
 */
public class JsrUtil {

    /**
     * 获取打印表达式
     *
     * @param msg 要打印的内容
     * @author yangxueling
     * @date 2021/12/17
     */
    public static JCTree.JCExpressionStatement getExecPrint(TreeMaker treeMaker, JavacElements elementUtils, String msg) {
        return treeMaker.Exec(
                treeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        treeMaker.Select(
                                treeMaker.Select(
                                        treeMaker.Ident(
                                                elementUtils.getName("System")
                                        ),
                                        elementUtils.getName("out")
                                ),
                                elementUtils.getName("println")
                        ),
                        List.<JCTree.JCExpression>of(
                                treeMaker.Literal(msg)
                        )
                )
        );
    }


    /**
     * 获取方法调用表达式
     *
     * @author yangxueling
     * @date 2021/12/17
     */
    public static JCTree.JCMethodInvocation getExecInvoke(TreeMaker treeMaker, JavacElements elementUtils, String invokePath, List<JCTree.JCExpression> args) {
        //组装调用表达式
        JCTree.JCExpression invokeExpression = null;
        for (String path : invokePath.split("\\.")) {
            if (invokeExpression == null) {
                invokeExpression = treeMaker.Ident(
                        elementUtils.getName(path)
                );
            } else {
                invokeExpression = treeMaker.Select(
                        invokeExpression,
                        elementUtils.getName(path)
                );
            }
        }


        //返回方法调用
        return treeMaker.Apply(
                List.nil(),
                invokeExpression,
                args
        );
    }


    /**
     * 获取校验表达式
     *
     * @author yangxueling
     * @date 2021/12/17
     */
    public static JCTree.JCIf getExecCheck(TreeMaker treeMaker, JavacElements elementUtils, String invokePath, List<JCTree.JCExpression> args) {

        //语法：
        //return;
        //treeMaker.Return(null);

        //返回IF表达式
        return treeMaker.If(
                getExecInvoke(treeMaker, elementUtils, invokePath, args),
                treeMaker.Return(null),
                null
        );
    }

}