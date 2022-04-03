package io.github.mrs.functionaltest.utils.testng.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import io.github.mrs.functionaltest.utils.testng.TestRetryAnalyzer;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

public class TestRetryAnalyzerListener implements IAnnotationTransformer {

    @SuppressWarnings("rawtypes")
	@Override
    public void transform(ITestAnnotation annotation,
    		Class testClass,
    		Constructor testConstructor,
    		Method testMethod) {
    	annotation.setRetryAnalyzer(TestRetryAnalyzer.class);
    }
}
