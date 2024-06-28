package dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Order {
    private List<String> ingredients = new ArrayList<>();

    public Order(List<String> ingredients){
        this.ingredients = ingredients;
    }
    public Order(){}

}
