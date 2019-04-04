package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    private InvoiceFactory invoiceFactoryStub;
    private TaxPolicy taxPolicyStub;
    private BookKeeper sut;

    @Before
    public void setUp() {
        invoiceFactoryStub = mock(InvoiceFactory.class);
        taxPolicyStub = mock(TaxPolicy.class);
        sut = new BookKeeper(invoiceFactoryStub);
    }

    @Test
    public void shouldReturnInvoiceWithSinglePosition() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);
        ProductData productDataStub = mock(ProductData.class);
        invoiceRequest.add(new RequestItem(productDataStub, 1, new Money(1)));

        when(productDataStub.getType()).thenReturn(ProductType.STANDARD);
        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(new Money(1), null));

        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);
        assertEquals(1, result.getItems().size());
    }

    @Test
    public void shouldCalculateTaxBeCalledTwice() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);
        ProductData productDataStub = mock(ProductData.class);
        invoiceRequest.add(new RequestItem(productDataStub, 1, new Money(1)));
        invoiceRequest.add(new RequestItem(productDataStub, 1, new Money(1)));

        when(productDataStub.getType()).thenReturn(ProductType.STANDARD);
        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(new Money(1), null));

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(taxPolicyStub, times(2)).calculateTax(any(), any());
    }

}
