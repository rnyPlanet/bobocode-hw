package org.example.nosuchbean;

import org.example.annotation.Autowired;
import org.example.annotation.Component;
import org.example.nouniquebean.PrinterService;

@Component
public class MessageService {
    @Autowired
    private PrinterService printerService;
}
