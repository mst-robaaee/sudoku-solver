package net.mostow.controller;

import net.mostow.delegate.Display;
import net.mostow.service.Savant;
import net.mostow.util.exchanger.Exchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/sudoku")
public class Informer {

    @Autowired
    Exchanger exchanger;

    @Autowired
    Savant tinker;
    @Autowired
    Display display;

    @RequestMapping(value = "/simple", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> simpleTable(@RequestBody String matrixBody){
        long passedTime=0L;
        try {
            long startProcessing = System.currentTimeMillis();
            ArrayList<ArrayList<Integer>> solvedForm = tinker.solve(matrixBody);
            long endProcessing = System.currentTimeMillis();
            passedTime = endProcessing - startProcessing;
            MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add("X-Response-Time", Long.toString(passedTime));
            return new ResponseEntity<>(exchanger.swapToJson(solvedForm).toString(), headers, HttpStatus.OK);
        } catch (Exception e ) {
            e.printStackTrace();
            return new ResponseEntity<>(String.format("internal error: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            System.out.println(String.format("response in %sms", passedTime));
        }
    }
}
