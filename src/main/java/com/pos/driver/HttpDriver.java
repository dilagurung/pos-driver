package com.pos.driver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by oa on 3/21/2018.
 */
public class HttpDriver
{

    static boolean isWeighDriverOn=false;
    static String printerName;
        public static void main(String[] args) throws Exception {

PrintingExample.callPrinter();
/*
            HttpServer server = HttpServer.create(new InetSocketAddress(7070), 0);
            server.createContext("/print-receipt", new MyHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
*/

        }
static class MyHandler implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException
    {
        String query=httpExchange.getRequestURI().getQuery();
        String attribute=query.split("=")[0];
        String value=query.split("=")[1];
        if(value.contains("drawer"))
        {
            openDrawer(); httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(null);
            os.close();
            return;
        }
        else if (attribute.equals("weight"))
        {
            // pass like this ?weight=com4
           if(!isWeighDriverOn)
           {   executePowerShell(value);
               isWeighDriverOn=true;
               try {
                   Thread.sleep(3000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }

            String response = weigh(value).replace("\u0002","");


            //httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
      /*      httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            httpExchange.sendResponseHeaders(204, -1);

      */
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        else if(attribute.equals("print"))
        {
            printerName=value;
        System.out.println(printerName+"\n\n\n\n\n name of printer "+printerName);
        }
        InputStreamReader isr =  new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1)
        {
            buf.append((char) b);
        }
        JSONParser parser = new JSONParser();
        Object obj = null; try {
        obj = parser.parse(buf.toString());
    } catch (ParseException e) {
        e.printStackTrace();
    }
        JSONObject jsonObject = (JSONObject) obj;
        System.out.println(jsonObject);
        String id =  jsonObject.get("id").toString(); //invoice id
        JSONObject organization = (JSONObject)(jsonObject.get("organization"));
        String terminal = (String) jsonObject.get("terminal");
        String user = (String) jsonObject.get("user");
        String total =  jsonObject.get("total")+"";
        String customer = (String) jsonObject.get("customer");
        JSONArray items=((JSONArray)jsonObject.get("saleSet"));
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String date= f.format(new Date(Long.parseLong(jsonObject.get("createdOn")+"")));
        String tax=""; //total tax
        new PrinterOptions().printReceipt(id,organization,terminal,user,total,customer,items,date,tax);
   //     ((HashMap<String,String>)((JSONArray)jsonObject.get("saleSet")).get(2)).get("productDescription");
        br.close();
        isr.close();


        String response = "hello world";
        httpExchange.sendResponseHeaders(200, response.length());
        System.out.println(response);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
public static void openDrawer()
{
    System.out.println("open drawer called");
    byte[] open = {27,112,0,100,(byte) 250};
//      byte[] cutter = {29, 86,49};
    PrintService pservice =
            PrintServiceLookup.lookupDefaultPrintService();
    DocPrintJob job = pservice.createPrintJob();
    DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
    Doc doc = new SimpleDoc(open,flavor,null);
    PrintRequestAttributeSet aset = new
            HashPrintRequestAttributeSet();
    try {
        job.print(doc, aset);
    } catch (PrintException ex) {
        System.out.println(ex.getMessage());
    }

}

protected  static String weigh(String com)
{

    Process proc=null;
    BufferedReader stdInput=null;
    try {
         proc= Runtime.getRuntime().exec("cmd.exe /c " + " type com"+com);
          stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null)
        {
            System.out.println(s);
            break;
        }
        return  s;
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    finally {
        try {
            stdInput.close();
            proc.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

return "0";
}


public static String executePowerShell(String comPort) throws IOException
{
    String command = "powershell.exe  $port= new-Object System.IO.Ports.SerialPort COM"+comPort +",9600,N,8,1; $port.open()";
    //String command = "powershell.exe  $port= new-Object System.IO.Ports.SerialPort COM4,9600,N,8,1; $port.open()";
    //Getting the version$port= new-Object System.IO.Ports.SerialPort COM4,9600,N,8,1
   // String command = "powershell.exe  $PSVersionTable.PSVersion";
    // Executing the command
    Process powerShellProcess = Runtime.getRuntime().exec(command);
    // Getting the results
    powerShellProcess.getOutputStream().close();
    String line;
    System.out.println("Standard Output:");
    BufferedReader stdout = new BufferedReader(new InputStreamReader(
            powerShellProcess.getInputStream()));
    while ((line = stdout.readLine()) != null) {
        System.out.println(line);
    }
    stdout.close();
    System.out.println("Standard Error:");
    BufferedReader stderr = new BufferedReader(new InputStreamReader(
            powerShellProcess.getErrorStream()));
    while ((line = stderr.readLine()) != null) {
        System.out.println(line);
    }
    stderr.close();
    return line;
}


}
/*

 $port= new-Object System.IO.Ports.SerialPort COM4,9600,N,8,1
 $port.open()

close powershell and type type com4
*/


