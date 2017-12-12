package org.eventuate.saga.invoiceservice.service;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.eventuate.saga.invoiceservice.model.Invoice;
import org.learn.eventuate.Constants;
import org.learn.eventuate.coreapi.InvoiceInfo;
import org.learn.eventuate.coreapi.RequestInvoiceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class InvoiceCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(InvoiceCommandHandler.class);

    @Autowired
    private InvoiceService invoiceService;

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(Constants.INVOCE_SERVICE)
                .onMessage(RequestInvoiceCommand.class, this::invoiceRequest)
                .build();
    }

    private Message invoiceRequest(CommandMessage<RequestInvoiceCommand> commandMessage) {
        log.info("received RequestInvoiceCommand");
        RequestInvoiceCommand command = commandMessage.getCommand();

        Invoice invoice = invoiceService.createInvoice(command.getOrderId(), command.getProductInfo());
        return withSuccess(new InvoiceInfo(invoice.getOrderId(), invoice.getId(), invoice.getInvoiceString()));
    }
}
