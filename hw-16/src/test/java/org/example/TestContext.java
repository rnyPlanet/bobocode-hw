package org.example;

import org.example.context.ApplicationContextImpl;
import org.example.exception.NoSuchBeanException;
import org.example.exception.NoUniqueBeanException;
import org.example.normal.MessageService;
import org.example.nouniquebean.PrinterService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestContext {

    @Test
    public void returnBeanByName() {
        var context = new ApplicationContextImpl("org.example.normal");
        assertNotNull(context.getBean("messageService", MessageService.class));
    }

    @Test
    public void noSuchBeanExceptionNoBeanWithSuchName() {
        var context = new ApplicationContextImpl("org.example.normal");
        assertThrows(NoSuchBeanException.class, () -> context.getBean("bean", PrinterService.class));
    }

    @Test
    public void noSuchBeanException() {
        assertThrows(NoSuchBeanException.class, () -> new ApplicationContextImpl("org.example.nosuchbean"));
    }

    @Test
    public void noUniqueBeanException() {
        assertThrows(NoUniqueBeanException.class, () -> new ApplicationContextImpl("org.example.nouniquebean"));
    }
}
