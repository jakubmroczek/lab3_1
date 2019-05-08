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
import test.utils.ClientDataBuilder;
import test.utils.InvoiceRequestBuilder;
import test.utils.RequestItemBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    private InvoiceFactory invoiceFactoryStub;
    private TaxPolicy taxPolicyStub;
    private ProductData productDataStub;
    private BookKeeper sut;

    @Before
    public void setUp() {
        invoiceFactoryStub = mock(InvoiceFactory.class);
        taxPolicyStub = mock(TaxPolicy.class);
        productDataStub =  mock(ProductData.class);
        sut = new BookKeeper(invoiceFactoryStub);
    }

    @Test
    public void shouldReturnInvoiceWithSinglePosition() {
        RequestItem requestItem = new RequestItemBuilder().withProductData(productDataStub).withQuantity(1).withTotalCost(new Money(1)).build();
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withItems(Arrays.asList(requestItem)).build();


        when(productDataStub.getType()).thenReturn(ProductType.STANDARD);
        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(new Money(1), null));

        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);
        assertEquals(1, result.getItems().size());
    }

    @Test
    public void shouldCalculateTaxBeCalledTwice() {
        RequestItem firstRequestItem = new RequestItemBuilder().withProductData(productDataStub).withQuantity(1).withTotalCost(new Money(1)).build();
        RequestItem secondRequestItem = new RequestItemBuilder().withProductData(productDataStub).withQuantity(5).withTotalCost(new Money(10)).build();

        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withItems(Arrays.asList(firstRequestItem, secondRequestItem)).build();

        when(productDataStub.getType()).thenReturn(ProductType.STANDARD);
        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(new Money(1), null));

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(taxPolicyStub, times(2)).calculateTax(any(), any());
    }

    @Test
    public void shouldReturnEmptyInvoice() {
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().build();

        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);

        assertEquals(0, result.getItems().size());
        assertEquals(Money.ZERO, result.getGros());
        assertEquals(Money.ZERO, result.getNet());
    }

    @Test
    public void shouldReturnProperClientData() {
        ClientData expectedClientData = new ClientDataBuilder().build();
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withClient(expectedClientData).build();

        when(invoiceFactoryStub.create(any())).thenCallRealMethod();
        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);

        assertEquals(expectedClientData.getAggregateId(), result.getClient().getAggregateId());
        assertEquals(expectedClientData.getName(), result.getClient().getName());
    }

    @Test
    public void shouldNotCallTaxPolicyOnEmptyInvoiceRequest() {
        ClientData expectedClientData = new ClientDataBuilder().build();
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withClient(expectedClientData).build();

        when(invoiceFactoryStub.create(any())).thenCallRealMethod();

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(taxPolicyStub, times(0)).calculateTax(any(), any());
    }

    @Test
    public void shouldInvoiceFactoryCreateOneInvoice() {
        ClientData expectedClientData = new ClientDataBuilder().build();
        RequestItem firstRequestItem = new RequestItemBuilder().withProductData(productDataStub).withQuantity(1).withTotalCost(new Money(1)).build();
        RequestItem secondRequestItem = new RequestItemBuilder().withProductData(productDataStub).withQuantity(5).withTotalCost(new Money(10)).build();
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withClient(expectedClientData).withItems(Arrays.asList(firstRequestItem, secondRequestItem)).build();

        when(invoiceFactoryStub.create(any())).thenCallRealMethod();
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(Money.ZERO, "test"));

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(invoiceFactoryStub, times(1)).create(any());
    }
}
