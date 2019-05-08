package test.utils;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;

import java.util.Date;

public class ReservationBuilder {
    
    private Id aggregateId = Id.generate();
    private Reservation.ReservationStatus status = Reservation.ReservationStatus.OPENED;
    private ClientData clientData = new ClientDataBuilder().build();
    private Date createDate = new Date();

    public ReservationBuilder withAggregateId(Id aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }

    public ReservationBuilder withStatus(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Reservation build() {
        return new Reservation(aggregateId, status, clientData, createDate);
    }
}
