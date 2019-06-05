package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType.STANDARD;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    private InvoiceFactory invoiceFactoryStub;
    private TaxPolicy taxPolicyStub;
    private BookKeeper sut;

    private ProductData getProductData() {
        return new ProductData(Id.generate(), Money.ZERO, "Ejżur,Ażure", STANDARD, new Date());
    }

    @Before
    public void setUp() {
        invoiceFactoryStub = mock(InvoiceFactory.class);
        taxPolicyStub = mock(TaxPolicy.class);
        sut = new BookKeeper(invoiceFactoryStub);
    }

    @Test
    public void shouldReturnInvoiceWithSinglePosition() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);
        invoiceRequest.add(new RequestItem(getProductData(), 1, new Money(1)));

        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(new Money(1), null));

        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);
        assertEquals(1, result.getItems().size());
    }

    @Test
    public void shouldCalculateTaxBeCalledTwice() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(null);

        invoiceRequest.add(new RequestItem(getProductData(), 1, new Money(1)));
        invoiceRequest.add(new RequestItem(getProductData(), 1, new Money(1)));

        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(new Money(1), null));

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(taxPolicyStub, times(2)).calculateTax(any(), any());
    }

    @Test
    public void shouldReturnEmptyInvoice() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(Id.generate(), "nowak"));

        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);

        assertEquals(0, result.getItems().size());
        assertEquals(Money.ZERO, result.getGros());
        assertEquals(Money.ZERO, result.getNet());
    }

    @Test
    public void shouldReturnProperClientData() {
        ClientData expectedClientData = new ClientData(Id.generate(), "nowak");

        InvoiceRequest invoiceRequest = new InvoiceRequest(expectedClientData);

        when(invoiceFactoryStub.create(any())).thenCallRealMethod();
        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);

        assertEquals(expectedClientData.getAggregateId(), result.getClient().getAggregateId());
        assertEquals(expectedClientData.getName(), result.getClient().getName());
    }

    @Test
    public void shouldNotCallTaxPolicyOnEmptyInvoiceRequest() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(Id.generate(), "nowak"));

        when(invoiceFactoryStub.create(any())).thenCallRealMethod();

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(taxPolicyStub, times(0)).calculateTax(any(), any());
    }

    @Test
    public void shouldInvoiceFactoryCreateOneInvoice() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(Id.generate(), "nowak"));

        invoiceRequest.add(new RequestItem(getProductData(), 10, new Money(100)));
        invoiceRequest.add(new RequestItem(getProductData(), 14, new Money(10)));

        when(invoiceFactoryStub.create(any())).thenCallRealMethod();
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(Money.ZERO, "test"));

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(invoiceFactoryStub, times(1)).create(any());
    }
}
