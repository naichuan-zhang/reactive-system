package com.naichuan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.naichuan.constants.OrderStatus;
import com.naichuan.serdeser.ObjectIdSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String userId;
    private List<LineItem> lineItems;
    private Long total;
    private OrderStatus orderStatus;
    private String responseMessage;

    public Order setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Order setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }
}
