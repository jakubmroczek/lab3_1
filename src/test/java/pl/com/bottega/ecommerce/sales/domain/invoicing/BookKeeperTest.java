package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookKeeperTest {

    @Test
    public void shouldReturnIvoiceWithSinglePosition() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);
        invoiceRequest.add(new RequestItem(null, 1, null));

        InvoiceFactory invoiceFactoryStub = mock(InvoiceFactory.class);
        TaxPolicy taxPolicyStub = mock(TaxPolicy.class);

//        when(invoiceStub.getItems()).thenReturn()
//        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice())
    }

}
