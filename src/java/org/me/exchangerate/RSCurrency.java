/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.exchangerate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Rhayan
 */
@Singleton
@Path("conversion")
public class RSCurrency {
    
    private final HashMap<String, SystemCurrency> currencies;    
    
    
    public RSCurrency(){
        currencies = new HashMap<>();
        setup();
    }
    
    @GET
    @Path("{currency1}/{currency2}/{amount_of_currency1}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getConversion(@PathParam("currency1") String currency1,
            @PathParam("currency2") String currency2, 
            @PathParam("amount_of_currency1") String amount_of_currency1){
        
        BigDecimal amount = new BigDecimal(amount_of_currency1.replaceAll(",", ""));
         
        BigDecimal currencyTo = amount.multiply(new BigDecimal(1));
        
        if (currency1 == "USD"){
            if (currency2 != "USD"){
                currencyTo = amount.multiply(currencies.get(currency2).aDollarToLocal);            
            }           
        }else if (currency1 != "USD"){
            if (currency2 == "USD"){
                currencyTo = amount.multiply(currencies.get(currency2).aLocalToDollar);
            }else{
                currencyTo = (amount.multiply(currencies.get(currency1).aLocalToDollar))
                                    .multiply(currencies.get(currency2).aDollarToLocal);
            }
        }else{
            //currency not recognize
        }
                
        
        //return amount.toPlainString() + currency1 + " = " + currencyTo.toPlainString() + currency2;
        return currencyTo.toPlainString();
    }

    @GET   
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, SystemCurrency> getCurrencies() {
        return currencies;
    }
    
           
    @PostConstruct
    public void init(){
        System.out.println("Singleton Object for this RESTfull Web Service has been created!");
    }
    
    @PreDestroy
    public void clean(){
        System.out.println("Singleton Object for this RESTfull Web Service has been cleaned!");
    }

    private void setup() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String csvFile = "/Users/Rhayan/SkyDrive/Documents/conversion.csv";        
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
        
        try {
  
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
 
			// use comma as separator
			String[] foreignrate = line.split(cvsSplitBy);
                        SystemCurrency cur = new SystemCurrency();
                        cur.setSymbol(foreignrate[0]);
                        cur.setName(foreignrate[1]);
                        cur.setaDollarToLocal(new BigDecimal(foreignrate[2].replaceAll(",", "")));
                        cur.setaLocalToDollar(new BigDecimal(foreignrate[3].replaceAll(",", "")));
 
			currencies.put(foreignrate[0], cur);                        
 		}
 		//loop map
//		for (Map.Entry<String, SystemCurrency> entry : currencies.entrySet()) {
// 
//			System.out.println("Country [symbol= " + entry.getKey() + " , rate to USD="
//				+ entry.getValue().aLocalToDollar + "]");
// 
//		}
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
	System.out.println("Setting up currency services is finished.");
  }
 
}  

