package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    @Mock
    public ReservationRepository reservationRepository;

    @Mock
    public ProductRepository productRepository;

    @Mock
    public SuggestionService suggestionService;

    @Mock
    public ClientRepository clientRepository;

    @Mock
    public Reservation reservation;

    private AddProductCommandHandler sut;

    private Product getProduct() {
        Product result = new Product(Id.generate(), Money.ZERO, "Cudzesy", ProductType.STANDARD);
        result.markAsRemoved();
        return result;
    }

    private SystemContext getSystemContext() {
        return new SystemContext();
    }

    @Before
    public void setUp() {
        sut = new AddProductCommandHandler(reservationRepository,productRepository , suggestionService, clientRepository, getSystemContext());

        when(productRepository.load(any())).thenReturn(getProduct());
        when(clientRepository.load(any())).thenReturn(new Client());
        when(reservationRepository.load(any())).thenReturn(reservation);
    }

    @Test
    public void shouldSuggestedProductBeInReservation() {
        final ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

        sut.handle(new AddProductCommand(Id.generate(), Id.generate(), 1));

        verify(reservationRepository).save(captor.capture());

        final Reservation response = captor.getValue();

        assertFalse(response.contains(getProduct()));
    }

    @Test
    public void shouldSuggestionProductBeCalled() {
        sut.handle(new AddProductCommand(Id.generate(), Id.generate(), 1));

        verify(suggestionService, times(1)).suggestEquivalent(any(), any());
    }

}
