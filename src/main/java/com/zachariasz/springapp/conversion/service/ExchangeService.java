package com.zachariasz.springapp.conversion.service;


import com.zachariasz.springapp.conversion.exception.ExchangeNotFoundException;
import com.zachariasz.springapp.conversion.model.Exchange;
import com.zachariasz.springapp.conversion.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

@Component
public class ExchangeService {
    private String baseExchange = "PLN";
    private final ExchangeRepository exchangeRepository;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    public String checkExchange(Exchange exchange) {
        if(checkId(exchange.getId()) != "" && checkValue(exchange.getValue()) != "")
            return checkId(exchange.getId()) + " " + checkValue(exchange.getValue());
        return checkId(exchange.getId()) + checkValue(exchange.getValue());
    }

    public String checkId(String id){
        String message = "";
        if (id == null) {
            message = message + "ID cannot be null";
        } else{
            if (id.length() != 3) {
                message = message + "3 chars, ";
            }
            if (!Pattern.matches("[a-zA-Z]+", id)) {
                message = message + "only a-z or A-Z letters, ";
            }
            if (message.length() > 0) {
                message = "ID should contain: " + message;
                return message.substring(0, message.length() - 2);
            }
        }
        return message;
    }
    public String checkValue(Double value){
        String message = "";
        if (value == null) {
            message = message + "VALUE cannot be null";
        }else{
            if(value == 0){
                message = message + "be zero, ";
            }
            if(message.length()>0){
                message = "VALUE should not: " + message;
                return message.substring(0, message.length() - 2);
            }
        }
        return message;
    }

    public Exchange getExchange(String firstExchange, String secondExchange) throws ExchangeNotFoundException {

        if(firstExchange.equals(secondExchange)){
            return new Exchange( firstExchange + "/" + secondExchange, 1.0);
        }

         Double firstExchangeValue = calculateExchangeValue(firstExchange);
         Double secondExchangeValue = calculateExchangeValue(secondExchange);

        if(firstExchangeValue == null || secondExchangeValue == null){
            throw new ExchangeNotFoundException(firstExchange + "/" + secondExchange);
        }else {
            Double result = BigDecimal.valueOf(firstExchangeValue/secondExchangeValue)
                    .setScale(3, RoundingMode.HALF_UP)
                    .doubleValue();
            return new Exchange(firstExchange + "/" + secondExchange, result);
        }
    }

    private Double calculateExchangeValue(String exchangeValue){
        if(exchangeValue.equals(baseExchange)){
            return 1.0;
        }else{
            Exchange exchange = exchangeRepository.findByIdIgnoreCase(exchangeValue);
            if(exchange == null){
                return null;
            }else {
                return exchange.getValue();
            }
        }
    }
}




