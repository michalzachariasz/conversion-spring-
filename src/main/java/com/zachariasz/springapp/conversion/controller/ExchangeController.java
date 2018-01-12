package com.zachariasz.springapp.conversion.controller;

import com.zachariasz.springapp.conversion.exception.ExchangeNotFoundException;
import com.zachariasz.springapp.conversion.exception.IncorrectInputDataException;
import com.zachariasz.springapp.conversion.model.Exchange;
import com.zachariasz.springapp.conversion.repository.ExchangeRepository;
import com.zachariasz.springapp.conversion.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final ExchangeRepository exchangeRepository;

    @Autowired
    public ExchangeController(ExchangeService exchangeService, ExchangeRepository exchangeRepository) {
        this.exchangeService = exchangeService;
        this.exchangeRepository = exchangeRepository;
    }

    @GetMapping("all")
    public HttpEntity<List<Exchange>> getAllExchanges() {
        return new ResponseEntity<>(exchangeRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping
    public Exchange getExchange(
            @RequestParam("FE") String firstExchange,
            @RequestParam("SE") String secondExchange) throws ExchangeNotFoundException, IncorrectInputDataException {
        checkInputParameters(firstExchange, secondExchange);
        return exchangeService.getExchange(firstExchange, secondExchange);
    }

    @PostMapping
    public HttpEntity<Exchange> addExchange(@RequestBody Exchange exchange) throws IncorrectInputDataException {
        String testResult = exchangeService.checkExchange(exchange);
        if (testResult.trim().length() == 0) {
            return new ResponseEntity<>(exchangeRepository.save(exchange), HttpStatus.CREATED);
        }
        throw new IncorrectInputDataException(testResult);
    }

    @DeleteMapping
    public HttpEntity<Exchange> deleteExchange(@RequestParam("id") String id) throws IncorrectInputDataException, ExchangeNotFoundException {
        String testResult = exchangeService.checkId(id);
        if (testResult.trim().length() == 0) {
            if(exchangeRepository.findByIdIgnoreCase(id) != null){
                exchangeRepository.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                throw new ExchangeNotFoundException(id);
            }
        }
            throw new IncorrectInputDataException(testResult);
    }

    private void checkInputParameters(String firstExchange, String secondExchange) throws IncorrectInputDataException {
        String firstExchangeMessage = exchangeService.checkId(firstExchange);
        String secondExchangeMessage = exchangeService.checkId(secondExchange);
        String message = "";

        if (firstExchangeMessage.trim().length() != 0) {
            message = message + " First Value -> " + firstExchangeMessage;
        }
        if (secondExchangeMessage.trim().length() != 0) {
            message = message + " Second Value -> " + secondExchangeMessage;
        }
        if (message.length() > 0) {
            message = message.substring(1, message.length());
            throw new IncorrectInputDataException(message);
        }
    }

}
