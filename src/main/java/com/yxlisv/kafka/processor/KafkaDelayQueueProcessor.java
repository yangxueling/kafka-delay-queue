package com.yxlisv.kafka.processor;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.yxlisv.kafka.annotation.KafkaDelayQueue;
import com.yxlisv.kafka.util.JsrUtil;
import com.yxlisv.kafka.util.KafkaUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;


/**
 * 使用JSR269规范解析KafkaDelayQueue`
 *
 * @author yangxueling
 * @date 2021/12/17
 */
@SupportedAnnotationTypes("com.yxlisv.kafka.annotation.KafkaDelayQueue")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class KafkaDelayQueueProcessor extends AbstractProcessor {


    /**
     * 处理方法
     * 在方法体前面加入前置校验
     *
     * @param annotations TypeElement
     * @param roundEnv    RoundEnvironment
     * @author yangxueling
     * @date 2021/12/17
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        final JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
        final TreeMaker treeMaker = TreeMaker.instance(context);

        //遍历元素
        for (Element element : roundEnv.getElementsAnnotatedWith(KafkaDelayQueue.class)) {

            //获取方法声明
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(element);

            //定位到方法开始位置
            treeMaker.pos = jcMethodDecl.pos;

            //ConsumerRecord 提取
            String msgField = null;
            for (JCTree.JCVariableDecl var : jcMethodDecl.getParameters()) {
                if (var.toString().contains("ConsumerRecord")) {
                    msgField = var.name.toString();
                    break;
                }
            }
            if (msgField == null) {
                return true;
            }

            //延迟时间
            long delayDuration = element.getAnnotation(KafkaDelayQueue.class).delayDuration();

            //调用延迟处理方法
            jcMethodDecl.body = treeMaker.Block(0, List.of(
                    treeMaker.Exec(
                            JsrUtil.getExecInvoke(
                                    treeMaker,
                                    elementUtils,
                                    KafkaUtil.class.getName().concat(".delay"),
                                    List.of(treeMaker.Ident(elementUtils.getName(msgField)), treeMaker.Literal(delayDuration))
                            )
                    ),
                    jcMethodDecl.body
            ));

        }
        return true;
    }

}