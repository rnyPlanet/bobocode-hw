package org.example.normal;

import org.example.annotation.Autowired;
import org.example.annotation.Component;

@Component
public class MessageService {
    @Autowired
    private PrinterService printerService;
}
