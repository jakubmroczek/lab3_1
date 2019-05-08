package test.utils;

import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerBuilder {

    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext = new SystemContext();

    public AddProductCommandHandlerBuilder(ReservationRepository reservationRepository, ProductRepository productRepository, SuggestionService suggestionService, ClientRepository clientRepository) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.suggestionService = suggestionService;
        this.clientRepository = clientRepository;
    }

    public AddProductCommandHandlerBuilder withSystemContext(SystemContext systemContext) {
        this.systemContext = systemContext;
        return this;
    }

    public AddProductCommandHandler build() {
        return new AddProductCommandHandler(reservationRepository, productRepository, suggestionService, clientRepository, systemContext);
    }
}
