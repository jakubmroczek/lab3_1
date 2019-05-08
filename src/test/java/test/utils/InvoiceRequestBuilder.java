package test.utils;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItem;

import java.util.Collections;
import java.util.List;

public class InvoiceRequestBuilder {

    private ClientData client = new ClientDataBuilder().build();
    private List<RequestItem> items = Collections.emptyList();

    public InvoiceRequestBuilder withClient(ClientData client) {
        this.client = client;
        return this;
    }

    public InvoiceRequestBuilder withItems(List<RequestItem> items) {
        this.items = items;
        return this;
    }

    public InvoiceRequest build() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        items.forEach(x -> invoiceRequest.add(x));
        return invoiceRequest;
    }
}
