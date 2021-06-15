package org.jfantasy.framework.spring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ClassPathScannerTest {

    private static final Log logger = LogFactory.getLog(ClassPathScannerTest.class);

    private ClassPathScanner pathScanner;

    @BeforeEach
    public void setUp() throws Exception {
        pathScanner = new ClassPathScanner();
    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindTargetClassNames() throws Exception {
        Set<String> classeNames = pathScanner.findTargetClassNames("org.jfantasy.framework.spring");
        for (String clazz : classeNames) {
            logger.debug(clazz);
        }
    }

    @Test
    public void testFindAnnotationedClasses() throws Exception {
        Set<Class> classes = pathScanner.findAnnotationedClasses("", JsonIgnoreProperties.class);
        for (Class clazz : classes) {
            logger.debug(clazz);
        }
    }

    @Test
    public void testFindInterfaceClasses() throws Exception {
        Set<Class> classes = ClassPathScanner.getInstance().findInterfaceClasses("org.jfantasy.*.bean", BaseBusEntity.class);
        for (Class clazz : classes) {
            logger.debug(clazz);
        }
    }
}