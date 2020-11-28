package com.zh.study.importSelector;

import com.zh.study.bean.Ball;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class BallImportSelector implements ImportSelector {
    /**
     * Select and return the names of which class(es) should be imported based on
     * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
     *
     * @param importingClassMetadata
     * @return the class names, or an empty array if none
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"com.zh.study.bean.Ball"};
    }
}
