package com.github.mrs.functionaltest.utils.testng.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.github.mrs.functionaltest.utils.testng.TestRetryAnalyzer;

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
