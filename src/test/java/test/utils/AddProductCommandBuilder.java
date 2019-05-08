package test.utils;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;

public class AddProductCommandBuilder {

    private Id orderId = Id.generate();
    private Id productId = Id.generate();
    private int quantity = 1;

    public AddProductCommandBuilder withOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    public AddProductCommandBuilder withProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    public AddProductCommandBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public AddProductCommand build() {
        return new AddProductCommand(orderId, productId, quantity);
    }

}
