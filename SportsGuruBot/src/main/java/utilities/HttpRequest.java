/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Jacopo
 */
public class HttpRequest {
    
    final String serverURL = "http://localhost:4000/";//localhost computer
    private String request;
    private String parameter;
    private HttpURLConnection connection;
    
    public HttpRequest(String request) {
        connection= null;
        //System.out.println("Richiesta Ricevuta: "+serverURL+request);
        this.request=request;
    }
    
    public String run(){
        String result="";
        try{
            URL yahoo = new URL(serverURL+request);
            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            
            while ((inputLine = in.readLine()) != null)
                //System.out.println(inputLine);
                result=result+inputLine+"\n";
            in.close();
            /*//Create connection
            URL url = new URL(serverURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type","application/json");
            
            connection.setRequestProperty("Content-Length",Integer.toString(request.getBytes().length));
            
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            //Send request
            DataOutputStream wr = new DataOutputStream (
            connection.getOutputStream());
            wr.writeBytes(request);
            wr.close();
            
            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
            }
            rd.close();
            return response.toString();*/
        } catch (Exception e) {
            System.out.println("Errore nell'esecuzione della request");
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}
