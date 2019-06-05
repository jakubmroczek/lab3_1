package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import test.utils.InvoiceRequestBuilder;
import test.utils.ProductDataBuilder;

import java.util.ArrayList;
import java.util.Date;
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

    private InvoiceRequest getInvoiceRequest(ClientData clientData, ProductData... data) {
        List<RequestItem> items = new ArrayList<>();
        for(ProductData productData : data) {
            items.add(new RequestItem(productData, 1, Money.ZERO));
        }
        return new InvoiceRequestBuilder().withClient(clientData).withItems(items).build();
    }

    private InvoiceRequest getInvoiceRequest(ProductData... data) {
        return getInvoiceRequest(null, data);
    }

    private ProductData getProductData() {
        return new ProductDataBuilder()
                .withName("Ejżur,Ażure").
                withPrice(Money.ZERO)
                .withProductId(Id.generate())
                .withSnapshotDate(new Date())
                .build();
    }

    @Before
    public void setUp() {
        invoiceFactoryStub = mock(InvoiceFactory.class);
        taxPolicyStub = mock(TaxPolicy.class);
        productDataStub =  mock(ProductData.class);
        sut = new BookKeeper(invoiceFactoryStub);

        when(invoiceFactoryStub.create(any())).thenReturn(new Invoice(null, null));
        when(taxPolicyStub.calculateTax(any(), any())).thenReturn(new Tax(Money.ZERO, "test"));
        when(invoiceFactoryStub.create(any())).thenCallRealMethod();
    }

    @Test
    public void shouldReturnInvoiceWithSinglePosition() {
        Invoice result = sut.issuance(getInvoiceRequest(getProductData()), taxPolicyStub);

        assertEquals(1, result.getItems().size());
    }

    @Test
    public void shouldCalculateTaxBeCalledTwice() {
        sut.issuance(getInvoiceRequest(getProductData(), getProductData()), taxPolicyStub);

        verify(taxPolicyStub, times(2)).calculateTax(any(), any());
    }

    @Test
    public void shouldReturnEmptyInvoice() {
        Invoice result = sut.issuance(getInvoiceRequest(), taxPolicyStub);

        assertEquals(0, result.getItems().size());
        assertEquals(Money.ZERO, result.getGros());
        assertEquals(Money.ZERO, result.getNet());
    }

    @Test
    public void shouldReturnProperClientData() {
        ClientData expectedClientData = new ClientData(Id.generate(), "nowak");

        InvoiceRequest invoiceRequest = getInvoiceRequest(expectedClientData);

        Invoice result = sut.issuance(invoiceRequest, taxPolicyStub);

        assertEquals(expectedClientData.getAggregateId(), result.getClient().getAggregateId());
        assertEquals(expectedClientData.getName(), result.getClient().getName());
    }

    @Test
    public void shouldNotCallTaxPolicyOnEmptyInvoiceRequest() {
        sut.issuance(getInvoiceRequest(), taxPolicyStub);

        verify(taxPolicyStub, times(0)).calculateTax(any(), any());
    }

    @Test
    public void shouldInvoiceFactoryCreateOneInvoice() {
        InvoiceRequest invoiceRequest = getInvoiceRequest(getProductData(), getProductData());

        sut.issuance(invoiceRequest, taxPolicyStub);

        verify(invoiceFactoryStub, times(1)).create(any());
    }
}
